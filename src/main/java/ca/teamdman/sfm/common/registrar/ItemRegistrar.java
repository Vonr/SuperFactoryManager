/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.registrar;

import ca.teamdman.sfm.SFM;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;


@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ItemRegistrar {
	public static final  ItemGroup group   = new ItemGroup(-1, "sfm") {
		@Override
		public ItemStack createIcon() {
			return new ItemStack(BlockRegistrar.Blocks.MANAGER);
		}
	};
	private static final Item      WAITING = null;

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> e) {
		Item[] items = {
				new BlockItem(BlockRegistrar.Blocks.MANAGER, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "manager"),
				new BlockItem(BlockRegistrar.Blocks.CABLE, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "cable"),
				new BlockItem(BlockRegistrar.Blocks.CRAFTER, new BlockItem.Properties().group(group)).setRegistryName(SFM.MOD_ID, "crafter")
		};
		e.getRegistry().registerAll(items);
		SFM.PROXY.fillItemGroup(group, items);
		LogManager.getLogger(SFM.MOD_NAME + " Items Registrar").debug("Registered items");
	}

	@ObjectHolder(SFM.MOD_ID)
	public static class Items {
		public static final Item MANAGER = WAITING;
	}
}
