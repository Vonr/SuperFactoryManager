package ca.teamdman.sfm.registrar;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.tile.ManagerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class TileEntityRegistrar {
	private static final com.mojang.datafixers.types.Type NO_TYPE;
	private static final TileEntityType<?>                WAITING = null;

	static {
		NO_TYPE = null;
	}

	@SubscribeEvent
	public static void onRegisterTileEntityTypes(@Nonnull final RegistryEvent.Register<TileEntityType<?>> e) {
		e.getRegistry().registerAll(
				TileEntityType.Builder.create(ManagerTileEntity::new, BlocksRegistrar.Blocks.MANAGER).build(NO_TYPE).setRegistryName(SFM.MOD_ID, "manager")
		);
		LogManager.getLogger(SFM.MOD_NAME + " Tile Entity Registrar").debug("Registered tiles");
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class Tiles {
		public static final TileEntityType<?> MANAGER = WAITING;
	}
}
