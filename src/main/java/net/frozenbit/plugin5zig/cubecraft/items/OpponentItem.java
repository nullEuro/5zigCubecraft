package net.frozenbit.plugin5zig.cubecraft.items;

import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.DuelsGameMode;

import static java.lang.String.format;


public class OpponentItem extends GameModeItem<DuelsGameMode> {
    private int dummyPing;
    private long lastDummyPingTime;

    public OpponentItem() {
        super(DuelsGameMode.class);
    }

    private static String getColoredPing(int ping) {
        return ping > 0 ? format("%s%d%s", (ping > 300 ? ChatColor.RED : ping > 150 ? ChatColor.YELLOW : ChatColor.WHITE),
                ping, ChatColor.RESET) : "?";
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return super.shouldRender(dummy);
    }

    @Override
    protected Object getValue(boolean dummy) {
        if (dummy) {
            if (System.currentTimeMillis() - lastDummyPingTime > 1000) {
                lastDummyPingTime = System.currentTimeMillis();
                dummyPing = (int) (Math.random() * 500.0);
            }
            return formatOpponentInfo("nullEuro", dummyPing);
        }
        NetworkPlayerInfo opponentInfo = getGameMode().getOpponentInfo();
        if (opponentInfo == null) {
            return ChatColor.GRAY + "...";
        }
        return formatOpponentInfo(opponentInfo.getGameProfile().getName(), opponentInfo.getPing());
    }

    private String formatOpponentInfo(String name, int ping) {
        return format("%s (%s)", name, getColoredPing(ping));
    }

    @Override
    public String getName() {
        return "Opponent";
    }
}
