/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.RelationshipController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonBackground;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.impl.LineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipFlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;

public class FlowRelationship extends FlowComponent {

	public static final Colour3f COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public final ManagerFlowController CONTROLLER;
	public RelationshipFlowData data;

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
		Optional<FlowRelationship> rel = new RelationshipController(CONTROLLER)
			.getFlowRelationships()
			.filter(r -> r.isCloseTo(mx, my))
			.findFirst();
		if (!rel.isPresent()) {
			return false;
		}
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new LineNodeFlowData(
				UUID.randomUUID(),
				new Position(
					mx - ButtonBackground.LINE_NODE.WIDTH / 2,
					my - ButtonBackground.LINE_NODE.HEIGHT / 2
				)
			)
		);
		return true;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		draw(screen, matrixStack, COLOUR);
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
	public int getZIndex() {
		return super.getZIndex() - 200;
	}

	public static class FlowRelationshipPositionPair {

		final Position FROM, TO;

		public FlowRelationshipPositionPair(Position FROM, Position TO) {
			this.FROM = FROM;
			this.TO = TO;
		}
	}
}
