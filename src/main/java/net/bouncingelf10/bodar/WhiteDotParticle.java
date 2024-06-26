package net.bouncingelf10.bodar;

import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;
import static net.bouncingelf10.bodar.RayCast.hit;

public class WhiteDotParticle extends SpriteBillboardParticle {

    static String blockIDString;
    static Vec3d colorBlockID;
    static BoDaRConfig config = BoDaRConfig.get();
    MinecraftClient client = MinecraftClient.getInstance();
    private static final Set<String> ores = new HashSet<>();
    private static final Set<String> functional = new HashSet<>();
    private static final Set<String> water = new HashSet<>();

    static Vec3d oreColor;
    static Vec3d functionalColor;
    static Vec3d waterColor;
    static Vec3d defaultColor;


    static void resetColors() {
        Color oreColorHex = parseColor(String.valueOf(config.oreColor));
        Color functionalColorHex = parseColor(String.valueOf(config.functionalColor));

        oreColor = new Vec3d(oreColorHex.getRed(), oreColorHex.getGreen(), oreColorHex.getBlue());
        functionalColor = new Vec3d(functionalColorHex.getRed(), functionalColorHex.getGreen(), functionalColorHex.getBlue());
        waterColor = new Vec3d(0, 0, 254);
        defaultColor = new Vec3d(254, 254, 254);
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

    static {
        loadBlockData();
        resetColors();
    }

    static void getBlockID(String blockID) {
        blockIDString = blockID;
        colorBlockID = getColorBlockID(blockIDString);
    }

    static void loadBlockData() {
        try (InputStream inputStream = WhiteDotParticle.class.getResourceAsStream("/assets/bodar/blocks.json")) {
            if (inputStream == null) {
                LOGGER.warn("Resource not found: /assets/bodar/blocks.json");
                throw new IOException("Resource not found: /assets/bodar/blocks.json");
            }
            JsonObject jsonObject = JsonParser.parseReader(new InputStreamReader(inputStream)).getAsJsonObject();
            JsonArray oresArray = jsonObject.getAsJsonArray("ores");
            JsonArray functionalArray = jsonObject.getAsJsonArray("functional");
            JsonArray waterArray = jsonObject.getAsJsonArray("water");

            for (int i = 0; i < oresArray.size(); i++) {
                ores.add(oresArray.get(i).getAsString());
            }

            for (int i = 0; i < functionalArray.size(); i++) {
                functional.add(functionalArray.get(i).getAsString());
            }

            for (int i = 0; i < waterArray.size(); i++) {
                water.add(waterArray.get(i).getAsString());
            }

        } catch (IOException e) {
            LOGGER.warn(String.valueOf(e));
        }
    }

    public static Vec3d getColorBlockID(String blockIDString) {
        if (ores.contains(blockIDString)) {
            return oreColor;
        } else if (functional.contains(blockIDString)) {
            return functionalColor;
        } else if (water.contains(blockIDString)) {
            return waterColor;
        } else {
            return defaultColor;
        }
    }

    private final BlockPos initialBlockPos;
    private Quaternionf rotation;
    protected WhiteDotParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Direction direction, BlockPos blockPos) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = config.particleSize;
        this.maxAge = config.maxAge;
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.rotation = getRotationQuaternion(direction);
        this.setColor((float) ((float) 255 - colorBlockID.x), (float) ((float) 255 - colorBlockID.y), (float) ((float) 255 - colorBlockID.z));
        this.initialBlockPos = blockPos;
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

    private boolean isFading;
    private int fadeOutStart;
    private int fadeOutDuration;

    @Override
    public void tick() {
        super.tick();

        float lifespan = (float) (maxAge * 0.95 - (Math.random() / 10));  // Calculate 95% of the maxAge
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

        // Check the block state of the initial block position
        BlockState blockState = this.world.getBlockState(this.initialBlockPos);
        // Check if the block is air (indicating it has been broken)
        if (blockState.isAir() && !isFading) {
            isFading = true;
            fadeOutStart = age; // Store the age when fading starts
            fadeOutDuration = config.fadeOutTime; // Duration over which the particle will fade out (20 ticks)
        }

        // Handle fading out
        if (isFading) {
            int fadeOutProgress = age - fadeOutStart;
            if (fadeOutProgress < fadeOutDuration) {
                this.alpha = 1 - (float) fadeOutProgress / fadeOutDuration;
            } else {
                this.markDead();
            }
        }
    }

    public static class Factory implements ParticleFactory<DefaultParticleType> {
        private final SpriteProvider spriteProvider;

        public Factory(SpriteProvider spriteProvider) {
            this.spriteProvider = spriteProvider;
            LOGGER.info("WhiteDotParticle Factory initialized with spriteProvider");
        }

        static Direction side;
        public static void setDirection(Direction sideGot) {
            side = sideGot;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            Direction direction = side; // Use the stored direction
            ///////////////////////////////////
            BlockPos particlePos = null;
            switch (hit.getType()) {
                case MISS, ENTITY: break;
                case BLOCK: {
                    BlockHitResult blockHit = (BlockHitResult) hit;
                    particlePos = blockHit.getBlockPos();
                }
            }
            ///////////////////////////////////
            BlockPos blockPos = particlePos; // Capture the block position
            WhiteDotParticle particle = new WhiteDotParticle(world, x, y, z, velocityX, velocityY, velocityZ, direction, blockPos);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
