package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.annotations.SerializedName;

public class FaceItem {
    @SerializedName("uv")
    private float[] uv;
    @SerializedName("uv_size")
    private float[] uvSize;
    @SerializedName("uv_rotation")
    private int uvRotation = 0;

    public float[] getRotatedUVs(float texW, float texH) {
        float u1 = uv[0] / texW, v1 = uv[1] / texH;
        float u2 = (uv[0] + uvSize[0]) / texW, v2 = (uv[1] + uvSize[1]) / texH;
        return switch (uvRotation) {
            case 90 -> new float[]{u1, v1, u1, v2, u2, v2, u2, v1};
            case 180 -> new float[]{u1, v2, u2, v2, u2, v1, u1, v1};
            case 270 -> new float[]{u2, v2, u2, v1, u1, v1, u1, v2};
            default -> new float[]{u2, v1, u1, v1, u1, v2, u2, v2};
        };
    }

    public float[] getUvSize() {
        return uvSize;
    }
}
