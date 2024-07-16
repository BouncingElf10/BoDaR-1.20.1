package net.bouncingelf10.bodar.client;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.networking.BoDaRPackets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
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


import java.util.Optional;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.*;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.Factory.setDirection;


public class RayCast {

    static HitResult hit;

    public static void rayCast(float xPixel, float yPixel) {
        MinecraftClient client = MinecraftClient.getInstance();
        double maxReach = config.reach;
        float tickDelta = 1.0F;
        boolean includeFluids = true;
        if (client.player != null) {
            includeFluids = !client.player.isSubmergedInWater();
        }

        if (client.cameraEntity != null && client.world != null) {
            Vec3d cameraPos = client.cameraEntity.getCameraPosVec(tickDelta);
            Vec3d screenVec = getWorldCoordsFromScreenCoords(client, xPixel, yPixel, cameraPos, maxReach, tickDelta);

            // Block raycast
            HitResult blockHit = client.world.raycast(new RaycastContext(cameraPos, screenVec, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, client.cameraEntity));

            EntityHitInfo entityHitInfo = raycastEntities(client, cameraPos, screenVec, maxReach);

            // Determine which hit is closer
            if (entityHitInfo != null && (blockHit.getType() == HitResult.Type.MISS || entityHitInfo.hitPos.squaredDistanceTo(cameraPos) < blockHit.getPos().squaredDistanceTo(cameraPos))) {
                hit = new EntityHitResult(entityHitInfo.entity, entityHitInfo.hitPos);
            } else {
                hit = blockHit;
            }

            Factory.setLastHit(hit);

            //LOGGER.info(String.valueOf(hit.getType()));
            switch(hit.getType()) {
                case MISS:
                    //LOGGER.info("Missed");
                    break;
                case BLOCK:
                    BlockHitResult blockHitResult = (BlockHitResult) hit;

                    BlockState blockState = client.world.getBlockState(blockHitResult.getBlockPos());
                    Block block = blockState.getBlock();
                    Identifier blockId = Registries.BLOCK.getId(block);

                    spawnParticle(new Vec3d((float) blockHitResult.getPos().x, (float) blockHitResult.getPos().y, (float) blockHitResult.getPos().z), blockHitResult.getSide(), String.valueOf(blockId));
                    break;
                case ENTITY:
                    EntityHitResult entityHitResult = (EntityHitResult) hit;
                    Entity entity = entityHitResult.getEntity();
                    //LOGGER.info("Hit entity: {}, type: {}", entity, entityHitResult.getType());

                    String entityID;
                    if (entity instanceof Monster) {
                        //LOGGER.info("The entity is hostile.");
                        entityID = "minecraft:entityhostile";
                    } else if (entity instanceof PassiveEntity) {
                        //LOGGER.info("The entity is passive.");
                        entityID = "minecraft:entitypassive";
                    } else if (entity instanceof PlayerEntity) {
                        //LOGGER.info("The entity is passive.");
                        entityID = "minecraft:entityplayer";
                    } else {
                        //LOGGER.info("The entity is of another type.");
                        entityID = "minecraft:entitymisc";
                    }

                    Vec3d hitPos = entityHitResult.getPos();
                    Direction hitDirection = getEntityHitDirection(hitPos, entityHitResult.getEntity());
                    if (!entityID.equals("minecraft:entityplayer")) {
                        spawnParticle(new Vec3d((float) hitPos.x, (float) hitPos.y, (float) hitPos.z), hitDirection, entityID);
                    }
                    break;
            }
        }
    }

    private static Direction getEntityHitDirection(Vec3d hitPos, Entity entity) {
        Box boundingBox = entity.getBoundingBox();
        Vec3d center = boundingBox.getCenter();

        double dx = hitPos.x - center.x;
        double dy = hitPos.y - center.y;
        double dz = hitPos.z - center.z;

        double absX = Math.abs(dx);
        double absY = Math.abs(dy);
        double absZ = Math.abs(dz);

        double xExtent = (boundingBox.maxX - boundingBox.minX) / 2;
        double yExtent = (boundingBox.maxY - boundingBox.minY) / 2;
        double zExtent = (boundingBox.maxZ - boundingBox.minZ) / 2;

        if (absX / xExtent > absY / yExtent && absX / xExtent > absZ / zExtent) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else if (absY / yExtent > absX / xExtent && absY / yExtent > absZ / zExtent) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    private static class EntityHitInfo {
        Entity entity;
        Vec3d hitPos;

        EntityHitInfo(Entity entity, Vec3d hitPos) {
            this.entity = entity;
            this.hitPos = hitPos;
        }
    }

    private static EntityHitInfo raycastEntities(MinecraftClient client, Vec3d start, Vec3d end, double maxDistance) {
        Vec3d vec3d = end.subtract(start);
        if (vec3d.lengthSquared() > maxDistance * maxDistance) {
            vec3d = vec3d.normalize().multiply(maxDistance);
        }
        Vec3d endVec = start.add(vec3d);

        assert client.world != null;
        Iterable<Entity> entities = client.world.getEntities();
        Entity closestHitEntity = null;
        Vec3d closestHitPos = null;
        double closestHitDistance = maxDistance;

        for (Entity entity : entities) {
            Box entityBox = entity.getBoundingBox().expand(entity.getTargetingMargin());
            Optional<Vec3d> optionalHit = entityBox.raycast(start, endVec);

            if (optionalHit.isPresent()) {
                Vec3d hitPos = optionalHit.get();
                double hitDistance = start.distanceTo(hitPos);
                if (hitDistance < closestHitDistance) {
                    closestHitEntity = entity;
                    closestHitPos = hitPos;
                    closestHitDistance = hitDistance;
                }
            }
        }

        return closestHitEntity != null ? new EntityHitInfo(closestHitEntity, closestHitPos) : null;
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

    public static void spawnParticle(Vec3d hitPos, Direction direction, String colorID) {
        if (world != null) {
            //LOGGER.info("Starting particle POS at: {}, {}, {}", hitPos.x, hitPos.y, hitPos.z);
            double displacement = 0.005 + Math.random() * 0.005;
            Vec3d displacedPos = hitPos.add(
                    direction.getOffsetX() * displacement,
                    direction.getOffsetY() * displacement,
                    direction.getOffsetZ() * displacement
            );
            //LOGGER.info("Spawning particle at: {}, {}, {}", displacedPos.x, displacedPos.y, displacedPos.z);
            getBlockID(colorID);
            setDirection(direction);
            world.addParticle(BoDaR.WhiteDotParticle, displacedPos.x, displacedPos.y, displacedPos.z, 0, 0, 0);
            if (config.multiplayerMode) {
                PacketByteBuf buf = PacketByteBufs.create();

                buf.writeDouble(displacedPos.x);
                buf.writeDouble(displacedPos.y);
                buf.writeDouble(displacedPos.z);
                buf.writeInt(direction.getId());
                buf.writeString(colorID);

                ClientPlayNetworking.send(BoDaRPackets.BODAR_PACKET_ID, buf);
            }
        } else {
            //LOGGER.warn("Cannot spawn particle: world is null");
        }
    }

    public static void spawnParticleServer(Vec3d hitPos, Direction direction, String colorID) {
        ClientWorld world = MinecraftClient.getInstance().world;
        double x = hitPos.x;
        double y = hitPos.y;
        double z = hitPos.z;

        //LOGGER.info("Attempting to spawn particle at: {}, {}, {}", x, y, z);
        getBlockID(colorID);
        setDirection(direction);
        assert world != null;
        world.addParticle(BoDaR.WhiteDotParticle, x, y, z, 0, 0, 0);
        //LOGGER.info("Particle spawned at: {}, {}, {}", x, y, z);
    }
}
