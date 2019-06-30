package ca.teamdman.sfm.registrar;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.block.ManagerBlock;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus= Mod.EventBusSubscriber.Bus.MOD)
public final class BlocksRegistrar {
	private static final Block WAITING = null;
	@ObjectHolder(SFM.MOD_ID)
	public static final class Blocks {
		public static final Block MANAGER = WAITING;
	}

	@SubscribeEvent
	public static void onRegisterBlocks(RegistryEvent.Register<Block> e) {
		e.getRegistry().registerAll(
				new ManagerBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(5F,6F).sound(SoundType.METAL)).setRegistryName(SFM.MOD_ID,"manager")
		);
		LogManager.getLogger(SFM.MOD_NAME + " Blocks Registrar").debug("Registered blocks");
	}
}
