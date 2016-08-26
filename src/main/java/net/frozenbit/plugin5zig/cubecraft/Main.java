package net.frozenbit.plugin5zig.cubecraft;

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
import org.lwjgl.input.Keyboard;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


@Plugin(name = "5zigCubecraft", version = Build.version)
public class Main {
    private static Main instance;
    private PrintWriter logger;
    private IKeybinding leaveKey, snakeKey;
    private boolean snake;

    private Stalker stalker;

    public static Main getInstance() {
        return instance;
    }

    // ideas: pre gamechat and game summary, friend join notification, no kit warning

    @EventHandler
    public void onLoad(LoadEvent loadEvent) throws IOException {
        logger = new PrintWriter(new FileWriter("cubecraft_5ziglog.txt"), true);

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

        instance = this;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        checkKeyBindings();
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

}
