package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.CommonProxy;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Arrays;

public class ClientProxy extends CommonProxy {
	@Override
	public void fillItemGroup(ItemGroup group, Item[] items) {
		group.fill(Arrays.stream(items)
			.map(ItemStack::new)
			.collect(NonNullList::create, NonNullList::add, NonNullList::addAll));
	}
}
