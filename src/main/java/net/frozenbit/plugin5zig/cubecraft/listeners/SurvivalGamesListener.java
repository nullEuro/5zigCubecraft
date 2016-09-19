package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SurvivalGamesMode;


public class SurvivalGamesListener extends AbstractCubeCraftGameListener<SurvivalGamesMode> {
    public SurvivalGamesListener() {
        super("sg");
    }

    @Override
    public Class<SurvivalGamesMode> getGameMode() {
        return SurvivalGamesMode.class;
    }

    @Override
    public void onMatch(SurvivalGamesMode gameMode, String key, IPatternResult match) {
        super.onMatch(gameMode, key, match);
        switch (key) {
            case "sg.chestType": {
                gameMode.setState(GameState.STARTING);
                gameMode.setTime(System.currentTimeMillis() + 1000 * 20);
                break;
            }
            case "sg.pregame": {
                int time = Integer.parseInt(match.get(0));
                gameMode.setTime(System.currentTimeMillis() + 1000 * time);
                break;
            }
        }
    }

    @Override
    public void onGameModeJoin(SurvivalGamesMode gameMode) {
        gameMode.setKitsEnabled(true);
    }

    @Override
    public boolean matchLobby(String lobby) {
        return lobby.contains("Survival Games");
    }
}
