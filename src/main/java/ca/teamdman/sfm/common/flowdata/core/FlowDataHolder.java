package ca.teamdman.sfm.common.flowdata.core;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface FlowDataHolder {
	Stream<FlowData> getData();
	Optional<FlowData> getData(UUID id);
	default <T> Optional<T> getData(UUID id, Class<T> clazz) {
		return getData(id)
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}
	void removeData(UUID id);
	void addData(FlowData data);
	void clearData();
}
