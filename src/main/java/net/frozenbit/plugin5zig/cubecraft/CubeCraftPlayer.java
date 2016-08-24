package net.frozenbit.plugin5zig.cubecraft;


import eu.the5zig.mod.util.NetworkPlayerInfo;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CubeCraftPlayer {

    private final Rank rank;
    private List<String> tags;
    private NetworkPlayerInfo info;

    public CubeCraftPlayer(Rank rank, List<String> tags, NetworkPlayerInfo info) {
        if (tags == null || info == null)
            throw new IllegalArgumentException("CubecraftPlayer doesn't want null :(");
        this.rank = rank;
        this.tags = tags;
        this.info = info;
    }

    public boolean canVote() {
        return rank != null && Rank.DIAMOND.compareTo(rank) <= 0
                || tags.contains("Mod")
                || tags.contains("SrMod")
                || tags.contains("Dev")
                || tags.contains("Admin")
                || tags.contains("Vanished");
    }

    public String getName() {
        return info.getGameProfile().getName();
    }

    public UUID getId() {
        return info.getGameProfile().getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CubeCraftPlayer that = (CubeCraftPlayer) o;

        return getName().equals(that.getName());

    }

    @Override
    public String toString() {
        return "CubeCraftPlayer{" +
                "rank=" + rank +
                ", tags=" + Arrays.toString(tags.toArray()) +
                ", name='" + getName() + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
