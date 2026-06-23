package rj.nexus.systems.cutscene.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import rj.nexus.systems.cutscene.NexusCutsceneData;

public record MoveCutscenePacket(NexusCutsceneData data) implements CustomPacketPayload {
    public static final Type<MoveCutscenePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("nexus", "move_cutscene"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MoveCutscenePacket> CODEC =
            StreamCodec.of(
                    (buf, p) -> NexusCutsceneData.STREAM_CODEC.encode(buf, p.data()),
                    buf -> new MoveCutscenePacket(NexusCutsceneData.STREAM_CODEC.decode(buf))
            );

    public static void send(final ServerPlayer player, final NexusCutsceneData data) {
        ServerPlayNetworking.send(player, new MoveCutscenePacket(data));
    }

    @Override
    public Type<MoveCutscenePacket> type() {
        return TYPE;
    }
}
