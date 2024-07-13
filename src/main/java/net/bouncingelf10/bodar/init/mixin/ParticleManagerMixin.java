package net.bouncingelf10.bodar.init.mixin;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(
            method = "addParticle(Lnet/minecraft/particle/ParticleEffect;DDDDDD)Lnet/minecraft/client/particle/Particle;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void onAddParticle(ParticleEffect parameters, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable ci) {
        //LOGGER.info(parameters.toString());
        if (BoDaRConfig.get().invisibleWorldMode) {
            if (parameters.getType().toString().contains("net.fabricmc.fabric.api.particle.v1.FabricParticleTypes")) {
                return;
            }
            ci.setReturnValue(null);
        }

    }
}