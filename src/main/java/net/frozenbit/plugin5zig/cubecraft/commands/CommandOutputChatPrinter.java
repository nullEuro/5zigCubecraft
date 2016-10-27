package net.frozenbit.plugin5zig.cubecraft.commands;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.util.minecraft.ChatColor;


/**
 * Implementation of {@link CommandOutputPrinter} that shows messages in
 * chat to the current user.
 */
public class CommandOutputChatPrinter implements CommandOutputPrinter {
    private static String optionalFormat(String msg, Object[] formatArgs) {
        return " " + (formatArgs.length == 0 ? msg : String.format(msg, formatArgs));
    }

    @Override
    public CommandOutputPrinter println(String msg, Object... formatArgs) {
        The5zigAPI.getAPI().messagePlayer(optionalFormat(msg, formatArgs));
        return this;
    }

    @Override
    public CommandOutputPrinter printErrln(String msg, Object... formatArgs) {
        The5zigAPI.getAPI().messagePlayer(ChatColor.RED + optionalFormat(msg, formatArgs));
        return this;
    }
}
