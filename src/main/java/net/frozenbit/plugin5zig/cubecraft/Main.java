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
import net.frozenbit.plugin5zig.cubecraft.items.*;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;
import net.frozenbit.plugin5zig.cubecraft.updater.Updater;
import org.lwjgl.input.Keyboard;

import java.io.*;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;


@Plugin(name = "5zigCubecraft", version = Build.version)
public class Main {
    private static final String LOG_FILE = "cubecraft_5ziglog.txt";
    private static final String CONFIG_FILE = "./the5zigmod/plugins/5zigCubecraft/config.json";
    private static Main instance;

    private IKeybinding leaveKey, snakeKey;
    private boolean snake;
    private Queue<Runnable> tasks = new ConcurrentLinkedQueue<>();

    private PrintWriter logger;
    private Stalker stalker;
    private Updater updater;
    private PluginConfig config;

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
            File configFile = new File(CONFIG_FILE);
            if (!configFile.exists()) {
                Files.createParentDirs(configFile);
                try (FileWriter writer = new FileWriter(configFile)) {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();
                    PluginConfig config = new PluginConfig();
                    gson.toJson(config, writer);
                    return config;
                }
            }
            return new Gson().fromJson(new FileReader(configFile), PluginConfig.class);
        } catch (IOException e) {
            The5zigAPI.getLogger().warn("Cannot create or read config file, assuming defaults", e);
            return new PluginConfig();
        }
    }

    @EventHandler
    public void onLoad(LoadEvent loadEvent) {
        logger = createLogger();
        config = readPluginConfig();

        The5zigAPI.getAPI().registerServerInstance(this, ServerInstance.class);

        The5zigAPI.getAPI().registerModuleItem(this, "snake", SnakeItem.class, Category.OTHER);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftvoters", VoterCountItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftkit", KitItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftchest", ChestTypeItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftstalker", StalkerItem.class, Category.SERVER_GENERAL);
        The5zigAPI.getAPI().registerModuleItem(this, "cubecraftassassinationmoney", MoneyItem.class, Category.SERVER_GENERAL);

        leaveKey = The5zigAPI.getAPI().registerKeyBiding("Leave the current game", Keyboard.KEY_L, "Cubecraft");
        snakeKey = The5zigAPI.getAPI().registerKeyBiding("Toggle Snake", Keyboard.KEY_P, "Misc");

        stalker = new Stalker();

        if (config.hasAutoUpdatesEnabled()) {
            updater = new Updater(this);
            updater.start();
        }

        instance = this;
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
        stalker.close();
        logger.close();
        if (updater != null) {
            updater.stop();
        }
    }

    public boolean isSnake() {
        return snake;
    }

    public Stalker getStalker() {
        return stalker;
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
