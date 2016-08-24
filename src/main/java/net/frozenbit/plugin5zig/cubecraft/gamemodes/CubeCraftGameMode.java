package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.server.GameMode;
import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class CubeCraftGameMode extends GameMode {
    private List<CubeCraftPlayer> players = new ArrayList<>();
    private Map<CubeCraftPlayer, ChestVote> votes = new HashMap<>();
    private String kit;
    private boolean kitsEnabled;
    private ChestVote chestType = ChestVote.NONE;

    public CubeCraftPlayer getPlayerByName(String name) {
        for (CubeCraftPlayer player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }

    public Map<CubeCraftPlayer, ChestVote> getVotes() {
        return votes;
    }

    public ChestVote getVote(CubeCraftPlayer player) {
        ChestVote vote = votes.get(player);
        return vote == null ? ChestVote.NONE : vote;
    }

    public List<CubeCraftPlayer> getPlayers() {
        return players;
    }

    public String getKit() {
        return kit;
    }

    public void setKit(String kit) {
        this.kit = kit;
    }

    public boolean hasKitsEnabled() {
        return kitsEnabled;
    }

    public void setKitsEnabled(boolean kitsEnabled) {
        this.kitsEnabled = kitsEnabled;
    }

    public ChestVote getChestType() {
        return chestType;
    }

    public void setChestType(ChestVote chestType) {
        this.chestType = chestType;
    }
}
