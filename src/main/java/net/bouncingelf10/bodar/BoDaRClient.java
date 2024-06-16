package net.bouncingelf10.bodar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static net.bouncingelf10.bodar.RayCast.rayCast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;

public class BoDaRClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoDaRClient.class);
    private static final Identifier JSON_ID = new Identifier("bodar", "particles/custom_particle.json");
    private static final Identifier TEXTURE_ID = new Identifier("bodar", "textures/particle/custom_particle.png");


    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing client");
        ModKeyBindings.registerKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBindings.exampleKeyBinding.wasPressed()) {
                LOGGER.info("R pressed");
                rayCast(0f, 1f);
            }
        });

        ParticleFactoryRegistry.getInstance().register(BoDaR.CUSTOM_PARTICLE, CustomParticle.Factory::new);
        LOGGER.info("CustomParticle Factory registered for bodar:custom_particle");
    }
}