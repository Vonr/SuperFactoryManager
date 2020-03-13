package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.common.CommonProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Collection;

public class ClientProxy extends CommonProxy {
	@Override
	public void fillItemGroup(ItemGroup group, Collection<Item> items) {
		group.fill(items.stream()
			.map(ItemStack::new)
			.collect(NonNullList::create, NonNullList::add, NonNullList::addAll));
	}
}
