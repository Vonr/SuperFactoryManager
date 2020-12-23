/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowInputButtonSpawner;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowOutputButtonSpawner;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowTimerTriggerSpawner;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.screen.ManagerScreen;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import java.util.Optional;
import java.util.stream.Collectors;
import org.lwjgl.glfw.GLFW;

public class ManagerFlowController extends FlowContainer {

	public final ManagerScreen SCREEN;
	public final RelationshipController RELATIONSHIP_CONTROLLER = new RelationshipController(this);
	public final DebugController DEBUG_CONTROLLER = new DebugController(this);
	public final CloneController CLONE_CONTROLLER = new CloneController(this);
	private final FlowIconButton INPUT_BUTTON_SPAWNER = new FlowInputButtonSpawner(this);
	private final FlowIconButton OUTPUT_BUTTON_SPAWNER = new FlowOutputButtonSpawner(this);
	private final FlowIconButton TIMER_TRIGGER_SPAWNER = new FlowTimerTriggerSpawner(this);

	public ManagerFlowController(ManagerScreen screen) {
		this.SCREEN = screen;
		rebuildChildren();
	}

	public void rebuildChildren() {
		getChildren().clear();
		addChild(DEBUG_CONTROLLER);
		addChild(CLONE_CONTROLLER);
		addChild(RELATIONSHIP_CONTROLLER);
		addChild(INPUT_BUTTON_SPAWNER);
		addChild(OUTPUT_BUTTON_SPAWNER);
		addChild(TIMER_TRIGGER_SPAWNER);
		SCREEN.getData()
			.map(data -> data.createController(this))
			.forEach(this::addChild);
	}

	public void notifyDataAdded(FlowData data) {
		addChild(data.createController(this));
	}

	public void notifyDataDeleted(FlowData data) {
		getChildren().stream()
			.filter(c -> c instanceof FlowDataHolder)
			.filter(c -> ((FlowDataHolder) c).getData().equals(data))
			.collect(Collectors.toList())
			.forEach(this::removeChild);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (super.keyPressed(keyCode, scanCode, modifiers, mx, my)) {
			return true;
		}

		// only check delete after all other event listeners
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			Optional<FlowComponent> elem = getElementUnderMouse(mx, my)
				.filter(c -> c instanceof FlowDataHolder);
			elem.ifPresent(c -> PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
				SCREEN.CONTAINER.windowId,
				SCREEN.CONTAINER.getSource().getPos(),
				((FlowDataHolder) c).getData().getId()
			)));
			return elem.isPresent();
		}
		return false;
	}
}
