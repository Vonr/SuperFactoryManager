package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.movementrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.RadioFlowButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData.FilterMode;
import net.minecraft.client.resources.I18n;

class FilterSection extends FlowContainer {

	private final RadioGroup ITEM_SELECTION_MODE_GROUP;
	private final RadioFlowButton WHITELIST_BUTTON;
	private final RadioFlowButton BLACKLIST_BUTTON;
	private MovementRuleFlowComponent PARENT;

	public FilterSection(MovementRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.get("gui.sfm.manager.tile_entity_rule.filter.title")
		));
		this.ITEM_SELECTION_MODE_GROUP = new RadioGroup() {
			@Override
			public void onSelectionChanged(RadioFlowButton member) {
				FilterMode next = member == WHITELIST_BUTTON
					? FilterMode.WHITELIST
					: FilterMode.BLACKLIST;
				if (PARENT.getData().filterMode != next) {
					PARENT.getData().filterMode = next;
					PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
				}
			}
		};
		WHITELIST_BUTTON = new RadioFlowButton(
			new Position(0, 15),
			new Size(35, 12),
			I18n.get("gui.sfm.flow.tileentityrule.button.whitelist"),
			ITEM_SELECTION_MODE_GROUP
		);
		addChild(WHITELIST_BUTTON);

		BLACKLIST_BUTTON = new RadioFlowButton(
			new Position(0, 30),
			new Size(35, 12),
			I18n.get("gui.sfm.flow.tileentityrule.button.blacklist"),
			ITEM_SELECTION_MODE_GROUP
		);
		addChild(BLACKLIST_BUTTON);
		onDataChanged(PARENT.getData());
	}


	public void onDataChanged(
		ItemMovementRuleFlowData data
	) {
		ITEM_SELECTION_MODE_GROUP.setSelected(
			data.filterMode == FilterMode.WHITELIST
				? WHITELIST_BUTTON
				: BLACKLIST_BUTTON
		);
	}
}
