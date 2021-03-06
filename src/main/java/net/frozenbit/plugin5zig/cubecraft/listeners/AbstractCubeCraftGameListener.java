package net.frozenbit.plugin5zig.cubecraft.listeners;

import com.google.common.base.Splitter;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.*;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.CubeCraftGameMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.VotableCubeCraftGameMode;

import java.util.*;
import java.util.regex.Pattern;

import static java.lang.String.format;

/**
 * Abstract base class for games on CubeCraft. Handles player list changes and common game state changes.
 * All listener methods (<code>on*</code>) need to call super.
 *
 * @param <T> GameMode class the listener handles
 */
public abstract class AbstractCubeCraftGameListener<T extends CubeCraftGameMode> extends eu.the5zig.mod.server.AbstractGameListener<T> {
    private static final Pattern RANK_PATTERN = Pattern.compile(":(\\w+):");
    private static final Pattern TAG_PATTERN = Pattern.compile("\\[(\\w+)\\]");
    private static final int PLAYER_LIST_COOLDOWN = 60;
    private int cooldown;
    private boolean requestPlayerList;
    private String prefix;
    private boolean summaryShown;

    protected AbstractCubeCraftGameListener(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onTick(CubeCraftGameMode gameMode) {
        if (cooldown > 0) {
            cooldown--;
            if (cooldown == 0 && requestPlayerList) {
                playerListCommand();
            }
        }
    }

    @Override
    public void onMatch(T gameMode, String key, IPatternResult match) {
        Iterator<String> parts = Splitter.on('.').split(key).iterator();

        String category = parts.next();
        if (!category.equals("generic") && !category.equals(prefix)) {
            return;
        }
        switch (parts.next()) {
            case "join":
                requestPlayerList();
                break;
            case "left":
                gameMode.getPlayers().remove(gameMode.getPlayerByName(match.get(0)));
                gameMode.playerListUpdate();
                if (gameMode.isStalkerEnabled()) {
                    gameMode.getStalker().onPlayerListUpdate(gameMode.getPlayers());
                }
                break;
            case "starting":
                gameMode.setState(GameState.STARTING);
                break;
            case "pregame":
                gameMode.setState(GameState.PREGAME);
                break;
            case "start":
                gameMode.setState(GameState.GAME);
                gameMode.setTime(System.currentTimeMillis());
                break;
            case "countdown":
                gameMode.setTime(System.currentTimeMillis() + 1000 * Integer.parseInt(match.get(0)));
                break;
            case "kit":
                gameMode.setKit(match.get(0));
                break;
            case "kill": {
                String ownName = The5zigAPI.getAPI().getGameProfile().getName();
                if (match.get(1).equals(ownName)) {
                    gameMode.setKills(gameMode.getKills() + 1);
                }
                if (gameMode.isStalkerEnabled()) {
                    gameMode.getStalker().onKill(match.get(0), match.get(1));
                }
                break;
            }
            case "points":
                gameMode.addPointsEarned(Integer.parseInt(match.get(0)));
                break;
            case "playerList":
                updatePlayerList(gameMode, match);
                if (gameMode.isStalkerEnabled()) {
                    gameMode.getStalker().onPlayerListUpdate(gameMode.getPlayers());
                }
                break;
            case "selfWin":
            case "selfDeath": {
                if (!summaryShown) {
                    summaryShown = true;
                    long gameTime = (System.currentTimeMillis() - gameMode.getTime()) / 1000;
                    long minutes = gameTime / 60;
                    long seconds = gameTime % 60;
                    The5zigAPI.getAPI().messagePlayer(
                            format("%sGame ended after %d:%02d! You killed %d players and earned %d points ",
                                    ChatColor.GOLD, minutes, seconds, gameMode.getKills(),
                                    gameMode.getPointsEarned()));
                }
                break;
            }
            case "vote": {
                if (gameMode instanceof VotableCubeCraftGameMode) {
                    ((VotableCubeCraftGameMode) gameMode).onVote(match.get(0), parts.next(), match.get(1));
                }
                break;
            }
            case "draw": {
                if (gameMode instanceof VotableCubeCraftGameMode) {
                    ((VotableCubeCraftGameMode) gameMode).onDrawResult(parts.next(), match.get(0));
                }
                break;
            }
            case "welcome": {
                gameMode.setState(GameState.FINISHED);
                break;
            }
        }
    }

    @Override
    public void onGameModeJoin(T gameMode) {
        summaryShown = false;
        gameMode.getPlayers().clear();
        if (gameMode.isStalkerEnabled()) {
            gameMode.getStalker().onPlayerListUpdate(gameMode.getPlayers());
        }
        Main.getInstance().getLogger().println("gamemode joined!");
        requestPlayerList();
    }

    protected void updatePlayerList(CubeCraftGameMode gameMode, IPatternResult match) {
        Map<String, CubeCraftPlayerBuilder> playerBuilders = new HashMap<>();
        for (NetworkPlayerInfo tabPlayer : The5zigAPI.getAPI().getServerPlayers())
            playerBuilders.put(tabPlayer.getGameProfile().getName(),
                    new CubeCraftPlayerBuilder().setInfo(tabPlayer));
        Collection<CubeCraftPlayerBuilder> cmdPlayerList = parsePlayerList(playerBuilders, match.get(0));
        List<CubeCraftPlayer> playerList = gameMode.getPlayers();
        playerList.clear();
        for (CubeCraftPlayerBuilder playerBuilder : playerBuilders.values()) {
            if (!cmdPlayerList.contains(playerBuilder))
                playerBuilder.setTags(Collections.singletonList("Vanished"));
            playerList.add(playerBuilder.createCubeCraftPlayer());
        }
        gameMode.playerListUpdate();
    }

    /**
     * Parse the output of the /list command
     *
     * @param playerBuilders    A map of player names to player builders to read the parsed info into
     * @param listPlayersString First match group of generic.playerList
     * @return A list of builders of every player encountered in the parsed list.
     */
    private Collection<CubeCraftPlayerBuilder> parsePlayerList(Map<String, CubeCraftPlayerBuilder> playerBuilders, String listPlayersString) {
        Set<CubeCraftPlayerBuilder> playerBuilderList = new HashSet<>();
        String[] listPlayerStrings = listPlayersString.split(", ");
        for (String listPlayer : listPlayerStrings) {
            List<String> tags = new ArrayList<>();
            String[] parts = listPlayer.split(" ");
            String name = parts[parts.length - 1];
            CubeCraftPlayerBuilder playerBuilder = playerBuilders.get(name);
            if (playerBuilder == null) {
                Main.getInstance().getLogger().println(
                        format("Player %s was in the /list but not in the tab list", name));
                continue;
            }
            for (int i = 0; i < parts.length - 1; i++) {
                String part = parts[i];
                String rankString = Util.extractGroup(part, RANK_PATTERN, 1);
                if (rankString != null) {
                    playerBuilder.setRank(Rank.valueOf(rankString.toUpperCase()));
                }
                String tag = Util.extractGroup(part, TAG_PATTERN, 1);
                if (tag != null) {
                    tags.add(tag);
                }
            }
            playerBuilder.setTags(tags);
            playerBuilderList.add(playerBuilder);
        }
        return playerBuilderList;
    }

    private void playerListCommand() {
        requestPlayerList = false;
        cooldown = PLAYER_LIST_COOLDOWN;
        getGameListener().sendAndIgnore("/list", "generic.playerList");
    }

    protected void requestPlayerList() {
        if (cooldown > 0) {
            requestPlayerList = true;
        } else {
            playerListCommand();
        }
    }
}
