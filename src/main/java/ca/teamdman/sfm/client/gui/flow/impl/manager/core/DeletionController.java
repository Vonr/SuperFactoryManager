/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.FlowData;
import java.util.Optional;
import java.util.UUID;
import org.lwjgl.glfw.GLFW;

public class DeletionController extends FlowComponent {

	public final ManagerFlowController CONTROLLER;

	public DeletionController(ManagerFlowController controller) {
		this.CONTROLLER = controller;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			Optional<UUID> elem = CONTROLLER.getElementsUnderMouse(mx, my)
				.filter(FlowDataHolder.class::isInstance)
				.map(FlowDataHolder.class::cast)
				.filter(FlowDataHolder::isDeletable)
				.map(FlowDataHolder::getData)
				.map(FlowData::getId)
				.findFirst();
			elem.ifPresent(CONTROLLER.SCREEN::sendFlowDataDeleteToServer);
			return elem.isPresent();
		}
		return false;
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 300;
	}
}
