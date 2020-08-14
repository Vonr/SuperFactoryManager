package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.manager.ManagerFlowController;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.FlowRelationshipData;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowRelationship implements IFlowView, IFlowController {
	public static final Colour3f COLOUR = new Colour3f(0.4f, 0.4f, 0.4f);
	public final ManagerFlowController CONTROLLER;
	public FlowRelationshipData data;
	public Position from = new Position();
	public Position to = new Position();

	public FlowRelationship(ManagerFlowController CONTROLLER,
		FlowRelationshipData data) {
		this.CONTROLLER = CONTROLLER;
		this.data = data;
		FlowData from = CONTROLLER.SCREEN.DATAS.get(data.from);
		FlowData to = CONTROLLER.SCREEN.DATAS.get(data.to);
		if (from instanceof PositionProvider) {
			this.from = ((PositionProvider) from).getPosition();
		}
		if (to instanceof PositionProvider) {
			this.to = ((PositionProvider) to).getPosition();
		}
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		screen.drawArrow(matrixStack, from, to, COLOUR);
	}
}
