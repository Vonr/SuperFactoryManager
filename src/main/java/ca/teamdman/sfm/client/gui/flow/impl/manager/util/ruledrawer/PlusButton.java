package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

class PlusButton extends FlowPlusButton {


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
		List<ITextProperties> rtn = new ArrayList<>();
		if (PARENT.isGlobalOpen) {
			rtn.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.addbutton.label_global"));
		} else {
			rtn.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.addbutton.label"));
			rtn.add(new StringTextComponent(""));
			rtn.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.addbutton.hint")
				.mergeStyle(TextFormatting.GRAY));
		}
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
			FlowData newRule = new ItemRuleFlowData();
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
