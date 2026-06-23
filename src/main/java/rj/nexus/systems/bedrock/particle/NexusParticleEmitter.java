package rj.nexus.systems.bedrock.particle;

import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import rj.nexus.systems.bedrock.particle.component.ParticleComponentHolder;
import rj.nexus.systems.bedrock.particle.component.ParticleComponentMap;
import rj.nexus.systems.bedrock.particle.component.ParticleComponents;
import rj.nexus.systems.bedrock.particle.component.misc.EventSubpart;
import rj.nexus.systems.bedrock.particle.curve.Curve;
import rj.nexus.systems.bedrock.particle.io.ParticleEffectFile;
import rj.nexus.systems.bedrock.particle.util.ShapeUtil;

import java.util.List;
import java.util.Map;

public class NexusParticleEmitter implements ParticleComponentHolder {

    final List<NexusParticleInstance> particles = new ObjectArrayList<>();

    private final ParticleEffectFile effectFile;
    private final ParticleComponentMap componentMap = new ParticleComponentMap();
    private final MolangRuntime runtime;
    private final ClientLevel level;

    private double x, y, z;
    private float xRot, yRot;
    private @Nullable Entity attachedEntity;

    private int age = 0;
    private boolean canEmit = true;
    private boolean dead    = false;

    public NexusParticleEmitter(ParticleEffectFile file, ClientLevel level,
                                double x, double y, double z,
                                float xRot, float yRot) throws MolangRuntimeException {
        this.effectFile = file;
        this.level      = level;
        this.x = x; this.y = y; this.z = z;
        this.xRot = xRot; this.yRot = yRot;

        this.initComponents(file.effect.components);
        this.runtime = MolangRuntime.runtime().create();

        var edit = runtime.edit();
        edit.setVariable("emitter_random_1", (float) Math.random());
        edit.setVariable("emitter_random_2", (float) Math.random());
        edit.setVariable("emitter_random_3", (float) Math.random());
        edit.setVariable("emitter_random_4", (float) Math.random());

        var init = get(ParticleComponents.EMITTER_INITIALIZATION);
        if (init != null && init.creationExpression != null)
            runtime.resolve(init.creationExpression);

        initCurves();
        updateRuntimeEmitter();

        var ltEvents = get(ParticleComponents.EMITTER_LIFETIME_EVENTS);
        if (ltEvents != null) for (var ev : ltEvents.creationEvent) runEvent(ev, null);
    }

    public void attachToEntity(Entity entity) { this.attachedEntity = entity; }
    public boolean isDead() { return dead; }

    public void tick() {
        if (attachedEntity != null) {
            if (!attachedEntity.isAlive()) { destroy(); return; }
            x = attachedEntity.getX(); y = attachedEntity.getY(); z = attachedEntity.getZ();
            xRot = attachedEntity.getXRot(); yRot = attachedEntity.getYRot();
        }

        var init = get(ParticleComponents.EMITTER_INITIALIZATION);
        if (init != null && init.perUpdateExpression != null) {
            try { runtime.resolve(init.perUpdateExpression); }
            catch (MolangRuntimeException e) { throw new RuntimeException(e); }
        }

        for (int i = particles.size() - 1; i >= 0; i--) {
            var p = particles.get(i);
            p.pushRuntimeVars(runtime);
            p.tick();
            if (p.isRemoved()) particles.remove(i);
        }

        try { emitParticles(); }
        catch (MolangRuntimeException e) { throw new RuntimeException(e); }

        var ltEvents = get(ParticleComponents.EMITTER_LIFETIME_EVENTS);
        if (ltEvents != null && ltEvents.timeline != null) {
            var evs = ltEvents.timeline.getEventsInRange(age * NexusParticles.TIME_SCALE, NexusParticles.TIME_SCALE);
            if (evs != null) for (var ev : evs) runEvent(ev, null);
        }

        age++;
        try { updateRuntimeEmitter(); }
        catch (MolangRuntimeException e) { throw new RuntimeException(e); }
    }

    private void emitParticles() throws MolangRuntimeException {
        if (!canEmit || particles.size() >= 20_000) return;

        var steady  = get(ParticleComponents.EMITTER_RATE_STEADY);
        var instant = get(ParticleComponents.EMITTER_RATE_INSTANT);
        var manual  = get(ParticleComponents.EMITTER_RATE_MANUAL);

        if (steady != null) {
            int   max     = (int) runtime.resolve(steady.maxParticles);
            float rate    = (float) runtime.resolve(steady.spawnRate);
            float perTick = rate * NexusParticles.TIME_SCALE;
            int toSpawn;
            if (perTick >= 1f) {
                toSpawn = (int) perTick;
            } else {
                int interval = Math.round(1f / Math.max(perTick, 0.001f));
                toSpawn = (interval > 0 && age % interval == 0) ? 1 : 0;
            }
            for (int i = 0; i < toSpawn && particles.size() < max; i++) spawnParticle();

        } else if (instant != null && age == 0) {
            int count = (int) runtime.resolve(instant.numParticles);
            for (int i = 0; i < count; i++) spawnParticle();

        } else if (manual != null) {
            int max = (int) runtime.resolve(manual.maxParticles);
            while (particles.size() < max) spawnParticle();
        }
    }

    private void spawnParticle() throws MolangRuntimeException {
        var data = ShapeUtil.initialParticleData(runtime, this);
        var offset = data.offset().toVector3f()
                .rotateX(xRot * Mth.DEG_TO_RAD)
                .rotateY(yRot * Mth.DEG_TO_RAD);
        var dir = data.direction().toVector3f()
                .rotateX(xRot * Mth.DEG_TO_RAD)
                .rotateY(yRot * Mth.DEG_TO_RAD);

        double px = x + offset.x, py = y + offset.y, pz = z + offset.z;

        var initSpeed = get(ParticleComponents.PARTICLE_INITIAL_SPEED);
        float sx = 0, sy = 0, sz = 0;
        if (initSpeed != null && !initSpeed.value().isEmpty()) {
            sx = runtime.resolve(initSpeed.value().get(0));
            sy = initSpeed.value().size() > 1 ? runtime.resolve(initSpeed.value().get(1)) : sx;
            sz = initSpeed.value().size() > 2 ? runtime.resolve(initSpeed.value().get(2)) : sx;
        }

        float roll = 0, rollRate = 0;
        var initSpin = get(ParticleComponents.PARTICLE_INITIAL_SPIN);
        if (initSpin != null) {
            roll     = runtime.resolve(initSpin.rotation);
            rollRate = runtime.resolve(initSpin.rotationRate);
        }

        try {
            NexusParticleInstance inst = new NexusParticleInstance(
                    this, level, px, py, pz, dir.x * sx, dir.y * sy, dir.z * sz, roll, rollRate);
            particles.add(inst);
            NexusParticleManager.addInstance(inst);
        } catch (Exception ignored) {}
    }

    private void updateRuntimeEmitter() throws MolangRuntimeException {
        float lifetime = Float.MAX_VALUE;
        boolean shouldDie = false;

        var looping = get(ParticleComponents.EMITTER_LIFETIME_LOOPING);
        var once    = get(ParticleComponents.EMITTER_LIFETIME_ONCE);

        if (looping != null) {
            float active = runtime.resolve(looping.activeTime);
            float sleep  = runtime.resolve(looping.sleepTime);
            lifetime     = active;
            float t      = age * NexusParticles.TIME_SCALE;
            float cycle  = active + sleep;
            float phase  = cycle > 0 ? t % cycle : t;
            canEmit      = phase < active;
        }
        if (once != null) {
            float active = runtime.resolve(once.activeTime);
            lifetime     = active;
            float t      = age * NexusParticles.TIME_SCALE;
            canEmit      = t < active;
            if (!canEmit && particles.isEmpty()) shouldDie = true;
        }

        var edit = runtime.edit();
        edit.setVariable("emitter_age",      age * NexusParticles.TIME_SCALE);
        edit.setVariable("emitter_lifetime", lifetime == Float.MAX_VALUE ? 0f : lifetime);

        if (shouldDie) destroy();
    }

    private void initCurves() {
        var edit = runtime.edit();
        for (Map.Entry<String, Curve> entry : effectFile.effect.curves.entrySet()) {
            String key     = entry.getKey();
            Curve  val     = entry.getValue();
            int    dot     = key.indexOf('.');
            String varName = dot >= 0 ? key.substring(dot + 1) : key;
            edit.setVariable(varName, val::evaluate);
        }
    }

    public void runEvent(String event, @Nullable NexusParticleInstance source) {
        Vec3 pos = source != null
                ? new Vec3(source.getX(), source.getY(), source.getZ())
                : new Vec3(x, y, z);

        var entry = effectFile.effect.events.get(event);
        if (entry == null) return;

        for (EventSubpart subpart : entry.collect()) {
            if (subpart.soundEffect != null) {
                level.playLocalSound(pos.x, pos.y, pos.z,
                        SoundEvent.createVariableRangeEvent(Identifier.parse(subpart.soundEffect.eventName())),
                        SoundSource.AMBIENT, 1f, 1f, false);
            }
            if (subpart.particleEffect != null) {
                Identifier pfx = subpart.particleEffect.effect();
                if (attachedEntity != null) NexusParticles.emit(pfx, attachedEntity);
                else NexusParticles.emit(pfx, level, pos.x, pos.y, pos.z, 0, 0);
            }
        }
    }

    public void destroy() {
        if (dead) return;
        var ltEvents = get(ParticleComponents.EMITTER_LIFETIME_EVENTS);
        if (ltEvents != null) for (var ev : ltEvents.expirationEvent) runEvent(ev, null);
        dead = true;
    }

    public Vec3 getPos()                  { return new Vec3(x, y, z); }
    public ClientLevel level()            { return level; }
    public ParticleEffectFile effectFile() { return effectFile; }
    public MolangRuntime runtime()        { return runtime; }

    @Override
    public ParticleComponentMap components() { return componentMap; }

    public record InitialParticleData(Vec3 offset, Vec3 direction) {
        public static final InitialParticleData ZERO = new InitialParticleData(Vec3.ZERO, Vec3.ZERO);
        public static InitialParticleData of(Vec3 o, Vec3 d) { return new InitialParticleData(o, d); }
    }
}