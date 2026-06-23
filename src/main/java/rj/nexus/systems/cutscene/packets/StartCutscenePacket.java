package rj.nexus.systems.cutscene.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import rj.nexus.systems.cutscene.NexusCutsceneData;

public record StartCutscenePacket(NexusCutsceneData data) implements CustomPacketPayload {
    public static final Type<StartCutscenePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("nexus", "start_cutscene"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StartCutscenePacket> CODEC =
            StreamCodec.of(
                    (buf, p) -> NexusCutsceneData.STREAM_CODEC.encode(buf, p.data()),
                    buf -> new StartCutscenePacket(NexusCutsceneData.STREAM_CODEC.decode(buf))
            );

    public static void send(final ServerPlayer player, final NexusCutsceneData data) {
        ServerPlayNetworking.send(player, new StartCutscenePacket(data));
    }

    @Override
    public Type<StartCutscenePacket> type() {
        return TYPE;
    }
}
