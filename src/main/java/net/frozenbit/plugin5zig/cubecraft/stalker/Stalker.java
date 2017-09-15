package net.frozenbit.plugin5zig.cubecraft.stalker;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.GameMode;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Stalker {

    private Storage storage;
    private String ownName;
    private GameMode gameMode;
    private final List<StalkedPlayer> stalkedPlayerList;
    private final List<StalkedPlayer> provisionalStalkedPlayerList;
    private int maxNameWidth = 0;
    private int shownPlayerCount = 0;
    private int playerListIteration = 0;

    public Stalker(GameMode gameMode) {
        this.gameMode = gameMode;
        storage = new Storage(gameMode);
        ownName = The5zigAPI.getAPI().getGameProfile().getName();
        stalkedPlayerList = new ArrayList<>();
        provisionalStalkedPlayerList = new ArrayList<>();
    }

    public void onKill(String victim, String killer) {
        boolean kill = killer.equals(ownName);
        boolean death = victim.equals(ownName);
        if (!kill && !death)
            return;
        String otherName = kill ? victim : killer;
        StalkedPlayer otherPlayer = null;
        synchronized (stalkedPlayerList) {
            for (StalkedPlayer player : stalkedPlayerList) {
                if (player.getName().equals(otherName)) {
                    otherPlayer = player;
                    break;
                }
            }
        }
        if (otherPlayer == null) {
            Main.getInstance().getLogger().println(String.format("Player %s is not in the player list", otherName));
            return;
        }
        if (otherPlayer.getKills() == 0 && otherPlayer.getDeaths() == 0)
            ++shownPlayerCount;
        if (kill)
            otherPlayer.onKill();
        else
            otherPlayer.onDeath();
        final StalkedPlayer storedPlayer = otherPlayer;
        new Thread(() -> storage.storePlayer(storedPlayer)).start();
    }

    public void onPlayerListUpdate(final List<CubeCraftPlayer> playerList) {
        maxNameWidth = 0;
        final int frozenPlayerListIteration = ++playerListIteration;
        shownPlayerCount = 0;
        new Thread(() -> {
            provisionalStalkedPlayerList.clear();
            synchronized (provisionalStalkedPlayerList) {
                for (CubeCraftPlayer cubeCraftPlayer : playerList) {
                    if (playerListIteration != frozenPlayerListIteration)
                        return;
                    maxNameWidth = Math.max(maxNameWidth, The5zigAPI.getAPI().getRenderHelper().getStringWidth(cubeCraftPlayer.getName()));
                    StalkedPlayer stalkedPlayer = storage.getStalkedPlayer(cubeCraftPlayer);
                    if (stalkedPlayer.getKills() != 0 || stalkedPlayer.getDeaths() != 0)
                        ++shownPlayerCount;
                    provisionalStalkedPlayerList.add(stalkedPlayer);
                }
                synchronized (stalkedPlayerList) {
                    stalkedPlayerList.addAll(provisionalStalkedPlayerList);
                }
            }
        }).start();
    }

    public List<StalkedPlayer> getStalkedPlayerList() {
        return stalkedPlayerList;
    }

    public StalkedPlayer getStalkedPlayerById(UUID id) {
        synchronized (stalkedPlayerList) {
            for (StalkedPlayer stalkedPlayer : stalkedPlayerList) {
                if (stalkedPlayer.getId().equals(id)) {
                    return stalkedPlayer;
                }
            }
        }
        return null;
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
