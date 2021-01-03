/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import java.util.Optional;
import org.lwjgl.glfw.GLFW;

public class DeletionController extends FlowComponent {

	public final ManagerFlowController CONTROLLER;

	public DeletionController(ManagerFlowController controller) {
		this.CONTROLLER = controller;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		// only check delete after all other event listeners
		if (keyCode == GLFW.GLFW_KEY_DELETE) {
			Optional<FlowComponent> elem = CONTROLLER.getElementUnderMouse(mx, my);
			elem = elem.filter(c -> c instanceof FlowDataHolder);
			elem.ifPresent(c -> PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
				CONTROLLER.SCREEN.getContainer().windowId,
				CONTROLLER.SCREEN.getContainer().getSource().getPos(),
				((FlowDataHolder) c).getData().getId()
			)));
			return elem.isPresent();
		}
		return false;
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 300;
	}
}
