package vswe.superfactory;

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
import vswe.superfactory.components.ModItemHelper;
import vswe.superfactory.network.FileHelper;
import vswe.superfactory.network.PacketEventHandler;
import vswe.superfactory.proxy.CommonProxy;
import vswe.superfactory.registry.ModBlocks;

import static vswe.superfactory.registry.ModBlocks.MANAGER;

@Mod(modid = SuperFactoryManager.MODID, name = "Super Factory Manager", version = "@VERSION@", dependencies = "required-after:forge@[14.21.0.2359,)")
public class SuperFactoryManager {
	public static final String              CHANNEL                      = "factorymanager";
	public static final String              MODID                        = "superfactorymanager";
	public static final byte                NBT_CURRENT_PROTOCOL_VERSION = 13;
	public static final String              NBT_PROTOCOL_VERSION         = "ProtocolVersion";
	public static final String              RESOURCE_LOCATION            = "superfactorymanager";
	public static final String              UNLOCALIZED_START            = "sfm.";
	public static final CreativeTabs        creativeTab                  = new CreativeTabs("sfm") {
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
	public static       SuperFactoryManager instance;
	public static       FMLEventChannel     packetHandler;
	@SidedProxy(clientSide = "vswe.superfactory.proxy.ClientProxy", serverSide = "vswe.superfactory.proxy.CommonProxy")
	public static       CommonProxy         proxy;

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
