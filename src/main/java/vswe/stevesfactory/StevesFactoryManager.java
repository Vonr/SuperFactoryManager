package vswe.stevesfactory;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import vswe.stevesfactory.components.ModItemHelper;
import vswe.stevesfactory.network.FileHelper;
import vswe.stevesfactory.network.PacketEventHandler;
import vswe.stevesfactory.proxy.CommonProxy;
import vswe.stevesfactory.registry.ModBlocks;

import static vswe.stevesfactory.registry.ModBlocks.MANAGER;

@Mod(modid = StevesFactoryManager.MODID, name = "Steve's Factory Manager", version = "@VERSION@", dependencies = "required-after:forge@[14.21.0.2359,)")
public class StevesFactoryManager {
	public static final String       CHANNEL                      = "factorymanager";
	public static final String       MODID                        = "stevesfactorymanager";
	public static final byte         NBT_CURRENT_PROTOCOL_VERSION = 13;
	public static final String       NBT_PROTOCOL_VERSION         = "ProtocolVersion";
	public static final String       RESOURCE_LOCATION            = "stevesfactorymanager";
	public static final String       UNLOCALIZED_START            = "sfm.";
	public static final CreativeTabs creativeTab                  = new CreativeTabs("sfm") {
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(MANAGER);
		}

		@Override
		public ItemStack getTabIconItem() {
			return ItemStack.EMPTY;
		}
	};
	@Mod.Instance(MODID)
	public static StevesFactoryManager instance;
	public static FMLEventChannel      packetHandler;
	@SidedProxy(clientSide = "vswe.stevesfactory.proxy.ClientProxy", serverSide = "vswe.stevesfactory.proxy.CommonProxy")
	public static CommonProxy          proxy;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		packetHandler = NetworkRegistry.INSTANCE.newEventDrivenChannel(CHANNEL);

		proxy.preInit();

		FileHelper.setConfigDir(event.getModConfigurationDirectory());

		packetHandler.register(new PacketEventHandler());


		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		FMLInterModComms.sendMessage("Waila", "register", "Provider.callbackRegister");
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		ModBlocks.addRecipes();

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ModItemHelper.init();
	}


}
