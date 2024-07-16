package net.bouncingelf10.bodar;

import net.bouncingelf10.bodar.config.BoDaRConfig;
import net.bouncingelf10.bodar.networking.BoDaRPackets;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;

public class BoDaR implements ModInitializer {
	public static final String MOD_ID = "bodar";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final Identifier BEEP_SOUND_ID = new Identifier(MOD_ID, "beep");
	public static SoundEvent BEEP_SOUND_EVENT = SoundEvent.of(BEEP_SOUND_ID);

	public static final Identifier WAVE_SOUND_ID = new Identifier(MOD_ID, "wave");
	public static SoundEvent WAVE_SOUND_EVENT = SoundEvent.of(WAVE_SOUND_ID);

	public static final DefaultParticleType WhiteDotParticle = FabricParticleTypes.simple();

	@Override
	public void onInitialize() {
		LOGGER.info("BoDaR Initialized!");
		Registry.register(Registries.PARTICLE_TYPE, new Identifier(MOD_ID, "custom_particle"), WhiteDotParticle);
		LOGGER.info("WhiteDotParticle registered with ID bodar:custom_particle");

		LOGGER.info("Registering AutoConfig");
		AutoConfig.register(BoDaRConfig.class, GsonConfigSerializer::new);

		LOGGER.info("Registering C2S Packets and Sound");
		BoDaRPackets.registerC2SPackets();
		Registry.register(Registries.SOUND_EVENT, BEEP_SOUND_ID, BEEP_SOUND_EVENT);
		Registry.register(Registries.SOUND_EVENT, WAVE_SOUND_ID, WAVE_SOUND_EVENT);

	}
}
