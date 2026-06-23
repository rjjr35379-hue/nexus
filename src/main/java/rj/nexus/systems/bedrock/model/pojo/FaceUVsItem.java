package rj.nexus.systems.bedrock.model.pojo;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.Direction;

public class FaceUVsItem {
    @SerializedName("north")
    private FaceItem north;
    @SerializedName("south")
    private FaceItem south;
    @SerializedName("east")
    private FaceItem east;
    @SerializedName("west")
    private FaceItem west;
    @SerializedName("up")
    private FaceItem up;
    @SerializedName("down")
    private FaceItem down;

    public FaceItem getFace(Direction d) {
        return switch (d) {
            case NORTH -> north;
            case SOUTH -> south;
            case EAST -> east;
            case WEST -> west;
            case UP -> up;
            case DOWN -> down;
        };
    }
}
