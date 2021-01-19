package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import ca.teamdman.sfm.common.util.SlotsRule;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

class AddRuleButton extends FlowPlusButton {

	private final ItemStack[] items = {
		new ItemStack(Blocks.BEACON),
		new ItemStack(Blocks.STONE),
		new ItemStack(Blocks.SAND),
		new ItemStack(Blocks.SANDSTONE),
		new ItemStack(Blocks.TURTLE_EGG),
		new ItemStack(Blocks.DRAGON_EGG),
		new ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE),
		new ItemStack(Blocks.CREEPER_HEAD),
	};
	private ManagerFlowController CONTROLLER;

	public AddRuleButton(ManagerFlowController controller) {
		super(
			new Position(),
			ItemStackFlowComponent.DEFAULT_SIZE.copy(),
			CONST.ADD_BUTTON
		);
		this.CONTROLLER = controller;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		//todo: remove debug item icons, or put more effort into random rule icons
		//todo: abstract onclicked???
		CONTROLLER.SCREEN.sendFlowDataToServer(
			new ItemStackTileEntityRuleFlowData(
				UUID.randomUUID(),
				"New tile entity rule",
				items[(int) (Math.random() * items.length)],
				new Position(0, 0),
				FilterMode.WHITELIST,
				Collections.emptyList(),
				Collections.emptyList(),
				EnumSet.allOf(Direction.class),
				new SlotsRule("")
			)
		);
	}
}
