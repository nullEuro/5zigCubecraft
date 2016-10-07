package net.frozenbit.plugin5zig.cubecraft.listeners;

import eu.the5zig.mod.The5zigAPI;
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

}
