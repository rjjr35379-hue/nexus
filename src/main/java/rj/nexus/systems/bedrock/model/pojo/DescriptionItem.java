package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.annotations.SerializedName;

public class DescriptionItem {
    @SerializedName("texture_width")
    private int textureWidth = 64;
    @SerializedName("texture_height")
    private int textureHeight = 64;
    @SerializedName("visible_bounds_width")
    private float boundsWidth = 2;
    @SerializedName("visible_bounds_height")
    private float boundsHeight = 2;
    @SerializedName("visible_bounds_offset")
    private float[] boundsOffset = {0, 0, 0};

    public int getTextureWidth() {
        return textureWidth;
    }

    public int getTextureHeight() {
        return textureHeight;
    }

    public float getBoundsWidth() {
        return boundsWidth;
    }

    public float getBoundsHeight() {
        return boundsHeight;
    }

    public float[] getBoundsOffset() {
        return boundsOffset;
    }
}
