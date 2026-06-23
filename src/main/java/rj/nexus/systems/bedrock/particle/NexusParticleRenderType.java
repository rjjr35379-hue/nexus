package rj.nexus.systems.bedrock.particle;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class NexusParticleRenderType {

    public enum Material {
        ALPHA,
        ADD,
        BLEND,
        ALPHA_GRAYSCALE;

        public static Material fromString(String s) {
            if (s == null) return ALPHA;
            return switch (s.toLowerCase()) {
                case "particles_add"             -> ADD;
                case "particles_blend"           -> BLEND;
                case "particles_alpha_grayscale" -> ALPHA_GRAYSCALE;
                default                          -> ALPHA;
            };
        }
    }

    private static final OutputTarget PARTICLE_OUTPUT = new OutputTarget(
            "nexus_particle_target",
            () -> Minecraft.getInstance().levelRenderer.getParticlesTarget()
    );

    private static final Map<String, RenderType> CACHE = new HashMap<>();

    private static final RenderPipeline PIPELINE_ALPHA = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                    .withLocation("pipeline/nexus_particle_alpha")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .build()
    );

    private static final RenderPipeline PIPELINE_ADD = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                    .withLocation("pipeline/nexus_particle_add")
                    .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
                    .build()
    );

    private static final RenderPipeline PIPELINE_BLEND = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.PARTICLE_SNIPPET)
                    .withLocation("pipeline/nexus_particle_blend")
                    .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                    .build()
    );

    public static RenderType get(Identifier texture, Material material) {
        String key = texture + ":" + material.name();
        return CACHE.computeIfAbsent(key, k -> {
            RenderPipeline pipeline = switch (material) {
                case ADD            -> PIPELINE_ADD;
                case BLEND          -> PIPELINE_BLEND;
                default             -> PIPELINE_ALPHA;
            };
            RenderSetup state = RenderSetup.builder(pipeline)
                    .withTexture("Sampler0", texture)
                    .setOutputTarget(PARTICLE_OUTPUT)
                    .useLightmap()
                    .createRenderSetup();
            return RenderType.create("nexus:particle/" + texture.getPath() + "/" + material.name().toLowerCase(), state);
        });
    }
}