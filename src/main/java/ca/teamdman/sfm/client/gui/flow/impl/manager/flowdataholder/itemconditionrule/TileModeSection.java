package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemconditionrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData.TileMode;
import net.minecraft.client.resources.I18n;

class TileModeSection extends FlowContainer {

	private final RadioGroup GROUP;
	private final RadioFlowButton ALL_BUTTON;
	private final RadioFlowButton ANY_BUTTON;
	private ItemConditionRuleFlowComponent PARENT;

	public TileModeSection(ItemConditionRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		this.GROUP = new RadioGroup() {
			@Override
			public void onSelectionChanged(RadioFlowButton member) {
				TileMode next = null;
				if (member == ALL_BUTTON) {
					next = TileMode.MATCH_ALL;
				}
				if (member == ANY_BUTTON) {
					next = TileMode.MATCH_ANY;
				}
				if (next != null && PARENT.getData().tileMode != next) {
					PARENT.getData().tileMode = next;
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
		if (data.tileMode == TileMode.MATCH_ALL) {
			next = ALL_BUTTON;
		}
		if (data.tileMode == TileMode.MATCH_ANY) {
			next = ANY_BUTTON;
		}
		if (next != null) {
			GROUP.setSelected(next);
		}
	}
}
