package net.bouncingelf10.bodar;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
        LOGGER.info("Key binding registered");
    }
}
