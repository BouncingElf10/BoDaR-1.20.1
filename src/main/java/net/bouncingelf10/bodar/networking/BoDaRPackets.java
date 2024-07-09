package net.bouncingelf10.bodar.networking;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.client.RayCast;
import net.bouncingelf10.bodar.networking.packet.BoDaRC2SPacket;
import net.bouncingelf10.bodar.networking.packet.BoDaRS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BoDaRPackets {
    public static final Identifier BODAR_PACKET_ID =  new Identifier(BoDaR.MOD_ID, "particle");


    public static void registerC2SPackets() {
        ServerPlayNetworking.registerGlobalReceiver(BODAR_PACKET_ID, BoDaRC2SPacket::receive);
    }

    public static void registerS2CPackets() {
        ClientPlayNetworking.registerGlobalReceiver(BoDaRS2CPacket.ID, (client, handler, buf, responseSender) -> {
            double x = buf.readDouble();
            double y = buf.readDouble();
            double z = buf.readDouble();
            Direction direction = Direction.byId(buf.readInt());
            String colorID = buf.readString();

            client.execute(() -> {
                // Handle the received data on the client side
                // For example, spawn particles or update UI
                RayCast.spawnParticleServer(new Vec3d(x, y, z), direction, colorID);
            });
        });
    }

}
