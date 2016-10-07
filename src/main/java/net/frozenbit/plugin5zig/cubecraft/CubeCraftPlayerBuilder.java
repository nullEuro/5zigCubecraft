package net.frozenbit.plugin5zig.cubecraft;

import eu.the5zig.mod.util.NetworkPlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class CubeCraftPlayerBuilder {
    private Rank rank = Rank.NONE;
    private List<String> tags = new ArrayList<>();
    private NetworkPlayerInfo info;

    public CubeCraftPlayerBuilder setRank(Rank rank) {
        this.rank = rank;
        return this;
    }

    public CubeCraftPlayerBuilder setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public CubeCraftPlayerBuilder setInfo(NetworkPlayerInfo info) {
        this.info = info;
        return this;
    }

    public CubeCraftPlayer createCubeCraftPlayer() {
        return new CubeCraftPlayer(rank, tags, info);
    }
}