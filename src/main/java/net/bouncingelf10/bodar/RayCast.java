package net.bouncingelf10.bodar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

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
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Ray hit block at " + hitX + " " + hitY + " " + hitZ));
                } else {
                    LOGGER.info("Ray did not hit any block within reach.");
                    MinecraftClient.getInstance().player.sendMessage(Text.of("Ray did not hit any block within reach."));
                }
            }
        }
    }
}
