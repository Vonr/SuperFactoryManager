package vswe.superfactory.util;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import java.util.*;
import java.util.stream.StreamSupport;

/**
 * A class used to cache the concatenated Tooltip string representation of items for searching performance improvements
 */
public class SearchUtil {
	private static final Map<ItemStack, String> cache = Collections.synchronizedMap(new LinkedHashMap<>());

	/**
	 * Populate the {@link SearchUtil#cache} object with ItemStacks and their respective tooltips
	 * Note: Tooltips, meaning when you hover over it, including the name
	 * Node: The method to get an ItemStack tooltip is costly, that's the point of this caching operation
	 */
	public static void buildCache() {
		long                   time_no_see = System.currentTimeMillis();
		NonNullList<ItemStack> stacks      = NonNullList.create();

		// Get all sub-items
		StreamSupport.stream(Item.REGISTRY.spliterator(), false)
				.filter(Objects::nonNull)
				.filter(i -> i.getCreativeTab() != null)
				.forEach(i -> {
					try {
						i.getSubItems(i.getCreativeTab(), stacks);
					} catch (Exception e) {
						// do nothing
					}
				});
		//todo: threading test
		// Index sub-item searchable strings
		stacks.stream()
				.filter(Objects::nonNull)
				.sorted(Comparator.comparing(ItemStack::getDisplayName))
				.sorted(Comparator.comparingInt(s -> s.getDisplayName().length()))
				.sorted(Comparator.comparingInt(s -> s.getItem().getRegistryName() != null && s.getItem().getRegistryName().getNamespace().equals("minecraft") ? 0 : 1))
				.forEach(stack -> {
					// Add full tooltip text
					cache.put(stack, String.join("\n", stack.getTooltip(null, ITooltipFlag.TooltipFlags.ADVANCED)));
					// Add just the stack name, so regex anchors play nice
					cache.put(stack, stack.getDisplayName());
				});
		System.out.println("Generated SFM item cache in " + (System.currentTimeMillis() - time_no_see) + "ms.");
	}

	public static Map<ItemStack, String> getCache() {
		return Collections.unmodifiableMap(cache);
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
