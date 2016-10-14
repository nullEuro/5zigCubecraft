package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Rank;

import java.util.Map;

import static com.google.common.base.MoreObjects.firstNonNull;

/**
 * Common superclass of gamemodes that support voting for time and loot
 */
public abstract class TimeLootVotableMode extends VotableCubeCraftGameMode {
    @Override
    protected String formatVoteString(CubeCraftPlayer player, Map<String, String> vote) {
        DaytimeType time = vote.containsKey("time") ?
                DaytimeType.fromString(vote.get("time")) : DaytimeType.NONE;
        return (player.getRank() == Rank.GOLD ? ChatColor.DARK_GRAY :
                LootType.fromString(firstNonNull(vote.get("loot"), "")).color)
               + player.getName() + ChatColor.RESET + " " + time.coloredSymbol();
    }

    @Override
    public String formatVoteResult(Map<String, String> draw) {
        LootType lootType = LootType.fromString(draw.get("loot"));
        return lootType + "/" + draw.get("time");
    }

    @Override
    protected boolean isShownInVoterList(CubeCraftPlayer player) {
        return player.isStaff() || Rank.GOLD.compareTo(player.getRank()) <= 0;
    }

    @Override
    protected String[] getVoteCategories() {
        return new String[]{"loot", "time"};
    }

    public enum DaytimeType {
        DAY_TIME("Day", ChatColor.GOLD, "●"),
        SUNSET("Sunset", ChatColor.GOLD, "◓"),
        NIGHT_TIME("Night", ChatColor.GOLD, "☾"),
        NONE("", ChatColor.RESET, "");

        public final String chatName;
        public final ChatColor color;
        public final String symbol;

        DaytimeType(String chatName, ChatColor color, String symbol) {
            this.chatName = chatName;
            this.color = color;
            this.symbol = symbol;
        }

        public static DaytimeType fromString(String daytime) {
            for (DaytimeType daytimeType : DaytimeType.values()) {
                if (daytimeType.chatName.equals(daytime)) {
                    return daytimeType;
                }
            }
            throw new IllegalArgumentException("value '" + daytime + "' unknown");
        }

        public String coloredSymbol() {
            return color + symbol + ChatColor.RESET;
        }

        @Override
        public String toString() {
            return chatName;
        }
    }

    public enum LootType {
        NONE("", ChatColor.GRAY),
        BASIC("Basic", ChatColor.GREEN),
        NORMAL("Normal", ChatColor.YELLOW),
        OVERPOWERED("Overpowered", ChatColor.DARK_RED);

        public final String chatName;
        public final ChatColor color;

        LootType(String chatName, ChatColor color) {
            this.chatName = chatName;
            this.color = color;
        }

        public static LootType fromString(String string) {
            for (LootType type : LootType.values()) {
                if (type.chatName.equalsIgnoreCase(string)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("type '" + string + "' not found");
        }

        @Override
        public String toString() {
            return color + chatName + ChatColor.RESET;
        }

    }
}
