package net.bouncingelf10.bodar;

import com.mojang.blaze3d.systems.RenderSystem;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.gui.ConfigScreenProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import javax.swing.*;

@Config(name = "bodar")
public class BoDaRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean isOn = true;

    @ConfigEntry.Gui.Tooltip
    public int size = 60;

    @ConfigEntry.Gui.Tooltip
    public double randomness = 1;

    @ConfigEntry.Gui.Tooltip
    public boolean randomRotation = true;

    @ConfigEntry.Gui.Tooltip
    public int maxAge = 400;

    @ConfigEntry.Gui.Tooltip
    public float particleSize = 0.02f;

    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
