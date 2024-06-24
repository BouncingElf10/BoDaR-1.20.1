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
    public int size = 60;

    @ConfigEntry.Gui.Tooltip
    public double randomness = 1;

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
    public int functionalColor = 999706;


    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
