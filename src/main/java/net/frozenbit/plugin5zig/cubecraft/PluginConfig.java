package net.frozenbit.plugin5zig.cubecraft;

/**
 * Simple data holder for the plugin configuration. This class is (de)serialized via Gson
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class PluginConfig {
    private boolean autoUpdatesEnabled = true;

    public boolean hasAutoUpdatesEnabled() {
        return autoUpdatesEnabled;
    }
}
