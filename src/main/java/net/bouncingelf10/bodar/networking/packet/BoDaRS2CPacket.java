package net.bouncingelf10.bodar.networking.packet;

import net.bouncingelf10.bodar.BoDaR;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;
import net.minecraft.util.Identifier;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class BoDaRS2CPacket {
    public static final Identifier ID = new Identifier(BoDaR.MOD_ID, "s2c_packet");

    public static void send(ServerPlayerEntity player, double x, double y, double z, Direction direction, String colorID) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(direction.getId());
        buf.writeString(colorID);
        ServerPlayNetworking.send(player, ID, buf);
        //LOGGER.info("Sending particle (S2C) POS at: {}, {}, {}", x, y, z);
    }
}