package net.bouncingelf10.bodar.client;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.bouncingelf10.bodar.init.ModKeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bouncingelf10.bodar.client.RayCast.rayCast;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.loadBlocks;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.resetColors;

public class BoDaRClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoDaRClient.class);

    @Override
    public void onInitializeClient() {

        LOGGER.info("Initializing client");
        ModKeyBindings.registerKeyBindings();
        LOGGER.info("Keybind(s) Initialised");

        BoDaRConfig config = BoDaRConfig.get();
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (ModKeyBindings.RKeyBinding.isPressed()) {
                    if (config.isOn) {
                        LOGGER.info("R pressed");
                        loadBlocks();
                        resetColors();
                        //LOGGER.info("Screen Resolution: {}, {}", window.getWidth(), window.getHeight());
                        var size = config.size;
                        var density = config.density;
                        var randomness = config.randomness;
                        for (double i = size * -1; i <= size; i = i + density) {
                            for (double j = size * -1; j <= size; j = j + density) {
                                float xOffset = (float) (j + Math.random() / randomness);
                                float yOffset = (float) (i + Math.random() / randomness);
                                LOGGER.info("New Offset: {}, {}", xOffset, yOffset);
                                rayCast(xOffset, yOffset);
                            }
                        }
                    }
                }
            });

        ParticleFactoryRegistry.getInstance().register(BoDaR.WhiteDotParticle, WhiteDotParticle.Factory::new);
        LOGGER.info("WhiteDotParticle Factory registered for bodar:custom_particle");
    }
}