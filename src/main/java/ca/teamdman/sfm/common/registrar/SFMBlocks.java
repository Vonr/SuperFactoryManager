/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.CableBlock;
import ca.teamdman.sfm.common.block.CrafterBlock;
import ca.teamdman.sfm.common.block.ManagerBlock;
import ca.teamdman.sfm.common.block.WorkstationBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public final class SFMBlocks {
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
		SFM.MOD_ID
	);
	public static final RegistryObject<ManagerBlock> MANAGER = BLOCKS.register("manager",
		ManagerBlock::new
	);
	public static final RegistryObject<CableBlock> CABLE = BLOCKS.register("cable",
		CableBlock::new
	);
	public static final RegistryObject<CrafterBlock> CRAFTER = BLOCKS.register("crafter",
		CrafterBlock::new
	);

	public static final RegistryObject<WorkstationBlock> WORKSTATION = BLOCKS.register("workstation",
		WorkstationBlock::new
	);
}
