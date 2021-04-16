/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonBackground;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerCreateLineNodePacketC2S;
import ca.teamdman.sfm.common.util.SFMUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.client.gui.screen.Screen;

public class FlowRelationship extends FlowComponent implements
	FlowDataHolder<RelationshipFlowData> {

	public static final Colour3f COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public final ManagerFlowController CONTROLLER;
	private RelationshipFlowData data;

	public FlowRelationship(
		ManagerFlowController CONTROLLER,
		RelationshipFlowData data
	) {
		this.CONTROLLER = CONTROLLER;
		this.data = data;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasControlDown()) {
			return false;
		}
		Optional<FlowRelationship> rel = CONTROLLER.getChildren().stream()
			.filter(FlowRelationship.class::isInstance)
			.map(FlowRelationship.class::cast)
			.filter(r -> r.isCloseTo(mx, my))
			.findFirst();
		if (!rel.isPresent()) {
			return false;
		}
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateLineNodePacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			rel.get().data.from,
			rel.get().data.to,
			new Position(
				mx - ButtonBackground.LINE_NODE.WIDTH / 2,
				my - ButtonBackground.LINE_NODE.HEIGHT / 2
			)
		));
		return true;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		draw(screen, matrixStack, COLOUR);
	}

	@Override
	public Stream<? extends FlowComponent> getElementsUnderMouse(int mx, int my) {
		return isCloseTo(mx, my) ? Stream.of(this) : Stream.empty();
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() - 200;
	}

	public void draw(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
		getPositions().ifPresent(pair -> screen.drawArrow(matrixStack, pair.FROM, pair.TO, colour));
	}

	/**
	 * Checks if the line is "close enough" to the point
	 *
	 * @param x x
	 * @param y y
	 * @return is close
	 */
	public boolean isCloseTo(int x, int y) {
		return getDistance(x, y) < 3;
	}

	/**
	 * Gets the smallest distance from a point to this line.
	 */
	public double getDistance(int x, int y) {
		Optional<FlowRelationshipPositionPair> pair = getPositions();
		if (!pair.isPresent()) {
			return Double.MAX_VALUE;
		}
		Position from = pair.get().FROM;
		Position to = pair.get().TO;
		return SFMUtil
			.getDistanceFromLine(
				x,
				y,
				from.getX(),
				from.getY(),
				to.getX(),
				to.getY()
			);
	}

	/**
	 * If both Flow elements exist for the FROM and TO ids of this relationship, returns the
	 * positions that line use for drawing
	 *
	 * @return position pair
	 */
	public Optional<FlowRelationshipPositionPair> getPositions() {
		Optional<FlowComponent> from = CONTROLLER.findFirstChild(data.from);
		Optional<FlowComponent> to = CONTROLLER.findFirstChild(data.to);
		return from
			.filter(__ -> to.isPresent())
			.map(fromShape -> new FlowRelationshipPositionPair(
				fromShape.getCentroid(),
				to.get().snapToEdge(fromShape.getCentroid())
			));
	}

	@Override
	public RelationshipFlowData getData() {
		return data;
	}

	@Override
	public void setData(RelationshipFlowData data) {
		this.data = data;
	}

	@Override
	public boolean isDeletable() {
		// can only delete if not a condition<=>node relationship
		return !(CONTROLLER.findFirstChild(data.to)
			.filter(ConditionLineNodeFlowComponent.class::isInstance)
			.isPresent()
			&& CONTROLLER.findFirstChild(data.from)
			.filter(ItemConditionFlowButton.class::isInstance)
			.isPresent());
	}

	public static class FlowRelationshipPositionPair {

		final Position FROM, TO;

		public FlowRelationshipPositionPair(Position FROM, Position TO) {
			this.FROM = FROM;
			this.TO = TO;
		}
	}
}
