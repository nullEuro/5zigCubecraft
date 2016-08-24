package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.mod.server.IPatternResult;
import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;


public class SkywarsGameListener extends AbstractCubeCraftGameListener {

    @Override
    public Class<SkywarsMode> getGameMode() {
        return SkywarsMode.class;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return lobby.contains("SkyWars");
    }

    @Override
    public void onGameModeJoin(SkywarsMode gameMode) {
        gameMode.setKitsEnabled(The5zigAPI.getAPI().getItemCount("item.paper") == 1);
        requestPlayerList();
    }

    @Override
    public void onMatch(SkywarsMode gameMode, String key, IPatternResult match) {
        switch (key) {
            case "skywars.join":
                requestPlayerList();
                break;
            case "skywars.left":
                gameMode.getPlayers().remove(gameMode.getPlayerByName(match.get(0)));
                break;
            case "skywars.starting":
                gameMode.setState(GameState.STARTING);
                break;
            case "skywars.countdown":
                gameMode.setTime(System.currentTimeMillis() + 1000 * Integer.parseInt(match.get(0)));
                break;
            case "skywars.start":
                gameMode.setState(GameState.GAME);
                gameMode.setTime(System.currentTimeMillis());
                break;
            case "skywars.kit":
                gameMode.setKit(match.get(0));
                break;
            case "skywars.chestvote": {
                CubeCraftPlayer player = gameMode.getPlayerByName(match.get(0));
                ChestVote chestVote = ChestVote.fromString(match.get(1));
                gameMode.getVotes().put(player, chestVote);
                break;
            }
            case "skywars.chestType": {
                ChestVote chestType = ChestVote.fromString(match.get(0));
                gameMode.setChestType(chestType);
                break;
            }
            case "skywars.kill":
                Main.getInstance().getStalker().onKill(match.get(0), match.get(1));
                break;
            case "playerList":
                updatePlayerList(gameMode, match);
                Main.getInstance().getStalker().onPlayerListUpdate(gameMode.getPlayers());
                break;
            case "welcome":
                gameMode.setState(GameState.FINISHED);
                break;
        }
    }

    @Override
    public boolean onServerChat(SkywarsMode gameMode, String message) {
        Main.getInstance().getLogger().println("chat: " + message);
        return super.onServerChat(gameMode, message);
    }

}
