package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Rank;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

import java.util.Map;

import static java.lang.String.format;

public class SurvivalGamesMode extends VotableCubeCraftGameMode {
    private static Stalker stalker;

    private static ChatColor lootTypeColor(String itemType) {
        if (itemType == null) {
            return ChatColor.GRAY;
        }
        switch (itemType) {
            case "Basic":
                return ChatColor.GREEN;
            case "Normal":
                return ChatColor.YELLOW;
            case "Overpowered":
                return ChatColor.DARK_RED;
            default:
                return null;
        }
    }

    @Override
    public String getName() {
        return "SurvivalGames";
    }

    @Override
    public Stalker getStalker() {
        if (stalker == null)
            stalker = new Stalker(this);
        return stalker;
    }

    @Override
    protected String formatVoteString(CubeCraftPlayer player, Map<String, String> vote) {
        StringBuilder voteStringBuilder = new StringBuilder()
                .append(lootTypeColor(vote.get("loot")))
                .append(player.getName())
                .append(ChatColor.RESET)
                .append(" ");
        if (vote.containsKey("health")) {
            HealthType healthVote = HealthType.valueOf(vote.get("health").toUpperCase());
            voteStringBuilder
                    .append(ChatColor.RED)
                    .append(healthVote.symbol)
                    .append(ChatColor.RESET)
                    .append(" ");
        }
        if (vote.containsKey("time")) {
            DaytimeType daytimeVote = DaytimeType.valueOf(vote.get("time").toUpperCase());
            voteStringBuilder.append(daytimeVote);
        }
        return voteStringBuilder.toString();
    }

    @Override
    public String formatVoteResult(Map<String, String> draw) {
        String itemType = draw.get("loot");
        HealthType health = HealthType.valueOf(draw.get("health").toUpperCase());
        DaytimeType daytimeType = DaytimeType.valueOf(draw.get("time").toUpperCase());
        return format("%s%s%s / %s / %s", lootTypeColor(itemType), itemType, ChatColor.RESET, health, daytimeType.getChatName());
    }

    @Override
    protected boolean isShownInVoterList(CubeCraftPlayer player) {
        return player.isStaff() || Rank.GOLD.compareTo(player.getRank()) <= 0;
    }

    @Override
    protected String[] getVoteCategories() {
        return new String[]{"health", "loot", "time"};
    }

    public enum HealthType {
        HARDCORE("♡"), NORMAL("♥"), DOUBLE("♥♥"), TRIPLE("♥♥♥");

        public final String symbol;

        HealthType(String symbol) {
            this.symbol = symbol;
        }


        @Override
        public String toString() {
            return ChatColor.RED + symbol + ChatColor.RESET;
        }
    }

    public enum DaytimeType {
        DAY("Day", "●"),
        SUNSET("Sunset", "◓"),
        NIGHT("Night", "☾"),
        NONE("", "");

        public final String chatName;
        public final String symbol;

        DaytimeType(String chatName, String symbol) {
            this.chatName = chatName;
            this.symbol = symbol;
        }

        public String getChatName() {
            return chatName;
        }

        @Override
        public String toString() {
            return ChatColor.GOLD + symbol + ChatColor.RESET;
        }
    }
}
