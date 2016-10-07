package net.frozenbit.plugin5zig.cubecraft.gamemodes;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;

import java.util.*;


public abstract class VotableCubeCraftGameMode extends CubeCraftGameMode {
    private Table<CubeCraftPlayer, String, String> votes = HashBasedTable.create();
    private Map<String, String> draw = new HashMap<>();
    private TimeLootVotableMode.LootType chestType = TimeLootVotableMode.LootType.NONE;
    private long lastDrawResultTime;
    private ArrayList<String> formattedVoterList;
    private String formattedVoteResult;

    protected abstract String formatVoteString(CubeCraftPlayer player, Map<String, String> vote);

    protected abstract String formatVoteResult(Map<String, String> draw);

    protected abstract boolean isShownInVoterList(CubeCraftPlayer player);

    protected abstract String[] getVoteCategories();

    public final long getVoteResultTime() {
        return lastDrawResultTime;
    }

    public final String getVoteResult() {
        if (formattedVoteResult == null) {
            formattedVoteResult = isDrawDone() ? formatVoteResult(Collections.unmodifiableMap(draw)) : "";
        }
        return formattedVoteResult;
    }

    public boolean isDrawDone() {
        for (String category : getVoteCategories()) {
            if (!draw.containsKey(category)) {
                return false;
            }
        }
        return true;
    }

    public final List<String> getFormattedVoterList() {
        if (formattedVoterList == null) {
            formattedVoterList = new ArrayList<>();
            for (CubeCraftPlayer player : players) {
                if (isShownInVoterList(player)) {
                    formattedVoterList.add(formatVoteString(player, votes.row(player)));
                }
            }
        }
        return formattedVoterList;
    }

    @Override
    public void playerListUpdate() {
        super.playerListUpdate();
        formattedVoterList = null;
    }

    public final void onDrawResult(String category, String vote) {
        draw.put(category, vote);
        lastDrawResultTime = System.currentTimeMillis();
        formattedVoteResult = null;
    }

    public final void onVote(String playerName, String category, String vote) {
        CubeCraftPlayer voter = getPlayerByName(playerName);
        votes.put(voter, category, vote);
        formattedVoterList = null;
    }

}
