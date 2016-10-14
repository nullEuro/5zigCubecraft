package net.frozenbit.plugin5zig.cubecraft.gamemodes;


import eu.the5zig.util.minecraft.ChatColor;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Rank;
import net.frozenbit.plugin5zig.cubecraft.stalker.Stalker;

import java.util.Map;

import static java.lang.String.format;

public class EggWarsGameMode extends VotableCubeCraftGameMode {
    private static Stalker stalker;

    private static ChatColor itemTypeColor(String itemType) {
        if (itemType == null) {
            return ChatColor.GRAY;
        }
        switch (itemType) {
            case "Hardcore":
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
        return "EggWars";
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
                .append(itemTypeColor(vote.get("items")))
                .append(player.getName())
                .append(ChatColor.RESET)
                .append(" ");
        if (vote.containsKey("health")) {
            HealthType healthVote = HealthType.valueOf(vote.get("health").toUpperCase());
            voteStringBuilder
                    .append(ChatColor.RED)
                    .append(healthVote.symbol)
                    .append(ChatColor.RESET);
        }
        return voteStringBuilder.toString();
    }

    @Override
    public String formatVoteResult(Map<String, String> draw) {
        String itemType = draw.get("items");
        HealthType health = HealthType.valueOf(draw.get("health").toUpperCase());
        return format("%s%s%s / %s", itemTypeColor(itemType), itemType, ChatColor.RESET, health);
    }

    @Override
    protected boolean isShownInVoterList(CubeCraftPlayer player) {
        return player.isStaff() || Rank.GOLD.compareTo(player.getRank()) <= 0;
    }

    @Override
    protected String[] getVoteCategories() {
        return new String[]{"health", "items"};
    }

    public enum HealthType {
        NORMAL("♥"), DOUBLE("♥♥"), TRIPLE("♥♥♥");

        public final String symbol;

        HealthType(String symbol) {
            this.symbol = symbol;
        }


        @Override
        public String toString() {
            return ChatColor.RED + symbol + ChatColor.RESET;
        }
    }
}
