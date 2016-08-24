package net.frozenbit.plugin5zig.cubecraft.stalker;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class Storage implements Closeable {

    private final static String DB_FILE = "stalker.db";
    private final static int STALKED_PLAYER_CACHE_SIZE = 200;
    private Options options;
    private HashMap<UUID, StalkedPlayer> stalkedPlayerCache;

    public Storage() {
        options = new Options();
        options.createIfMissing(true);
        stalkedPlayerCache = new HashMap<>();
    }

    public StalkedPlayer getStalkedPlayer(CubeCraftPlayer player) {
        StalkedPlayer stalkedPlayer = stalkedPlayerCache.get(player.getId());
        if (stalkedPlayer == null) {
            try (DB db = factory.open(new File(DB_FILE), options)) {
                ByteBuffer key = ByteBuffer.allocate(16);
                key.putLong(player.getId().getMostSignificantBits());
                key.putLong(player.getId().getLeastSignificantBits());
                byte[] rawPlayerData = db.get(key.array());
                if (rawPlayerData != null) {
                    stalkedPlayer = new StalkedPlayer(player, new JSONObject(asString(rawPlayerData)));
                } else {
                    stalkedPlayer = new StalkedPlayer(player, 0, 0);
                }
                if (stalkedPlayerCache.size() > STALKED_PLAYER_CACHE_SIZE) {
                    List<NetworkPlayerInfo> currentPlayers = The5zigAPI.getAPI().getServerPlayers();
                    Set<UUID> cachedPlayers = stalkedPlayerCache.keySet();
                    for (UUID cachedPlayer : cachedPlayers) {
                        boolean isCurrrentPlayer = false;
                        for (NetworkPlayerInfo currentPlayer : currentPlayers) {
                            if (currentPlayer.getGameProfile().getId().equals(cachedPlayer)) {
                                isCurrrentPlayer = true;
                                break;
                            }
                        }
                        if (!isCurrrentPlayer)
                            stalkedPlayerCache.remove(cachedPlayer);
                    }
                }
                stalkedPlayerCache.put(player.getId(), stalkedPlayer);
            } catch (IOException | DBException ignored) {

            }
        }
        return stalkedPlayer;
    }

    public void storePlayer(StalkedPlayer player) {
        stalkedPlayerCache.put(player.getId(), player);
        try (DB db = factory.open(new File(DB_FILE), options)) {
            ByteBuffer key = ByteBuffer.allocate(16);
            key.putLong(player.getId().getMostSignificantBits());
            key.putLong(player.getId().getLeastSignificantBits());
            db.put(key.array(), bytes(player.toJSON().toString()));
        } catch (IOException | DBException e) {
            Main.getInstance().getLogger().println("Database Error: " + e);
        }
    }

    public void close() {

    }

}
