package net.frozenbit.plugin5zig.cubecraft.items;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.DuelsGameMode;
import net.frozenbit.plugin5zig.cubecraft.stalker.StalkedPlayer;

import static java.lang.String.format;


public class OpponentItem extends GameModeItem<DuelsGameMode> {
    private int dummyPing;
    private int lines = 1;
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
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        if (dummy) {
            if (System.currentTimeMillis() - lastDummyPingTime > 1000) {
                lastDummyPingTime = System.currentTimeMillis();
                dummyPing = (int) (Math.random() * 500.0);
            }
            The5zigAPI.getAPI().getRenderHelper().drawString(formatOpponentInfo("nullEuro", dummyPing), x, y);
            The5zigAPI.getAPI().getRenderHelper().drawString(formatOpponentStats(3, 7), x, y + 10);
            lines = 2;
            return;
        }
        NetworkPlayerInfo opponentInfo = getGameMode().getOpponentInfo();
        if (opponentInfo == null) {
            The5zigAPI.getAPI().getRenderHelper().drawString(getPrefix() + ChatColor.GRAY + "...", x, y);
            lines = 1;
            return;
        }
        The5zigAPI.getAPI().getRenderHelper().drawString(formatOpponentInfo(opponentInfo.getGameProfile().getName(), opponentInfo.getPing()), x, y);
        StalkedPlayer stalkedOpponent = getGameMode().getStalker().getStalkedPlayerById(opponentInfo.getGameProfile().getId());
        if (stalkedOpponent != null) {
            The5zigAPI.getAPI().getRenderHelper().drawString(formatOpponentStats(stalkedOpponent.getKills(), stalkedOpponent.getDeaths()), x, y + 10);
            lines = 2;
        } else {
            lines = 1;
        }
    }

    private String formatOpponentInfo(String name, int ping) {
        return format("%s%s (%s)", getPrefix(), name, getColoredPing(ping));
    }

    private String formatOpponentStats(int kills, int deaths) {
        return format("%d kills, %d deaths", kills, deaths);
    }

    @Override
    protected Object getValue(boolean dummy) {
        return "";
    }

    @Override
    public int getWidth(boolean dummy) {
        return 50;
    }

    @Override
    public int getHeight(boolean dummy) {
        return lines * 10;
    }

    @Override
    public String getName() {
        return "Opponent";
    }
}
