package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class ItemStackSearchIndexer implements Callable<Multimap<ItemStack, String>> {

	private final ExecutorService pool = Executors.newFixedThreadPool(1);

	private static void populateStressTest(List<ItemStack> list) {
		Iterator<ItemStack> iter = list.listIterator();
		while (list.size() < 100000) {
			list.add(iter.next());
		}
	}

	public CompletableFuture<Multimap<ItemStack, String>> buildCache() {
		return CompletableFuture.supplyAsync(this::call);
	}

	/**
	 * Generate a lookup object with ItemStacks and their respective tooltips Note: Tooltips,
	 * meaning when you hover over it, including the name
	 * <br/>
	 * Note: The method to get an ItemStack tooltip is costly, that's the point of this caching
	 * operation
	 */
	@Override
	public Multimap<ItemStack, String> call() {
		long start = System.currentTimeMillis();

		Multimap<ItemStack, String> rtn = ArrayListMultimap.create();

		getSearchableItems()
			.filter(Objects::nonNull)
			.filter(itemStack -> !itemStack.isEmpty())
			.forEach(stack -> {
				try {
					// Add just the stack name, so regex anchors play nice
					rtn.put(stack, stack.getDisplayName().getString());

					// Add full tooltip text
					rtn.put(
						stack,
						stack.getTooltipLines(null, TooltipFlags.ADVANCED).stream()
							.map(ITextComponent::getString)
							.collect(Collectors.joining(" "))
					);

					// Add oredict
					stack.getItem().getTags().forEach(tag -> rtn.put(stack, tag.toString()));
				} catch (Exception ignored) {
				}
			});

		SFM.LOGGER.info(
			SFMUtil.getMarker(SearchUtil.class),
			"Indexed {} items in {} ms",
			rtn.keys().size(),
			System.currentTimeMillis() - start
		);

		return rtn;
	}

	public static Stream<ItemStack> getSearchableItems() {
		return Registry.ITEM.stream()
			.filter(Objects::nonNull)
			.filter(i -> i.getCreativeTabs().size() > 0)
			.map(ItemStackSearchIndexer::getSubItems)
			.flatMap(Collection::stream);
	}

	private static List<ItemStack> getSubItems(Item item) {
		NonNullList<ItemStack> rtn = NonNullList.create();
		try {
			item.fillItemCategory(ItemGroup.TAB_SEARCH, rtn);
		} catch (Exception ignored) {
		}
		return rtn;
	}
}
