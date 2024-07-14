package net.bouncingelf10.bodar.init.mixin;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {

    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable ci) {
        if (BoDaRConfig.get().invisibleWorldMode && BoDaRConfig.get().isOn){
            ParticleType<?> particleType = parameters.getType();

            if (particleType != BoDaR.WhiteDotParticle) {
                ci.setReturnValue(null);
            }
        }
    }
}