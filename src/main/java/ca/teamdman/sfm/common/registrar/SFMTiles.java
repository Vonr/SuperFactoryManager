/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;


import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SFMTiles {

	public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister
		.create(ForgeRegistries.TILE_ENTITIES, SFM.MOD_ID);

	public static final RegistryObject<TileEntityType<ManagerTileEntity>> MANAGER = TILES
		.register(
			"manager",
			() -> TileEntityType.Builder
				.create(ManagerTileEntity::new, SFMBlocks.MANAGER.get())
				.build(null)
		);

	public static final RegistryObject<TileEntityType<CrafterTileEntity>> CRAFTER = TILES
		.register(
			"crafter",
			() -> TileEntityType.Builder
				.create(CrafterTileEntity::new, SFMBlocks.CRAFTER.get())
				.build(null)
		);
}
