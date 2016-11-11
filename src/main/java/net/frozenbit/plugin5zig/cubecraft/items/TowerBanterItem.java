package net.frozenbit.plugin5zig.cubecraft.items;


import com.google.common.collect.ImmutableList;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.TowerDefenceGameMode;

import java.util.List;

import static java.lang.String.format;

public class TowerBanterItem extends GameModeItem<TowerDefenceGameMode> {
    private static final List<TowerDefenceGameMode.Tower> DUMMY_TOWERS = ImmutableList.of(
            new TowerDefenceGameMode.Tower("Ice Tower", 80, The5zigAPI.getAPI().getItemByName("bow")),
            new TowerDefenceGameMode.Tower("Artillery Tower", 100, The5zigAPI.getAPI().getItemByName("tnt")),
            new TowerDefenceGameMode.Tower("Mage Tower", 200, The5zigAPI.getAPI().getItemByName("dragon_egg")),
            new TowerDefenceGameMode.Tower("Poison Tower", 800, The5zigAPI.getAPI().getItemByName("potion")),
            new TowerDefenceGameMode.Tower("Leach Tower", 2000, The5zigAPI.getAPI().getItemByName("nether_star"))
    );

    public TowerBanterItem() {
        super(TowerDefenceGameMode.class, GameState.GAME, GameState.ENDGAME);
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        int lineCount = 0;
        The5zigAPI.getAPI().getRenderHelper().drawString(getPrefix(), x, y);
        lineCount++;
        int coins = dummy ? 250 : getGameMode().getCoins();
        List<TowerDefenceGameMode.Tower> towers = getTowers(dummy);
        if (towers.isEmpty()) {
            The5zigAPI.getAPI().getRenderHelper().drawString(
                    ChatColor.DARK_GRAY + (ChatColor.ITALIC + "Open the tower build menu"), x, y + 10);
            The5zigAPI.getAPI().getRenderHelper().drawString(
                    ChatColor.DARK_GRAY + (ChatColor.ITALIC + "to activate this item"), x, y + 20);
        }
        for (TowerDefenceGameMode.Tower tower : towers) {
            int coinsLeft = coins - tower.price;
            String line = format("%s%s (⛃ %d)",
                    coinsLeft >= 0 ? ChatColor.GREEN : ChatColor.RED,
                    tower.shortName, tower.price);
            The5zigAPI.getAPI().getRenderHelper().drawString(line, x, y + lineCount * 10);
            lineCount++;
        }
    }

    private List<TowerDefenceGameMode.Tower> getTowers(boolean dummy) {
        return dummy ? DUMMY_TOWERS : getGameMode().getTowers();
    }

    @Override
    public int getWidth(boolean dummy) {
        return 200;
    }

    @Override
    public int getHeight(boolean dummy) {
        int towerCount = getTowers(dummy).size();
        return (towerCount == 0 ? 3 : (towerCount + 1)) * 10;
    }

    @Override
    protected Object getValue(boolean dummy) {
        return "";
    }

    @Override
    public String getName() {
        return "Towers";
    }
}
