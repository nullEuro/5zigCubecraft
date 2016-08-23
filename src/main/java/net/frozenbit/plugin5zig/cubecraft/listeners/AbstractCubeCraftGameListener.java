package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.server.IPatternResult;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Rank;
import net.frozenbit.plugin5zig.cubecraft.Util;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public abstract class AbstractCubeCraftGameListener extends eu.the5zig.mod.server.AbstractGameListener<SkywarsMode> {
    private static final Pattern RANK_PATTERN = Pattern.compile(":(\\w+):");
    private static final Pattern TAG_PATTERN = Pattern.compile("\\[(\\w+)\\]");
    private static final int PLAYER_LIST_COOLDOWN = 60;
    private int cooldown;
    private boolean requestPlayerList;

    @Override
    public void onTick(SkywarsMode gameMode) {
        if (cooldown > 0) {
            cooldown--;
            if (cooldown == 0 && requestPlayerList) {
                playerListCommand();
            }
        }
    }

    void updatePlayerList(SkywarsMode gameMode, IPatternResult match) {
        List<CubeCraftPlayer> players = gameMode.getPlayers();
        players.clear();
        String playerListString = match.get(0);
        String[] playersStrings = playerListString.split(", ");
        for (String player : playersStrings) {
            List<String> tags = new ArrayList<>();
            Rank rank = null;
            String[] parts = player.split(" ");
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                String rankString = Util.extractGroup(part, RANK_PATTERN, 1);
                if (rankString != null) {
                    rank = Rank.fromString(rankString);
                }
                String tag = Util.extractGroup(part, TAG_PATTERN, 1);
                if (tag != null) {
                    tags.add(tag);
                }
            }
            String name = parts[parts.length - 1];
            CubeCraftPlayer cubeCraftPlayer = new CubeCraftPlayer(rank, tags, name);
            players.add(cubeCraftPlayer);
        }
    }

    private void playerListCommand() {
        requestPlayerList = false;
        cooldown = PLAYER_LIST_COOLDOWN;
        getGameListener().sendAndIgnore("/list", "playerList");
    }

    protected void requestPlayerList() {
        if (cooldown > 0) {
            requestPlayerList = true;
        } else {
            playerListCommand();
        }
    }
}
