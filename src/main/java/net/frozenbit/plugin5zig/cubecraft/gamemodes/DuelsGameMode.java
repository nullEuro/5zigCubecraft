package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.util.NetworkPlayerInfo;


public class DuelsGameMode extends GameMode {
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
}
