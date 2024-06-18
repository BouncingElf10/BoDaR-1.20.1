package net.bouncingelf10.bodar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import static net.bouncingelf10.bodar.RayCast.rayCast;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                Window window = client.getWindow();
                LOGGER.info("Screen Resolution: {}, {}", window.getWidth(), window.getHeight());

                var density = 1;
                var width = window.getWidth() / 60;
                var height = window.getHeight() / 60;

                var totalPoints = Math.floor(width * height * density);

                var spacing = Math.sqrt(1 / density);

                // Adjust origin to the center of the screen
                var centerX = width / 2;
                var centerY = height / 2;

                // Distribute points across the window
                for (var x = -centerX; x < centerX; x += spacing) {
                    for (var y = -centerY; y < centerY; y += spacing) {
                        // Adjust x and y to the center of each grid cell for more accurate distribution
                        float xOffset = (float) (x + spacing / 2);
                        float yOffset = (float) (y + spacing / 2);
                        LOGGER.info("New Offset: {}, {}", xOffset, yOffset);
                        rayCast(xOffset, yOffset);
                    }
                }
            }
        });

        ParticleFactoryRegistry.getInstance().register(BoDaR.CUSTOM_PARTICLE, CustomParticle.Factory::new);
        LOGGER.info("CustomParticle Factory registered for bodar:custom_particle");
    }
}