package rj.nexus.systems.screenshake;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public record ShakePacket(
        ShakeParams params,
        boolean positioned,
        double ox, double oy, double oz,
        double maxDist
) implements CustomPacketPayload {

    public static final Type<ShakePacket> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("untitledketchup", "shake"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ShakePacket> CODEC =
            StreamCodec.of(
                    (buf, p) -> {
                        ShakeParams.STREAM_CODEC.encode(buf, p.params());
                        buf.writeBoolean(p.positioned());
                        if (p.positioned()) {
                            buf.writeDouble(p.ox());
                            buf.writeDouble(p.oy());
                            buf.writeDouble(p.oz());
                            buf.writeDouble(p.maxDist());
                        }
                    },
                    buf -> {
                        ShakeParams params = ShakeParams.STREAM_CODEC.decode(buf);
                        boolean pos = buf.readBoolean();
                        if (pos) {
                            double x = buf.readDouble(), y = buf.readDouble(), z = buf.readDouble();
                            double d = buf.readDouble();
                            return new ShakePacket(params, true, x, y, z, d);
                        }
                        return new ShakePacket(params, false, 0, 0, 0, 0);
                    }
            );

    public static void sendBlast(ServerLevel level, Vec3 origin, double radius, ShakeParams params) {
        ShakePacket pkt = new ShakePacket(params, true, origin.x, origin.y, origin.z, radius);
        double rSq = radius * radius;
        level.players().forEach(p -> {
            if (p.distanceToSqr(origin.x, origin.y, origin.z) <= rSq)
                ServerPlayNetworking.send(p, pkt);
        });
    }

    public static void sendScreen(ServerLevel level, Vec3 origin, double radius, ShakeParams params) {
        ShakePacket pkt = new ShakePacket(params, false, 0, 0, 0, 0);
        double rSq = radius * radius;
        level.players().forEach(p -> {
            if (p.distanceToSqr(origin.x, origin.y, origin.z) <= rSq)
                ServerPlayNetworking.send(p, pkt);
        });
    }

    @Override
    public Type<ShakePacket> type() { return TYPE; }
}