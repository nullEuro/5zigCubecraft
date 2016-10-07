package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.TimeLootVotableMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.VotableCubeCraftGameMode;

public class VoterCountItem extends GameModeItem<VotableCubeCraftGameMode> {
    private int voterCount;

    public VoterCountItem() {
        super(VotableCubeCraftGameMode.class);
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        The5zigAPI.getAPI().getRenderHelper().drawString(getPrefix(), x, y);
        voterCount = 0;
        if (!dummy) {
            for (String voter : getGameMode().getFormattedVoterList()) {
                ++voterCount;
                The5zigAPI.getAPI().getRenderHelper().drawString(voter, x, y + voterCount * 10);
            }
        } else {
            renderDummyVotes(x, y);
        }
    }

    private void renderDummyVotes(int x, int y) {
        int voterCount = 0;
        for (TimeLootVotableMode.LootType vote : TimeLootVotableMode.LootType.values()) {
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
        return ((!dummy ? voterCount : TimeLootVotableMode.LootType.values().length) + 1) * 10;
    }

    @Override
    protected Object getValue(boolean dummy) {
        return "";
    }

}
