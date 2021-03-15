package ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemrule.ItemRuleFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;

public class TileMatcherSpawnerDrawer extends FlowDrawer {

	public TileMatcherSpawnerDrawer(ItemRuleFlowComponent parent, Position pos) {
		super(pos, 3, 3);
		addChild(new TilePositionMatcherSpawnerButton(parent));
		update();
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 100;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.pauseScissor();
		super.draw(screen, matrixStack, mx, my, deltaTime);
		screen.resumeScissor();
	}
}
