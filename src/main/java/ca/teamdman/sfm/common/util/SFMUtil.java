package ca.teamdman.sfm.common.util;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class SFMUtil {

	/**
	 * Gets the marker used for logging purposes
	 *
	 * @param clazz The class used for naming the marker
	 * @return Logging marker
	 */
	public static Marker getMarker(Class clazz) {
		String[] x = clazz.getName().split("\\.");
		return MarkerManager.getMarker(x[x.length - 1]);
	}

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
}
