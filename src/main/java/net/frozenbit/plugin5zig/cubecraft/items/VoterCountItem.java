package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import net.frozenbit.plugin5zig.cubecraft.ChestVote;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.CubeCraftGameMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;

public class VoterCountItem extends GameModeItem<CubeCraftGameMode> {
    private int voterCount;

    public VoterCountItem() {
        super(SkywarsMode.class);
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        The5zigAPI.getAPI().getRenderHelper().drawString(getPrefix(), x, y);
        voterCount = 0;
        if (!dummy) {
            for (CubeCraftPlayer player : getGameMode().getPlayers()) {
                if (player.canVote()) {
                    ++voterCount;
                    ChestVote vote = getGameMode().getVote(player);
                    The5zigAPI.getAPI().getRenderHelper().drawString(vote.color + player.name,
                            x, y + voterCount * 10);
                }
            }
        } else {
            renderDummyVotes(x, y);
        }
    }

    private void renderDummyVotes(int x, int y) {
        int voterCount = 0;
        for (ChestVote vote : ChestVote.values()) {
            ++voterCount;
            The5zigAPI.getAPI().getRenderHelper().drawString(vote.color + "nullEuro",
                    x, y + voterCount * 10);
        }
    }

    @Override
    public int getWidth(boolean dummy) {
        return 50;
    }

    @Override
    public String getName() {
        return "Voters";
    }

    @Override
    public int getHeight(boolean dummy) {
        return ((!dummy ? voterCount : ChestVote.values().length) + 1) * 10;
    }

    @Override
    protected Object getValue(boolean dummy) {
        return "";
    }

}
