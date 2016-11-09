package net.frozenbit.plugin5zig.cubecraft.stalker;

import eu.the5zig.mod.The5zigAPI;
import eu.the5zig.mod.server.GameMode;
import eu.the5zig.mod.util.NetworkPlayerInfo;
import net.frozenbit.plugin5zig.cubecraft.CubeCraftPlayer;
import net.frozenbit.plugin5zig.cubecraft.Main;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.Options;
import org.json.JSONObject;

import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class Storage implements Closeable {

    private final static int STALKED_PLAYER_CACHE_SIZE = 200;
    private final static String GAMEMODE_WILDCARD = "all";
    private final static CopyOnWriteArraySet<UUID> updatedPlayers;
    private final static Options options;
    private final File db_file;
    private ConcurrentHashMap<UUID, StalkedPlayer> stalkedPlayerCache;

    static {
        updatedPlayers = new CopyOnWriteArraySet<>();
        options = new Options();
        options.createIfMissing(true);
    }

    public Storage(GameMode gameMode) {
        db_file = Main.PLUGIN_PATH.resolve(String.format("stalker/%s.db", gameMode.getName())).toFile();
        stalkedPlayerCache = new ConcurrentHashMap<>();
    }

    public StalkedPlayer getStalkedPlayer(CubeCraftPlayer player) {
        if (!updatedPlayers.isEmpty())
            for (UUID id : updatedPlayers)
                stalkedPlayerCache.remove(id);
        StalkedPlayer stalkedPlayer = stalkedPlayerCache.get(player.getId());
        if (stalkedPlayer == null) {
            synchronized (Storage.class) {
                try (DB db = factory.open(db_file, options)) {
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
                        Set<UUID> cachedPlayers = new HashSet<>(stalkedPlayerCache.keySet());
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
        }
        return stalkedPlayer;
    }

    public void storePlayer(StalkedPlayer player) {
        synchronized (Storage.class) {
            try (DB db = factory.open(db_file, options)) {
                ByteBuffer key = ByteBuffer.allocate(16);
                key.putLong(player.getId().getMostSignificantBits());
                key.putLong(player.getId().getLeastSignificantBits());
                db.put(key.array(), bytes(player.toJSON().toString()));
            } catch (IOException | DBException e) {
                Main.getInstance().getLogger().println("Database Error: " + e);
            }
        }
    }

    public void close() {

    }

    private interface DbModifier {
        void modify(DB db, byte[] key, String gamemode);
    }

    private static List<String> getPLayerStats(UUID id, DbModifier modifier) {
        List<String> playerStats = new ArrayList<>();
        File stalkerDir = Main.PLUGIN_PATH.resolve("stalker").toFile();
        File[] directories = stalkerDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (directories == null)
            throw new RuntimeException("Illegal database state: No directories");
        for (File directory : directories) {
            String gamemode = directory.getName().replace(".db", "");
            JSONObject jsonPlayerStats = null;
            synchronized (Storage.class) {
                try (DB db = factory.open(directory, options)) {
                    ByteBuffer key = ByteBuffer.allocate(16);
                    key.putLong(id.getMostSignificantBits());
                    key.putLong(id.getLeastSignificantBits());
                    if (modifier != null)
                        modifier.modify(db, key.array(), gamemode);
                    byte[] rawPlayerData = db.get(key.array());
                    if (rawPlayerData != null) {
                        jsonPlayerStats = new JSONObject(asString(rawPlayerData));
                    }
                } catch (IOException | DBException ignored) {

                }
            }
            if (jsonPlayerStats != null) {
                playerStats.add(String.format("    %s: %d kills, %d deaths", gamemode, jsonPlayerStats.getInt("kills"), jsonPlayerStats.getInt("deaths")));
            }
        }
        if (playerStats.size() == 0)
            playerStats.add("    No stats for this player");
        return playerStats;
    }

    public static List<String> getPlayerStats(UUID id) {
        return getPLayerStats(id, null);
    }

    public static List<String> deletePlayerStats(UUID id, final String gamemode) {
        List<String> output = getPLayerStats(id, new DbModifier() {
            @Override
            public void modify(DB db, byte[] key, String currentGamemode) {
                if (gamemode.equals(GAMEMODE_WILDCARD) || gamemode.equals(currentGamemode))
                    db.delete(key);
            }
        });
        updatedPlayers.add(id);
        return output;
    }
}
