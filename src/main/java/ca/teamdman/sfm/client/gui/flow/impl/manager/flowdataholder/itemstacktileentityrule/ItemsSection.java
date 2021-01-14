package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemStackMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import java.util.Optional;
import net.minecraft.client.resources.I18n;

class ItemsSection extends FlowContainer {

	protected final FlowDrawer DRAWER;
	protected ItemStackTileEntityRuleFlowComponent PARENT;

	public ItemsSection(ItemStackTileEntityRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));


		DRAWER = new FlowDrawer(new Position(0, 16), 4, 3);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

		onDataChanged(PARENT.getData());
	}

	public void onDataChanged(
		ItemStackTileEntityRuleFlowData data
	) {
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
