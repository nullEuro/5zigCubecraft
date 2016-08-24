package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import net.frozenbit.plugin5zig.cubecraft.*;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, CubeCraftPlayerBuilder> playerBuilders = new HashMap<>();
        for (NetworkPlayerInfo tabPlayer : The5zigAPI.getAPI().getServerPlayers())
            playerBuilders.put(tabPlayer.getGameProfile().getName(), new CubeCraftPlayerBuilder().setInfo(tabPlayer));
        String listPlayersString = match.get(0);
        String[] listPlayerStrings = listPlayersString.split(", ");
        for (String listPlayer : listPlayerStrings) {
            List<String> tags = new ArrayList<>();
            String[] parts = listPlayer.split(" ");
            String name = parts[parts.length - 1];
            CubeCraftPlayerBuilder playerBuilder = playerBuilders.get(name);
            if (playerBuilder == null) {
                Main.getInstance().getLogger().println(String.format("Player %s was in the /list but not in the tab list", name));
                continue;
            }
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                String rankString = Util.extractGroup(part, RANK_PATTERN, 1);
                if (rankString != null) {
                    playerBuilder.setRank(Rank.fromString(rankString));
                }
                String tag = Util.extractGroup(part, TAG_PATTERN, 1);
                if (tag != null) {
                    tags.add(tag);
                }
            }
            playerBuilder.setTags(tags);
        }
        List<CubeCraftPlayer> playerList = gameMode.getPlayers();
        playerList.clear();
        for (CubeCraftPlayerBuilder playerBuilder : playerBuilders.values()) {
            playerList.add(playerBuilder.createCubeCraftPlayer());
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
