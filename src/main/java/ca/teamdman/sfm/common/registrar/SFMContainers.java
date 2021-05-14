/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.CrafterContainer;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.container.TileContainerType;
import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.container.WorkstationContainerType;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMContainers {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister
		.create(ForgeRegistries.CONTAINERS, SFM.MOD_ID);

	public static final RegistryObject<ContainerType<CrafterContainer>> CRAFTER = CONTAINER_TYPES
		.register(
			"crafter",
			() -> IForgeContainerType.create(CrafterContainer::create)
		);

	public static final RegistryObject<ContainerType<ManagerContainer>> MANAGER = CONTAINER_TYPES
		.register(
			"manager",
			() -> IForgeContainerType.create(ManagerContainer::create)
		);

	public static final RegistryObject<TileContainerType<WorkstationContainer, WorkstationTileEntity>> WORKSTATION = CONTAINER_TYPES
		.register(
			"workstation",
			WorkstationContainerType::new
		);
}
