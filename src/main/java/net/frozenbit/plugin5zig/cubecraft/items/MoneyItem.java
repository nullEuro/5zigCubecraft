package net.frozenbit.plugin5zig.cubecraft.items;


import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.AssassinationMode;

public class MoneyItem extends GameModeItem<AssassinationMode> {

    public MoneyItem() {
        super(AssassinationMode.class);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return dummy ? 547 :
                (The5zigAPI.getAPI().getItemCount("item.charcoal") +
                 5 * The5zigAPI.getAPI().getItemCount("item.goldNugget") +
                 10 * The5zigAPI.getAPI().getItemCount("item.ghastTear"));
    }

    @Override
    public String getName() {
        return "Money";
    }
}
