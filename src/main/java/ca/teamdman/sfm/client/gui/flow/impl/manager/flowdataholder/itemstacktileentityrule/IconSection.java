package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import net.minecraft.client.resources.I18n;

class IconSection extends FlowContainer {

	private ItemStackTileEntityRuleFlowComponent PARENT;
	private final IconComponent ICON;

	public IconSection(ItemStackTileEntityRuleFlowComponent parent,Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.icon.title")
		));

		this.ICON = new IconComponent(PARENT, new Position(0, 15));
		addChild(ICON);
	}

	public void onDataChanged(ItemStackTileEntityRuleFlowData data) {
		ICON.BUTTON.setItemStack(data.icon);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 10;
	}
}
