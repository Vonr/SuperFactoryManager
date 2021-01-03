/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipFlowData;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.INBTSerializable;

public interface FlowDataContainer extends INBTSerializable<ListNBT> {

	default <T> Optional<T> getData(UUID id, Class<T> clazz) {
		return getData(id)
			.filter(clazz::isInstance)
			.map(clazz::cast);
	}

	Optional<FlowData> getData(UUID id);

	@SuppressWarnings("unchecked")
	default <T extends FlowData> Stream<T> getData(Class<T> clazz) {
		return getData()
			.filter(clazz::isInstance)
			.map(data -> (T) data);
	}

	Stream<FlowData> getData();

	void removeData(UUID id);

	void addData(FlowData data);

	void clearData();

	void addChangeListener(UUID id, BiConsumer<FlowData, ChangeType> callback);

	default void notifyChanged(FlowData data, ChangeType type) {
		notifyChanged(data.getId(), type);
	}

	void notifyChanged(
		UUID id, ChangeType type
	);


	@Override
	default ListNBT serializeNBT() {
		ListNBT list = new ListNBT();
		getData().forEach(d -> list.add(d.getSerializer().toNBT(d)));
		return list;
	}

	@Override
	default void deserializeNBT(ListNBT list) {
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

	enum ChangeType {
		ADDED,
		UPDATED,
		DELETED;
	}
}
