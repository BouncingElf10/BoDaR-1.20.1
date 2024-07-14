package net.bouncingelf10.bodar.init.mixin;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.systems.RenderSystem;

@Mixin(WorldRenderer.class)
public class SkyboxMixin {
    @Inject(method = "renderSky(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FLnet/minecraft/client/render/Camera;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void onRenderSky(CallbackInfo ci) {
        BoDaRConfig config = BoDaRConfig.get();
        if (config.invisibleWorldMode && config.isOn) {
            RenderSystem.clearColor(
                    ((config.skyboxColor >> 16) & 0xFF) / 255f,
                    ((config.skyboxColor >> 8) & 0xFF) / 255f,
                    (config.skyboxColor & 0xFF) / 255f,
                    1.0f
            );
            RenderSystem.clear(16384, false);
            ci.cancel();
        }
    }
}