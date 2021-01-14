package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowRadioButton.RadioGroup;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData.FilterMode;
import java.util.Optional;
import net.minecraft.client.resources.I18n;

class ItemsSection extends FlowContainer {

	protected final FlowDrawer DRAWER;
	private final RadioGroup ITEM_SELECTION_MODE_GROUP;
	private final FlowRadioButton WHITELIST_BUTTON;
	private final FlowRadioButton BLACKLIST_BUTTON;
	protected ItemStackTileEntityRuleFlowComponent PARENT;


	public ItemsSection(ItemStackTileEntityRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));

		this.ITEM_SELECTION_MODE_GROUP = new RadioGroup() {
			@Override
			public void onSelectionChanged(FlowRadioButton member) {
				FilterMode next = member == WHITELIST_BUTTON
					? FilterMode.WHITELIST
					: FilterMode.BLACKLIST;
				if (PARENT.getData().filterMode != next) {
					PARENT.getData().filterMode = next;
					PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
				}
			}
		};
		WHITELIST_BUTTON = new FlowRadioButton(
			new Position(0, 15),
			new Size(35, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.whitelist"),
			ITEM_SELECTION_MODE_GROUP
		);
		addChild(WHITELIST_BUTTON);

		BLACKLIST_BUTTON = new FlowRadioButton(
			new Position(40, 15),
			new Size(35, 12),
			I18n.format("gui.sfm.flow.tileentityrule.button.blacklist"),
			ITEM_SELECTION_MODE_GROUP
		);
		addChild(BLACKLIST_BUTTON);

		DRAWER = new FlowDrawer(new Position(0, 90), 4, 3);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

		onDataChanged(PARENT.getData());
	}

	public void onDataChanged(
		ItemStackTileEntityRuleFlowData data
	) {
		ITEM_SELECTION_MODE_GROUP.setSelected(
			data.filterMode == FilterMode.WHITELIST
				? WHITELIST_BUTTON
				: BLACKLIST_BUTTON
		);
		rebuildChildren();
	}

	public void rebuildChildren() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(new AddMatcherButton(
			PARENT.CONTROLLER,
			PARENT.getData(),
			new Position(5, getSize().getHeight() - 21)
		));
		PARENT.getData().matcherIds.stream()
			.map(PARENT.CONTROLLER::findFirstChild)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(FlowDataHolder.class::isInstance)
			.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemStackMatcher)
			.map(c -> new MatcherDrawerItem(this, c))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}
}
