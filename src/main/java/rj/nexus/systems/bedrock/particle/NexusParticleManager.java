package rj.nexus.systems.bedrock.particle;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

import java.util.List;

public class NexusParticleManager {

    private static boolean registered = false;
    private static final List<NexusParticleInstance> INSTANCES = new ObjectArrayList<>();

    public static void register() {
        if (registered) return;
        registered = true;

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.level == null) return;
            var emitters = NexusParticles.ACTIVE_EMITTERS;
            for (int i = emitters.size() - 1; i >= 0; i--) {
                var e = emitters.get(i);
                e.tick();
                if (e.isDead()) emitters.remove(i);
            }
        });

        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register((LevelRenderContext context) -> {
            if (INSTANCES.isEmpty()) return;
            Camera camera = Minecraft.getInstance().gameRenderer.mainCamera;
            float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
            MultiBufferSource.BufferSource bufferSource = context.bufferSource();

            for (int i = INSTANCES.size() - 1; i >= 0; i--) {
                NexusParticleInstance inst = INSTANCES.get(i);
                if (inst.isRemoved()) { INSTANCES.remove(i); continue; }
                Identifier tex = inst.getTexture();
                if (tex == null) continue;
                NexusParticleRenderType.Material mat = inst.getMaterial();
                RenderType rt = NexusParticleRenderType.get(tex, mat);
                inst.renderManual(bufferSource.getBuffer(rt), camera, partialTick);
                bufferSource.endBatch(rt);
            }
        });
    }

    public static void addInstance(NexusParticleInstance instance) {
        INSTANCES.add(instance);
    }
}