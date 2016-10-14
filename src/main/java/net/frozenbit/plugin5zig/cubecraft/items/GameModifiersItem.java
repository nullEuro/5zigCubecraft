package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.TimeLootVotableMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.VotableCubeCraftGameMode;

public class GameModifiersItem extends GameModeItem<VotableCubeCraftGameMode> {

    public GameModifiersItem() {
        super(VotableCubeCraftGameMode.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return dummy ? TimeLootVotableMode.LootType.OVERPOWERED : getGameMode().getVoteResult();
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        super.render(x, y, renderLocation, dummy);

        if (!dummy && getGameMode() != null && getGameMode().getVoteResultTime() > System.currentTimeMillis() - 2500) {
            The5zigAPI.getAPI().getRenderHelper().drawLargeText(String.valueOf(getValue(false)));
        }
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return dummy || (super.shouldRender(false) && getGameMode().isDrawDone());
    }

    @Override
    public String getName() {
        return "Mode";
    }
}
