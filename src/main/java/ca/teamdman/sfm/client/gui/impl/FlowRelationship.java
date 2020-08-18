package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.FlowIconButton.ButtonBackground;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import net.minecraft.client.gui.screen.Screen;

public class FlowRelationship implements IFlowView, IFlowController {

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
		Optional<FlowRelationship> rel = CONTROLLER.RELATIONSHIP_CONTROLLER.getFlowRelationships()
			.filter(r -> r.isCloseTo(mx, my))
			.findFirst();
		if (!rel.isPresent()) {
			return false;
		}
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateLineNodePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
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
	public int getZIndex() {
		return -1;
	}

	@Override
	public IFlowView getView() {
		return this;
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
	 * If both Flow elements exist for the FROM and TO ids of this relationship, returns the
	 * positions that line use for drawing
	 *
	 * @return position pair
	 */
	public Optional<FlowRelationshipPositionPair> getPositions() {
		Optional<IFlowTangible> from = CONTROLLER.getController(data.from)
			.filter(c -> c instanceof IFlowTangible)
			.map(c -> (IFlowTangible) c);
		Optional<IFlowTangible> to = CONTROLLER.getController(data.to)
			.filter(c -> c instanceof IFlowTangible)
			.map(c -> (IFlowTangible) c);

		return from
			.filter(__ -> to.isPresent())
			.map(fromShape -> new FlowRelationshipPositionPair(
				fromShape.getCentroid(),
				to.get().snapToEdge(fromShape.getCentroid())
			));
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		draw(screen, matrixStack, COLOUR);
	}

	public void draw(BaseScreen screen, MatrixStack matrixStack, Colour3f colour) {
		getPositions().ifPresent(pair -> screen.drawArrow(matrixStack, pair.FROM, pair.TO, colour));
	}

	public static class FlowRelationshipPositionPair {

		final Position FROM, TO;

		public FlowRelationshipPositionPair(Position FROM, Position TO) {
			this.FROM = FROM;
			this.TO = TO;
		}
	}
}
