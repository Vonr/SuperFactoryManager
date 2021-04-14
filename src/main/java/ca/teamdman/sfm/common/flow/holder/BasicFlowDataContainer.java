package ca.teamdman.sfm.common.flow.holder;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer.FlowDataContainerChange.ChangeType;
import ca.teamdman.sfm.common.util.SFMUtil;
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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

public class BasicFlowDataContainer extends Observable implements INBTSerializable<CompoundNBT> {

	public final int NBT_SCHEMA_VERSION = 2;
	public final String NBT_SCHEMA_VERSION_KEY = "__version";
	public final String NBT_SCHEMA_DATA_KEY = "__data";
	private final HashMap<UUID, FlowData> DELEGATE = new HashMap<>();

	public Stream<UUID> getDescendants(
		UUID start,
		boolean recursive
	) {
		return SFMUtil.getRecursiveStream(
			(current, next, results) -> stream()
				.filter(RelationshipFlowData.class::isInstance)
				.map(RelationshipFlowData.class::cast)
				.filter(rel -> rel.from.equals(current))
				.map(rel -> get(rel.to))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(v -> {
					if (v instanceof LineNodeFlowData) {
						// line nodes don't count as "recursive"
						next.accept(v.getId());
					} else {
						// accept child
						results.accept(v.getId());
						if (recursive) {
							// if recursive, grab child's children
							next.accept(v.getId());
						}
					}
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

	public Stream<UUID> getAncestors(
		UUID start,
		boolean recursive
	) {
		return SFMUtil.getRecursiveStream(
			(current, next, results) -> stream()
				.filter(RelationshipFlowData.class::isInstance)
				.map(RelationshipFlowData.class::cast)
				.filter(rel -> rel.to.equals(current))
				.map(rel -> get(rel.from))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.forEach(v -> {
					if (v instanceof LineNodeFlowData) {
						// line nodes don't count as "recursive"
						next.accept(v.getId());
					} else {
						// accept parent
						results.accept(v.getId());
						if (recursive) {
							// if recursive, grab parent's parents
							next.accept(v.getId());
						}
					}
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
		notifyObservers(new FlowDataContainerClosedClientEvent());
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
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = new CompoundNBT();
		tag.putInt(NBT_SCHEMA_VERSION_KEY, NBT_SCHEMA_VERSION);
		ListNBT list = new ListNBT();
		stream()
			.map(FlowData::serialize)
			.forEach(list::add);
		tag.put(NBT_SCHEMA_DATA_KEY, list);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		upgradeToLatestSchema(tag);
		if (tag.getInt(NBT_SCHEMA_VERSION_KEY) != NBT_SCHEMA_VERSION) {
			throw new IllegalArgumentException("tag schema not latest after upgrading");
		}

		tag.getList(NBT_SCHEMA_DATA_KEY, NBT.TAG_COMPOUND).stream()
			.map(c -> ((CompoundNBT) c))
			.map(FlowDataSerializer::deserialize)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted(Comparator.comparing(a -> a instanceof RelationshipFlowData))
			.forEach(data -> data.addToDataContainer(this));
	}

	private void upgradeToLatestSchema(CompoundNBT tag) {
		int version = tag.getInt(NBT_SCHEMA_VERSION_KEY);
		if (version != NBT_SCHEMA_VERSION) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"Updating schema from version {} to {}",
				version,
				NBT_SCHEMA_VERSION
			);
		}

		switch (version) {
			case 1:
				tag.getList("__data", NBT.TAG_COMPOUND).stream()
					.map(CompoundNBT.class::cast)
					.filter(t -> t.getString("__type").equals("sfm:item_rule"))
					.forEach(t -> t.putString("__type", "sfm:item_movement_rule"));
				tag.putInt(NBT_SCHEMA_VERSION_KEY, 2);
		}
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

	public static class FlowDataContainerClosedClientEvent {

	}
}
