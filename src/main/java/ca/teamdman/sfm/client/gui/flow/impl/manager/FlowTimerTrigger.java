/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.IFlowCloneable;
import ca.teamdman.sfm.client.gui.flow.core.IFlowDeletable;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPanel;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowTimerTriggerData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateInputPacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.ManagerPositionPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;

public class FlowTimerTrigger extends FlowIconButton implements IFlowDeletable, IFlowCloneable {

	public final ManagerFlowController CONTROLLER;
	public FlowTimerTriggerData data;

	public FlowTimerTrigger(ManagerFlowController controller, FlowTimerTriggerData data) {
		super(ButtonLabel.TRIGGER);
		this.CONTROLLER = controller;
		this.data = data;
		POS.setMovable(true);
		POS.getPosition().setXY(data.getPosition());
	}

	@Override
	public Optional<FlowData> getData() {
		return Optional.of(data);
	}

	@Override
	public void drawGhostAtPosition(
		BaseScreen screen, MatrixStack matrixStack, int x, int y, float deltaTime
	) {
		BACKGROUND.drawGhostAt(screen, matrixStack, x, y);
		ICON.drawGhostAt(screen, matrixStack, x + 4, y + 4);
	}

	@Override
	public void cloneWithPosition(int x, int y) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateInputPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			new Position(x, y)
		));
	}

	@Override
	public void delete() {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			data.getId()
		));
	}

	@Override
	public FlowPanel createPositionBox(Position pos, int width, int height) {
		//noinspection DuplicatedCode
		return new FlowPanel(pos, new Size(width, height)) {
			@Override
			public void onMoveFinished(int startMouseX, int startMouseY,
				int finishMouseX, int finishMouseY, int button) {
				PacketHandler.INSTANCE.sendToServer(new ManagerPositionPacketC2S(
					CONTROLLER.SCREEN.CONTAINER.windowId,
					CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
					data.getId(),
					this.getPosition()));
			}
		};
	}
}
