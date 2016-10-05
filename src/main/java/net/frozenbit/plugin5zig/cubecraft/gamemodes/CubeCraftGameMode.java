package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.server.GameMode;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;

import java.util.ArrayList;
import java.util.List;


public abstract class CubeCraftGameMode extends GameMode {
    private List<CubeCraftPlayer> players = new ArrayList<>();
    private String kit;
    private boolean kitsEnabled;
    private int pointsEarned;

    public CubeCraftPlayer getPlayerByName(String name) {
        for (CubeCraftPlayer player : players) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
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

    public int getPointsEarned() {
        return pointsEarned;
    }

    public void addPointsEarned(int pointsEarned) {
        this.pointsEarned += pointsEarned;
    }
}
