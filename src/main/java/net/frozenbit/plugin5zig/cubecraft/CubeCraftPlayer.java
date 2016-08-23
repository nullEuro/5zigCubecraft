package net.frozenbit.plugin5zig.cubecraft;


import java.util.Arrays;
import java.util.List;

public class CubeCraftPlayer {

    public final String name;
    private final Rank rank;
    private List<String> tags;

    public CubeCraftPlayer(Rank rank, List<String> tags, String name) {
        this.rank = rank;
        this.tags = tags;
        this.name = name;
    }

    public boolean canVote() {
        return rank != null && Rank.DIAMOND.compareTo(rank) <= 0
               || tags.contains("Mod")
               || tags.contains("SrMod")
               || tags.contains("Dev")
               || tags.contains("Admin");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubeCraftPlayer that = (CubeCraftPlayer) o;

        return name.equals(that.name);

    }

    @Override
    public String toString() {
        return "CubeCraftPlayer{" +
               "rank=" + rank +
               ", tags=" + Arrays.toString(tags.toArray()) +
               ", name='" + name + '\'' +
               '}';
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
