package ca.teamdman.sfm.common.flowdata;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

public interface FlowDataHolder {
	Stream<FlowData> getData();
	Optional<FlowData> getData(UUID id);
	void removeData(UUID id);
	void addData(FlowData data);
	void clearData();
}
