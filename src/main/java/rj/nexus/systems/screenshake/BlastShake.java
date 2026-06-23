package rj.nexus.systems.screenshake;


import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class BlastShake implements ShakeInstance {
    private final ShakeParams params;
    private final Vec3 origin;
    private final double maxDist;

    public BlastShake(ShakeParams params, Vec3 origin, double maxDist) {
        this.params  = params;
        this.origin  = origin;
        this.maxDist = maxDist;
    }

    @Override
    public void apply(PoseStack stack, int elapsed, float partial) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Camera cam = mc.gameRenderer.getMainCamera();
        Vec3 eye   = cam.position();
        double dist = eye.distanceTo(origin);
        if (dist > maxDist) return;

        double nd       = dist / maxDist;
        float distFall  = (float) Math.exp(-nd * nd * 3.0);
        float env       = params.envelope(elapsed + partial);
        if (env * distFall <= 0.001f) return;

        float t = Mth.clamp(elapsed + partial, 0f, params.duration());
        float phase = t / params.duration() * (float)(Math.PI * 2) * params.frequency;
        float dampedWave = (float)(Math.sin(phase) * Math.exp(-phase * 0.25));

        Vec3 look    = mc.player.getLookAngle();
        Vec3 globalUp = new Vec3(0, 1, 0);
        Vec3 right    = globalUp.cross(look).normalize();
        Vec3 camUp    = look.cross(right).normalize();

        Vec3 toOrigin = origin.subtract(eye);
        double dot = toOrigin.dot(look);
        Vec3 onPlane = toOrigin.subtract(look.scale(dot));
        if (onPlane.lengthSqr() < 1e-8) onPlane = camUp;
        onPlane = onPlane.normalize();

        double yaw = Math.atan2(onPlane.dot(right), onPlane.dot(camUp));
        if (Double.isNaN(yaw)) yaw = 0;

        Vector3f axis = new Vector3f(0f, 1f, 0f).rotateZ((float)(yaw + Math.PI * 0.5));
        float angleDeg = params.amplitude * env * distFall * dampedWave;

        stack.mulPose(new Quaternionf(new AxisAngle4f(
                (float) Math.toRadians(angleDeg), axis.x, axis.y, axis.z
        )));
    }

    @Override
    public boolean expired(int elapsed) {
        return elapsed > params.duration();
    }
}