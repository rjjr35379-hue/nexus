package rj.nexus.systems.bedrock.particle.component;

import net.minecraft.resources.Identifier;
import rj.nexus.systems.bedrock.particle.component.emitter.*;
import rj.nexus.systems.bedrock.particle.component.particle.*;

@SuppressWarnings("unused")
public class ParticleComponents {
    // Emitter
    public static final ParticleComponentType<EmitterInitialization>     EMITTER_INITIALIZATION    = reg("emitter_initialization",    EmitterInitialization.class);
    public static final ParticleComponentType<EmitterLifetimeLooping>    EMITTER_LIFETIME_LOOPING  = reg("emitter_lifetime_looping",   EmitterLifetimeLooping.class);
    public static final ParticleComponentType<EmitterLifetimeEvents>     EMITTER_LIFETIME_EVENTS   = reg("emitter_lifetime_events",    EmitterLifetimeEvents.class);
    public static final ParticleComponentType<EmitterLifetimeExpression> EMITTER_LIFETIME_EXPRESSION = reg("emitter_lifetime_expression", EmitterLifetimeExpression.class);
    public static final ParticleComponentType<EmitterLifetimeOnce>       EMITTER_LIFETIME_ONCE     = reg("emitter_lifetime_once",      EmitterLifetimeOnce.class);
    public static final ParticleComponentType<EmitterShapeEntityAABB>    EMITTER_SHAPE_ENTITY_AABB = reg("emitter_shape_entity_aabb",  EmitterShapeEntityAABB.class);
    public static final ParticleComponentType<EmitterShapeDisc>          EMITTER_SHAPE_DISC        = reg("emitter_shape_disc",         EmitterShapeDisc.class);
    public static final ParticleComponentType<EmitterShapeBox>           EMITTER_SHAPE_BOX         = reg("emitter_shape_box",          EmitterShapeBox.class);
    public static final ParticleComponentType<EmitterShapeCustom>        EMITTER_SHAPE_CUSTOM      = reg("emitter_shape_custom",       EmitterShapeCustom.class);
    public static final ParticleComponentType<EmitterShapePoint>         EMITTER_SHAPE_POINT       = reg("emitter_shape_point",        EmitterShapePoint.class);
    public static final ParticleComponentType<EmitterShapeSphere>        EMITTER_SHAPE_SPHERE      = reg("emitter_shape_sphere",       EmitterShapeSphere.class);
    public static final ParticleComponentType<EmitterRateInstant>        EMITTER_RATE_INSTANT      = reg("emitter_rate_instant",       EmitterRateInstant.class);
    public static final ParticleComponentType<EmitterRateManual>         EMITTER_RATE_MANUAL       = reg("emitter_rate_manual",        EmitterRateManual.class);
    public static final ParticleComponentType<EmitterRateSteady>         EMITTER_RATE_STEADY       = reg("emitter_rate_steady",        EmitterRateSteady.class);
    public static final ParticleComponentType<EmitterLocalSpace>         EMITTER_LOCAL_SPACE       = reg("emitter_local_space",        EmitterLocalSpace.class);
    // Particle
    public static final ParticleComponentType<ParticleLifetimeExpression>  PARTICLE_LIFETIME_EXPRESSION    = reg("particle_lifetime_expression",   ParticleLifetimeExpression.class);
    public static final ParticleComponentType<ParticleInitialSpeed>         PARTICLE_INITIAL_SPEED          = reg("particle_initial_speed",          ParticleInitialSpeed.class);
    public static final ParticleComponentType<ParticleInitialSpin>          PARTICLE_INITIAL_SPIN           = reg("particle_initial_spin",           ParticleInitialSpin.class);
    public static final ParticleComponentType<ParticleExpireIfInBlocks>     PARTICLE_EXPIRE_IF_IN_BLOCKS    = reg("particle_expire_if_in_blocks",    ParticleExpireIfInBlocks.class);
    public static final ParticleComponentType<ParticleExpireIfNotInBlocks>  PARTICLE_EXPIRE_IF_NOT_IN_BLOCKS= reg("particle_expire_if_not_in_blocks",ParticleExpireIfNotInBlocks.class);
    public static final ParticleComponentType<ParticleLifetimeEvents>       PARTICLE_LIFETIME_EVENTS        = reg("particle_lifetime_events",        ParticleLifetimeEvents.class);
    public static final ParticleComponentType<ParticleKillPlane>            PARTICLE_KILL_PLANE             = reg("particle_kill_plane",             ParticleKillPlane.class);
    public static final ParticleComponentType<ParticleMotionCollision>      PARTICLE_MOTION_COLLISION       = reg("particle_motion_collision",       ParticleMotionCollision.class);
    public static final ParticleComponentType<ParticleMotionDynamic>        PARTICLE_MOTION_DYNAMIC         = reg("particle_motion_dynamic",         ParticleMotionDynamic.class);
    public static final ParticleComponentType<ParticleMotionParametric>     PARTICLE_MOTION_PARAMETRIC      = reg("particle_motion_parametric",      ParticleMotionParametric.class);
    public static final ParticleComponentType<ParticleAppearanceBillboard>  PARTICLE_APPEARANCE_BILLBOARD   = reg("particle_appearance_billboard",   ParticleAppearanceBillboard.class);
    public static final ParticleComponentType<ParticleAppearanceLighting>   PARTICLE_APPEARANCE_LIGHTING    = reg("particle_appearance_lighting",    ParticleAppearanceLighting.class);
    public static final ParticleComponentType<ParticleAppearanceTinting>    PARTICLE_APPEARANCE_TINTING     = reg("particle_appearance_tinting",     ParticleAppearanceTinting.class);

    private static <T extends ParticleComponent<?>> ParticleComponentType<T> reg(String name, Class<T> type) {
        return ParticleComponentRegistry.registerComponent(Identifier.withDefaultNamespace(name), type);
    }

    public static void init() {}
}
