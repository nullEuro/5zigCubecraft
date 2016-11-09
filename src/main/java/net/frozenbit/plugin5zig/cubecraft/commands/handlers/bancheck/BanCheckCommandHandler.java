package net.frozenbit.plugin5zig.cubecraft.commands.handlers.bancheck;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandOutputPrinter;
import net.frozenbit.plugin5zig.cubecraft.commands.UsageException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A command that check previous bans and mutes of a user
 */
public class BanCheckCommandHandler extends CommandHandler {
    private static final URI BASE_URI = URI.create("https://appeals.cubecraft.net");
    private final Main main;
    private ExecutorService executorService;
    private CloseableHttpClient client;

    public BanCheckCommandHandler(Main main) {
        super("bans", "Check past bans and mutes for a player on CubeCraft");
        this.main = main;
    }

    @Override
    public void onRegister() {
        executorService = Executors.newCachedThreadPool();
        client = HttpClients.createDefault();
    }

    @Override
    public void onUnregister() {
        try {
            client.close();
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Error closing http client", e);
        }
        client = null;
        executorService.shutdownNow();
        try {
            //noinspection StatementWithEmptyBody
            while (!executorService.awaitTermination(1, TimeUnit.SECONDS)) ;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        executorService = null;
    }

    @Override
    public void run(String cmd, List<String> args, final CommandOutputPrinter printer) throws UsageException {
        if (args.isEmpty()) {
            throw new UsageException("Missing username argument");
        }
        printer.println(ChatColor.GREEN + "Loading infractions...");
        executorService.submit(new QueryPunishmentsRunnable(args.get(0), printer));
    }

    @Override
    public void printUsage(String cmd, CommandOutputPrinter printer) {
        printer.println(".%s <username>", cmd);
    }

    /**
     * Runnable that queries the appeals page for a username and prints the results
     */
    private class QueryPunishmentsRunnable implements Runnable {
        private final String username;
        private final CommandOutputPrinter printer;

        public QueryPunishmentsRunnable(String username, CommandOutputPrinter printer) {
            this.username = username;
            this.printer = printer;
        }

        @Override
        public void run() {
            try {
                URI uri = new URIBuilder(BASE_URI).setPath("/find_appeals/" + username).build();
                List<Punishment> punishments = client.execute(new HttpGet(uri), new BanResponseHandler());
                main.runOnMainThread(new ShowPunishmentsRunnable(punishments, printer, username));
            } catch (URISyntaxException e) {
                // this is bullshit
                throw new RuntimeException(e);
            } catch (final IOException e) {
                The5zigAPI.getLogger().error("Cannot query punishment list", e);
                main.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        printer.printErrln("Unable to query infractions: " + e.getMessage());
                    }
                });
            }
        }
    }

    /**
     * Runnable that writes a formatted list of the punishments to a {@link CommandOutputPrinter}
     */
    private class ShowPunishmentsRunnable implements Runnable {
        private final List<Punishment> punishments;
        private final CommandOutputPrinter printer;
        private final String username;

        public ShowPunishmentsRunnable(List<Punishment> punishments, CommandOutputPrinter printer, String username) {
            this.punishments = punishments;
            this.printer = printer;
            this.username = username;
        }

        @Override
        public void run() {
            if (punishments == null) {
                printer.println(ChatColor.GRAY + "%s has never joined the server", username);
            } else if (punishments.isEmpty()) {
                printer.println(ChatColor.GREEN + "%s was never banned or muted", username);
            } else {
                Collections.sort(punishments, new Comparator<Punishment>() {
                    @Override
                    public int compare(Punishment a, Punishment b) {
                        return a.getStart().compareTo(b.getStart());
                    }
                });
                for (Punishment punishment : punishments) {
                    printer.println(" * %s", punishment);
                }
            }
        }
    }
}
