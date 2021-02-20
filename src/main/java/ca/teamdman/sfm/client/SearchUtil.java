/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client;

import ca.teamdman.sfm.common.config.Config.Client;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.minecraft.item.ItemStack;

public class SearchUtil {
	private static final CompletableFuture<Multimap<ItemStack, String>> cache =
		new ItemStackSearchIndexer().buildCache();

	public static final Comparator<ItemStack> SEARCH_RESULT_COMPARATOR = Comparator
		.comparingInt(SearchUtil::sortMinecraftFirst)
		.thenComparingInt(SearchUtil::sortShorterNamesFirst)
		.thenComparing(x -> x.getDisplayName().getString());

	public static class SearchResults {
		private final Queue<ItemStack> result;
		private final Thread background;
		private final AtomicBoolean finished;
		private int processed = 0;

		public SearchResults(
			Thread background,
			Queue<ItemStack> result,
			AtomicBoolean finished
		) {
			this.background = background;
			this.result = result;
			this.finished = finished;
		}

		public Queue<ItemStack> get() {
			return result;
		}

		public void cancel() {
			background.stop();
		}

		public Stream<ItemStack> streamLatestResults() {
			if (processed == result.size()) return Stream.empty();
			return IntStream.range(processed, result.size())
				.mapToObj(i -> result.poll());
		}

		public boolean isFinished() {
			return finished.get();
		}
	}

	public static SearchResults search(String query) {
		// compile search pattern
		Pattern pattern = getPattern(query);

		// prepare concurrent result list
		Queue<ItemStack> results = new ConcurrentLinkedQueue<>();
		AtomicBoolean finished = new AtomicBoolean(false);
		// prepare background search process
		Thread t = new Thread(() ->{
			try {
				for (Entry<ItemStack, Collection<String>> entry : cache.get().asMap().entrySet()) {
					if (entry.getValue().stream().anyMatch(v -> pattern.matcher(v).find())) {
						results.add(entry.getKey());
					}
				}
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			} finally {
				finished.set(true);
			}
		});

		// start the search process
		t.start();

		// return a custom future to allow for cancelling searches by killing the thread
		return new SearchResults(t, results, finished);
	}

	private static Pattern getPattern(String search) {
		Pattern basicPattern = Pattern.compile(Pattern.quote(search), Pattern.CASE_INSENSITIVE);
		if (!Client.enableRegexSearch) {
			return basicPattern;
		}
		try {
			return Pattern.compile(search, Pattern.CASE_INSENSITIVE);
		} catch (PatternSyntaxException e) {
			return basicPattern;
		}
	}

	private static int sortMinecraftFirst(ItemStack in) {
		return in.getItem().getRegistryName() != null
			&& in.getItem().getRegistryName().getNamespace().equals("minecraft") ? 0 : 1;
	}

	private static int sortShorterNamesFirst(ItemStack in) {
		return in.getDisplayName().getString().length();
	}
}
