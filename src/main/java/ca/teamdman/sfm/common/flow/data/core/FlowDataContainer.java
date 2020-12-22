/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

public interface FlowDataContainer {
	Stream<FlowData> getData();
	Optional<FlowData> getData(UUID id);
	default <T> Optional<T> getData(UUID id, Class<T> clazz) {
		return getData(id)
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	@SuppressWarnings("unchecked")
	default <T extends FlowData> Stream<T> getData(Class<T> clazz) {
		return getData()
			.filter(clazz::isInstance)
			.map(data -> (T) data);
	}

	void removeData(UUID id);
	void addData(FlowData data);
	void clearData();

	void notifyChanged(
		UUID id, ChangeType type
	);
	void onChange(UUID id, BiConsumer<FlowData, ChangeType> callback);

	default void notifyChanged(FlowData data, ChangeType type) {
		notifyChanged(data.getId(), type);
	};

	public enum ChangeType {
		ADDED,
		UPDATED;
	}
}
