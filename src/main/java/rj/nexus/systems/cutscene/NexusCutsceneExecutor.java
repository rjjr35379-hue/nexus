package rj.nexus.systems.cutscene;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;


public final class NexusCutsceneExecutor {

    private final NexusCutsceneData data;
    private int currentTick = 0;

    public NexusCutsceneExecutor(final NexusCutsceneData data) {
        this.data = data;
    }

    private static float applyEasing(final NexusEasingType type, final float t) {
        return switch (type) {
            case LINEAR -> t;
            case EASE_IN -> t * t;
            case EASE_OUT -> 1f - (1f - t) * (1f - t);
            case EASE_IN_OUT -> t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f;
        };
    }

    private static float lerpAngle(final float a, final float b, final float t) {
        return a + Mth.wrapDegrees(b - a) * t;
    }

    private static Vec3 catmullRom(final Vec3 p0, final Vec3 p1, final Vec3 p2,
                                   final Vec3 p3, final float t) {
        final float t2 = t * t, t3 = t2 * t;
        return new Vec3(
                cr(p0.x, p1.x, p2.x, p3.x, t, t2, t3),
                cr(p0.y, p1.y, p2.y, p3.y, t, t2, t3),
                cr(p0.z, p1.z, p2.z, p3.z, t, t2, t3)
        );
    }

    private static double cr(final double a, final double b, final double c, final double d,
                             final float t, final float t2, final float t3) {
        return 0.5 * ((2 * b) + (-a + c) * t + (2 * a - 5 * b + 4 * c - d) * t2 + (-a + 3 * b - 3 * c + d) * t3);
    }

    public boolean tick(final NexusClientCameraEntity camera) {
        camera.xo = camera.getX();
        camera.yo = camera.getY();
        camera.zo = camera.getZ();
        if (this.currentTick >= this.data.getDuration()) return true;
        final Vec3 pos = this.getCameraPos(this.currentTick, 0f);
        camera.setPos(pos);
        final float[] rot = this.getCameraRot(this.currentTick, 0f);
        camera.yRotO = camera.getYRot();
        camera.xRotO = camera.getXRot();
        camera.setYRot(rot[0]);
        camera.setXRot(rot[1]);
        this.currentTick++;
        return false;
    }

    public Vec3 getCameraPos(final int tick, final float partial) {
        final List<NexusCameraPos> pts = this.data.getPositions();
        if (pts.isEmpty()) return Vec3.ZERO;
        if (pts.size() == 1) return pts.getFirst().pos();
        final float t = applyEasing(this.data.getTimeEasing(),
                Mth.clamp((tick + partial) / (float) this.data.getDuration(), 0f, 1f));
        final int segCount = pts.size() - 1;
        final float segF = t * segCount;
        final int seg = Mth.clamp((int) segF, 0, segCount - 1);
        final float local = segF - seg;
        if (this.data.getCurveType() == NexusCurveType.CATMULLROM && pts.size() >= 4) {
            return catmullRom(
                    pts.get(Math.max(0, seg - 1)).pos(),
                    pts.get(seg).pos(),
                    pts.get(Math.min(pts.size() - 1, seg + 1)).pos(),
                    pts.get(Math.min(pts.size() - 1, seg + 2)).pos(),
                    local);
        }
        return pts.get(seg).lerpPos(pts.get(seg + 1), local);
    }

    public float[] getCameraRot(final int tick, final float partial) {
        final List<NexusCameraPos> pts = this.data.getPositions();
        if (pts.isEmpty()) return new float[]{0f, 0f};
        if (pts.size() == 1) return new float[]{pts.getFirst().yaw(), pts.getFirst().pitch()};
        final float t = applyEasing(this.data.getLookEasing(),
                Mth.clamp((tick + partial) / (float) this.data.getDuration(), 0f, 1f));
        final float segF = t * (pts.size() - 1);
        final int seg = Mth.clamp((int) segF, 0, pts.size() - 2);
        final float local = segF - seg;
        return new float[]{
                lerpAngle(pts.get(seg).yaw(), pts.get(seg + 1).yaw(), local),
                Mth.lerp(local, pts.get(seg).pitch(), pts.get(seg + 1).pitch())
        };
    }

    public float getCameraRoll(final int tick, final float partial) {
        final List<NexusCameraPos> pts = this.data.getPositions();
        if (pts.size() < 2) return pts.isEmpty() ? 0f : pts.getFirst().roll();
        final float t = Mth.clamp((tick + partial) / (float) this.data.getDuration(), 0f, 1f);
        final float segF = t * (pts.size() - 1);
        final int seg = Mth.clamp((int) segF, 0, pts.size() - 2);
        return Mth.lerp(segF - seg, pts.get(seg).roll(), pts.get(seg + 1).roll());
    }

    public boolean hasEnded() {
        return this.currentTick >= this.data.getDuration();
    }

    public int getTick() {
        return this.currentTick;
    }

    public NexusCutsceneData getData() {
        return this.data;
    }
}
