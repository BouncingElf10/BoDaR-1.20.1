package net.bouncingelf10.bodar;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import net.minecraft.world.RaycastContext;
import org.joml.Quaternionf;
import org.joml.Vector3f;


import static net.bouncingelf10.bodar.BoDaR.LOGGER;
import static net.bouncingelf10.bodar.WhiteDotParticle.*;
import static net.bouncingelf10.bodar.WhiteDotParticle.Factory.setDirection;


public class RayCast {

    static HitResult hit;

    public static void rayCast(float xPixel, float yPixel) {

        MinecraftClient client = MinecraftClient.getInstance();
        double maxReach = config.reach; // The farthest target the cameraEntity can detect
        float tickDelta = 1.0F; // Used for tracking animation progress; no tracking is 1.0F
        boolean includeFluids = true; // Whether to detect fluids as blocks
        if (client.player != null) { // if user is underwater to stop particles from collecting
            includeFluids = !client.player.isSubmergedInWater();
        }

        if (client.cameraEntity != null && client.world != null) {
            Vec3d cameraPos = client.cameraEntity.getCameraPosVec(tickDelta);
            Vec3d screenVec = getWorldCoordsFromScreenCoords(client, xPixel, yPixel, cameraPos, maxReach, tickDelta);

            hit = client.world.raycast(new RaycastContext(cameraPos, screenVec, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, client.cameraEntity));

            switch(hit.getType()) {
                case MISS:
                    // nothing near enough
                    break;
                case BLOCK:
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    setDirection(blockHit.getSide());

                    BlockState blockState = client.world.getBlockState(blockHit.getBlockPos());
                    Block block = blockState.getBlock();
                    Identifier blockId = Registries.BLOCK.getId(block);

                    getBlockID(String.valueOf(blockId));

                    spawnParticle((float) blockHit.getPos().x, (float) blockHit.getPos().y, (float) blockHit.getPos().z, blockHit.getSide());
                    break;
                case ENTITY:
                    EntityHitResult entityHit = (EntityHitResult) hit;
                    LOGGER.info("Hit enitity: {}", entityHit.getEntity().getEntityName());
                    break;
            }
        }
    }

    private static Vec3d getWorldCoordsFromScreenCoords(MinecraftClient client, float xPixel, float yPixel, Vec3d cameraPos, double maxReach, float tickDelta) {

        assert client.cameraEntity != null; //IDK if it isn't but meh will this really effect me

        Vector3f cameraDirection = client.cameraEntity.getRotationVec(tickDelta).toVector3f();
        double fov = client.options.getFov().getValue();


        Vector3f verticalRotationAxis = new Vector3f(cameraDirection);
        verticalRotationAxis.cross(new Vector3f(0, 1, 0)); // POSITIVE_Y
        verticalRotationAxis.normalize();

        Vector3f horizontalRotationAxis = new Vector3f(cameraDirection);
        horizontalRotationAxis.cross(verticalRotationAxis);
        horizontalRotationAxis.normalize();

        Vec3d direction = map(fov, cameraDirection, horizontalRotationAxis, verticalRotationAxis, xPixel, yPixel);
        return cameraPos.add(direction.multiply(maxReach));
    }

    private static Vec3d map(double fov, Vector3f center, Vector3f horizontalRotationAxis,
                             Vector3f verticalRotationAxis, float x, float y) {

        float horizontalRotation = (float) (x * fov / 1000); //30 -> 0.03
        float verticalRotation = (float) (y * fov / 1000); //120 -> 0.12
        //LOGGER.info("horizontalRotation: {}, verticalRotation: {}", horizontalRotation, verticalRotation);
        Vector3f temp = new Vector3f(center);

        Quaternionf horizontalRotationQuat = new Quaternionf().rotationAxis(horizontalRotation, horizontalRotationAxis);
        horizontalRotationQuat.normalize(); // Ensure quaternion is normalized
        horizontalRotationQuat.transform(temp);

        Quaternionf verticalRotationQuat = new Quaternionf().rotationAxis(verticalRotation, verticalRotationAxis);
        verticalRotationQuat.normalize(); // Ensure quaternion is normalized
        verticalRotationQuat.transform(temp);

        return new Vec3d(temp.x, temp.y, temp.z);
    }


    static ClientWorld world = MinecraftClient.getInstance().world;

    public static void spawnParticle(float hitX, float hitY, float hitZ, Direction direction) {
        if (world != null) {
            // LOGGER.info("Spawning Particle at: {}, {}, {}", hitX, hitY, hitZ);

            switch (direction) {
                case UP -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY + Math.random() / 1000, hitZ, 0, 0, 0);
                case DOWN -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY - Math.random() / 1000, hitZ, 0, 0, 0);
                case NORTH -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY, hitZ - Math.random() / 1000, 0, 0, 0);
                case EAST -> world.addParticle(BoDaR.WhiteDotParticle, hitX + Math.random() / 1000, hitY, hitZ, 0, 0, 0);
                case SOUTH -> world.addParticle(BoDaR.WhiteDotParticle, hitX, hitY, hitZ + Math.random() / 1000, 0, 0, 0);
                case WEST -> world.addParticle(BoDaR.WhiteDotParticle, hitX - Math.random() / 1000, hitY, hitZ, 0, 0, 0);
            }
        }
    }
}
