package vswe.stevesfactory.registry;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.GameData;
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
//		GameData.register_impl(new ClusterUpgradeRecipe()); // TODO: fix
		GameData.register_impl(new ClusterRecipe(new ResourceLocation(StevesFactoryManager.UNLOCALIZED_START + "clusterrecipe")));
	}

}
