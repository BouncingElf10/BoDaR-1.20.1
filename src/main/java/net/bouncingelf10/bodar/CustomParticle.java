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

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class CustomParticle extends SpriteBillboardParticle {

    private final Quaternionf rotation;

    protected CustomParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Direction direction) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = 0.02F;
        this.maxAge = 400;
        this.setVelocity(velocityX, velocityY, velocityZ);
        this.rotation = getRotationQuaternion(direction);
    }

    private float getScale(float tickDelta) {
        return 0.02F;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    private Quaternionf getRotationQuaternion(Direction direction) {
        switch (direction) {
            case UP:
                LOGGER.info("UP");
                return new Quaternionf().rotateX((float) Math.toRadians(90));
            case DOWN:
                LOGGER.info("DOWN");
                return new Quaternionf().rotateX((float) Math.toRadians(-90));
            case NORTH:
                LOGGER.info("NORTH");
                return new Quaternionf().rotateY((float) Math.toRadians(0));
            case EAST:
                LOGGER.info("EAST");
                return new Quaternionf().rotateY((float) Math.toRadians(-90));
            case SOUTH:
                LOGGER.info("SOUTH");
                return new Quaternionf().rotateY((float) Math.toRadians(180));
            case WEST:
                LOGGER.info("WEST");
                return new Quaternionf().rotateY((float) Math.toRadians(90));
            default:
                LOGGER.warn("No valid direction");
                return new Quaternionf();
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

        float scale = this.getScale(tickDelta);
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
        float lifespan = (float) (maxAge * 0.95);  // Calculate 95% of the maxAge
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
            LOGGER.info("CustomParticle Factory initialized with spriteProvider");
        }

        static Direction side;
        public static void setDirection(Direction sideGot) {
            side = sideGot;
        }

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            Direction direction = side; // Use the stored direction
            CustomParticle particle = new CustomParticle(world, x, y, z, velocityX, velocityY, velocityZ, direction);
            //LOGGER.info("Creating CustomParticle at ({}, {}, {}) with velocity ({}, {}, {}) and direction ({})", x, y, z, velocityX, velocityY, velocityZ, side);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
