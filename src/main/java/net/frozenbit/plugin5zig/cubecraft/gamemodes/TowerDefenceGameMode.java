package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.mod.gui.ingame.ItemStack;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

import java.util.Collections;
import java.util.List;

public class TowerDefenceGameMode extends CubeCraftGameMode {
    private List<Tower> towers = Collections.emptyList();
    private int coins;
    private int exp;
    private int castleHealth;

    @Override
    public boolean isStalkerEnabled() {
        return false;
    }

    @Override
    public Stalker getStalker() {
        throw new IllegalStateException("Stalker is not enabled");
    }

    @Override
    public String getName() {
        return "Tower Defence";
    }

    public List<Tower> getTowers() {
        return towers;
    }

    public void setTowers(List<Tower> towers) {
        this.towers = towers;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getCastleHealth() {
        return castleHealth;
    }

    public void setCastleHealth(int castleHealth) {
        this.castleHealth = castleHealth;
    }

    public static class Tower {
        public final String name;
        public final String shortName;
        public final int price;
        public final ItemStack icon;

        public Tower(String name, int price, ItemStack icon) {
            this.name = name;
            this.shortName = name.replaceAll(" Tower$", "");
            this.price = price;
            this.icon = icon;
        }
    }
}
