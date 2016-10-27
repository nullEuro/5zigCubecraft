package net.frozenbit.plugin5zig.cubecraft.commands;


import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.event.ChatSendEvent;
import eu.the5zig.mod.event.EventHandler;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.Build;

import java.util.*;

public class CommandRegistry {
    private static final Splitter ARG_SPLITTER =
            Splitter.on(CharMatcher.BREAKING_WHITESPACE)
                    .omitEmptyStrings();
    private static final CommandOutputPrinter CHAT_PRINTER = new CommandOutputChatPrinter();
    private static final Comparator<CommandHandler> CMD_NAME_COMPARATOR = new Comparator<CommandHandler>() {
        @Override
        public int compare(CommandHandler a, CommandHandler b) {
            return a.getName().compareTo(b.getName());
        }
    };

    private final List<CommandHandler> handlers;
    private final Map<String, CommandHandler> commandMap;

    public CommandRegistry() {
        commandMap = new HashMap<>();
        handlers = new ArrayList<>();
        register(new HelpCommandHandler());
    }

    /**
     * Register a new command
     *
     * @param handler Command to register
     * @return this for chaining
     */
    public CommandRegistry register(CommandHandler handler) {
        if (handler == null) {
            throw new IllegalArgumentException("command must not be null");
        }
        if (commandMap.containsKey(handler.getName())) {
            throw new IllegalArgumentException("Duplicate command " + handler.getName());
        }
        for (String alias : handler.getAliases()) {
            if (commandMap.containsKey(alias)) {
                throw new IllegalArgumentException("Duplicate alias " + alias);
            }
        }
        handler.onRegister();
        commandMap.put(handler.getName(), handler);
        for (String alias : handler.getAliases()) {
            commandMap.put(alias, handler);
        }
        handlers.add(handler);
        Collections.sort(handlers, CMD_NAME_COMPARATOR);
        return this;
    }

    /**
     * Clears all registered commands and calls their cleanup lifecycle methods. Should be called when the
     * Plugin is unloaded.
     */
    public void close() {
        for (CommandHandler handler : handlers) {
            handler.onUnregister();
        }
        handlers.clear();
        commandMap.clear();
    }

    @EventHandler
    public void onChatSend(ChatSendEvent event) {
        String message = event.getMessage();
        if (message.startsWith(".") && !message.startsWith("..")) {
            event.setCancelled(true);
            The5zigAPI.getAPI().messagePlayer(ChatColor.GOLD + "# " + ChatColor.RESET + message);
            List<String> split = ARG_SPLITTER.splitToList(message);
            String commandName = split.get(0).substring(1);
            List<String> args = split.subList(1, split.size());
            if (commandMap.containsKey(commandName)) {
                CommandHandler handler = commandMap.get(commandName);
                try {
                    handler.run(commandName, args, CHAT_PRINTER);
                } catch (UsageException e) {
                    CHAT_PRINTER.printErrln(e.getMessage());
                }
            } else {
                CHAT_PRINTER.printErrln("Command not found");
            }
        }
    }

    private class HelpCommandHandler extends CommandHandler {
        public HelpCommandHandler() {
            super("help", "Lists available commands");
        }

        @Override
        public void run(String cmd, List<String> args, CommandOutputPrinter printer) throws UsageException {
            if (args.isEmpty()) {
                printer.println(ChatColor.GREEN + "CubeCraft plugin version " + Build.version);
                printer.println("");
                for (CommandHandler handler : handlers) {
                    showBriefHelp(printer, handler);
                }
                printer.println("");
                printUsage(cmd, printer);
            } else {
                String commandStr = args.get(0).replaceFirst("^\\.", "");
                if (!commandMap.containsKey(commandStr)) {
                    printer.printErrln("Command does not exist");
                    return;
                }
                CommandHandler handler = commandMap.get(commandStr);
                printer.println("%s. Usage:", handler.getDescription());
                handler.printUsage(commandStr, printer);
            }
        }

        private void showBriefHelp(CommandOutputPrinter printer, CommandHandler handler) {
            printer.println("%s.%s%s - %s", ChatColor.YELLOW, handler.getName(), ChatColor.RESET,
                    handler.getDescription());
        }

        @Override
        public void printUsage(String cmd, CommandOutputPrinter printer) {
            printer.println(".help <command> - Display help of a specific command");
        }
    }
}
