package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;


public class GameListener extends AbstractGameListener<GameMode> {
    private long countdown;

    @Override
    public Class<GameMode> getGameMode() {
        return null;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return false;
    }

    @Override
    public void onMatch(GameMode gameMode, String key, IPatternResult match) {
        if (key.equals("generic.welcome")) {
            getGameListener().switchLobby(null);
            return;
        }
        if (gameMode != null && gameMode.getState() != GameState.FINISHED) {
            return;
        }
        if (key.equals("generic.join")) {
            countdown = 10;
        } else if (key.startsWith("assassination.")) {
            getGameListener().switchLobby("Assassination");
        } else if (key.equals("duels.connect")) {
            getGameListener().switchLobby("Duels");
        }
    }

    @Override
    public void onTick(GameMode gameMode) {
        if (countdown > 0) {
            countdown--;
            if (countdown == 0) {
                getGameListener().switchLobby(The5zigAPI.getAPI().getSideScoreboard().getTitle());
            }
        }
    }
}
