package vswe.superfactory.util;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.Comparator;
import java.util.Objects;
import java.util.stream.StreamSupport;

/**
 * A class used to cache the concatenated Tooltip string representation of items for searching performance improvements
 */
public class SearchUtil {
	private static final Multimap<ItemStack, String> cache = Multimaps.synchronizedListMultimap(LinkedListMultimap.create());

	/**
	 * Populate the {@link SearchUtil#cache} object with ItemStacks and their respective tooltips
	 * Note: Tooltips, meaning when you hover over it, including the name
	 * Node: The method to get an ItemStack tooltip is costly, that's the point of this caching operation
	 */
	public static void buildCache() {
		new Thread(() -> {
			long time_no_see = System.currentTimeMillis();
			try {
				NonNullList<ItemStack> stacks = NonNullList.create();

				// Get all sub-items
				StreamSupport.stream(Item.REGISTRY.spliterator(), false)
						.filter(Objects::nonNull)
						.filter(i -> i.getCreativeTab() != null)
						.forEach(i -> {
							try {
								i.getSubItems(i.getCreativeTab(), stacks);
							} catch (Exception ignored) {
							}
						});

				stacks.stream()
						.filter(Objects::nonNull)
						.filter(itemStack -> !itemStack.isEmpty())
						.sorted(Comparator.<ItemStack>comparingInt(s -> s.getItem().getRegistryName() != null && s.getItem().getRegistryName().getNamespace().equals("minecraft") ? 0 : 1)
								.thenComparingInt(s -> s.getDisplayName().length())
								.thenComparing(ItemStack::getDisplayName))
						.forEach(stack -> {
							try {
								// Add just the stack name, so regex anchors play nice
								cache.put(stack, stack.getDisplayName());
								// Add full tooltip text
								cache.put(stack, String.join("\n", stack.getTooltip(null, ITooltipFlag.TooltipFlags.ADVANCED)));
							} catch (Exception ignored) {
							}
						});
				System.out.println("[SFM] Indexed " + stacks.size() + " items in " + (System.currentTimeMillis() - time_no_see) + "ms.");

			} catch (Exception ignored) {
				cache.put(ItemStack.EMPTY, ""); // Make sure cache isn't empty in case of errors
			}
		}).start();
	}

	public static Multimap<ItemStack, String> getCache() {
//		return Collections.unmodifiableMap(cache);
		return cache;
	}

	/**
	 * Update scroller contents on frame if there is a new cached list
	 * @param event RenderWorldLastEvent
	 */
	//	@SubscribeEvent
	//	public static void renderEvent(RenderWorldLastEvent event) {
	//		if (!scrollersQueue.isEmpty()) {
	//			scrollersQueue.forEach((s, i) -> {
	//				s.getResult().clear();
	//				s.getResult().addAll(i);
	//			});
	//			scrollersQueue.clear();
	//		}
	//	}
}
