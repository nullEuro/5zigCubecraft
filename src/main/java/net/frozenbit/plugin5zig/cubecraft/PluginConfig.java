package net.frozenbit.plugin5zig.cubecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple data holder for the plugin configuration. This class is (de)serialized via Gson
 */
@SuppressWarnings({"FieldCanBeLocal", "WeakerAccess"})
public class PluginConfig {
    private boolean autoUpdatesEnabled = true;
    private List<String> quickMessages = new ArrayList<>();

    public boolean hasAutoUpdatesEnabled() {
        return autoUpdatesEnabled;
    }

    public List<String> getQuickMessages() {
        return quickMessages;
    }
}
