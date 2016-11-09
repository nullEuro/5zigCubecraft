package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.server.GameMode;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


public abstract class CubeCraftGameMode extends GameMode {
    protected List<CubeCraftPlayer> players = new CopyOnWriteArrayList<>();
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

    /**
     * @return A list of players currently in this gamemode. The list can be modified, however
     * {@link #playerListUpdate()} MUST be called afterwards.
     */
    public List<CubeCraftPlayer> getPlayers() {
        return players;
    }

    public void playerListUpdate() {
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

    public boolean isStalkerEnabled() {
        return true;
    }

    /**
     * Return a singleton instance for the stalker of this gamemode. Before calling this method
     * always check {@link #isStalkerEnabled()}.
     * <p>
     * Implementing classes that do not need a stalker must overwrite {@link #isStalkerEnabled()}
     * and should throw an exception here.
     *
     * @return A stalker singleton.
     */
    public abstract Stalker getStalker();
}
