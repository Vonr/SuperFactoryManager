package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackComparerMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;

class AddMatcherButton extends FlowPlusButton {

	private ManagerFlowController CONTROLLER;
	private ItemStackTileEntityRuleFlowData data;

	public AddMatcherButton(
		ManagerFlowController CONTROLLER, ItemStackTileEntityRuleFlowData data,
		Position pos
	) {
		super(pos, ItemStackFlowComponent.DEFAULT_SIZE, CONST.ADD_BUTTON);
		this.CONTROLLER = CONTROLLER;
		this.data = data;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		FlowData matcher = new ItemStackComparerMatcherFlowData(
			UUID.randomUUID(),
			new ItemStack(Blocks.STONE),
			0
		);
		data.matcherIds.add(matcher.getId());
		CONTROLLER.SCREEN.sendFlowDataToServer(matcher, data);
	}
}
