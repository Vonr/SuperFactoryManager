package vswe.superfactory;


import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import vswe.superfactory.config.ConfigHolder;


@Mod(SuperFactoryManager.MOD_ID)
@Mod.EventBusSubscriber(bus= Mod.EventBusSubscriber.Bus.MOD)
public class SuperFactoryManager {
	public final static String MOD_ID = "sfm";
	public static final Logger LOGGER = LogManager.getLogger();

	public SuperFactoryManager() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ConfigHolder.SERVER_SPEC);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);

		bus.addListener(this::onSetup);
	}

	private void onSetup(final FMLCommonSetupEvent e) {

	}
}
