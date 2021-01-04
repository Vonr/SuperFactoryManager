/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.config.Config.Client;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import net.minecraft.client.util.ITooltipFlag.TooltipFlags;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;

public class SearchUtil {

	private static final Multimap<ItemStack, String> cache = Multimaps.synchronizedListMultimap(
		LinkedListMultimap.create()
	);
	private static boolean cacheReady = false;

	public static void buildCacheInBackground() {
		if (cacheReady) {
			return;
		}
		new Thread(() -> {
			long time_no_see = System.currentTimeMillis();
			try {
				buildCache();
			} finally {
				SFM.LOGGER.info(
					SFMUtil.getMarker(SearchUtil.class),
					"Indexed {} items in {} ms",
					cache.keys().size(),
					System.currentTimeMillis() - time_no_see
				);
				cacheReady = true;
			}
		}).start();
	}

	/**
	 * Populate the {@link SearchUtil#cache} object with ItemStacks and their respective tooltips
	 * Note: Tooltips, meaning when you hover over it, including the name Node: The method to get an
	 * ItemStack tooltip is costly, that's the point of this caching operation
	 */
	public static void buildCache() {
		cache.clear();
		getSearchableItems().stream()
			.filter(Objects::nonNull)
			.filter(itemStack -> !itemStack.isEmpty())
			.sorted(getSearchPriorityComparator())
			.forEach(stack -> {
				try {
					// Add just the stack name, so regex anchors play nice
					cache.put(stack, stack.getDisplayName().getString());

					// Add full tooltip text
					cache.put(
						stack,
						stack.getTooltip(null, TooltipFlags.ADVANCED).stream()
							.map(ITextComponent::getString)
							.collect(Collectors.joining(" "))
					);

					// Add oredict
					stack.getItem().getTags().forEach(tag -> cache.put(stack, tag.toString()));
				} catch (Exception ignored) {
				}
			});

	}

	public static List<ItemStack> getSearchableItems() {
		NonNullList<ItemStack> stacks = NonNullList.create();
		Registry.ITEM.stream()
			.filter(Objects::nonNull)
			.filter(i -> i.getCreativeTabs().size() > 0)
			.forEach(i -> {
				try {
					i.fillItemGroup(ItemGroup.SEARCH, stacks);
				} catch (Exception ignored) {

				}
			});
		return stacks;
	}

	private static void populateStressTest(List<ItemStack> list) {
//		if (Launcher.INSTANCE.blackboard().get(Keys.of("fml.deobfuscatedEnvironment")).isPresent()) {
		Iterator<ItemStack> iter = list.listIterator();
		while (list.size() < 100000) {
			list.add(iter.next());
		}
//		}
	}

	private static int sortMinecraftFirst(ItemStack in) {
		return in.getItem().getRegistryName() != null
			&& in.getItem().getRegistryName().getNamespace().equals("minecraft") ? 0 : 1;
	}

	private static int sortShorterNamesFirst(ItemStack in) {
		return in.getDisplayName().getString().length();
	}

	private static Comparator<ItemStack> getSearchPriorityComparator() {
		return Comparator.comparingInt(SearchUtil::sortMinecraftFirst)
			.thenComparingInt(SearchUtil::sortShorterNamesFirst)
			.thenComparing(x -> x.getDisplayName().getString());
	}

	public static Multimap<ItemStack, String> getCache() {
		//		return Collections.unmodifiableMap(cache);
		return cache;
	}

	public static class Query {
		private boolean running;
		private Queue<ItemStack> results = new ConcurrentLinkedQueue<>();
		private String query;
		private Thread background;

		public Query(String query) {
			this.query = query;
		}

		/**
		 * Updates the search query and restarts the search
		 *
		 * @param query Search text
		 */
		public void updateQuery(String query) {
			this.query = query;
			start();
		}

		public void start() {
			if (background != null) {
				stop();
				try {
					background.join(); // Wait for previous background search to finish
				} catch (InterruptedException ignored) {

				}
			}
			results.clear();
			running = true;
			background = new Thread(this::gatherResults);
			background.start();
		}

		public Pattern getPattern() {
			Pattern basicPattern = Pattern.compile(Pattern.quote(query), Pattern.CASE_INSENSITIVE);
			if (!Client.enableRegexSearch) {
				return basicPattern;
			}
			try {
				return Pattern.compile(query, Pattern.CASE_INSENSITIVE);
			} catch (PatternSyntaxException e) {
				return basicPattern;
			}
		}

		public void gatherResults() {
			Pattern pattern = getPattern();
			for (Entry<ItemStack, Collection<String>> entry : getCache().asMap().entrySet()) {
				if (!running) {
					return;
				}
				if (entry.getValue().stream().anyMatch(v -> pattern.matcher(v).find())) {
					results.add(entry.getKey());
					onResultsUpdated(results);
				}
			}
		}

		public void onResultsUpdated(Queue<ItemStack> results) {

		}

		public void stop() {
			this.running = false;
		}
	}
}
