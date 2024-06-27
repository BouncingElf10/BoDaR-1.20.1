package net.bouncingelf10.bodar;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.AutoConfig;


@Config(name = "bodar")
public class BoDaRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean isOn = true;

    @ConfigEntry.Gui.Tooltip
    public double size = 5.0;

    @ConfigEntry.Gui.Tooltip
    public double density = 1.0;

    @ConfigEntry.Gui.Tooltip
    public double randomness = 1.0;

    @ConfigEntry.Gui.Tooltip
    public int reach = 24;

    @ConfigEntry.Gui.Tooltip
    public boolean randomRotation = true;

    @ConfigEntry.Gui.Tooltip
    public int maxAge = 400;

    @ConfigEntry.Gui.Tooltip
    public float particleSize = 0.02f;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int oreColor = 119935;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int functionalColor = 11112511;


    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
