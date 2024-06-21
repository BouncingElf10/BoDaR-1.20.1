package net.bouncingelf10.bodar;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

@Config(name = "bodar")
public class BoDaRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public int size = 10;

    @ConfigEntry.Gui.Tooltip
    public double randomness = 0.5;

    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
