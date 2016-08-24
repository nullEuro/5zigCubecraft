package net.frozenbit.plugin5zig.cubecraft;

import eu.the5zig.mod.util.NetworkPlayerInfo;

import java.util.List;

public class CubeCraftPlayerBuilder {
    private Rank rank;
    private List<String> tags;
    private NetworkPlayerInfo info;

    public CubeCraftPlayerBuilder setRank(Rank rank) {
        this.rank = rank;
        return this;
    }

    public CubeCraftPlayerBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public boolean isTagsSet() {
        return !(tags == null);
    }

    public CubeCraftPlayerBuilder setInfo(NetworkPlayerInfo info) {
        this.info = info;
        return this;
    }

    public CubeCraftPlayer createCubeCraftPlayer() {
        return new CubeCraftPlayer(rank, tags, info);
    }
}