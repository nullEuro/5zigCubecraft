package net.frozenbit.plugin5zig.cubecraft.commands.stalker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.Util;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandOutputPrinter;
import net.frozenbit.plugin5zig.cubecraft.commands.UsageException;
import net.frozenbit.plugin5zig.cubecraft.stalker.Storage;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class StalkerCommandHandler extends CommandHandler {
    private static final URI MOJANG_API = URI.create("https://api.mojang.com/profiles/minecraft");

    private final Main main;
    private ExecutorService executorService;
    private CloseableHttpClient client;


    public StalkerCommandHandler(Main main) {
        super("stalker", "Manage stored statistics for other players", "stats");
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
        if (cmd.equals("stats")) {
            if (args.isEmpty())
                throw new UsageException("Missing username argument");
            executorService.submit(new LoadPlayerIdRunnable(Collections.singletonList("show"), args, printer));
            return;
        }
        switch (args.get(0)) {
            case "show":
                if (args.size() < 2)
                    throw new UsageException("Missing username argument");
                executorService.submit(new Util.LoggingRunnable(new LoadPlayerIdRunnable(args.subList(0, 1), args.subList(1, args.size()), printer)));
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

    @Override
    public void printUsage(String cmd, CommandOutputPrinter printer) {
        printer.println(".%s show <username> [<username2>...]", cmd);
        printer.println(".%s delete all|<gamemode> <username> [<username2>...]", cmd);
        printer.println(".stats <username> [<username2>...]", cmd);
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
            HttpPost request = new HttpPost(MOJANG_API);
            request.addHeader("Content-Type", "application/json");
            HashMap<String, UserData> nameToUser = new HashMap<>();
            try {
                request.setEntity(new StringEntity(new Gson().toJson(usernames,
                        new TypeToken<Collection<String>>() {
                        }.getType())));
                List<UserData> users = client.execute(request, new MojangResponseHandler());
                for (UserData user : users) {
                    nameToUser.put(user.name.toLowerCase(), user);
                }
            } catch (final IOException e) {
                The5zigAPI.getLogger().error("Cannot query id list", e);
                main.runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        printer.printErrln("Unable to query uuids: " + e.getMessage());
                    }
                });
                return;
            }
            final List<String> output = new ArrayList<>();
            for (String name : usernames)
                if (!nameToUser.containsKey(name.toLowerCase()))
                    output.add(ChatColor.RED + String.format("Player not found: %s", name));
            switch (commands.get(0)) {
                case "show":
                    for (UserData user : nameToUser.values()) {
                        output.add(playerStatsHeader(user));
                        output.addAll(Storage.getPlayerStats(user.id));
                    }
                    break;
                case "delete":
                    for (UserData user : nameToUser.values()) {
                        output.add(playerStatsHeader(user));
                        output.addAll(Storage.deletePlayerStats(user.id, commands.get(1)));
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command: " + commands.get(0));
            }
            main.runOnMainThread(new Runnable() {
                @Override
                public void run() {
                    for (String line : output)
                        printer.println(line);
                }
            });
        }

        private String playerStatsHeader(UserData user) {
            return String.format("Stats for %s%s%s%s:", ChatColor.LIGHT_PURPLE, ChatColor.BOLD,
                    user.name, ChatColor.RESET);
        }
    }
}
