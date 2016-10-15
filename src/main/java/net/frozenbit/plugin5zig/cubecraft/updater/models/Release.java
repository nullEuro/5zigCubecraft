package net.frozenbit.plugin5zig.cubecraft.updater.models;


import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class Release {
    @SerializedName("tag_name")
    private String tagName;
    private List<Asset> assets;

    public String getTagName() {
        return tagName;
    }

    public Asset getJarAsset() {
        for (Asset asset : assets) {
            if (asset.type.equals("application/x-java-archive")) {
                return asset;
            }
        }
        return null;
    }

    public static class Asset {
        private String name;
        private String url;
        @SerializedName("content_type")
        private String type;

        public String getUrl() {
            return url;
        }

        public String getName() {
            return name;
        }
    }
}
