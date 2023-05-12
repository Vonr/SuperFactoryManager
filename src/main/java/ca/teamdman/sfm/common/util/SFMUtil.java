package ca.teamdman.sfm.common.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.contents.TranslatableContents;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SFMUtil {

	/**
	 * Gets a stream using a self-feeding mapping function. Prevents the
	 * re-traversal of elements that have been visited before.
	 *
	 * @param operator Consumes queue elements to build the result set and
	 *                 append the next queue elements
	 * @param first    Initial value, not checked against the filter
	 * @param <T>      Type that the mapper consumes and produces
	 * @return Stream result after termination of the recursive mapping process
	 */
	public static <T> Stream<T> getRecursiveStream(
		RecursiveBuilder<T> operator, T first
	) {
		Stream.Builder<T> builder = Stream.builder();
		Set<T> debounce = new HashSet<>();
		Deque<T> toVisit = new ArrayDeque<>();
		toVisit.add(first);
		debounce.add(first);
		while (toVisit.size() > 0) {
			T current = toVisit.pop();
			operator.accept(current, next -> {
				if (!debounce.contains(next)) {
					debounce.add(next);
					toVisit.add(next);
				}
			}, builder::add);
		}
		return builder.build();
	}

	public interface RecursiveBuilder<T> {

		void accept(T next, Consumer<T> nextQueue, Consumer<T> resultBuilder);
	}

	public static CompoundTag serializeTranslation(TranslatableContents contents) {
		CompoundTag tag = new CompoundTag();
		tag.putString("key", contents.getKey());
		ListTag args = new ListTag();
		for (var arg : contents.getArgs()) {
			args.add(StringTag.valueOf(arg.toString()));
		}
		tag.put("args", args);
		return tag;
	}

	public static TranslatableContents deserializeTranslation(CompoundTag tag) {
		var key = tag.getString("key");
		var args = tag
				.getList("args", Tag.TAG_STRING)
				.stream()
				.map(StringTag.class::cast)
				.map(StringTag::getAsString)
				.toArray();
		return new TranslatableContents(key, args);
	}


}
