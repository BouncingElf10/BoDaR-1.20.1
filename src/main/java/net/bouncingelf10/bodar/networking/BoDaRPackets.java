package net.bouncingelf10.bodar.networking;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.client.RayCast;
import net.bouncingelf10.bodar.networking.packet.BoDaRC2SPacket;
import net.bouncingelf10.bodar.networking.packet.BoDaRS2CPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class BoDaRPackets {
    public static final Identifier BODAR_PACKET_ID =  new Identifier(BoDaR.MOD_ID, "particle");

    private static final Map<Vec3d, Long> recentlySpawnedParticles = new ConcurrentHashMap<>();
    private static final long PARTICLE_EXPIRATION_TIME = 1000; // milliseconds

    public static void addRecentParticle(Vec3d position) {
        recentlySpawnedParticles.put(position, System.currentTimeMillis());
    }

    public static boolean canSpawnParticle(Vec3d position) {
        long currentTime = System.currentTimeMillis();
        recentlySpawnedParticles.entrySet().removeIf(entry -> currentTime - entry.getValue() > PARTICLE_EXPIRATION_TIME);
        return !recentlySpawnedParticles.containsKey(position);
    }

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
            //LOGGER.info("Client received particle POS at: {}, {}, {}", x, y, z);

            client.execute(() -> {
                // Force spawn particle on main thread
                if (canSpawnParticle(new Vec3d(x, y, z))) {
                    MinecraftClient.getInstance().execute(() -> {
                        RayCast.spawnParticleServer(new Vec3d(x, y, z), direction, colorID);
                    });
                }
            });

            addRecentParticle(new Vec3d(x, y, z));

            if (recentlySpawnedParticles.size() >= 5) {
                recentlySpawnedParticles.clear();
            }
        });
    }



}
