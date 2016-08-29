package net.frozenbit.plugin5zig.cubecraft.stalker;

import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import org.json.JSONObject;

import java.util.UUID;

public class StalkedPlayer {
    private int kills, deaths;
    private CubeCraftPlayer cubeCraftPlayer;

    public StalkedPlayer(CubeCraftPlayer cubeCraftPlayer, int kills, int deaths) {
        this.cubeCraftPlayer = cubeCraftPlayer;
        this.kills = kills;
        this.deaths = deaths;
    }

    public StalkedPlayer(CubeCraftPlayer cubeCraftPlayer, JSONObject playerData) {
        this.cubeCraftPlayer = cubeCraftPlayer;
        kills = playerData.getInt("kills");
        deaths = playerData.getInt("deaths");
    }

    public String getName() {
        return cubeCraftPlayer.getName();
    }

    public UUID getId() {
        return cubeCraftPlayer.getId();
    }

    public int getKills() {
        return kills;
    }

    public void onKill() {
        ++kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void onDeath() {
        ++deaths;
    }

    public int getThreat() {
        return deaths - kills;
    }

    public ChatColor getThreatColor() {
        if (getThreat() <= 0)
            return ChatColor.WHITE;
        else if (getThreat() <= 3)
            return ChatColor.YELLOW;
        else
            return ChatColor.RED;
    }

    public JSONObject toJSON() {
        JSONObject playerData = new JSONObject();
        playerData.put("kills", kills);
        playerData.put("deaths", deaths);
        return playerData;
    }
}