package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.CubeCraftGameMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SurvivalGamesMode;

public class KitItem extends GameModeItem<CubeCraftGameMode> {

    public KitItem() {
        super(CubeCraftGameMode.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        String kit;
        if (dummy) {
            kit = "lumberjack";
        } else {
            kit = (getGameMode() != null ? getGameMode().getKit() : null);
        }
        return kit != null ? ChatColor.GREEN + kit : ChatColor.RED + "not selected";
    }

    @Override
    public String getName() {
        return "Kit";
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return dummy ||
               (super.shouldRender(false) && getGameMode().hasKitsEnabled()
                && !(getGameMode() instanceof SurvivalGamesMode
                     && getGameMode().getState() == GameState.LOBBY));
    }
}
