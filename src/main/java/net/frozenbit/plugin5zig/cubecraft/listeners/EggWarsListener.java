package net.frozenbit.plugin5zig.cubecraft.listeners;


import net.frozenbit.plugin5zig.cubecraft.gamemodes.EggWarsGameMode;

public class EggWarsListener extends AbstractCubeCraftGameListener<EggWarsGameMode> {
    public EggWarsListener() {
        super("eggwars");
    }

    @Override
    public Class<EggWarsGameMode> getGameMode() {
        return EggWarsGameMode.class;
    }

    @Override
    public void onGameModeJoin(EggWarsGameMode gameMode) {
        super.onGameModeJoin(gameMode);
        gameMode.setKitsEnabled(true);
    }

    @Override
    public boolean matchLobby(String lobby) {
        return lobby.contains("EggWars");
    }
}
