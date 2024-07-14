package net.bouncingelf10.bodar.client;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class WhiteDotParticle extends SpriteBillboardParticle {

    static String blockIDString;
    static Vec3d colorBlockID;
    static BoDaRConfig config = BoDaRConfig.get();
    MinecraftClient client = MinecraftClient.getInstance();

    static Vec3d oreColor;
    static Vec3d functionalColor;
    static Vec3d waterColor;
    static Vec3d lavaColor;
    static Vec3d defaultColor;
    static Vec3d entityhostileColor;
    static Vec3d entitypassiveColor;
    static Vec3d entitymiscColor;


    static void resetColors() {
        Color oreColorHex = parseColor(String.valueOf(config.oreColor));
        Color functionalColorHex = parseColor(String.valueOf(config.functionalColor));

        oreColor = new Vec3d(oreColorHex.getRed(), oreColorHex.getGreen(), oreColorHex.getBlue());
        functionalColor = new Vec3d(functionalColorHex.getRed(), functionalColorHex.getGreen(), functionalColorHex.getBlue());
        waterColor = new Vec3d(0, 0, 254);
        lavaColor = new Vec3d(254, 98, 0);
        defaultColor = new Vec3d(254, 254, 254);
        entityhostileColor = new Vec3d(254, 0, 0);
        entitypassiveColor = new Vec3d(0, 254, 0);
        entitymiscColor = new Vec3d(0, 0, 254);
    }

    static Color parseColor(String colorString) {
        int colorValue = Integer.parseInt(colorString);

        // Convert integer to hexadecimal string
        StringBuilder hex = new StringBuilder(Integer.toHexString(colorValue));

        // Ensure the hex string is formatted correctly to 6 characters
        while (hex.length() < 6) {
            hex.insert(0, "0");
        }

        // Extract RGB components from the hex string
        int r = Integer.parseInt(hex.substring(0, 2), 16);
        int g = Integer.parseInt(hex.substring(2, 4), 16);
        int b = Integer.parseInt(hex.substring(4, 6), 16);

        return new Color(r, g, b);
    }

    static void getBlockID(String blockID) {
        blockIDString = blockID;
        colorBlockID = getColorBlockID(blockIDString);
    }

    private static Set<String> ores = new HashSet<>();
    private static Set<String> functional = new HashSet<>();
    private static final Set<String> water = new HashSet<>();
    private static final Set<String> lava = new HashSet<>();
    private static final Set<String> entityhostile = new HashSet<>();
    private static final Set<String> entitypassive = new HashSet<>();
    private static final Set<String> entitymisc = new HashSet<>();

    static void loadBlocks() {
        ores = Stream.of(config.ores).collect(Collectors.toSet());
        functional = Stream.of(config.functionals).collect(Collectors.toSet());
        water.add("minecraft:water");
        lava.add("minecraft:lava");
        entityhostile.add("minecraft:entityhostile");
        entitypassive.add("minecraft:entitypassive");
        entitymisc.add("minecraft:entitymisc");
    }

    static {
        loadBlocks();
        resetColors();
    }

    static boolean isDefaultColor = false;
    public static Vec3d getColorBlockID(String blockIDString) {
        if (ores.contains(blockIDString)) {
            return oreColor;
        } else if (functional.contains(blockIDString)) {
            return functionalColor;
        } else if (water.contains(blockIDString)) {
            return waterColor;
        } else if (lava.contains(blockIDString)) {
            return lavaColor;
        } else if (entityhostile.contains(blockIDString)) {
            return entityhostileColor;
        } else if (entitypassive.contains(blockIDString)) {
            return entitypassiveColor;
        } else if (entitymisc.contains(blockIDString)) {
            return entitymiscColor;
        } else {
            isDefaultColor = true;
            return defaultColor;
        }
    }

    private static int getBlockColor(ClientWorld world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (config.particleColorMode == BoDaRConfig.ColorMode.WORLD) {
            isDefaultColor = false;
            return block.getDefaultMapColor().color;
        } else {
            if (isDefaultColor) {
                isDefaultColor = false;
                return block.getDefaultMapColor().color;
            }
            return vec3dToInt(getColorBlockID(blockIDString));
        }
    }

    private static int vec3dToInt(Vec3d vec) {
        int r = (int) vec.getX() & 0xFF;
        int g = (int) vec.getY() & 0xFF;
        int b = (int) vec.getZ() & 0xFF;
        return (r << 16) | (g << 8) | b;
    }


    private final Vec3d initialBlockHitPos;
    private final Vec3d initialHitPos;
    private Vec3d initialEntityPos = null;
    private final BlockPos initialBlockPos;
    private Quaternionf rotation;
    protected WhiteDotParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Direction direction, BlockPos blockPos, Entity hitEntity, Vec3d hitPos, int blockColor) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = config.particleSize;
        this.maxAge = config.maxAge;
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.rotation = getRotationQuaternion(direction);

        if (config.particleColorMode == BoDaRConfig.ColorMode.WORLD && blockColor != -1) {
            float r = ((blockColor >> 16) & 0xFF) / 255f;
            float g = ((blockColor >> 8) & 0xFF) / 255f;
            float b = (blockColor & 0xFF) / 255f;
            this.setColor(r, g, b);
        } else if (config.particleColorMode == BoDaRConfig.ColorMode.MIXED && blockColor != -1) {
            float r = ((blockColor >> 16) & 0xFF) / 255f;
            float g = ((blockColor >> 8) & 0xFF) / 255f;
            float b = (blockColor & 0xFF) / 255f;
            this.setColor(r, g, b);
        } else {
            this.setColor((float) ((float) 255 - colorBlockID.x), (float) ((float) 255 - colorBlockID.y), (float) ((float) 255 - colorBlockID.z));
        }

        this.initialBlockPos = blockPos != null ? blockPos : new BlockPos((int)x, (int)y, (int)z);
        this.initialHitPos = hitPos;
        this.initialEntityPos = hitEntity != null ? hitEntity.getPos() : null;
        this.initialBlockHitPos = blockPos != null ? new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()) : null;
        this.isFading = false;
        this.fadeStartAge = 0;
    }

    private float getScale() {
        return config.particleSize;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    private Quaternionf getRotationQuaternion(Direction direction) {
        if (config.randomRotation) {
            return switch (direction) {
                case UP -> new Quaternionf().rotateX((float) Math.toRadians(90))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
                case DOWN -> new Quaternionf().rotateX((float) Math.toRadians(-90))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
                case NORTH -> new Quaternionf().rotateY((float) Math.toRadians(0))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
                case EAST -> new Quaternionf().rotateY((float) Math.toRadians(-90))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
                case SOUTH -> new Quaternionf().rotateY((float) Math.toRadians(180))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
                case WEST -> new Quaternionf().rotateY((float) Math.toRadians(90))
                        .rotateZ((float) Math.toRadians(random.nextInt(360)));
            };
        } else {
            return switch (direction) {
                case UP -> new Quaternionf().rotateX((float) Math.toRadians(90));
                case DOWN -> new Quaternionf().rotateX((float) Math.toRadians(-90));
                case NORTH -> new Quaternionf().rotateY((float) Math.toRadians(0));
                case EAST -> new Quaternionf().rotateY((float) Math.toRadians(-90));
                case SOUTH -> new Quaternionf().rotateY((float) Math.toRadians(180));
                case WEST -> new Quaternionf().rotateY((float) Math.toRadians(90));
            };
        }

    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {

        Vec3d cameraPos = camera.getPos();
        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());

        Vector3f[] vertices = new Vector3f[] {
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };

        float scale = this.getScale();
        for (Vector3f vertex : vertices) {
            vertex.mul(scale);
            vertex.rotate(rotation);
            vertex.add(x, y, z);
        }

        float minU = this.getMinU();
        float maxU = this.getMaxU();
        float minV = this.getMinV();
        float maxV = this.getMaxV();
        int light = this.getBrightness(tickDelta);

        vertexConsumer.vertex(vertices[0].x, vertices[0].y, vertices[0].z).texture(minU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vertices[1].x, vertices[1].y, vertices[1].z).texture(minU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vertices[2].x, vertices[2].y, vertices[2].z).texture(maxU, minV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
        vertexConsumer.vertex(vertices[3].x, vertices[3].y, vertices[3].z).texture(maxU, maxV).color(this.red, this.green, this.blue, this.alpha).light(light).next();
    }

    private final int randomRotation = random.nextInt(360);

    private static final int ENTITY_FADE_DURATION = config.fadeOutTime * 6; // 3 seconds at 20 TPS
    private static final int BLOCK_FADE_DURATION = config.fadeOutTime; // 1 second at 20 TPS
    private int fadeDuration;
    private int fadeStartAge;
    private boolean isFading;

    @Override
    public void tick() {
        super.tick();

        float lifespan = (float) (maxAge * 0.95 - (Math.random() / 10));
        if (config.isOn) {
            if (age >= lifespan && !isFading) {
                this.alpha = 1 - ((age - lifespan) / (maxAge - lifespan));
            } else if (!isFading) {
                this.alpha = 1;
            }
        } else {
            this.markDead();
        }

        if (config.doubleSided) {
            //face the direction the player is in, so it works with transparent blocks

            assert client.player != null; //This won't have any consequences...
            Vec3d pos1 = client.player.getCameraPosVec(1f);
            Vec3d pos2 = new Vec3d(this.x, this.y, this.z);

            Vec3d relativePos = pos1.subtract(pos2);

            Vector3f forward = new Vector3f(0, 0, 1);

            Vector3f rotatedForward = this.rotation.transform(new Vector3f(forward));

            double absX = Math.abs(rotatedForward.x);
            double absY = Math.abs(rotatedForward.y);
            double absZ = Math.abs(rotatedForward.z);

            String primaryAxis;
            if (absZ > absX && absZ > absY) {
                primaryAxis = "z";
            } else if (absX > absY) {
                primaryAxis = "x";
            } else {
                primaryAxis = "y";
            }

            double x = relativePos.x;
            double y = relativePos.y;
            double z = relativePos.z;

            switch (primaryAxis) {
                case "x":
                    if (x > 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(-90))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(-90));
                        }
                    } else if (x < 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(90))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(90));
                        }
                    }
                    break;
                case "y":
                    if (y > 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateX((float) Math.toRadians(90))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateX((float) Math.toRadians(90));
                        }
                    } else if (y < 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateX((float) Math.toRadians(-90))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateX((float) Math.toRadians(-90));
                        }
                    }
                    break;
                case "z":
                    if (z > 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(180))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(180));
                        }
                    } else if (z < 0) {
                        if (config.randomRotation) {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(0))
                                    .rotateZ((float) Math.toRadians(randomRotation));
                        } else {
                            this.rotation = new Quaternionf().rotateY((float) Math.toRadians(0));
                        }
                    }
                    break;
            }
        }

        boolean shouldFade = false;
        boolean isEntityParticle = initialEntityPos != null && initialHitPos != null;

        if (isEntityParticle) {
            Entity entity = null;
            for (Entity e : world.getEntities()) {
                if (e.getPos().squaredDistanceTo(initialEntityPos) < 0.01) {
                    entity = e;
                    break;
                }
            }

            if (entity != null) {
                Vec3d currentEntityPos = entity.getPos();
                Vec3d entityMovement = currentEntityPos.subtract(initialEntityPos);
                Vec3d newParticlePos = initialHitPos.add(entityMovement);

                double distanceMoved = initialEntityPos.distanceTo(currentEntityPos);
                if (distanceMoved > 0.1) { // Entity has moved significantly
                    shouldFade = true;
                } else {
                    // Update particle position relative to entity movement
                    this.setPos(newParticlePos.x, newParticlePos.y, newParticlePos.z);
                }
            } else {
                // Entity not found, possibly despawned
                shouldFade = true;
            }
        } else if (initialBlockPos != null && initialBlockHitPos != null) {
            BlockState blockState = this.world.getBlockState(this.initialBlockPos);
            if (blockState.isAir()) {
                shouldFade = true;
            } else {
                // Maintain the initial displacement from the block
                Vec3d displacement = new Vec3d(this.x, this.y, this.z).subtract(initialBlockHitPos);
                this.setPos(initialBlockHitPos.x + displacement.x,
                        initialBlockHitPos.y + displacement.y,
                        initialBlockHitPos.z + displacement.z);
            }
        }

        // Start fading if necessary
        if (shouldFade && !isFading) {
            isFading = true;
            fadeStartAge = age;
            fadeDuration = isEntityParticle ? ENTITY_FADE_DURATION : BLOCK_FADE_DURATION;
        }

        // Handle fading
        if (isFading) {
            int fadeAge = age - fadeStartAge;
            if (fadeAge >= fadeDuration) {
                this.markDead();
            } else {
                this.alpha = 1.0f - (float)fadeAge / fadeDuration;
            }
        }

        // Handle normal particle aging
        if (this.age++ >= this.maxAge) {
            this.markDead();
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;
        private static Direction side;
        private static HitResult lastHit;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
            LOGGER.info("WhiteDotParticle Factory initialized with spriteProvider");
        }

        public static void setDirection(Direction sideGot) {
            side = sideGot;
        }

        public static void setLastHit(HitResult hitResult) {
            lastHit = hitResult;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            Direction direction = side;
            BlockPos particlePos = null;
            Entity hitEntity = null;
            Vec3d hitPos = new Vec3d(x, y, z);
            int blockColor = -1;

            if (lastHit != null) {
                switch (lastHit.getType()) {
                    case BLOCK:
                        BlockHitResult blockHit = (BlockHitResult) lastHit;
                        particlePos = blockHit.getBlockPos();
                        hitPos = blockHit.getPos();
                        // Add displacement here as well
                        double displacement = 0.005 + Math.random() * 0.005;
                        hitPos = hitPos.add(
                                direction.getOffsetX() * displacement,
                                direction.getOffsetY() * displacement,
                                direction.getOffsetZ() * displacement
                        );

                        if (config.particleColorMode == BoDaRConfig.ColorMode.DEFAULT) {
                            break;
                        } else {
                            blockColor = getBlockColor(world, particlePos);
                        }

                        break;
                    case ENTITY:
                        EntityHitResult entityHit = (EntityHitResult) lastHit;
                        hitEntity = entityHit.getEntity();
                        hitPos = entityHit.getPos();
                        break;
                    default:
                        break;
                }
            }

            WhiteDotParticle particle = new WhiteDotParticle(world, hitPos.x, hitPos.y, hitPos.z, velocityX, velocityY, velocityZ, direction, particlePos, hitEntity, hitPos, blockColor);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
