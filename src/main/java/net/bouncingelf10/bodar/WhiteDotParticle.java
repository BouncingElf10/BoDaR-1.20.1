package net.bouncingelf10.bodar;

import net.minecraft.client.particle.*;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import me.shedaniel.math.Color;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class WhiteDotParticle extends SpriteBillboardParticle {

    static String blockIDString;
    static Vec3d colorBlockID;
    static BoDaRConfig config = BoDaRConfig.get();

    private static final Set<String> ores = new HashSet<>();
    private static final Set<String> functional = new HashSet<>();

    static Vec3d oreColor;
    static Vec3d functionalColor;
    static Vec3d defaultColor;


    static void resetColors() {
        Color oreColorHex = Color.ofRGB(Integer.valueOf(("#" + config.oreColor).substring( 1, 3 ), 16 ),
                Integer.valueOf(("#" + config.oreColor).substring( 3, 5 ), 16 ),
                Integer.valueOf(("#" + config.oreColor).substring( 5, 7 ), 16 ));

        Color functionalColorHex = Color.ofRGB(Integer.valueOf(("#" + config.functionalColor).substring( 1, 3 ), 16 ),
                Integer.valueOf(("#" + config.functionalColor).substring( 3, 5 ), 16 ),
                Integer.valueOf(("#" + config.functionalColor).substring( 5, 7 ), 16 ));

        oreColor = new Vec3d(oreColorHex.getRed(), oreColorHex.getGreen(), oreColorHex.getBlue());
        functionalColor = new Vec3d(functionalColorHex.getRed(), functionalColorHex.getGreen(), functionalColorHex.getBlue());
        defaultColor = new Vec3d(254, 254, 254);
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

            for (int i = 0; i < oresArray.size(); i++) {
                ores.add(oresArray.get(i).getAsString());
            }

            for (int i = 0; i < functionalArray.size(); i++) {
                functional.add(functionalArray.get(i).getAsString());
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
        } else {
            return defaultColor;
        }
    }

    private final Quaternionf rotation;
    protected WhiteDotParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Direction direction) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = config.particleSize;
        this.maxAge = config.maxAge;
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.rotation = getRotationQuaternion(direction);
        this.setColor((float) ((float) 255 - colorBlockID.x), (float) ((float) 255 - colorBlockID.y), (float) ((float) 255 - colorBlockID.z));
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

    @Override
    public void tick() {
        super.tick();
        float lifespan = (float) (maxAge * 0.95 - (Math.random() / 10));  // Calculate 95% of the maxAge
        if (age >= lifespan) {
            this.alpha = 1 - ((age - lifespan) / (maxAge - lifespan));
        } else {
            this.alpha = 1;
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
            WhiteDotParticle particle = new WhiteDotParticle(world, x, y, z, velocityX, velocityY, velocityZ, direction);
            //LOGGER.info("Creating WhiteDotParticle at ({}, {}, {}) with velocity ({}, {}, {}) and direction ({})", x, y, z, velocityX, velocityY, velocityZ, side);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
