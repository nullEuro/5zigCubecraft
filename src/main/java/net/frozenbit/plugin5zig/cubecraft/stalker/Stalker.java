package net.frozenbit.plugin5zig.cubecraft.stalker;

import eu.the5zig.mod.The5zigAPI;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;

import java.util.ArrayList;
import java.util.List;

public class Stalker {

    private Storage storage;
    private String ownName;
    private List<StalkedPlayer> stalkedPlayerList;
    private int maxNameWidth = 0;
    private int shownPlayerCount = 0;

    public Stalker() {
        storage = new Storage();
        ownName = The5zigAPI.getAPI().getGameProfile().getName();
        stalkedPlayerList = new ArrayList<>();
    }

    public void onKill(String victim, String killer) {
        boolean kill = killer.equals(ownName);
        boolean death = victim.equals(ownName);
        if (!kill && !death)
            return;
        String otherName;
        otherName = kill ? victim : killer;
        StalkedPlayer otherPlayer = null;
        for (StalkedPlayer player : stalkedPlayerList) {
            if (player.getName().equals(otherName)) {
                otherPlayer = player;
                break;
            }
        }
        if (otherPlayer == null) {
            Main.getInstance().getLogger().println(String.format("Player %s is not in the player list", otherName));
            return;
        }
        if (kill)
            otherPlayer.onKill();
        else
            otherPlayer.onDeath();
        storage.storePlayer(otherPlayer);
    }

    public void onPlayerListUpdate(List<CubeCraftPlayer> playerList) {
        maxNameWidth = 0;
        stalkedPlayerList.clear();
        shownPlayerCount = 0;
        for (CubeCraftPlayer cubeCraftPlayer : playerList) {
            maxNameWidth = Math.max(maxNameWidth, The5zigAPI.getAPI().getRenderHelper().getStringWidth(cubeCraftPlayer.getName()));
            StalkedPlayer stalkedPlayer = storage.getStalkedPlayer(cubeCraftPlayer);
            if (stalkedPlayer.getKills() != 0 || stalkedPlayer.getDeaths() != 0)
                ++shownPlayerCount;
            stalkedPlayerList.add(stalkedPlayer);
        }
    }

    public List<StalkedPlayer> getStalkedPlayerList() {
        return stalkedPlayerList;
    }

    public int getMaxNameWidth() {
        return maxNameWidth;
    }

    public int getShownPlayerCount() {
        return shownPlayerCount;
    }

    public void close() {
        storage.close();
    }

}
