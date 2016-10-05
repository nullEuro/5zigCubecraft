package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.util.NetworkPlayerInfo;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

public class DuelsGameMode extends CubeCraftGameMode {
    private static Stalker stalker;

    private NetworkPlayerInfo opponentInfo;

    @Override
    public String getName() {
        return "Duels";
    }

    public NetworkPlayerInfo getOpponentInfo() {
        return opponentInfo;
    }

    public void setOpponentInfo(NetworkPlayerInfo opponentInfo) {
        this.opponentInfo = opponentInfo;
    }

    @Override
    public Stalker getStalker() {
        if (stalker == null)
            stalker = new Stalker(this);
        return stalker;
    }
}
