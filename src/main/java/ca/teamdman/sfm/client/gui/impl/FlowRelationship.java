package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.ITangible;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateLineNodePacketC2S;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import net.minecraft.client.gui.screen.Screen;

public class FlowRelationship implements IFlowView, IFlowController {

	public static final Colour3f COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public static final Colour3f SELECTED_COLOUR = new Colour3f(0.4f, 0.4f, 0.8f);
	public final ManagerFlowController CONTROLLER;
	public RelationshipFlowData data;
	public boolean selected = false;

	public FlowRelationship(ManagerFlowController CONTROLLER,
		RelationshipFlowData data) {
		this.CONTROLLER = CONTROLLER;
		this.data = data;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (!Screen.hasControlDown()) {
			return false;
		}
		Optional<FlowRelationship> rel = CONTROLLER.RELATIONSHIP_CONTROLLER.getFlowRelationships()
			.filter(r -> getDistance(mx, my) < 4)
			.findFirst();
		if (!rel.isPresent()) {
			return false;
		}
		selected = true;
		PacketHandler.INSTANCE.sendToServer(new ManagerCreateLineNodePacketC2S(
			CONTROLLER.SCREEN.CONTAINER.windowId,
			CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
			rel.get().data.from,
			rel.get().data.to,
			new Position(mx, my)
		));
		return true;
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		selected = false;
		return true;
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return false;
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
	 * Gets the smallest distance from a point to this line. https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
	 */
	public double getDistance(int x, int y) {
		return mapPositions((from, to) -> Math.abs(
			(from.getY() - to.getY()) * x - (from.getX() - to.getX()) * y + from.getX() * to.getY()
				- from.getY() * to.getX()) / Math
			.sqrt(Math.pow(from.getY() - to.getY(), 2) + Math.pow(from.getX() - to.getX(), 2)));
	}

	public <R> R mapPositions(BiFunction<Position, Position, R> callback) {
		Optional<ITangible> from = CONTROLLER.getController(data.from)
			.filter(c -> c instanceof ITangible)
			.map(c -> (ITangible) c);
		Optional<ITangible> to = CONTROLLER.getController(data.to)
			.filter(c -> c instanceof ITangible)
			.map(c -> (ITangible) c);

		if (!from.isPresent() || !to.isPresent()) {
			return null;
		}

		return callback
			.apply(from.get().getCentroid(), to.get().snapToEdge(from.get().getCentroid()));
	}

	public void ifPositionsPresent(BiConsumer<Position, Position> callback) {
		mapPositions((from, to) -> {
			callback.accept(from, to);
			return Void.TYPE;
		});
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		ifPositionsPresent((from, to) ->
			screen.drawArrow(matrixStack, from, to, selected ? SELECTED_COLOUR : COLOUR));
	}
}
