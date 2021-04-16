package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.ItemMode;
import net.minecraft.client.resources.I18n;

class ItemModeSection extends FlowContainer {
	private final RadioGroup GROUP;
	private final RadioFlowButton ALL_BUTTON;
	private final RadioFlowButton ANY_BUTTON;
	private ItemConditionRuleFlowComponent PARENT;

	public ItemModeSection(ItemConditionRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		this.GROUP = new RadioGroup() {
			@Override
			public void onSelectionChanged(RadioFlowButton member) {
				ItemMode next = null;
				if (member == ALL_BUTTON) {
					next = ItemMode.MATCH_ALL;
				}
				if (member == ANY_BUTTON) {
					next = ItemMode.MATCH_ANY;
				}
				if (next != null && PARENT.getData().itemMode != next) {
					PARENT.getData().itemMode = next;
					PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
				}
			}
		};
		ALL_BUTTON = new RadioFlowButton(
			new Position(0, 0),
			new Size(14, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.match_all"),
			I18n.format("gui.sfm.flow.tileentityrule.tooltip.match_all"),
			GROUP
		);
		addChild(ALL_BUTTON);

		ANY_BUTTON = new RadioFlowButton(
			new Position(16, 0),
			new Size(14, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.match_any"),
			I18n.format("gui.sfm.flow.tileentityrule.tooltip.match_any"),
			GROUP
		);
		addChild(ANY_BUTTON);

		onDataChanged(PARENT.getData());
	}


	public void onDataChanged(
		ItemConditionRuleFlowData data
	) {
		RadioFlowButton next = null;
		if (data.itemMode == ItemMode.MATCH_ALL) {
			next = ALL_BUTTON;
		}
		if (data.itemMode == ItemMode.MATCH_ANY) {
			next = ANY_BUTTON;
		}
		if (next != null) {
			GROUP.setSelected(next);
		}
	}
}
