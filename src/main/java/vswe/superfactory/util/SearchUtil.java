package vswe.superfactory.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vswe.superfactory.components.ScrollController;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.StreamSupport;

/**
 * A class used to cache the concatenated Tooltip string representation of items for searching performance improvements
 */
public class SearchUtil {
	private static final Map<ScrollController<ItemStack>, List<ItemStack>> scrollersQueue = new LinkedHashMap<>();
	private static final ConcurrentHashMap<ItemStack, String>              cache          = new ConcurrentHashMap<>();

	/**
	 * Populate the {@link SearchUtil#cache} object with ItemStacks and their respective tooltips
	 * Note: Tooltips, meaning when you hover over it, including the name
	 * Node: The method to get an ItemStack tooltip is costly, that's the point of this caching operation
	 */
	public static void buildCache() {
		long time_no_see = System.currentTimeMillis();
		NonNullList<ItemStack> stacks = NonNullList.create();
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
		stacks.stream()
				.filter(Objects::nonNull)
				.forEach(stack -> {
					cache.put(stack, String.join("\n",stack.getTooltip(null, ITooltipFlag.TooltipFlags.ADVANCED)));
					cache.put(stack, stack.getDisplayName());
					//todo: investigate if NORMAL is needed
				});
		System.out.println("Generated SFM item cache in " + (System.currentTimeMillis()-time_no_see) + "ms.");
	}

	public static Map<ItemStack, String> getCache() {
		return Collections.unmodifiableMap(cache);
	}

	public static void queueContentUpdate(ScrollController controller, List<ItemStack> content) {
		scrollersQueue.put(controller, content);
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
