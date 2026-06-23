package rj.nexus.systems.cutscene.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

public record StopCutscenePacket() implements CustomPacketPayload {
    public static final Type<StopCutscenePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("nexus", "stop_cutscene"));
    public static final StreamCodec<RegistryFriendlyByteBuf, StopCutscenePacket> CODEC =
            StreamCodec.of((buf, p) -> {
            }, buf -> new StopCutscenePacket());

    public static void send(final ServerPlayer player) {
        ServerPlayNetworking.send(player, new StopCutscenePacket());
    }

    @Override
    public Type<StopCutscenePacket> type() {
        return TYPE;
    }
}
