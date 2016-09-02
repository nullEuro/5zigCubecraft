package net.frozenbit.plugin5zig.cubecraft.updater.models;


import com.google.gson.annotations.SerializedName;

public class PluginInfo {
    @SerializedName("main")
    private String mainClassName;

    public String getMainClassName() {
        return mainClassName;
    }
}
