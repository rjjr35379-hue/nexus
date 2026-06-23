package rj.nexus.systems.cutscene;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;


public final class NexusCutsceneData {

    public static final StreamCodec<FriendlyByteBuf, NexusCutsceneData> STREAM_CODEC =
            StreamCodec.of(NexusCutsceneData::encode, NexusCutsceneData::decode);
    private final List<NexusCameraPos> positions = new ArrayList<>();
    private int duration = 60;
    private NexusCurveType curveType = NexusCurveType.CATMULLROM;
    private NexusEasingType timeEasing = NexusEasingType.LINEAR;
    private NexusEasingType lookEasing = NexusEasingType.LINEAR;
    private StopMode stopMode = StopMode.AUTOMATIC;
    private @Nullable NexusCutsceneData nextCutscene = null;
    private NexusCutsceneData() {
    }

    public static NexusCutsceneData create() {
        return new NexusCutsceneData();
    }

    private static void encode(final FriendlyByteBuf buf, final NexusCutsceneData d) {
        buf.writeInt(d.duration);
        buf.writeEnum(d.curveType);
        buf.writeEnum(d.timeEasing);
        buf.writeEnum(d.lookEasing);
        buf.writeEnum(d.stopMode);
        buf.writeInt(d.positions.size());
        for (final NexusCameraPos p : d.positions) {
            NexusCameraPos.STREAM_CODEC.encode(buf, p);
        }
        final boolean hasNext = d.nextCutscene != null;
        buf.writeBoolean(hasNext);
        if (hasNext) encode(buf, d.nextCutscene);
    }

    private static NexusCutsceneData decode(final FriendlyByteBuf buf) {
        final NexusCutsceneData d = new NexusCutsceneData();
        d.duration = buf.readInt();
        d.curveType = buf.readEnum(NexusCurveType.class);
        d.timeEasing = buf.readEnum(NexusEasingType.class);
        d.lookEasing = buf.readEnum(NexusEasingType.class);
        d.stopMode = buf.readEnum(StopMode.class);
        final int size = buf.readInt();
        for (int i = 0; i < size; i++) d.positions.add(NexusCameraPos.STREAM_CODEC.decode(buf));
        if (buf.readBoolean()) d.nextCutscene = decode(buf);
        return d;
    }

    public NexusCutsceneData add(final NexusCameraPos p) {
        this.positions.add(p);
        return this;
    }

    public NexusCutsceneData duration(final int ticks) {
        this.duration = ticks;
        return this;
    }

    public NexusCutsceneData curve(final NexusCurveType t) {
        this.curveType = t;
        return this;
    }

    public NexusCutsceneData timeEasing(final NexusEasingType t) {
        this.timeEasing = t;
        return this;
    }

    public NexusCutsceneData lookEasing(final NexusEasingType t) {
        this.lookEasing = t;
        return this;
    }

    public NexusCutsceneData stopMode(final StopMode m) {
        this.stopMode = m;
        return this;
    }

    public NexusCutsceneData next(final NexusCutsceneData n) {
        this.nextCutscene = n;
        return this;
    }

    public List<NexusCameraPos> getPositions() {
        return this.positions;
    }

    public int getDuration() {
        return this.duration;
    }

    public NexusCurveType getCurveType() {
        return this.curveType;
    }

    public NexusEasingType getTimeEasing() {
        return this.timeEasing;
    }

    public NexusEasingType getLookEasing() {
        return this.lookEasing;
    }

    public StopMode getStopMode() {
        return this.stopMode;
    }

    public @Nullable NexusCutsceneData getNext() {
        return this.nextCutscene;
    }

    public enum StopMode {AUTOMATIC, PLAYER, UNSTOPPABLE}
}
