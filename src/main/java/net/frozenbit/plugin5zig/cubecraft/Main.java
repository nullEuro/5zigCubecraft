package net.frozenbit.plugin5zig.cubecraft;

import com.google.common.io.ByteStreams;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.LoadEvent;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.event.UnloadEvent;
import eu.the5zig.mod.modules.Category;
import eu.the5zig.mod.plugin.Plugin;
import eu.the5zig.mod.util.IKeybinding;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandRegistry;
import net.frozenbit.plugin5zig.cubecraft.commands.bancheck.BanCheckCommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.stalker.StalkerCommandHandler;
import net.frozenbit.plugin5zig.cubecraft.items.*;
import net.frozenbit.plugin5zig.cubecraft.updater.Updater;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Plugin(name = "5zigCubecraft", version = Build.version)
public class Main {
    public static final Path PLUGIN_PATH = Paths.get("the5zigmod/plugins/5zigCubecraft/");
    private static final String LOG_FILE = "cubecraft_5ziglog.txt";
    private static final File CONFIG_FILE = PLUGIN_PATH.resolve("config.json").toFile();
    private static Main instance;

    private IKeybinding leaveKey, snakeKey;
    private boolean snake;
    private Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    private PrintWriter logger;
    private Updater updater;
    private PluginConfig config;
    private CommandRegistry commandRegistry;

    public static Main getInstance() {
        return instance;
    }

    // ideas: pre gamechat and game summary, friend join notification, no kit warning

    /**
     * Returns a {@link PrintWriter} that writes to {@link #LOG_FILE}. If the log file cannot be
     * opened, the returned writer will discard all messages.
     *
     * @return A PrintWriter that writes to the log file. Never null.
     */
    private static PrintWriter createLogger() {
        try {
            return new PrintWriter(new FileWriter(LOG_FILE), true);
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Cannot open log file, messages will be lost", e);
            return new PrintWriter(ByteStreams.nullOutputStream());
        }
    }

    private static PluginConfig readPluginConfig() {
        try {
            PluginConfig config;
            if (!CONFIG_FILE.exists()) {
                config = new PluginConfig();
                Files.createParentDirs(CONFIG_FILE);
            } else {
                Gson gson = new Gson();
                config = gson.fromJson(new FileReader(CONFIG_FILE), PluginConfig.class);
            }
            writeConfig(config);
            return config;
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Cannot create or read config file, assuming defaults", e);
            return new PluginConfig();
        }
    }

    /**
     * Write the specified configuration to the config file. Will overwrite the old file or create a new one when
     * it does not exist yet.
     *
     * @param config Configuration to serialize and write
     * @throws IOException when the writing fails (no permissions, the file exists but is a directory, ...)
     */
    public static void writeConfig(PluginConfig config) throws IOException {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();
            gson.toJson(config, writer);
        }
    }

    @EventHandler
    public void onLoad(LoadEvent loadEvent) {
        logger = createLogger();
        config = readPluginConfig();

        commandRegistry = new CommandRegistry();
        commandRegistry.register(new BanCheckCommandHandler(this));
        commandRegistry.register(new StalkerCommandHandler(this));
        The5zigAPI.getAPI().getPluginManager().registerListener(this, commandRegistry);

        The5zigAPI.getAPI().registerServerInstance(this, ServerInstance.class);

        The5zigAPI.getAPI().registerModuleItem(this, "snake", SnakeItem.class, Category.OTHER);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftvoters", VoterListItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftkit", KitItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftchest", GameModifiersItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftstalker", StalkerItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftassassinationmoney", MoneyItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftduelsopponent", OpponentItem.class, Category.SERVER_GENERAL);

        leaveKey = The5zigAPI.getAPI().registerKeyBiding("Leave the current game", Keyboard.KEY_L, "Cubecraft");
        snakeKey = The5zigAPI.getAPI().registerKeyBiding("Toggle Snake", Keyboard.KEY_P, "Misc");

        if (config.hasAutoUpdatesEnabled()) {
            updater = new Updater(this);
            updater.start();
        }

        // copy the old skywars database - REMOVE THIS SOME TIME IN THE FUTURE!
        migrateSkywarsDb();

        instance = this;
    }

    private void migrateSkywarsDb() {
        File source = new File("stalker.db");
        File target = PLUGIN_PATH.resolve("stalker/Skywars.db").toFile();
        if (source.isDirectory() && !target.isDirectory()) {
            File[] db_files = source.listFiles();
            if (db_files == null) {
                getLogger().println("old skywars stalker.db exists but can not be read");
                return;
            }
            try {
                java.nio.file.Files.createDirectories(target.toPath());
                for (File file : db_files) {
                    Files.copy(file, new File(target, file.getName()));
                }
            } catch (IOException e) {
                getLogger().println(e);
            }
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        checkKeyBindings();
        Runnable task = tasks.poll();
        if (task != null) {
            task.run();
        }
    }

    private void checkKeyBindings() {
        if (leaveKey.isPressed()) {
            The5zigAPI.getAPI().sendPlayerMessage("/leave");
        }
        if (snakeKey.isPressed()) {
            snake = !snake;
        }
    }

    @EventHandler
    public void onUnload(UnloadEvent event) {
        instance = null;
        logger.close();
        if (updater != null) {
            updater.stop();
        }
        if (commandRegistry != null) {
            commandRegistry.close();
        }
    }

    public boolean isSnake() {
        return snake;
    }

    public PrintWriter getLogger() {
        return logger;
    }

    public PluginConfig getConfig() {
        return config;
    }

    /**
     * Run a task on Minecrafts' main thread. The task will be run at some
     * point in the future (usually on the next game tick) on the main thread.
     *
     * @param task Task to run
     */
    public void runOnMainThread(Runnable task) {
        tasks.add(task);
    }

}
