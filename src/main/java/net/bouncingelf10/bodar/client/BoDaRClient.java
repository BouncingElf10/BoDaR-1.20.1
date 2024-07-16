package net.bouncingelf10.bodar.client;

import net.bouncingelf10.bodar.BoDaR;
import net.bouncingelf10.bodar.networking.BoDaRPackets;
import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.bouncingelf10.bodar.init.BoDaRKeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.sound.SoundCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.bouncingelf10.bodar.client.RayCast.rayCast;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.loadBlocks;
import static net.bouncingelf10.bodar.client.WhiteDotParticle.resetColors;

public class BoDaRClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoDaRClient.class);

    @Override
    public void onInitializeClient() {

        LOGGER.info("Initializing client: Registering Keybindings and S2CPackets");
        BoDaRKeyBindings.registerKeyBindings();

        BoDaRPackets.registerS2CPackets();
        final PositionedSoundInstance[] currentSound = {null};
        BoDaRConfig config = BoDaRConfig.get();
            ClientTickEvents.END_CLIENT_TICK.register(client -> {
                if (BoDaRKeyBindings.RKeyBinding.isPressed()) {
                    if (config.isOn && currentSound[0] == null) {
                        LOGGER.info("R pressed");
                        loadBlocks();
                        resetColors();
                        //LOGGER.info("Screen Resolution: {}, {}", window.getWidth(), window.getHeight());

                        currentSound[0] = new PositionedSoundInstance(BoDaR.WAVE_SOUND_EVENT.getId(), SoundCategory.AMBIENT, 1F, 1.0F, SoundInstance.createRandom(), true, 0, SoundInstance.AttenuationType.NONE, 0.0, 0.0, 0.0, true);
                        MinecraftClient.getInstance().getSoundManager().play(currentSound[0]);

                        var size = config.size;
                        var density = config.density;
                        var randomness = config.density / 2;
                        for (double i = size * -1; i <= size; i = i + density) {
                            for (double j = size * -1; j <= size; j = j + density) {
                                float xOffset = (float) (j + (Math.random() * 2 - 1) * randomness);
                                float yOffset = (float) (i + (Math.random() * 2 - 1) * randomness);
                                //LOGGER.info("New Offset: {}, {}", xOffset, yOffset);
                                rayCast(xOffset, yOffset);
                            }
                        }
                    } else if (config.isOn && currentSound[0] != null) {
                        LOGGER.info("R pressed");
                        loadBlocks();
                        resetColors();
                        //LOGGER.info("Screen Resolution: {}, {}", window.getWidth(), window.getHeight());

                        var size = config.size;
                        var density = config.density;
                        var randomness = config.density / 2;
                        for (double i = size * -1; i <= size; i = i + density) {
                            for (double j = size * -1; j <= size; j = j + density) {
                                float xOffset = (float) (j + (Math.random() * 2 - 1) * randomness);
                                float yOffset = (float) (i + (Math.random() * 2 - 1) * randomness);
                                //LOGGER.info("New Offset: {}, {}", xOffset, yOffset);
                                rayCast(xOffset, yOffset);
                            }
                        }
                    }
                } else {
                    if (currentSound[0] != null) {
                        MinecraftClient.getInstance().getSoundManager().stop(currentSound[0]);
                        currentSound[0] = null;
                        LOGGER.info("R released, sound stopped");
                    }
                }
            });

        ParticleFactoryRegistry.getInstance().register(BoDaR.WhiteDotParticle, WhiteDotParticle.Factory::new);
        LOGGER.info("WhiteDotParticle Factory registered for bodar:custom_particle");
    }
}