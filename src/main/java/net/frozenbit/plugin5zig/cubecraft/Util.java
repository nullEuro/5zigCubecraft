package net.frozenbit.plugin5zig.cubecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import eu.the5zig.mod.The5zigAPI;

import java.io.IOException;
import java.math.BigInteger;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Util {
    /**
     * Gson instance that can parse data returned from the Mojang API.
     */
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(UUID.class, new UUIDTypeAdapter().nullSafe())
            .create();

    public static String extractGroup(String string, Pattern pattern, int group) {
        Matcher matcher = pattern.matcher(string);
        return matcher.matches() ? matcher.group(group) : null;
    }

    public static class LoggingRunnable implements Runnable {
        private final Runnable delegate;

        public LoggingRunnable(Runnable delegate) {
            this.delegate = delegate;
        }

        @Override
        public final void run() {
            try {
                delegate.run();
            } catch (RuntimeException e) {
                The5zigAPI.getLogger().error("Error in thread " + Thread.currentThread().getName(), e);
                throw e;
            }
        }
    }

    /**
     * Adapter that works with dashless UUIDs as retured by the Mojang API.
     */
    private static class UUIDTypeAdapter extends TypeAdapter<UUID> {
        @Override
        public void write(JsonWriter out, UUID value) throws IOException {
            out.value(value.toString().replaceAll("-", ""));
        }

        @Override
        public UUID read(JsonReader in) throws IOException {
            String stringId = in.nextString();
            return new UUID(
                    new BigInteger(stringId.substring(0, 16), 16).longValue(),
                    new BigInteger(stringId.substring(16), 16).longValue());
        }
    }

}
