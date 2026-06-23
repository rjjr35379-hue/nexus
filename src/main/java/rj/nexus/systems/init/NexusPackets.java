package rj.nexus.systems.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import rj.nexus.systems.cutscene.NexusCutsceneHandler;
import rj.nexus.systems.cutscene.packets.MoveCutscenePacket;
import rj.nexus.systems.cutscene.packets.StartCutscenePacket;
import rj.nexus.systems.cutscene.packets.StopCutscenePacket;
import rj.nexus.systems.screenshake.ShakeCenter;
import rj.nexus.systems.screenshake.ShakePacket;

public final class NexusPackets {

    private NexusPackets() {
    }

    public static void registerCommon() {
        PayloadTypeRegistry.clientboundPlay().register(ShakePacket.TYPE,         ShakePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StartCutscenePacket.TYPE, StartCutscenePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(StopCutscenePacket.TYPE, StopCutscenePacket.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(MoveCutscenePacket.TYPE, MoveCutscenePacket.CODEC);
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {

        ClientPlayNetworking.registerGlobalReceiver(StartCutscenePacket.TYPE,
                (payload, ctx) -> ctx.client().execute(
                        () -> NexusCutsceneHandler.startCutscene(payload.data())
                ));

        ClientPlayNetworking.registerGlobalReceiver(StopCutscenePacket.TYPE,
                (payload, ctx) -> ctx.client().execute(NexusCutsceneHandler::stopCutscene));

        ClientPlayNetworking.registerGlobalReceiver(MoveCutscenePacket.TYPE,
                (payload, ctx) -> ctx.client().execute(
                        () -> NexusCutsceneHandler.moveCamera(payload.data())
                ));
        ClientPlayNetworking.registerGlobalReceiver(ShakePacket.TYPE, (payload, ctx) ->
                ctx.client().execute(() -> {
                    if (payload.positioned())
                        ShakeCenter.addBlastShake(payload.params(),
                                new net.minecraft.world.phys.Vec3(payload.ox(), payload.oy(), payload.oz()),
                                payload.maxDist());
                    else
                        ShakeCenter.addScreenShake(payload.params());
                })
        );
    }
}
