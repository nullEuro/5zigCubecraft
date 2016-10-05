package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;

import java.util.HashMap;
import java.util.Map;


public abstract class VotableCubeCraftGameMode extends CubeCraftGameMode {
    private Map<CubeCraftPlayer, ChestVote> votes = new HashMap<>();
    private ChestVote chestType = ChestVote.NONE;
    private long chestTypeTime;

    public Map<CubeCraftPlayer, ChestVote> getVotes() {
        return votes;
    }

    public ChestVote getVote(CubeCraftPlayer player) {
        ChestVote vote = votes.get(player);
        return vote == null ? ChestVote.NONE : vote;
    }

    //public ChestVote typeFromString(String name);

    public ChestVote getChestType() {
        return chestType;
    }

    public void setChestType(ChestVote chestType) {
        this.chestType = chestType;
        chestTypeTime = System.currentTimeMillis();
    }

    public long getChestTypeTime() {
        return chestTypeTime;
    }

}
