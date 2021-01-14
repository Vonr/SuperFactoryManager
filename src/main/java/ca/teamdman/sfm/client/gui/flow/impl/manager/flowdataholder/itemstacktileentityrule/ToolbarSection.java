package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public class ToolbarSection extends FlowContainer {

	private ItemStackTileEntityRuleFlowComponent PARENT;

	public ToolbarSection(ItemStackTileEntityRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new MinimizeButton(
			PARENT,
			new Position(180, 5),
			new Size(10, 10)
		));
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		super.draw(screen, matrixStack, mx, my, deltaTime);

		screen.drawString(
			matrixStack,
			PARENT.getData().name,
			getPosition().getX() + 5,
			getPosition().getY() + 5,
			CONST.TEXT_LIGHT
		);
	}
}
