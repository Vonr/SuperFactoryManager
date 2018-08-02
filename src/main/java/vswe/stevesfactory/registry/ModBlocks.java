package vswe.stevesfactory.registry;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
import reborncore.common.util.RebornCraftingHelper;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.blocks.*;
import vswe.stevesfactory.tiles.*;

import java.util.List;

@Mod.EventBusSubscriber(modid = StevesFactoryManager.MODID)
@GameRegistry.ObjectHolder(StevesFactoryManager.MODID)
public final class ModBlocks {

	public static final Block CABLE            = Blocks.AIR;
	public static final Block CABLE_BREAKER    = Blocks.AIR;
	public static final Block CABLE_BUD        = Blocks.AIR;
	public static final Block CABLE_CAMOUFLAGE = Blocks.AIR;
	public static final Block CABLE_CLUSTER    = Blocks.AIR;
	public static final Block CABLE_INPUT      = Blocks.AIR;
	public static final Block CABLE_INTAKE     = Blocks.AIR;
	public static final Block CABLE_OUTPUT     = Blocks.AIR;
	public static final Block CABLE_RELAY      = Blocks.AIR;
	public static final Block CABLE_SIGN       = Blocks.AIR;
	public static final Block MANAGER          = Blocks.AIR;

	public static List<Block> blocks;

	private ModBlocks() {
	}

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		blocks = Lists.newArrayList(
				new BlockManager().setRegistryName("manager"),
				new BlockCable().setRegistryName("cable"),
				new BlockCableRelay().setRegistryName("cable_relay"),
				new BlockCableOutput().setRegistryName("cable_output"),
				new BlockCableInput().setRegistryName("cable_input"),
				new BlockCableIntake().setRegistryName("cable_intake"),
				new BlockCableBUD().setRegistryName("cable_bud"),
				new BlockCableBreaker().setRegistryName("cable_breaker"),
				new BlockCableCluster().setRegistryName("cable_cluster"),
				new BlockCableCamouflages().setRegistryName("cable_camouflage"),
				new BlockCableSign().setRegistryName("cable_sign")
		);
		blocks.forEach(event.getRegistry()::register);

		registerTiles();
	}

	private static void registerTiles() {
		GameRegistry.registerTileEntity(TileEntityManager.class, StevesFactoryManager.MODID + ":manager");
		GameRegistry.registerTileEntity(TileEntityRelay.class, StevesFactoryManager.MODID + ":cable_relay");
		GameRegistry.registerTileEntity(TileEntityOutput.class, StevesFactoryManager.MODID + ":cable_output");
		GameRegistry.registerTileEntity(TileEntityInput.class, StevesFactoryManager.MODID + ":cable_input");
		GameRegistry.registerTileEntity(TileEntityIntake.class, StevesFactoryManager.MODID + ":cable_intake");
		GameRegistry.registerTileEntity(TileEntityBUD.class, StevesFactoryManager.MODID + ":cable_bud");
		GameRegistry.registerTileEntity(TileEntityBreaker.class, StevesFactoryManager.MODID + ":cable_breaker");
		GameRegistry.registerTileEntity(TileEntityCluster.class, StevesFactoryManager.MODID + ":cable_cluster");
		GameRegistry.registerTileEntity(TileEntityCamouflage.class, StevesFactoryManager.MODID + ":cable_camouflage");
		GameRegistry.registerTileEntity(TileEntitySignUpdater.class, StevesFactoryManager.MODID + ":cable_sign");
	}

	public static void addRecipes() {
		RebornCraftingHelper.addShapedOreRecipe(new ItemStack(MANAGER),
				"III",
				"IRI",
				"SPS",
				'R', Blocks.REDSTONE_BLOCK,
				'P', Blocks.PISTON,
				'I', Items.IRON_INGOT,
				'S', Blocks.STONE
		);

		RebornCraftingHelper.addShapedOreRecipe(new ItemStack(CABLE, 8),
				"GPG",
				"IRI",
				"GPG",
				'R', Items.REDSTONE,
				'G', Blocks.GLASS,
				'I', Items.IRON_INGOT,
				'P', Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_RELAY, 1),
				CABLE,
				Blocks.HOPPER
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_OUTPUT, 1),
				CABLE,
				Items.REDSTONE,
				Items.REDSTONE,
				Items.REDSTONE
		);


		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_INPUT, 1),
				CABLE,
				Items.REDSTONE
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_RELAY, 1, 8),
				new ItemStack(CABLE_RELAY, 1, 0),
				new ItemStack(Items.DYE, 1, 4)
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_INTAKE, 1, 0),
				CABLE,
				Blocks.HOPPER,
				Blocks.HOPPER,
				Blocks.DROPPER
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_BUD, 1),
				CABLE,
				Items.QUARTZ,
				Items.QUARTZ,
				Items.QUARTZ
		);


		//        RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_BREAKER, 1),
		//                CABLE,
		//                Items.IRON_PICKAXE,
		//                Blocks.DISPENSER
		//        );

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_INTAKE, 1, 8),
				new ItemStack(CABLE_INTAKE, 1, 0),
				Items.GOLD_INGOT
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_CLUSTER, 1),
				CABLE,
				Items.ENDER_PEARL,
				Items.ENDER_PEARL,
				Items.ENDER_PEARL
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_CAMOUFLAGE, 1, 0),
				CABLE,
				new ItemStack(Blocks.WOOL, 1, 14),
				new ItemStack(Blocks.WOOL, 1, 13),
				new ItemStack(Blocks.WOOL, 1, 11)
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_CAMOUFLAGE, 1, 1),
				new ItemStack(CABLE_CAMOUFLAGE, 1, 0),
				new ItemStack(CABLE_CAMOUFLAGE, 1, 0),
				Blocks.IRON_BARS,
				Blocks.IRON_BARS
		);

		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_CAMOUFLAGE, 1, 2),
				new ItemStack(CABLE_CAMOUFLAGE, 1, 1),
				Blocks.STICKY_PISTON
		);


		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(CABLE_SIGN, 1),
				CABLE,
				new ItemStack(Items.DYE, 0),
				Items.FEATHER
		);

		RebornCraftingHelper.addShapedOreRecipe(new ItemStack(ModItems.DISK), " x ", "xyx", " x ", 'x', "ingotIron", 'y', new ItemStack(ModBlocks.MANAGER));
		RebornCraftingHelper.addShapelessOreRecipe(new ItemStack(ModItems.DISK), new ItemStack(ModItems.DISK));

		//        GameData.register_impl(new ClusterUpgradeRecipe());
		GameData.register_impl(new ClusterRecipe(new ResourceLocation(StevesFactoryManager.UNLOCALIZED_START + "clusterrecipe")));
	}

}
