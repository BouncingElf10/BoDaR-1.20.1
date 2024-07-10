package net.bouncingelf10.bodar.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Direction;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class BoDaRC2SPacket {
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();
        Direction direction = Direction.byId(buf.readInt());
        String colorID = buf.readString();

        //LOGGER.info("Received particle (C2S) POS at: {}, {}, {}", x, y, z);

        server.getPlayerManager().getPlayerList().forEach(serverPlayer ->
                BoDaRS2CPacket.send(serverPlayer, x, y, z, direction, colorID)
        );
    }
}
