package net.bouncingelf10.bodar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bouncingelf10.bodar.RayCast.rayCast;

public class BoDaRClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoDaRClient.class);
    private static final Identifier JSON_ID = new Identifier("bodar", "particles/custom_particle.json");
    private static final Identifier TEXTURE_ID = new Identifier("bodar", "textures/particle/custom_particle.png");

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing client");
        ModKeyBindings.registerKeyBindings();
        LOGGER.info("Keybind(s) Initialised");

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBindings.RKeyBinding.isPressed()) {
                LOGGER.info("R pressed");
                Window window = client.getWindow();
                //LOGGER.info("Screen Resolution: {}, {}", window.getWidth(), window.getHeight());
                var size = 60;
                var randomness = 1;
                var width = window.getWidth() / size;
                var height = window.getHeight() / size;

                for (var i = (int)(height / 2 * -1); i <= (int)(height / 2); i++) {
                    for (var j = (int)(width / 2 * -1); j <= (int)(width / 2); j++) {
                        float xOffset = (float) (j + Math.random() * (2 * randomness) - randomness);
                        float yOffset = (float) (i + Math.random() * (2 * randomness) - randomness);
                        //LOGGER.info("New Offset: {}, {}", xOffset, yOffset);
                        rayCast(xOffset, yOffset);
                    }
                }

            }
        });

        ParticleFactoryRegistry.getInstance().register(BoDaR.WhiteDotParticle, WhiteDotParticle.Factory::new);
        LOGGER.info("WhiteDotParticle Factory registered for bodar:custom_particle");
    }
}