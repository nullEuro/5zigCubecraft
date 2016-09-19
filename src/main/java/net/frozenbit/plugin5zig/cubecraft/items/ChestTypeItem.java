package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.CubeCraftGameMode;

public class ChestTypeItem extends GameModeItem<CubeCraftGameMode> {

    public ChestTypeItem() {
        super(CubeCraftGameMode.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        ChestVote chestType;
        if (dummy) {
            chestType = ChestVote.OVERPOWERED;
        } else {
            chestType = getGameMode() != null ?
                    getGameMode().getChestType() : ChestVote.NONE;
        }
        return chestType.color + chestType.serverName;
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        super.render(x, y, renderLocation, dummy);

        if (!dummy && getGameMode() != null && getGameMode().getChestTypeTime() > System.currentTimeMillis() - 2500) {
            The5zigAPI.getAPI().getRenderHelper().drawLargeText(String.valueOf(getValue(false)));
        }
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return dummy || (super.shouldRender(false) && getGameMode().getChestType() != ChestVote.NONE);
    }

    @Override
    public String getName() {
        return "Chests";
    }
}
