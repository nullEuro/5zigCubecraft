package net.frozenbit.plugin5zig.cubecraft.updater;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import eu.the5zig.mod.The5zigAPI;
import net.frozenbit.plugin5zig.cubecraft.Build;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.updater.models.PluginInfo;
import net.frozenbit.plugin5zig.cubecraft.updater.models.Release;
import org.apache.commons.lang3.SystemUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.iq80.leveldb.util.Closeables;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;

import static java.lang.String.format;

public class Updater {
    private static final String LATEST_RELEASE_URL =
            "https://api.github.com/repos/nullEuro/5zigCubecraft/releases/latest";
    private static final Pattern VERSION_PATTERN =
            Pattern.compile("^v(\\d+)\\.(\\d+)\\.(\\d+)$");
    private static final String MAIN_CLASS_NAME = "net.frozenbit.plugin5zig.cubecraft.Main";
    private static final String PLUGIN_DIRECTORY = "./the5zigmod/plugins";
    private static final String JAR_NAME = "5zigCubecraft.jar";
    private CloseableHttpClient client;
    private Thread updateThread;
    private Main main;
    /**
     * Updated jar if there is a new release, else null
     */
    private File downloadedJar;
    private Release.Asset downloadedAsset;

    public Updater(Main main) {
        this.main = main;
        client = HttpClients.createDefault();
    }

    /**
     * Checks if a http response has a non-error code.
     *
     * @param response Response
     * @return {@link HttpResponse#getEntity()}. This method will never return
     * <code>null</code> but throw an exception
     * @throws ClientProtocolException if the status code is >= 300 or the response
     *                                 contains no content
     */
    private static HttpEntity checkResponse(HttpResponse response)
            throws ClientProtocolException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            throw new HttpResponseException(
                    statusLine.getStatusCode(),
                    statusLine.getReasonPhrase());
        }
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }
        return entity;
    }

    /**
     * Delete files currently in use by this JVM on windows systems. This works by starting a new
     * process when the JVM terminates.
     *
     * @param files Files to delete
     */
    private static void deleteFilesWindowsWorkaround(final List<File> files) {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (File file : files) {
                        Runtime.getRuntime().exec(new String[]{
                                // start process detached from JVM
                                "cmd", "/c", "start", "/b", "cmd", "/c",
                                // try to delete the file until it succeeds
                                format("for /l %%N in () do (del %1$s & IF NOT EXIST %1$s exit)", file.getName()),
                                // discard output to avoid stalling the process
                                "2>&1",
                                ">NUL"
                        }, null, file.getParentFile());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }));
    }

    /**
     * Check for a new release of this plugin and download it in a separate thread
     */
    public void start() {
        updateThread = new Thread(new Runnable() {
            @Override
            public void run() {
                checkReleases();
            }
        });
        updateThread.start();
    }

    /**
     * If the download of a new release is still running, cancel it. If it was already downloaded,
     * swap the current jar with the updated one.
     */
    public void stop() {
        if (downloadedJar != null) {
            installNewJar();
        }
        Closeables.closeQuietly(client);
        updateThread.interrupt();
    }

    /**
     * Check the latest Github release for a new version of this plugin.
     * If there is one, download its Jar file and set {@link #downloadedJar}
     */
    private void checkReleases() {
        HttpGet releaseRequest = new HttpGet(LATEST_RELEASE_URL);
        releaseRequest.setHeader("Accept", "application/json");

        try {
            Release release = client.execute(releaseRequest, new ReleaseResponseHandler());

            if (!isNewVersion(release)) {
                The5zigAPI.getLogger().info("5zigCubecraft plugin is up to date!");
                return;
            }

            The5zigAPI.getLogger().info("5zigCubecraft update available, downloading...");

            downloadedAsset = release.getJarAsset();
            if (downloadedAsset == null) {
                The5zigAPI.getLogger().warn("Release without JAR asset");
                return;
            }

            downloadedJar = downloadJar(downloadedAsset);
            if (downloadedJar != null) {
                main.runOnMainThread(new ShowUpdateNotificationRunnable());
            }
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Cannot check for updates", e);
        }
    }

    /**
     * Find the currently running 5zigCubecraft jar file and replace it with {@link #downloadedJar}
     */
    private void installNewJar() {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                deleteFilesWindowsWorkaround(findOwnJars());
            } else {
                for (File jar : findOwnJars()) {
                    Files.delete(jar.toPath());
                }
            }

            // if this fails, this plugin has just uninstalled itself
            Files.move(downloadedJar.toPath(), Paths.get(PLUGIN_DIRECTORY, downloadedAsset.getName()));
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Cannot install 5zigCubecraft update", e);
        }
    }

    private boolean isNewVersion(Release release) {
        String tagName = release.getTagName();
        Matcher matcher = VERSION_PATTERN.matcher(tagName);
        if (!matcher.matches()) {
            The5zigAPI.getLogger().warn("Invalid release tag name: '%s'", tagName);
            return false;
        }

        int major = Integer.parseInt(matcher.group(1)),
                minor = Integer.parseInt(matcher.group(2)),
                patch = Integer.parseInt(matcher.group(3));

        return major > Build.major
               || (major == Build.major && minor > Build.minor)
               || (major == Build.major && minor == Build.minor && patch > Build.patch);
    }

    private File downloadJar(Release.Asset jarAsset) {
        File target = new File(PLUGIN_DIRECTORY, jarAsset.getName() + ".dl");
        try {
            HttpGet jarDownloadRequest = new HttpGet(jarAsset.getUrl());
            jarDownloadRequest.setHeader("Accept", "application/octet-stream");
            client.execute(jarDownloadRequest, new DownloadResponseHandler(target));
            return target;
        } catch (IOException e) {
            //noinspection ResultOfMethodCallIgnored
            target.delete();
            return null;
        }
    }

    /**
     * Find 5zigCubecrafts' jar file(s)
     *
     * @return List of all jar files  in the 5zig plugin directory that contain this plugin
     */
    private List<File> findOwnJars() {
        List<File> oldJars = new ArrayList<>();
        for (File jar : getPluginJars()) {
            try {
                PluginInfo pluginInfo = getPluginInfo(jar);
                if (pluginInfo != null && MAIN_CLASS_NAME.equals(pluginInfo.getMainClassName())) {
                    oldJars.add(jar);
                }
            } catch (IOException e) {
                The5zigAPI.getLogger().warn("Unable to inspect " + jar.getName(), e);
            }
        }
        return oldJars;
    }

    /**
     * Reads the plugin.json file from the specified jar file.
     *
     * @param file jar file
     * @return The parsed plugin info or null if there is none or it is invalid
     * @throws IOException When the plugin descriptor cannot be accessed
     */
    private PluginInfo getPluginInfo(File file) throws IOException {
        JarFile jarFile = new JarFile(file);
        Gson gson = new Gson();
        ZipEntry pluginJsonEntry = jarFile.getEntry("plugin.json");
        if (pluginJsonEntry == null) {
            return null;
        }
        InputStream inputStream = jarFile.getInputStream(pluginJsonEntry);
        InputStreamReader reader = new InputStreamReader(inputStream);
        try {
            return gson.fromJson(reader, PluginInfo.class);
        } catch (JsonParseException e) {
            return null;
        }
    }

    /**
     * Get all jar files for all 5zig plugins
     *
     * @return All Jar files. Returns an empty array on error.
     */
    private File[] getPluginJars() {
        File pluginDir = new File(PLUGIN_DIRECTORY);
        File[] jars = pluginDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isFile() && file.getName().matches(".*.jar");
            }
        });
        if (jars == null) {
            The5zigAPI.getLogger().warn("Unable to iterate plugins");
            return new File[0];
        } else {
            return jars;
        }
    }

    private static class ReleaseResponseHandler implements ResponseHandler<Release> {
        private final Gson gson;

        private ReleaseResponseHandler() {
            gson = new Gson();
        }

        @Override
        public Release handleResponse(HttpResponse response) throws IOException {
            HttpEntity entity = checkResponse(response);
            ContentType contentType = ContentType.getOrDefault(entity);
            Charset charset = contentType.getCharset();
            Reader reader = new InputStreamReader(entity.getContent(), charset);
            return gson.fromJson(reader, Release.class);
        }
    }

    private static class DownloadResponseHandler implements ResponseHandler<File> {
        private final File target;

        private DownloadResponseHandler(File target) {
            this.target = target;
        }

        @Override
        public File handleResponse(HttpResponse response) throws IOException {
            HttpEntity entity = checkResponse(response);
            try (FileOutputStream out = new FileOutputStream(this.target)) {
                entity.writeTo(out);
            }
            return this.target;
        }
    }

    private static class ShowUpdateNotificationRunnable implements Runnable {
        @Override
        public void run() {
            The5zigAPI.getAPI().createOverlay()
                    .displayMessage("5zigCubecraft updated", "Restart to apply");
        }
    }
}
