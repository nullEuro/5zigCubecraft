package net.frozenbit.plugin5zig.cubecraft.commands;


public class UsageException extends Exception {
    /**
     * @param message Message that will be shown to the user
     */
    public UsageException(String message) {
        super(message);
    }
}
