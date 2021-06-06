/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.CraftingContractItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SFMItems {

	public static final ItemGroup GROUP = new ItemGroup(-1, "sfm") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(SFMBlocks.MANAGER.get());
		}
	};

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(
		ForgeRegistries.ITEMS,
		SFM.MOD_ID
	);

	public static final RegistryObject<BlockItem> MANAGER = ITEMS.register(
		"manager",
		() -> new BlockItem(
			SFMBlocks.MANAGER.get(),
			new Item.Properties().group(GROUP)
		)
	);
	public static final RegistryObject<BlockItem> CABLE = ITEMS.register(
		"cable",
		() -> new BlockItem(
			SFMBlocks.CABLE.get(),
			new Item.Properties().group(GROUP)
		)
	);
	public static final RegistryObject<BlockItem> CRAFTER = ITEMS.register(
		"crafter",
		() -> new BlockItem(
			SFMBlocks.CRAFTER.get(),
			new Item.Properties().group(GROUP)
		)
	);
	public static final RegistryObject<BlockItem> WORKSTATION = ITEMS.register(
		"workstation",
		() -> new BlockItem(
			SFMBlocks.WORKSTATION.get(),
			new Item.Properties().group(GROUP)
		)
	);


	public static final RegistryObject<CraftingContractItem> CRAFTING_CONTRACT = ITEMS
		.register(
			"crafting_contract",
			CraftingContractItem::new
		);

	public static final RegistryObject<BlockItem> WATER_INTAKE = ITEMS.register(
			"water_intake",
			() -> new BlockItem(
				SFMBlocks.WATER_INTAKE.get(),
				new Properties().group(GROUP)
			)
		);
}
