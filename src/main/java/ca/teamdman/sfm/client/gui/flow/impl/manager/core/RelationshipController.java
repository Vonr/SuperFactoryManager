/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.core;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowRelationship;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.FlowRelationshipData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateRelationshipPacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;

@SuppressWarnings("UnstableApiUsage")
public class RelationshipController implements IFlowController, IFlowView {

	public final ManagerFlowController CONTROLLER;
	private final Position fromPos = new Position();
	private final Position toPos = new Position();
	private UUID from;
	private boolean isDragging = false;

	public RelationshipController(ManagerFlowController CONTROLLER) {
		this.CONTROLLER = CONTROLLER;
	}

	public Stream<FlowRelationshipData> getFlowRelationshipDatas() {
		return CONTROLLER.SCREEN.DATAS.values().stream()
			.filter(data -> data instanceof FlowRelationshipData)
			.map(data -> ((FlowRelationshipData) data));
	}

	public Stream<FlowRelationship> getFlowRelationships() {
		return CONTROLLER.getControllers()
			.filter(c -> c instanceof FlowRelationship)
			.map(c -> ((FlowRelationship) c));
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasShiftDown()) {
			return false;
		}

		Optional<IFlowController> controller = CONTROLLER.getElementUnderMouse(mx, my);
		if (controller.isPresent()) {
			isDragging = true;
			//noinspection OptionalGetWithoutIsPresent
			from = controller.get().getData().get().getId();
			fromPos.setXY(((IFlowTangible) controller.get()).getCentroid());
			toPos.setXY(mx, my);
			return true;
		}

		return false;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (!isDragging) {
			return false;
		}
		CONTROLLER.getElementUnderMouse(mx, my)
			.map(IFlowController::getData)
			.map(Optional::get)
			.map(FlowData::getId)
			.ifPresent(to -> createRelationship(from, to));
		isDragging = false;
		from = null;
		return true;
	}


	public void createRelationship(UUID from, UUID to) {
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateRelationshipPacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			from,
			to
		));
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		if (!isDragging) {
			return false;
		}
		toPos.setXY(mx, my);
		CONTROLLER.getElementUnderMouse(mx, my)
			.map(x -> ((IFlowTangible) x).snapToEdge(fromPos))
			.ifPresent(toPos::setXY);
		return true;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		if (isDragging) {
			screen.drawArrow(matrixStack, fromPos, toPos, FlowRelationship.COLOUR);
		}
	}
}
