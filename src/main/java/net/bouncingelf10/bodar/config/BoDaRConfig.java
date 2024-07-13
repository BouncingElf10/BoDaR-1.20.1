package net.bouncingelf10.bodar.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.MinecraftClient;


@Config(name = "bodar")
public class BoDaRConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    public boolean isOn = true;

    @ConfigEntry.Gui.Tooltip
    public boolean invisibleWorldMode = false;

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
    public boolean doubleSided = true;

    @ConfigEntry.Gui.Tooltip
    public int maxAge = 400;

    @ConfigEntry.Gui.RequiresRestart
    @ConfigEntry.Gui.Tooltip
    public int fadeOutTime = 20;

    @ConfigEntry.Gui.Tooltip
    public float particleSize = 0.02f;

    public enum ColorMode {
        DEFAULT,
        MIXED,
        WORLD
    }

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @ConfigEntry.Gui.Tooltip(count = 4)
    public ColorMode particleColorMode = ColorMode.DEFAULT;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int oreColor = 119935;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.ColorPicker
    public int functionalColor = 11112511;

    @ConfigEntry.Gui.Tooltip
    public String[] ores = {
            "minecraft:coal_ore",
            "minecraft:iron_ore",
            "minecraft:gold_ore",
            "minecraft:diamond_ore",
            "minecraft:emerald_ore",
            "minecraft:redstone_ore",
            "minecraft:lapis_ore",
            "minecraft:nether_quartz_ore",
            "minecraft:nether_gold_ore",
            "minecraft:copper_ore",
            "minecraft:deepslate_coal_ore",
            "minecraft:deepslate_iron_ore",
            "minecraft:deepslate_gold_ore",
            "minecraft:deepslate_diamond_ore",
            "minecraft:deepslate_emerald_ore",
            "minecraft:deepslate_redstone_ore",
            "minecraft:deepslate_lapis_ore",
            "minecraft:deepslate_copper_ore"
    };

    @ConfigEntry.Gui.Tooltip
    public String[] functionals = {
            "minecraft:furnace",
            "minecraft:crafting_table",
            "minecraft:anvil",
            "minecraft:chest",
            "minecraft:ender_chest",
            "minecraft:shulker_box",
            "minecraft:brewing_stand",
            "minecraft:enchanting_table",
            "minecraft:beacon",
            "minecraft:jukebox",
            "minecraft:dispenser",
            "minecraft:dropper",
            "minecraft:hopper",
            "minecraft:blast_furnace",
            "minecraft:smoker",
            "minecraft:stonecutter",
            "minecraft:grindstone",
            "minecraft:cartography_table",
            "minecraft:loom",
            "minecraft:smithing_table",
            "minecraft:barrel",
            "minecraft:composter",
            "minecraft:ladder"
    };


    public static BoDaRConfig get() {
        return AutoConfig.getConfigHolder(BoDaRConfig.class).getConfig();
    }
}
