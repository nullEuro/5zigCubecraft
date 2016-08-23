package net.frozenbit.plugin5zig.cubecraft;


public enum Rank {
    STONE, IRON, LAPIZ, GOLD, DIAMOND, EMERALD, OBSIDIAN;

    public static Rank fromString(String string) {
        for (Rank rank : values()) {
            if (rank.name().equalsIgnoreCase(string)) {
                return rank;
            }
        }
        throw new IllegalArgumentException("rank '" + string + "' not found");
    }
}
