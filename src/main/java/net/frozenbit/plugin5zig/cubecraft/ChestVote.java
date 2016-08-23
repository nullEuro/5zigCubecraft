package net.frozenbit.plugin5zig.cubecraft;


import eu.the5zig.util.minecraft.ChatColor;

public enum ChestVote {
    NONE("", ChatColor.GRAY),
    BASIC("Basic", ChatColor.GREEN),
    NORMAL("Normal", ChatColor.YELLOW),
    OVERPOWERED("Overpowered", ChatColor.DARK_RED);

    public final String serverName;
    public final ChatColor color;

    ChestVote(String serverName, ChatColor color) {
        this.serverName = serverName;
        this.color = color;
    }

    public static ChestVote fromString(String string) {
        for (ChestVote type : values()) {
            if (type.serverName.equalsIgnoreCase(string)) {
                return type;
            }
        }
        throw new IllegalArgumentException("type '" + string + "' not found");
    }
}
