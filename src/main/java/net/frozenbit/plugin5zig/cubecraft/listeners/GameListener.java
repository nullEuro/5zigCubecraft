package net.frozenbit.plugin5zig.cubecraft.listeners;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.AbstractGameListener;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.commands.handlers.RespondCommandHandler;


public class GameListener extends AbstractGameListener<GameMode> {
    private long countdown;
    private RespondCommandHandler respondCommandHandler;

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
            countdown = 20;
        } else if (key.startsWith("message.receive")) {
            RespondCommandHandler.MessageType messageType = RespondCommandHandler.MessageType.valueOf(
                    Iterables.getLast(Splitter.on('.').splitToList(key)).toUpperCase());
            String sender = match.get(0);
            respondCommandHandler.onMessageReceived(messageType, sender);
        } else if (key.startsWith("assassination.")) {
            getGameListener().switchLobby("Assassination");
        } else if (key.equals("duels.starting")) {
            getGameListener().switchLobby("Duels");
        }
    }

    @Override
    public void onServerJoin() {
        respondCommandHandler = new RespondCommandHandler();
        Main.getInstance().getCommandRegistry().register(respondCommandHandler);
    }

    @Override
    public void onServerDisconnect(GameMode gameMode) {
        Main.getInstance().getCommandRegistry().unregister(respondCommandHandler);
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
