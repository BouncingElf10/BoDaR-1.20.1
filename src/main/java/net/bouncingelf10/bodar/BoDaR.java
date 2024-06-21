package net.bouncingelf10.bodar;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class BoDaR implements ModInitializer {
	public static final String MOD_ID = "bodar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final DefaultParticleType CUSTOM_PARTICLE = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		LOGGER.info("BoDaR Initialized!");
		Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "custom_particle"), CUSTOM_PARTICLE);
		LOGGER.info("CustomParticle registered with ID bodar:custom_particle");

		LOGGER.info("Registering AutoConfig");
		AutoConfig.register(BoDaRConfig.class, GsonConfigSerializer::new);
	}
}
