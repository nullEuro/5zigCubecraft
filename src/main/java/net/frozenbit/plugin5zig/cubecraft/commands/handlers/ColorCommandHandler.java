package net.frozenbit.plugin5zig.cubecraft.commands.handlers;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandHandler;
import net.frozenbit.plugin5zig.cubecraft.commands.CommandOutputPrinter;
import net.frozenbit.plugin5zig.cubecraft.commands.UsageException;

import java.util.Iterator;
import java.util.List;

/**
 * Handler for .color
 * Shows help for minecrafts color codes
 */
public class ColorCommandHandler extends CommandHandler {
    public static final Iterable<ChatColor> RAINBOW_IT =
            Iterables.cycle(ChatColor.DARK_RED, ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
                    ChatColor.GREEN, ChatColor.DARK_GREEN, ChatColor.DARK_AQUA, ChatColor.AQUA,
                    ChatColor.BLUE, ChatColor.LIGHT_PURPLE, ChatColor.DARK_PURPLE);

    public ColorCommandHandler() {
        super("color", "Show all color codes", "c");
    }

    @Override
    public void run(String cmd, List<String> args, CommandOutputPrinter printer) throws UsageException {
        if (args.isEmpty()) {
            StringBuilder lineBuf = new StringBuilder();
            for (ChatColor chatColor : ChatColor.values()) {
                lineBuf.append(chatColor)
                        .append(chatColor.getCode())
                        .append(ChatColor.RESET)
                        .append(chatColor == ChatColor.MAGIC ? "(k)" : "")
                        .append(" ");
            }
            printer.println(lineBuf.toString());
        } else {
            String text = Joiner.on(' ').join(args);
            StringBuilder fancy = new StringBuilder();
            Iterator<ChatColor> rainbowColors = RAINBOW_IT.iterator();
            for (char c : text.toCharArray()) {
                if (c != ' ') {
                    fancy.append("&")
                            .append(rainbowColors.next().getCode());
                }
                fancy.append(c);
            }
            The5zigAPI.getAPI().sendPlayerMessage(fancy.toString());
        }
    }

    @Override
    public void printUsage(String cmd, CommandOutputPrinter printer) {
        printer.println(".color - show a cheatsheet of the color codes")
                .println(".color <text> - makes the given text colorful and posts it")
                .println(ChatColor.ITALIC + "Note that only ranked players can use colors on CubeCraft")
                .println("Alias: .c");
    }
}
