package rj.nexus.systems.bedrock.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import rj.nexus.systems.bedrock.particle.component.ParticleComponents;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleAppearanceBillboard;
import rj.nexus.systems.bedrock.particle.component.particle.ParticleAppearanceBillboard.CameraMode;

import java.util.List;

public class NexusParticleInstance extends Particle {

    private static final double MAX_COLLISION_VEL_SQ = Mth.square(100.0);

    private final NexusParticleEmitter parent;
    private final NexusParticleRenderType.Material material;

    private final double spawnX, spawnY, spawnZ;

    private float maxLifetime = 1f;
    private MolangExpression lifetimeExpression;

    private boolean particleRemoved = false;
    private int particleAge = 0;

    private float roll  = 0f;
    private float rolld = 0f;

    private final Vector3f prevPos3f = new Vector3f();
    private final Vector3f pos3f;
    private final Vector3f accel = new Vector3f();
    private final Vector3f vel   = new Vector3f();

    private final boolean physics;
    private final boolean dynamicMotion;
    private final boolean parametricMotion;

    private float bbRadius = 0.2f;

    private final float rnd1 = (float) Math.random();
    private final float rnd2 = (float) Math.random();
    private final float rnd3 = (float) Math.random();
    private final float rnd4 = (float) Math.random();

    private Identifier texture;
    private int lastFrame = -1;

    private int tintArgb = 0xFFFFFFFF;
    private float sizeX  = 0.1f, sizeY = 0.1f;

    public NexusParticleInstance(NexusParticleEmitter parent, ClientLevel level,
                                 double px, double py, double pz,
                                 float vx, float vy, float vz,
                                 float roll, float rollRate) throws MolangRuntimeException {
        super(level, px, py, pz);
        this.parent  = parent;
        this.spawnX  = px; this.spawnY = py; this.spawnZ = pz;
        this.pos3f   = new Vector3f((float) px, (float) py, (float) pz);
        this.vel.set(vx, vy, vz);
        this.roll    = roll;
        this.rolld   = rollRate;

        this.physics          = parent.has(ParticleComponents.PARTICLE_MOTION_COLLISION);
        this.dynamicMotion    = parent.has(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
        this.parametricMotion = parent.has(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);
        if (physics) bbRadius = parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION).collisionRadius;

        String matStr = parent.effectFile().effect.description.material;
        this.material = NexusParticleRenderType.Material.fromString(matStr);

        pushRuntimeVars(parent.runtime());

        var lt = parent.get(ParticleComponents.PARTICLE_LIFETIME_EXPRESSION);
        if (lt != null) {
            maxLifetime        = parent.runtime().resolve(lt.maxLifetime);
            lifetimeExpression = lt.expirationExpression;
        }

        texture = resolveTexture();
        updateSize();
        updateTint();

        var ltEv = parent.get(ParticleComponents.PARTICLE_LIFETIME_EVENTS);
        if (ltEv != null) for (var ev : ltEv.creationEvent) parent.runEvent(ev, this);
    }

    public void pushRuntimeVars(MolangRuntime rt) {
        var edit = rt.edit();
        edit.setVariable("particle_random_1", rnd1);
        edit.setVariable("particle_random_2", rnd2);
        edit.setVariable("particle_random_3", rnd3);
        edit.setVariable("particle_random_4", rnd4);
        edit.setVariable("particle_age",      scaledAge());
        edit.setVariable("particle_lifetime", maxLifetime);
    }

    private float scaledAge() { return particleAge * NexusParticles.TIME_SCALE; }

    @Override
    public void tick() {
        try {
            prevPos3f.set(pos3f);
            pushRuntimeVars(parent.runtime());

            if (!isAliveCheck()) { markRemoved(); return; }

            if (parametricMotion) tickParametric();
            if (dynamicMotion)    tickDynamic();

            var ltEv = parent.get(ParticleComponents.PARTICLE_LIFETIME_EVENTS);
            if (ltEv != null && ltEv.timeline != null) {
                var evs = ltEv.timeline.getEventsInRange(scaledAge(), NexusParticles.TIME_SCALE);
                if (evs != null) for (var ev : evs) parent.runEvent(ev, this);
            }

            updateFlipbook();
            updateSize();
            updateTint();

            xo = x; yo = y; zo = z;
            x = pos3f.x; y = pos3f.y; z = pos3f.z;

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private boolean isAliveCheck() throws MolangRuntimeException {
        var inBlocks  = parent.get(ParticleComponents.PARTICLE_EXPIRE_IF_IN_BLOCKS);
        var notBlocks = parent.get(ParticleComponents.PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS);
        if (inBlocks != null || notBlocks != null) {
            BlockState bs = level.getBlockState(BlockPos.containing(pos3f.x, pos3f.y, pos3f.z));
            if (inBlocks != null)
                for (var b : inBlocks.value()) if (b == bs.getBlock()) return false;
            if (notBlocks != null) {
                boolean found = false;
                for (var b : notBlocks.value()) if (b == bs.getBlock()) { found = true; break; }
                if (!found) return false;
            }
        }
        if (lifetimeExpression != null && parent.runtime().resolve(lifetimeExpression) == 1f)
            return false;
        particleAge++;
        return (particleAge - 1) * NexusParticles.TIME_SCALE < maxLifetime;
    }

    private void tickParametric() throws MolangRuntimeException {
        var para = parent.get(ParticleComponents.PARTICLE_MOTION_PARAMETRIC);
        Vec3 ep = parent.getPos();
        float ox = parent.runtime().resolve(para.relativePosition[0]);
        float oy = parent.runtime().resolve(para.relativePosition[1]);
        float oz = para.relativePosition.length > 2 ? parent.runtime().resolve(para.relativePosition[2]) : 0f;
        pos3f.set((float)(ep.x + ox), (float)(ep.y + oy), (float)(ep.z + oz));
        if (para.direction.length > 0) {
            vel.x = parent.runtime().resolve(para.direction[0]);
            vel.y = parent.runtime().resolve(para.direction[1]);
            vel.z = para.direction.length > 2 ? parent.runtime().resolve(para.direction[2]) : 0f;
        }
        roll = parent.runtime().resolve(para.rotation);
    }

    private void tickDynamic() throws MolangRuntimeException {
        var dyn = parent.get(ParticleComponents.PARTICLE_MOTION_DYNAMIC);
        float ax = parent.runtime().resolve(dyn.linearAcceleration[0]);
        float ay = parent.runtime().resolve(dyn.linearAcceleration[1]);
        float az = dyn.linearAcceleration.length > 2 ? parent.runtime().resolve(dyn.linearAcceleration[2]) : 0f;
        float drag = parent.runtime().resolve(dyn.linearDragCoefficient);
        float dt = NexusParticles.TIME_SCALE;
        ax -= vel.x * drag; ay -= vel.y * drag; az -= vel.z * drag;
        vel.add(ax * dt, ay * dt, az * dt);
        doPhysicsMove(vel.x * dt, vel.y * dt, vel.z * dt);

        float rotAccel = parent.runtime().resolve(dyn.rotationAcceleration);
        float rotDrag  = parent.runtime().resolve(dyn.rotationDragCoefficient);
        rotAccel -= rotDrag * rolld;
        rolld += rotAccel * dt;
        roll  += rolld    * dt;
    }

    private void doPhysicsMove(double dx, double dy, double dz) {
        if (physics && (dx != 0 || dy != 0 || dz != 0)
                && dx*dx + dy*dy + dz*dz < MAX_COLLISION_VEL_SQ) {
            AABB bb = new AABB(pos3f.x-bbRadius, pos3f.y, pos3f.z-bbRadius,
                    pos3f.x+bbRadius, pos3f.y+bbRadius, pos3f.z+bbRadius);
            Vec3 corrected = Entity.collideBoundingBox(null, new Vec3(dx, dy, dz), bb, level, List.of());
            boolean xc = corrected.x != dx, yc = corrected.y != dy, zc = corrected.z != dz;
            if (xc || yc || zc) {
                var mc = parent.get(ParticleComponents.PARTICLE_MOTION_COLLISION);
                for (var ev : mc.events) {
                    if (ev.minSpeed != 0 && ev.minSpeed > vel.length()) continue;
                    parent.runEvent(ev.event, this);
                }
                if (mc.expireOnContact) { markRemoved(); return; }
                if (xc) vel.x = 0; if (yc) vel.y = 0; if (zc) vel.z = 0;
                if (yc) {
                    vel.y *= -mc.coefficientOfRestitution;
                    float s = NexusParticles.TIME_SCALE;
                    vel.x = Mth.sign(vel.x) * Math.max(0, Math.abs(vel.x) - mc.collisionDrag * s);
                    vel.z = Mth.sign(vel.z) * Math.max(0, Math.abs(vel.z) - mc.collisionDrag * s);
                }
                dx = corrected.x; dy = corrected.y; dz = corrected.z;
            }
        }
        pos3f.add((float) dx, (float) dy, (float) dz);
    }

    private Identifier resolveTexture() throws MolangRuntimeException {
        var bill = parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (bill == null || bill.uv == null) return NexusParticleTextures.getTexture(parent.effectFile(), 0, 0);
        var uv = bill.uv;
        if (uv.flipbook != null) return NexusParticleTextures.getTexture(parent.effectFile(), 0, 0);
        if (uv.uv != null) {
            float u = parent.runtime().resolve(uv.uv[0]);
            float v = parent.runtime().resolve(uv.uv[1]);
            float uw = parent.runtime().resolve(uv.uvSize[0]);
            float uh = parent.runtime().resolve(uv.uvSize[1]);
            return NexusParticleTextures.getOrCreateUVSlice(parent.effectFile(), u, v, uw, uh);
        }
        return NexusParticleTextures.getTexture(parent.effectFile(), (int)(rnd1 * 10), 0);
    }

    private void updateFlipbook() throws MolangRuntimeException {
        var bill = parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (bill == null || bill.uv == null || bill.uv.flipbook == null) return;
        var flip = bill.uv.flipbook;
        int max = Math.max(1, (int) parent.runtime().resolve(flip.max_frame));
        int frame;
        if (flip.stretch_to_lifetime) {
            frame = (int)(Math.min(scaledAge() / Math.max(maxLifetime, 0.001f), 1.0) * (max - 1));
        } else {
            frame = (int)(scaledAge() * flip.frames_per_second);
            frame = flip.loop ? frame % max : Math.min(frame, max - 1);
        }
        if (frame != lastFrame) {
            lastFrame = frame;
            texture = NexusParticleTextures.getTexture(parent.effectFile(), (int)(rnd1 * 10), frame);
        }
    }

    private void updateSize() throws MolangRuntimeException {
        var bill = parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        if (bill == null || bill.size.isEmpty()) return;
        float sx = parent.runtime().resolve(bill.size.get(0));
        float sy = bill.size.size() > 1 ? parent.runtime().resolve(bill.size.get(1)) : sx;
        sizeX = Float.isNaN(sx) || Float.isInfinite(sx) ? 0 : sx;
        sizeY = Float.isNaN(sy) || Float.isInfinite(sy) ? 0 : sy;
    }

    private void updateTint() throws MolangRuntimeException {
        var tinting = parent.get(ParticleComponents.PARTICLE_APPEARANCE_TINTING);
        if (tinting == null) return;
        boolean grayscale = material == NexusParticleRenderType.Material.ALPHA_GRAYSCALE;
        if (grayscale) {
            tintArgb = tinting.isRGBA() ? tinting.rgba(parent.runtime()) : tinting.color.color(parent.runtime());
        } else {
            tintArgb = tinting.isRGBA() ? tinting.rgba(parent.runtime()) : tinting.color.color(parent.runtime());
        }
    }

    public void renderManual(VertexConsumer buffer, Camera camera, float partialTick) {
        if (texture == null || sizeX == 0 || sizeY == 0) return;

        int light = parent.has(ParticleComponents.PARTICLE_APPEARANCE_LIGHTING)
                ? getLightAtPos()
                : 0xF000F0;

        Vec3 camPos = camera.position();
        float px = (float)(Mth.lerp(partialTick, xo, x) - camPos.x());
        float py = (float)(Mth.lerp(partialTick, yo, y) - camPos.y());
        float pz = (float)(Mth.lerp(partialTick, zo, z) - camPos.z());

        var bill = parent.get(ParticleComponents.PARTICLE_APPEARANCE_BILLBOARD);
        CameraMode mode = bill != null ? bill.cameraMode : CameraMode.ROTATE_XYZ;

        Quaternionf camRot = camera.rotation();
        Vector3f camRight = new Vector3f(1, 0, 0).rotate(camRot);
        Vector3f camUp    = new Vector3f(0, 1, 0).rotate(camRot);

        float cos = Mth.cos(roll);
        float sin = Mth.sin(roll);

        Vector3f right, up;
        switch (mode) {
            case ROTATE_XYZ, LOOKAT_XYZ -> {
                right = new Vector3f(
                        camRight.x * cos + camUp.x * sin,
                        camRight.y * cos + camUp.y * sin,
                        camRight.z * cos + camUp.z * sin
                ).mul(sizeX);
                up = new Vector3f(
                        -camRight.x * sin + camUp.x * cos,
                        -camRight.y * sin + camUp.y * cos,
                        -camRight.z * sin + camUp.z * cos
                ).mul(sizeY);
            }
            case ROTATE_Y, LOOKAT_Y -> {
                float yaw = (float) Math.atan2(camPos.x - x, camPos.z - z);
                float cy = Mth.cos(yaw), sy2 = Mth.sin(yaw);
                right = new Vector3f(
                        (cy * cos + sy2 * sin) * sizeX,
                        sin * sizeX,
                        (-sy2 * cos + cy * sin) * sizeX
                );
                up = new Vector3f(0, cos * sizeY, 0);
            }
            case DIRECTION_X -> {
                Vector3f dir = velDir();
                right = new Vector3f(dir).mul(sizeX);
                up    = new Vector3f(0, 1, 0).mul(sizeY);
            }
            case DIRECTION_Y -> {
                Vector3f dir = velDir();
                right = new Vector3f(1, 0, 0).mul(sizeX);
                up    = new Vector3f(dir).mul(sizeY);
            }
            case DIRECTION_Z -> {
                Vector3f dir = velDir();
                Vector3f perp = new Vector3f(camRight).cross(dir).normalize();
                right = new Vector3f(perp).mul(sizeX);
                up    = new Vector3f(dir).mul(sizeY);
            }
            case EMITTER_TRANSFORM_XY -> {
                right = new Vector3f(cos * sizeX, sin * sizeX, 0);
                up    = new Vector3f(-sin * sizeY, cos * sizeY, 0);
            }
            case EMITTER_TRANSFORM_XZ -> {
                right = new Vector3f(cos * sizeX, 0, sin * sizeX);
                up    = new Vector3f(-sin * sizeY, 0, cos * sizeY);
            }
            case EMITTER_TRANSFORM_YZ -> {
                right = new Vector3f(0, cos * sizeX, sin * sizeX);
                up    = new Vector3f(0, -sin * sizeY, cos * sizeY);
            }
            default -> {
                right = new Vector3f(camRight).mul(sizeX);
                up    = new Vector3f(camUp).mul(sizeY);
            }
        }

        int a = (tintArgb >> 24) & 0xFF;
        int r = (tintArgb >> 16) & 0xFF;
        int g = (tintArgb >> 8)  & 0xFF;
        int b =  tintArgb        & 0xFF;

        if (material == NexusParticleRenderType.Material.ADD) a = 255;

        buffer.addVertex(px - right.x - up.x, py - right.y - up.y, pz - right.z - up.z).setUv(0, 1).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(px - right.x + up.x, py - right.y + up.y, pz - right.z + up.z).setUv(0, 0).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(px + right.x + up.x, py + right.y + up.y, pz + right.z + up.z).setUv(1, 0).setColor(r, g, b, a).setLight(light);
        buffer.addVertex(px + right.x - up.x, py + right.y - up.y, pz + right.z - up.z).setUv(1, 1).setColor(r, g, b, a).setLight(light);
    }

    private Vector3f velDir() {
        float len = vel.length();
        if (len < 1e-5f) return new Vector3f(0, 1, 0);
        return new Vector3f(vel).mul(1f / len);
    }

    private int getLightAtPos() {
        double lx = Mth.lerp(0.5, xo, x);
        double ly = Mth.lerp(0.5, yo, y);
        double lz = Mth.lerp(0.5, zo, z);
        BlockPos pos = BlockPos.containing(lx, ly, lz);
        return level.hasChunkAt(pos) ? LevelRenderer.getLightCoords(level, pos) : 0xF000F0;
    }

    @Override
    public ParticleRenderType getGroup() { return ParticleRenderType.NO_RENDER; }

    public void markRemoved() {
        if (!particleRemoved) {
            var ltEv = parent.get(ParticleComponents.PARTICLE_LIFETIME_EVENTS);
            if (ltEv != null) for (var ev : ltEv.expirationEvent) parent.runEvent(ev, this);
        }
        particleRemoved = true;
        this.removed    = true;
    }

    public boolean isRemoved()   { return particleRemoved; }
    public Identifier getTexture() { return texture; }
    public NexusParticleRenderType.Material getMaterial() { return material; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
}