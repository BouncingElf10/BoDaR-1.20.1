package net.bouncingelf10.bodar;

import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModKeyBindings {
    public static final Logger LOGGER = LoggerFactory.getLogger(ModKeyBindings.class);

    public static KeyBinding RKeyBinding;

    public static void registerKeyBindings() {
        RKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bodar.example", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_R, // The keycode of the key
                "category.bodar.main" // The translation key of the keybinding's category.
        ));

        KeyBinding openConfigKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bodar.open_config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                "category.bodar"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                LOGGER.info("Config key pressed!");
                if (client != null) {
                    LOGGER.info("Opening config screen");
                    client.setScreen(AutoConfig.getConfigScreen(BoDaRConfig.class, null).get());
                }
            }
        });

    }
}
