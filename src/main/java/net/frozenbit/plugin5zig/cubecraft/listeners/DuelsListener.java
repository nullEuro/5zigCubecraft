package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.DuelsGameMode;


public class DuelsListener extends AbstractCubeCraftGameListener<DuelsGameMode> {
    public static final int OPPONENT_UPDATE_INTERVAL = 10;
    private long timer;

    public DuelsListener() {
        super("duels");
    }

    @Override
    public Class<DuelsGameMode> getGameMode() {
        return DuelsGameMode.class;
    }

    @Override
    public void onMatch(DuelsGameMode gameMode, String key, IPatternResult match) {
        switch (key) {
            case "duels.starting":
                gameMode.setState(GameState.STARTING);
                updateOpponentInfo(gameMode);
                break;
            case "duels.countdown":
                gameMode.setTime(System.currentTimeMillis() + 1000 * Integer.parseInt(match.get(0)));
                break;
            case "duels.start":
                gameMode.setState(GameState.GAME);
                gameMode.setTime(System.currentTimeMillis());
                break;
        }
    }

    @Override
    public void onTick(DuelsGameMode gameMode) {
        if (gameMode.getState() != GameState.LOBBY) {
            timer++;
            if (timer % OPPONENT_UPDATE_INTERVAL == 0) {
                updateOpponentInfo(gameMode);
            }
        }
    }

    private void updateOpponentInfo(DuelsGameMode gameMode) {
        for (NetworkPlayerInfo playerInfo : The5zigAPI.getAPI().getServerPlayers()) {
            // search the player list for an entry that is not yourself
            if (!playerInfo.getGameProfile().equals(The5zigAPI.getAPI().getGameProfile())) {
                gameMode.setOpponentInfo(playerInfo);
                return;
            }
        }
    }

    @Override
    public void onGameModeJoin(DuelsGameMode gameMode) {
        timer = 0;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return "Duels".equals(lobby);
    }
}
