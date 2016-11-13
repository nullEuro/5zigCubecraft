package net.frozenbit.plugin5zig.cubecraft.listeners;

import com.google.common.collect.Ordering;
import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.gui.ingame.ItemStack;
import eu.the5zig.mod.server.GameState;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.TowerDefenceGameMode;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TowerDefenceListener extends AbstractCubeCraftGameListener<TowerDefenceGameMode> {
    public static final Comparator<TowerDefenceGameMode.Tower> PRICE_COMPARATOR = new Comparator<TowerDefenceGameMode.Tower>() {
        @Override
        public int compare(TowerDefenceGameMode.Tower o1, TowerDefenceGameMode.Tower o2) {
            return Integer.compare(o1.price, o2.price);
        }
    };
    public static final String COIN_SCORE_NAME = "§6⛃§e Coins";
    public static final String CASTLE_HEALTH_SCORE_NAME = "§c❤§d Castle Health";
    public static final String EXP_SCORE_NAME = "§3✦§b EXP";
    private static final Pattern PRICE_PATTERN = Pattern.compile("Price: (\\d+) coins");
    private Map<String, TowerDefenceGameMode.Tower> towers;
    private long tickCount;

    public TowerDefenceListener() {
        super("td");
    }

    @Override
    public Class<TowerDefenceGameMode> getGameMode() {
        return TowerDefenceGameMode.class;
    }

    @Override
    public boolean matchLobby(String lobby) {
        return lobby.contains("Tower Defence");
    }

    @Override
    public void onGameModeJoin(TowerDefenceGameMode gameMode) {
        super.onGameModeJoin(gameMode);
        towers = new HashMap<>();
    }

    @Override
    public void onTick(TowerDefenceGameMode gameMode) {
        super.onTick(gameMode);
        tickCount++;
        if (tickCount % 5 == 0) {
            HashMap<String, Integer> lines = The5zigAPI.getAPI().getSideScoreboard().getLines();
            if (lines.containsKey(COIN_SCORE_NAME)) {
                gameMode.setCoins(lines.get(COIN_SCORE_NAME));
            }
            if (lines.containsKey(CASTLE_HEALTH_SCORE_NAME)) {
                gameMode.setCastleHealth(lines.get(CASTLE_HEALTH_SCORE_NAME));
            }
            if (lines.containsKey(EXP_SCORE_NAME)) {
                gameMode.setExp(lines.get(EXP_SCORE_NAME));
            }

            if (gameMode.getState() == GameState.LOBBY && !lines.containsKey("§5Map:")) {
                long startTime = gameMode.getTime();
                gameMode.setState(GameState.GAME);
                gameMode.setTime(startTime);
            }
        }
    }

    @Override
    public void onChestSetSlot(TowerDefenceGameMode gameMode, String containerTitle, int slot, ItemStack itemStack) {
        super.onChestSetSlot(gameMode, containerTitle, slot, itemStack);
        if ("Tower builder".equals(containerTitle) && slot < 18 && !"iron_bars".equals(itemStack.getKey())) {
            String towerName = ChatColor.stripColor(itemStack.getDisplayName());
            if (itemStack.getLore().size() < 2) {
                The5zigAPI.getLogger().warn("No lore for {}", towerName);
                return;
            }
            String priceStr = ChatColor.stripColor(itemStack.getLore().get(1));
            Matcher priceMatcher = PRICE_PATTERN.matcher(priceStr);
            if (!priceMatcher.matches()) {
                The5zigAPI.getLogger().warn("Unable to read price from string '{}' for {}", priceStr, towerName);
                return;
            }
            int price = Integer.parseInt(priceMatcher.group(1));
            towers.put(towerName, new TowerDefenceGameMode.Tower(towerName, price, itemStack));
            if (towers.size() != gameMode.getTowers().size()) {
                gameMode.setTowers(Ordering.from(PRICE_COMPARATOR).immutableSortedCopy(towers.values()));
            }
        }
    }

}
