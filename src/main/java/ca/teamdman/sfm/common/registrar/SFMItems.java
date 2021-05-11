/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
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

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS,
		SFM.MOD_ID
	);

	public static final RegistryObject<Item> MANAGER = ITEMS.register("manager",
		() -> new BlockItem(
			SFMBlocks.MANAGER.get(),
			new Item.Properties().group(GROUP)
		)
	);
	public static final RegistryObject<Item> CABLE = ITEMS.register("cable",
		() -> new BlockItem(
			SFMBlocks.CABLE.get(),
			new Item.Properties().group(GROUP)
		)
	);
	public static final RegistryObject<Item> CRAFTER = ITEMS.register("cable",
		() -> new BlockItem(
			SFMBlocks.CRAFTER.get(),
			new Item.Properties().group(GROUP)
		)
	);

}
