package net.bouncingelf10.bodar;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.AutoConfig;

@Config(name = "bodar")
public class BoDaRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public int size = 10;

    @ConfigEntry.Gui.Tooltip
    public double randomness = 0.5;

    @ConfigEntry.Gui.Tooltip
    public boolean randomRotation = true;

    @ConfigEntry.Gui.Tooltip
    public int maxAge = 400;

    @ConfigEntry.Gui.Tooltip
    public double particleSize = 0.02;

    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
