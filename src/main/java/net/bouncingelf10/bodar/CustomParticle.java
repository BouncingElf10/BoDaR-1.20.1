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

    protected CustomParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = 0.2F;
        this.maxAge = 400;
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    private float getScale(float tickDelta) {
        return 0.2F;
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    static Quaternionf rotation;

    public static void getDirection(Direction direction) {
        //LOGGER.info("getDirection: {}", String.valueOf(direction));
        switch (direction) {
                case UP:
                    rotation = new Quaternionf().rotateX((float) Math.toRadians(90)); /*LOGGER.info("SWITCHING TO UP");*/ break;
                case DOWN:
                    rotation = new Quaternionf().rotateX((float) Math.toRadians(-90)); /*LOGGER.info("SWITCHING TO DOWN");*/ break;
                case NORTH:
                    rotation = new Quaternionf().rotateY((float) Math.toRadians(0)); /*LOGGER.info("SWITCHING TO NORTH");*/ break;
                case EAST:
                    rotation = new Quaternionf().rotateY((float) Math.toRadians(-90)); /*LOGGER.info("SWITCHING TO EAST");*/ break;
                case SOUTH:
                    rotation = new Quaternionf().rotateY((float) Math.toRadians(180)); /*LOGGER.info("SWITCHING TO SOUTH");*/ break;
                case WEST:
                    rotation = new Quaternionf().rotateY((float) Math.toRadians(90)); /*LOGGER.info("SWITCHING TO WEST");*/ break;
                default: LOGGER.warn("No valid direction");break;
        }
        //LOGGER.info("Quaternionf rotation: {}", rotation);
    }

    @Override
    public void buildGeometry(VertexConsumer vertexConsumer, Camera camera, float tickDelta) {
        Vec3d cameraPos = camera.getPos();
        float x = (float) (MathHelper.lerp(tickDelta, this.prevPosX, this.x) - cameraPos.getX());
        float y = (float) (MathHelper.lerp(tickDelta, this.prevPosY, this.y) - cameraPos.getY());
        float z = (float) (MathHelper.lerp(tickDelta, this.prevPosZ, this.z) - cameraPos.getZ());


        // Optionally, you can apply random rotation to the particles
        // Quaternion rotation = new Quaternion(
        //     this.random.nextFloat() * 360.0F,
        //     this.random.nextFloat() * 360.0F,
        //     this.random.nextFloat() * 360.0F,
        //     true
        // );
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
            //LOGGER.info(String.valueOf(rotation));
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
        //this.alpha = (-(1/(float)maxAge) * age + 1);
        float lifespan = (float) (maxAge * 0.95);  // Calculate 5% of the maxAge
        if (age >= lifespan) {
            //LOGGER.info(String.valueOf(age));
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

        @Override
        public Particle createParticle(DefaultParticleType type, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            CustomParticle particle = new CustomParticle(world, x, y, z, velocityX, velocityY, velocityZ);
            LOGGER.info("Creating CustomParticle at ({}, {}, {}) with velocity ({}, {}, {})", x, y, z, velocityX, velocityY, velocityZ);
            particle.setSprite(this.spriteProvider);
            return particle;
        }
    }
}
