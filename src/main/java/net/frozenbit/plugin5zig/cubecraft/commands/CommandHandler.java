package net.frozenbit.plugin5zig.cubecraft.commands;


import java.util.List;

/**
 * Represents a command and the corresponding action
 */
public abstract class CommandHandler {
    private final String name, description;
    private final String[] aliases;

    /**
     * Construct a new command. Must be registered with {@link CommandRegistry#register(CommandHandler)}
     *
     * @param name        Name of the command. When the user types .name in the chat this command will be invoked
     * @param description Short description of what the command does.
     * @param aliases     Aliases this command should also react to
     */
    public CommandHandler(String name, String description, String... aliases) {
        this.name = name;
        this.description = description;
        this.aliases = aliases;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getAliases() {
        return aliases;
    }

    /**
     * Lice cycle method that gets called before the command is registered and ready to be invoked
     */
    public void onRegister() {
    }

    /**
     * Life cycle method that gets called after unregistering a command handler. Can be used to
     * clean up resources.
     */
    public void onUnregister() {
    }

    /**
     * Invoke this command and print the result to the user
     *
     * @param cmd     Alias or name that was used by the user
     * @param args    Arguments to this command, split on whitespaces. Does no do any complex parsing, like quotes.
     * @param printer Printer to write the output of this command to
     * @throws UsageException When the user gave invalid arguments. The message of the throwable will be printed to the user
     */
    public abstract void run(String cmd, List<String> args, CommandOutputPrinter printer) throws UsageException;

    /**
     * Show the usage of this command
     *
     * @param cmd     Name or alias to show usage help for. If appropriate, the command can display a help for all aliases anyways.
     * @param printer Printer to write the help text to
     */
    public abstract void printUsage(String cmd, CommandOutputPrinter printer);
}
