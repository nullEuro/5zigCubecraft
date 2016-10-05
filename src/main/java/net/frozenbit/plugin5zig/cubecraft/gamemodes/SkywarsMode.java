package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

public class SkywarsMode extends VotableCubeCraftGameMode {
    private static Stalker stalker;

    public SkywarsMode() {
        super();
    }

    @Override
    public String getName() {
        return "Skywars";
    }

    @Override
    public Stalker getStalker() {
        if (stalker == null)
            stalker = new Stalker(this);
        return stalker;
    }
}
