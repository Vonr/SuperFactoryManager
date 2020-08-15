package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.ITangible;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.RelationshipFlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Optional;

public class FlowRelationship implements IFlowView, IFlowController {
	public static final Colour3f COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public final ManagerFlowController CONTROLLER;
	public RelationshipFlowData data;

	public FlowRelationship(ManagerFlowController CONTROLLER,
		RelationshipFlowData data) {
		this.CONTROLLER = CONTROLLER;
		this.data = data;
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		Optional<ITangible> from = CONTROLLER.getController(data.from)
			.filter(c -> c instanceof ITangible)
			.map(c -> (ITangible) c);
		Optional<ITangible> to = CONTROLLER.getController(data.to)
			.filter(c -> c instanceof ITangible)
			.map(c -> (ITangible) c);

		if (!from.isPresent() || !to.isPresent()) {
			return;
		}
		screen.drawArrow(matrixStack,
			from.get().getCentroid(),
			to.get().snapToEdge(from.get().getCentroid()),
			FlowRelationship.COLOUR);
	}
}
