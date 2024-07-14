package net.bouncingelf10.bodar.init.mixin;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class RenderManagerMixin {
    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void onRenderEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        if (BoDaRConfig.get().invisibleWorldMode && BoDaRConfig.get().isOn) {
            if (entity instanceof PlayerEntity) {
                return;
            } else if (entity instanceof ItemEntity) {
                return;
            }
            ci.cancel();
        }
    }
    @Inject(method = "renderLayer", at = @At("HEAD"), cancellable = true)
    private void onRenderBlock(RenderLayer renderLayer, MatrixStack matrices, double cameraX, double cameraY, double cameraZ, Matrix4f positionMatrix, CallbackInfo ci) {
        if (BoDaRConfig.get().invisibleWorldMode && BoDaRConfig.get().isOn) {
            ci.cancel();
        }
    }
    @Inject(method = "renderClouds(Lnet/minecraft/client/util/math/MatrixStack;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void onRenderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (BoDaRConfig.get().invisibleWorldMode && BoDaRConfig.get().isOn) {
            ci.cancel();
        }
    }
    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void onRenderWeather(LightmapTextureManager manager, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci) {
        if (BoDaRConfig.get().invisibleWorldMode && BoDaRConfig.get().isOn) {
            ci.cancel();
        }
    }
}