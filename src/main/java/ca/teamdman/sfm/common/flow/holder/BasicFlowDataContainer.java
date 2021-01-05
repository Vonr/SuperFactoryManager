package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class BasicFlowDataContainer extends Observable implements INBTSerializable<ListNBT> {

	private final HashMap<UUID, FlowData> DELEGATE = new HashMap<>();

	public Stream<FlowData> getDescendants(
		FlowData start,
		boolean recursive
	) {
		return SFMUtil.getRecursiveStream(
			(current, next, results) -> stream()
				.filter(RelationshipFlowData.class::isInstance)
				.map(data -> (RelationshipFlowData) data)
				.filter(rel -> rel.from.equals(current.getId()))
				.map(rel -> get(rel.from))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(v -> {
					if (recursive) {
						next.accept(v);
					}
					results.accept(v);
				}),
			start
		);
	}

	public Stream<FlowData> stream() {
		return DELEGATE.values().stream();
	}

	public Optional<FlowData> get(UUID id) {
		return Optional.ofNullable(DELEGATE.get(id));
	}

	public Stream<FlowData> getAncestors(
		FlowData start,
		boolean recursive
	) {
		return SFMUtil.getRecursiveStream(
			(current, next, results) -> stream()
				.filter(RelationshipFlowData.class::isInstance)
				.map(data -> (RelationshipFlowData) data)
				.filter(rel -> rel.to.equals(current.getId()))
				.map(rel -> get(rel.to))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(v -> {
					if (recursive) {
						next.accept(v);
					}
					results.accept(v);
				}),
			start
		);
	}

	public FlowData put(FlowData data) {
		FlowData old = DELEGATE.put(data.getId(), data);
		cleanupObserver(old);
		setChanged();
		notifyObservers(new FlowDataContainerChange(
			data,
			old == null ? FlowDataContainerChange.ChangeType.ADDED
				: FlowDataContainerChange.ChangeType.UPDATED
		));
		return old;
	}

	/**
	 * Remove an object if it is observing this container
	 *
	 * @param o Object that may or may not be an observer. If not observer, no effect.
	 */
	public void cleanupObserver(Object o) {
		if (o instanceof Observer) {
			deleteObserver(((Observer) o));
		}
	}

	public int size() {
		return DELEGATE.size();
	}

	public FlowData remove(UUID key) {
		FlowData data = DELEGATE.remove(key);
		if (data != null) {
			cleanupObserver(data);
			setChanged();
			notifyObservers(
				new FlowDataContainerChange(data, FlowDataContainerChange.ChangeType.REMOVED));
		}
		return data;
	}

	public void notifyGuiClosed() {
		setChanged();
		notifyObservers(new FlowDataContainerClosedEvent());
	}

	public void notifyChanged(FlowData data) {
		setChanged();
		notifyObservers(new FlowDataContainerChange(
			data,
			ChangeType.UPDATED
		));
	}

	public boolean removeIf(Predicate<FlowData> pred) {
		Set<FlowData> removed = DELEGATE.values().stream()
			.filter(pred)
			.collect(Collectors.toSet());
		boolean rtn = DELEGATE.entrySet().removeIf((entry) -> pred.test(entry.getValue()));
		if (rtn) {
			removed.forEach(this::cleanupObserver);
			setChanged();
			removed.forEach(data -> notifyObservers(new FlowDataContainerChange(
				data,
				ChangeType.REMOVED
			)));
		}
		return rtn;
	}

	public void clear() {
		List<FlowData> old = new ArrayList<>(DELEGATE.values());
		DELEGATE.clear();
		setChanged();
		old.stream()
			.map(data -> new FlowDataContainerChange(
				data,
				FlowDataContainerChange.ChangeType.REMOVED
			))
			.forEach(this::notifyObservers);
	}

	public <T> Optional<T> get(UUID id, Class<T> clazz) {
		return Optional.ofNullable(DELEGATE.get(id))
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	@Override
	public ListNBT serializeNBT() {
		ListNBT list = new ListNBT();
		stream().forEach(d -> list.add(d.getSerializer().toNBT(d)));
		return list;
	}

	@Override
	public void deserializeNBT(ListNBT list) {
		list.stream()
			.map(c -> ((CompoundNBT) c))
			.map(c -> {
				Optional<FlowData> data = FlowDataSerializer.getSerializer(c)
					.map(serializer -> serializer.fromNBT(c));
				if (!data.isPresent()) {
					SFM.LOGGER.warn("Could not find factory for {}", c);
				}
				return data;
			})
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted(Comparator.comparing(a -> a instanceof RelationshipFlowData))
			.forEach(data -> data.addToDataContainer(this));
	}

	public <T extends FlowData> Stream<T> get(Class<T> clazz) {
		return stream()
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	public static class FlowDataContainerChange {

		public final FlowData DATA;
		public final ChangeType CHANGE;

		public FlowDataContainerChange(
			FlowData data,
			ChangeType change
		) {
			this.DATA = data;
			this.CHANGE = change;
		}

		public enum ChangeType {
			ADDED,
			REMOVED,
			UPDATED;
		}
	}

	public static class FlowDataContainerClosedEvent {

	}
}
