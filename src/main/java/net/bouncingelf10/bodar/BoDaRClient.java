package net.bouncingelf10.bodar;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Vec3d;
import static net.bouncingelf10.bodar.RayCast.rayCast;


public class BoDaRClient implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger(BoDaRClient.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing client");
        ModKeyBindings.registerKeyBindings();
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (ModKeyBindings.exampleKeyBinding.wasPressed()) {
                LOGGER.info("R pressed");
                rayCast(0f, 0f);
            }
        });
    }
}
