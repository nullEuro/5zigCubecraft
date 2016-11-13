package net.frozenbit.plugin5zig.cubecraft.commands.stalker;

import java.util.UUID;

/**
 * Simple user data holder as returned by the Mojang API. (de-)serialized via {@link com.google.gson.Gson}.
 */
public class UserData {
    public final UUID id;
    public final String name;

    public UserData(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
}
