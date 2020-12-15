/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import ca.teamdman.sfm.common.tile.ManagerTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;

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
				TileEntityType.Builder.create(ManagerTileEntity::new, BlockRegistrar.Blocks.MANAGER).build(NO_TYPE).setRegistryName(SFM.MOD_ID, "manager"),
				TileEntityType.Builder.create(CrafterTileEntity::new, BlockRegistrar.Blocks.CRAFTER).build(NO_TYPE).setRegistryName(SFM.MOD_ID, "crafter")
		);
		LogManager.getLogger(SFM.MOD_NAME + " Tile Entity Registrar").debug("Registered tiles");
	}

	@ObjectHolder(SFM.MOD_ID)
	public static final class Tiles {
		public static final TileEntityType<?> MANAGER = WAITING;
		public static final TileEntityType<?> CRAFTER = WAITING;
	}
}
