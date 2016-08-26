package net.frozenbit.plugin5zig.cubecraft.listeners;

import net.frozenbit.plugin5zig.cubecraft.gamemodes.AssassinationMode;


public class AssassinationListener extends AbstractCubeCraftGameListener<AssassinationMode> {
    @Override
    public Class<AssassinationMode> getGameMode() {
        return AssassinationMode.class;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return lobby.equals("Assassination");
    }
}
