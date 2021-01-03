/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.ControllerScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketC2S;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerScreen extends ControllerScreen<ManagerFlowController> implements
	FlowDataContainer, IHasContainer<ManagerContainer> {

	private final HashMap<UUID, FlowData> DATAS = new HashMap<>();
	private final ManagerFlowController CONTROLLER;
	private final ManagerContainer CONTAINER;
	private final Multimap<UUID, BiConsumer<FlowData, ChangeType>> listeners = ArrayListMultimap
		.create();

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(name, 512, 256);
		this.CONTAINER = container;
		this.CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public ManagerFlowController getController() {
		return CONTROLLER;
	}

	@Override
	public void notifyChanged(
		UUID id, ChangeType type
	) {
		getData(id).ifPresent(data -> {
			listeners.get(id).forEach(c -> c.accept(data, type));
			listeners.get(null).forEach(c -> c.accept(data, type));
		});
		getController().getChildren().stream()
			.filter(c -> c instanceof FlowDataHolder)
			.map(c -> ((FlowDataHolder) c))
			.filter(c -> c.getData().getId().equals(id))
			.forEach(FlowDataHolder::onDataChanged);
	}

	@Override
	public void addChangeListener(
		UUID id, BiConsumer<FlowData, ChangeType> callback
	) {
		listeners.put(id, callback);
	}

	public void sendFlowDataToServer(FlowData data) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowDataPacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			data
		));
	}

	@Override
	public Stream<FlowData> getData() {
		return DATAS.values().stream();
	}

	@Override
	public void removeData(UUID id) {
		FlowData data = DATAS.remove(id);
		if (data != null) {
			getController().notifyDataDeleted(data);
		}
	}

	@Override
	public void clearData() {
		DATAS.clear();
	}

	@Override
	public void addData(FlowData data) {
		if (DATAS.containsKey(data.getId())) {
			getController().findFirstChild(data)
				.filter(c -> ((FlowDataHolder) c).getData().getClass().equals(data.getClass()))
				.ifPresent(c -> ((FlowDataHolder) c).getData().merge(data));
			notifyChanged(data.getId(), ChangeType.UPDATED);
		} else {
			DATAS.put(data.getId(), data);
			getController().notifyDataAdded(data);
			notifyChanged(data.getId(), ChangeType.ADDED);
		}
	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return Optional.ofNullable(DATAS.get(id));
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER
			.debug(SFMUtil.getMarker(getClass()), "Loading {} data entries from tile",
				(int) CONTAINER.getSource().getFlowDataContainer().getData().count()
			);
		DATAS.clear();
		CONTAINER.getSource().getFlowDataContainer().getData()
			.forEach(data -> DATAS.put(data.getId(), data));
		getController().rebuildChildren();
	}

	@Override
	public ManagerContainer getContainer() {
		return CONTAINER;
	}
}
