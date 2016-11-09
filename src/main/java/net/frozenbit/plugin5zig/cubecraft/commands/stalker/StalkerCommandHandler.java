package net.frozenbit.plugin5zig.cubecraft.commands.stalker;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandOutputPrinter;
import net.frozenbit.plugin5zig.cubecraft.commands.UsageException;
import net.frozenbit.plugin5zig.cubecraft.stalker.Storage;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StalkerCommandHandler extends CommandHandler {
    private static URI MOJANG_API;

    private final Main main;
    private ExecutorService executorService;
    private CloseableHttpClient client;

    static {
        try {
            MOJANG_API = new URI("https://api.mojang.com/profiles/minecraft");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public StalkerCommandHandler(Main main) {
        super("stalker", "Manage stored statistics for other players");
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
    public void run(String cmd, List<String> args, CommandOutputPrinter printer) throws UsageException {
        if (args.isEmpty())
            throw new UsageException("Missing command argument");
        switch (args.get(0)) {
            case "show":
                if (args.size() < 2)
                    throw new UsageException("Missing username argument");
                executorService.submit(new LoadPlayerIdRunnable(args.subList(0, 1), args.subList(1, args.size()), printer));
                break;
            case "delete":
                if (args.size() < 2)
                    throw new UsageException("Missing username argument");
                if (args.size() < 3)
                    throw new UsageException("Missing gamemode argument");
                executorService.submit(new LoadPlayerIdRunnable(args.subList(0, 2), args.subList(2, args.size()), printer));
                break;
            default:
                throw new UsageException(String.format("Invalid command: %s", args.get(0)));
        }
    }

    private class LoadPlayerIdRunnable implements Runnable {
        private final List<String> commands;
        private final List<String> usernames;
        private final CommandOutputPrinter printer;

        public LoadPlayerIdRunnable(List<String> commands, List<String> usernames, CommandOutputPrinter printer) {
            this.commands = commands;
            this.usernames = usernames;
            this.printer = printer;
        }

        @Override
        public void run() {
            JSONArray jsonUsernames = new JSONArray(usernames);
            HttpPost request = new HttpPost(MOJANG_API);
            request.addHeader("Content-Type", "application/json");
            HashMap<String, UUID> nameIdPairs;
            try {
                request.setEntity(new StringEntity(jsonUsernames.toString()));
                nameIdPairs = client.execute(request, new MojangResponseHandler());
            } catch (final IOException e) {
                The5zigAPI.getLogger().error("Cannot query uuid list", e);
                main.runOnMainThread(() -> printer.printErrln("Unable to query uuids: " + e.getMessage()));
                return;
            }
            List<String> output = new ArrayList<>();
            for (String name : usernames)
                if (!nameIdPairs.containsKey(name))
                    output.add(ChatColor.RED + String.format("Player not found: %s", name));
            switch (commands.get(0)) {
                case "show":
                    for (String name : nameIdPairs.keySet()) {
                        output.add(String.format("Stats for %s:", name));
                        output.addAll(Storage.getPlayerStats(nameIdPairs.get(name)));
                    }
                    break;
                case "delete":
                    for (String name : nameIdPairs.keySet()) {
                        output.add(String.format("Stats for %s:", name));
                        output.addAll(Storage.deletePlayerStats(nameIdPairs.get(name), commands.get(1)));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("invalid command: " + commands.get(0));
            }
            main.runOnMainThread(() -> {
                for (String line : output)
                    printer.println(line);
            });
        }
    }

    @Override
    public void printUsage(String cmd, CommandOutputPrinter printer) {
        printer.println(".%s show <username> [<username2>...]", cmd);
        printer.println(".%s delete all|<gamemode> <username> [<username2>...]", cmd);
    }
}
