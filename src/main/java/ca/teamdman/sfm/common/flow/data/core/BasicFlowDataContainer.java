package ca.teamdman.sfm.common.flow.data.core;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public class BasicFlowDataContainer implements FlowDataContainer {
	private final Multimap<UUID, BiConsumer<FlowData, ChangeType>> listeners = ArrayListMultimap
		.create();
	private final HashMap<UUID, FlowData> DATAS = new HashMap<>();

	@Override
	public Stream<FlowData> getData() {
		return DATAS.values().stream();
	}


	@Override
	public Optional<FlowData> getData(UUID id) {
		return Optional.ofNullable(DATAS.get(id));
	}

	@Override
	public void removeData(UUID id) {
		FlowData data = DATAS.remove(id);
		notifyChanged(data.getId(), ChangeType.DELETED);
	}

	@Override
	public void addData(FlowData data) {
		if (DATAS.containsKey(data.getId())) {
			DATAS.put(data.getId(), data);
			notifyChanged(data.getId(), ChangeType.UPDATED);
		} else {
			DATAS.put(data.getId(), data);
			notifyChanged(data.getId(), ChangeType.ADDED);
		}
	}

	@Override
	public void clearData() {
		List<FlowData> old = new ArrayList<>(DATAS.values());
		DATAS.clear();
		old.forEach(data -> notifyChanged(data.getId(), ChangeType.DELETED));
	}

	@Override
	public void notifyChanged(
		UUID id, ChangeType type
	) {
		getData(id).ifPresent(data -> {
			listeners.get(id).forEach(c -> c.accept(data, type));
			listeners.get(null).forEach(c -> c.accept(data, type));
		});
	}

	@Override
	public void addChangeListener(
		UUID id, BiConsumer<FlowData, ChangeType> callback
	) {
		listeners.put(id, callback);
	}
}
