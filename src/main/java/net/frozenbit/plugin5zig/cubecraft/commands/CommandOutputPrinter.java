package net.frozenbit.plugin5zig.cubecraft.commands;

/**
 * Accepts strings and writes them somewhere
 */
public interface CommandOutputPrinter {
    /**
     * Print an output message line
     *
     * @param msg        Message
     * @param formatArgs optional format args. Used to {@link String#format(String, Object...) format} the message
     * @return This instance for easy chaining
     */
    CommandOutputPrinter println(String msg, Object... formatArgs);

    /**
     * Print an error message line
     *
     * @param msg        Message
     * @param formatArgs optional format args. Used to {@link String#format(String, Object...) format} the message
     * @return This instance for easy chaining
     */
    CommandOutputPrinter printErrln(String msg, Object... formatArgs);
}
