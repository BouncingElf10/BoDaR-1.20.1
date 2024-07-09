package net.bouncingelf10.bodar;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.bouncingelf10.bodar.networking.BoDaRPackets;
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

	public static final DefaultParticleType WhiteDotParticle = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		LOGGER.info("BoDaR Initialized!");
		Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "custom_particle"), WhiteDotParticle);
		LOGGER.info("WhiteDotParticle registered with ID bodar:custom_particle");

		LOGGER.info("Registering AutoConfig");
		AutoConfig.register(BoDaRConfig.class, GsonConfigSerializer::new);

		LOGGER.info("Registering C2S Packets");
		BoDaRPackets.registerC2SPackets();
	}
}
