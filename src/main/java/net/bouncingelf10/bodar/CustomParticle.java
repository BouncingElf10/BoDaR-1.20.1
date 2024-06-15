package net.bouncingelf10.bodar;

import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import static net.bouncingelf10.bodar.BoDaR.LOGGER;

public class CustomParticle extends SpriteBillboardParticle {
    protected CustomParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        super(world, x, y, z, velocityX, velocityY, velocityZ);
        this.scale = 0.2F;
        this.maxAge = 400;
        this.setVelocity(velocityX, velocityY, velocityZ);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
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
            //LOGGER.info("bruh please work"); (did in fact not work)
            return particle;
        }
    }
}
