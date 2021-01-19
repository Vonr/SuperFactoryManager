package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import ca.teamdman.sfm.common.util.SlotsRule;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

class PlusButton extends FlowPlusButton {

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
	private final ItemStackTileEntityRuleDrawer PARENT;

	public PlusButton(ItemStackTileEntityRuleDrawer parent) {
		super(
			new Position(),
			ItemStackFlowComponent.DEFAULT_SIZE.copy(),
			CONST.ADD_BUTTON
		);
		PARENT = parent;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<TranslationTextComponent> rtn = new ArrayList<>();
		rtn.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.addbutton.hint"));
		return rtn;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		if (PARENT.isGlobalOpen || Screen.hasShiftDown()) {
			// Toggle global drawer open when shift is held
			// Close global drawer when it is open, regardless of shift status
			PARENT.isGlobalOpen = !PARENT.isGlobalOpen;
			PARENT.rebuildDrawer();
		} else {
			//todo: remove debug item icons, or put more effort into random rule icons
			FlowData newRule = new ItemStackTileEntityRuleFlowData(
				UUID.randomUUID(),
				"New tile entity rule",
				items[(int) (Math.random() * items.length)],
				new Position(0, 0),
				FilterMode.WHITELIST,
				Collections.emptyList(),
				Collections.emptyList(),
				EnumSet.allOf(Direction.class),
				new SlotsRule("")
			);
			if (PARENT.isGlobalOpen) {
				// If global drawer is open, just create new rule
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(newRule);
			} else {
				// If global is hidden, also associate new rule with parent

				List<UUID> next = PARENT.getChildrenRuleIds();
				next.add(newRule.getId());
				FlowData nextParent = PARENT.getDataWithNewChildren(next);

				// Create new rule and associate the parent with it
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(newRule, nextParent);
			}
		}
	}
}
