package net.frozenbit.plugin5zig.cubecraft.items;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.modules.GameModeItem;
import eu.the5zig.mod.render.RenderLocation;
import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.Main;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.CubeCraftGameMode;
import net.frozenbit.plugin5zig.cubecraft.gamemodes.SkywarsMode;
import net.frozenbit.plugin5zig.cubecraft.stalker.StalkedPlayer;

import java.util.List;

public class StalkerItem extends GameModeItem<CubeCraftGameMode> {

    private final static int minWidthName = 100;
    private final static int widthKills = 30;
    private final static int widthDeaths = 40;

    private int widthName;

    public StalkerItem() {
        super(SkywarsMode.class);
    }

    @Override
    public void render(int x, int y, RenderLocation renderLocation, boolean dummy) {
        widthName = Math.max(minWidthName, Main.getInstance().getStalker().getMaxNameWidth() + 20);
        List<StalkedPlayer> stalkedPlayers = Main.getInstance().getStalker().getStalkedPlayerList();
        if (Main.getInstance().getStalker().getShownPlayerCount() == 0 && !dummy)
            return;
        The5zigAPI.getAPI().getRenderHelper().drawString(ChatColor.UNDERLINE + "Name", x, y);
        The5zigAPI.getAPI().getRenderHelper().drawString(ChatColor.UNDERLINE + "Kills", x + widthName, y);
        The5zigAPI.getAPI().getRenderHelper().drawString(ChatColor.UNDERLINE + "Deaths", x + widthName + widthKills, y);
        int lineNumber = 1;
        if (!dummy) {
            for (StalkedPlayer player : stalkedPlayers) {
                if (player.getKills() == 0 && player.getDeaths() == 0)
                    continue;
                drawPlayer(player.getName(), player.getThreatColor(), player.getKills(), player.getDeaths(), x, y, lineNumber++);
            }
        } else {
            drawPlayer("MagnificentSpam", ChatColor.WHITE, 4, 2, x, y, lineNumber++);
            drawPlayer("nullEuro", ChatColor.YELLOW, 3, 5, x, y, lineNumber++);
            drawPlayer("CrimsonGala", ChatColor.RED, 0, 4, x, y, lineNumber++);
            drawPlayer("Tran_Van_Tuan", ChatColor.WHITE, 1, 1, x, y, lineNumber);
        }
    }

    private void drawPlayer(String name, ChatColor threatColor, int kills, int deaths, int x, int y, int lineNumber) {
        The5zigAPI.getAPI().getRenderHelper().drawString(threatColor + String.format("%.20s", name), x, y + lineNumber * 10);
        The5zigAPI.getAPI().getRenderHelper().drawString(threatColor + String.format("%d", kills), x + widthName, y + lineNumber * 10);
        The5zigAPI.getAPI().getRenderHelper().drawString(threatColor + String.format("%d", deaths), x + widthName + widthKills, y + lineNumber * 10);
    }

    @Override
    public int getWidth(boolean dummy) {
        if (dummy)
            return 190;
        widthName = Math.max(minWidthName, Main.getInstance().getStalker().getMaxNameWidth() + 20);
        return widthName + widthKills + widthDeaths;
    }

    @Override
    public int getHeight(boolean dummy) {
        if (dummy)
            return 50;
        return 10 * (1 + Main.getInstance().getStalker().getStalkedPlayerList().size());
    }

    @Override
    protected Object getValue(boolean b) {
        return "";
    }

    @Override
    public String getName() {
        return "Stalker";
    }

    @Override
    public boolean shouldRender(boolean dummy) {
        return dummy || The5zigAPI.getAPI().isPlayerListShown();
    }
}
