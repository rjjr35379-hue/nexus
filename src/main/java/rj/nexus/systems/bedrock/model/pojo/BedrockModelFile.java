package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.annotations.SerializedName;

public class BedrockModelFile {
    @SerializedName("format_version")
    private String formatVersion;
    @SerializedName("minecraft:geometry")
    private GeometryNew[] geoNew;

    public boolean isLegacy() {
        return geoNew == null;
    }

    public GeometryNew getGeometry() {
        return (geoNew != null && geoNew.length > 0) ? geoNew[0] : null;
    }

    public static class GeometryNew {
        @SerializedName("description")
        private DescriptionItem description;
        @SerializedName("bones")
        private BoneItem[] bones;

        public DescriptionItem getDescription() {
            return description;
        }

        public BoneItem[] getBones() {
            return bones;
        }
    }
}
