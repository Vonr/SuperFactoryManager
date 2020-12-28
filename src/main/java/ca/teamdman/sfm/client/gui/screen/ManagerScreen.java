/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.BaseContainerScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerScreen extends BaseContainerScreen<ManagerContainer> implements
	FlowDataContainer {

	public final HashMap<UUID, FlowData> DATAS = new HashMap<>();
	public final ManagerFlowController CONTROLLER;
	private final Multimap<UUID, BiConsumer<FlowData, ChangeType>> listeners = ArrayListMultimap
		.create();

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, 512, 256, inv, name);
		CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public void notifyChanged(
		UUID id, ChangeType type
	) {
		getData(id).ifPresent(data -> {
			listeners.get(id).forEach(c -> c.accept(data, type));
			listeners.get(null).forEach(c -> c.accept(data, type));
		});
		CONTROLLER.getChildren().stream()
			.filter(c -> c instanceof FlowDataHolder)
			.map(c -> ((FlowDataHolder) c))
			.filter(c -> c.getData().getId().equals(id))
			.forEach(FlowDataHolder::onDataChanged);
	}

	@Override
	public void onChange(
		UUID id, BiConsumer<FlowData, ChangeType> callback
	) {
		listeners.put(id, callback);
	}

	@Override
	public Stream<FlowData> getData() {
		return DATAS.values().stream();
	}

	@Override
	public void removeData(UUID id) {
		FlowData data = DATAS.remove(id);
		if (data != null) {
			CONTROLLER.notifyDataDeleted(data);
		}
	}

	@Override
	public void clearData() {
		DATAS.clear();
	}

	@Override
	public void addData(FlowData data) {
		if (DATAS.containsKey(data.getId())) {
			CONTROLLER.findFirstChild(data)
				.filter(c -> ((FlowDataHolder) c).getData().getClass().equals(data.getClass()))
				.ifPresent(c -> ((FlowDataHolder) c).getData().merge(data));
			notifyChanged(data.getId(), ChangeType.UPDATED);
		} else {
			DATAS.put(data.getId(), data);
			CONTROLLER.notifyDataAdded(data);
			notifyChanged(data.getId(), ChangeType.ADDED);
		}
	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return Optional.ofNullable(DATAS.get(id));
	}

	@Override
	public boolean mouseClickedScaled(int mx, int my, int button) {
		return CONTROLLER.mousePressed(mx, my, button);
	}

	@Override
	public boolean keyPressedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleasedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return CONTROLLER.keyReleased(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean mouseReleasedScaled(int mx, int my, int button) {
		return CONTROLLER.mouseReleased(mx, my, button);
	}

	@Override
	public boolean onMouseDraggedScaled(int mx, int my, int button, int dmx, int dmy) {
		return CONTROLLER.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseScrolledScaled(int mx, int my, double scroll) {
		return CONTROLLER.mouseScrolled(mx, my, scroll);
	}

	@Override
	public void drawScaled(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		super.drawScaled(matrixStack, mx, my, partialTicks);
		CONTROLLER.draw(this, matrixStack, mx, my, partialTicks);
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER
			.debug(SFMUtil.getMarker(getClass()), "Loading {} data entries from tile",
				CONTAINER.getSource().getDataCount()
			);
		DATAS.clear();
		CONTAINER.getSource().getData().forEach(data -> DATAS.put(data.getId(), data.copy()));
		CONTROLLER.rebuildChildren();
	}
}
