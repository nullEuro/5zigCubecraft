package net.frozenbit.plugin5zig.cubecraft;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.mod.event.TickEvent;
import eu.the5zig.mod.render.RenderHelper;
import eu.the5zig.mod.util.IKeybinding;
import eu.the5zig.util.minecraft.ChatColor;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;


public class QuickChat {
    private static final int[] DEFAULT_KEYS = {Keyboard.KEY_NUMPAD1, Keyboard.KEY_NUMPAD2, Keyboard.KEY_NUMPAD3,
            Keyboard.KEY_NUMPAD4, Keyboard.KEY_NUMPAD5, Keyboard.KEY_NUMPAD6, Keyboard.KEY_NUMPAD7,
            Keyboard.KEY_NUMPAD8, Keyboard.KEY_NUMPAD9, Keyboard.KEY_NUMPAD0,};

    private List<QuickMessage> messages = new ArrayList<>();

    public QuickChat(PluginConfig config) {
        List<String> quickMessages = config.getQuickMessages();
        for (int i = 0; i < quickMessages.size() && i < DEFAULT_KEYS.length; i++) {
            String message = ChatColor.translateAlternateColorCodes('&', quickMessages.get(i));
            int endIndex = trimColoredString(message, 100);
            The5zigAPI.getLogger().info("endIndex: " + endIndex);
            if (endIndex + 3 < message.length()) {
                message = message.substring(0, endIndex) + "…";
            }
            IKeybinding keyBinding = The5zigAPI.getAPI()
                    .registerKeyBiding("Msg " + (i + 1) + ": " + message, DEFAULT_KEYS[i], "QuickChat");
            messages.add(new QuickMessage(quickMessages.get(i), keyBinding));
        }
    }

    private static int trimColoredString(String message, int width) {
        int i = 0;
        RenderHelper renderHelper = The5zigAPI.getAPI().getRenderHelper();
        while (i < message.length() && renderHelper.getStringWidth(message.substring(0, i)) < width) {
            if (message.charAt(i) == ChatColor.COLOR_CHAR && i + 1 < message.length()
                && ChatColor.ALL_CODES.indexOf(message.charAt(i + 1)) != -1) {
                i++;
            }
            i++;
        }
        return i;
    }

    @EventHandler
    public void onTick(TickEvent event) {
        for (QuickMessage message : messages) {
            if (message.keybinding.isPressed()) {
                The5zigAPI.getAPI().sendPlayerMessage(Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) ? "!" + message.message : message.message);
            }
        }
    }

    private class QuickMessage {
        public final String message;
        public final IKeybinding keybinding;

        public QuickMessage(String message, IKeybinding keybinding) {
            this.message = message;
            this.keybinding = keybinding;
        }
    }
}
