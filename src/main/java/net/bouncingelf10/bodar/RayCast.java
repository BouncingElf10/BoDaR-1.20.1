package net.bouncingelf10.bodar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;
import static net.bouncingelf10.bodar.CustomParticle.Factory.setDirection;


import net.bouncingelf10.bodar.CustomParticle.*;

public class RayCast {
    public static void rayCast(float offsetX, float offsetY) {
        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if the client player is available
        if (client.player != null) {
            Vec2f headRotation = client.player.getRotationClient();
            Vec3d headLocation = client.player.getCameraPosVec(1);
            LOGGER.info("Player Head Rotation: {}, {}", headRotation.y, headRotation.x);
            LOGGER.info("Player Head Location: {}, {}, {}", headLocation.x, headLocation.y, headLocation.z);
            double reachDistance = 16.0; // Set your desired reach distance

            // Calculate the direction vector
            float pitch = headRotation.x;
            float yaw = headRotation.y;
            double deltaX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            double deltaY = -Math.sin(Math.toRadians(pitch));
            double deltaZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            //apply offset
            headLocation = headLocation.add(deltaX * offsetX, deltaY * offsetY, deltaZ * offsetX);
            LOGGER.info("Player New Offset Head Location: {}, {}, {}", headLocation.x, headLocation.y, headLocation.z);
            Vec3d direction = new Vec3d(deltaX, deltaY, deltaZ).normalize().multiply(reachDistance);

            // Perform a raycast from the player's head location
            Vec3d target = headLocation.add(direction);
            if (client.world != null) {
                BlockHitResult result = client.world.raycast(new RaycastContext(headLocation, target, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, client.player));
                if (result.getType() == BlockHitResult.Type.BLOCK) {
                    BlockPos blockPos = result.getBlockPos();
                    Vec3d hitVec = result.getPos();

                    // Convert BlockPos to float coordinates
                    float hitX = (float) hitVec.x;
                    float hitY = (float) hitVec.y;
                    float hitZ = (float) hitVec.z;

                    LOGGER.info("Ray hit block at {}, {}, {}", hitX, hitY, hitZ);
                    setDirection(result.getSide());
                    //MinecraftClient.getInstance().player.sendMessage(Text.of("Ray hit block at " + hitX + " " + hitY + " " + hitZ));
                    spawnParticle(hitX, hitY, hitZ, result.getSide());
                } else {
                    LOGGER.info("Ray did not hit any block within reach.");
                    //MinecraftClient.getInstance().player.sendMessage(Text.of("Ray did not hit any block within reach."));
                }
            }
        }
    }

    static ClientWorld world = MinecraftClient.getInstance().world;


    public static void spawnParticle(float hitX, float hitY, float hitZ, Direction direction) {
        if (world != null) {
            //LOGGER.info("Spawning CustomParticle at ({}, {}, {}) with velocity ({}, {}, {})", hitX, hitY, hitZ, 0, 0, 0);
            switch (direction) {
                case UP -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX, hitY + 0.001, hitZ, 0, 0, 0);
                case DOWN -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX, hitY - 0.001, hitZ, 0, 0, 0);
                case NORTH -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX, hitY, hitZ - 0.001, 0, 0, 0);
                case EAST -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX + 0.001, hitY, hitZ, 0, 0, 0);
                case SOUTH -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX, hitY, hitZ + 0.001, 0, 0, 0);
                case WEST -> world.addParticle(BoDaR.CUSTOM_PARTICLE, hitX - 0.001, hitY, hitZ, 0, 0, 0);
            }

        }
    }
}
