package net.bouncingelf10.bodar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static net.bouncingelf10.bodar.WhiteDotParticle.Factory.setDirection;
import static net.bouncingelf10.bodar.WhiteDotParticle.getBlockID;

public class RayCast {
    public static void rayCast(float offsetX, float offsetY) {
        // Get the Minecraft client instance
        MinecraftClient client = MinecraftClient.getInstance();

        // Check if the client player is available
        if (client.player != null && client.world != null) {
            // Get the player's current rotation (pitch and yaw) and position
            float pitch = client.player.getPitch(1.0F);
            float yaw = client.player.getYaw(1.0F);
            Vec3d headLocation = client.player.getCameraPosVec(1.0F);

            // Convert viewport offsets to world offsets based on player's view direction
            double reachDistance = 16.0; // Reach distance

            // Calculate the direction vector
            double deltaX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
            double deltaY = -Math.sin(Math.toRadians(pitch));
            double deltaZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));


            // Create right and up vectors for the player's view direction
            Vec3d rightVec = new Vec3d(-Math.cos(Math.toRadians(yaw)), 0, -Math.sin(Math.toRadians(yaw))).normalize();

            Vec3d upVec;
            if (client.player.getPitch() >= 65 || client.player.getPitch() <= -65) {
                if (client.player.getHorizontalFacing() == Direction.WEST || client.player.getHorizontalFacing() == Direction.EAST) {
                    upVec = new Vec3d(1, 0, 0);
                } else {
                    upVec = new Vec3d(0, 0, 1);
                }
            } else {
                upVec = new Vec3d(0, 1, 0);
            }

            // Apply offsets in screen space
            Vec3d adjustedLocation = headLocation.add(rightVec.multiply(offsetX)).add(upVec.multiply(offsetY));
            Vec3d direction = new Vec3d(deltaX, deltaY, deltaZ).normalize().multiply(reachDistance);

            // Perform a raycast from the adjusted head location
            Vec3d target = adjustedLocation.add(direction);
            BlockHitResult result = client.world.raycast(new RaycastContext(headLocation, target, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, client.player));
            // Handle raycast result
            if (result.getType() == BlockHitResult.Type.BLOCK) {
                Vec3d hitVec = result.getPos();

                // Convert BlockPos to float coordinates
                float hitX = (float) hitVec.x;
                float hitY = (float) hitVec.y;
                float hitZ = (float) hitVec.z;

                //LOGGER.info("Ray hit block at {}, {}, {}", hitX, hitY, hitZ);
                BlockState blockState = world.getBlockState(result.getBlockPos());
                Block block = blockState.getBlock();
                Identifier blockId = Registries.BLOCK.getId(block);

                getBlockID(String.valueOf(blockId));
                //LOGGER.info("Block: {}", blockId);

                setDirection(result.getSide());
                spawnParticle(hitX, hitY, hitZ, result.getSide());
            }
        }
    }

    static ClientWorld world = MinecraftClient.getInstance().world;


    public static void spawnParticle(float hitX, float hitY, float hitZ, Direction direction) {
        if (world != null) {
            //LOGGER.info("Spawning Particle at: {}, {}, {}", hitX, hitY, hitZ);
            switch (direction) {
                case UP -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY + 0.0001, hitZ, 0, 0, 0);
                case DOWN -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY - 0.0001, hitZ, 0, 0, 0);
                case NORTH -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY, hitZ - 0.0001, 0, 0, 0);
                case EAST -> world.addParticle(BoDaR.WhiteDotParticle, hitX + 0.0001, hitY, hitZ, 0, 0, 0);
                case SOUTH -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY, hitZ + 0.0001, 0, 0, 0);
                case WEST -> world.addParticle(BoDaR.WhiteDotParticle, hitX - 0.0001, hitY, hitZ, 0, 0, 0);
            }

        }
    }
}
