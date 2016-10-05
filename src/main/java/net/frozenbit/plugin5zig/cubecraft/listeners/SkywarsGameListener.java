package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.IPatternResult;
import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;


public class SkywarsGameListener extends AbstractCubeCraftGameListener<SkywarsMode> {

    public SkywarsGameListener() {
        super("skywars");
    }

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
        super.onGameModeJoin(gameMode);
        gameMode.setKitsEnabled(The5zigAPI.getAPI().getItemCount("item.paper") == 1);
        requestPlayerList();
    }

    @Override
    public void onMatch(SkywarsMode gameMode, String key, IPatternResult match) {
        super.onMatch(gameMode, key, match);
        switch (key) {
            case "skywars.left":
                Main.getInstance().getStalker().onPlayerListUpdate(gameMode.getPlayers());
                break;
            case "skywars.chestType": {
                ChestVote chestType = ChestVote.fromString(match.get(0));
                gameMode.setChestType(chestType);
                break;
            }
            case "skywars.chestVote": {
                CubeCraftPlayer player = gameMode.getPlayerByName(match.get(0));
                ChestVote chestVote = ChestVote.fromString(match.get(1));
                gameMode.getVotes().put(player, chestVote);
                break;
            }
            case "skywars.kill":
                Main.getInstance().getStalker().onKill(match.get(0), match.get(1));
                break;
            case "generic.playerList":
                Main.getInstance().getStalker().onPlayerListUpdate(gameMode.getPlayers());
                break;
        }
    }

    @Override
    public boolean onServerChat(SkywarsMode gameMode, String message) {
        Main.getInstance().getLogger().println("chat: " + message);
        return super.onServerChat(gameMode, message);
    }

}
