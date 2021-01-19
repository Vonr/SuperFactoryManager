package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

class SelectionRulesDrawerItem extends ItemStackFlowComponent {

	public ItemStackTileEntityRuleFlowData DATA;
	private final ItemStackTileEntityRuleDrawer PARENT;

	public SelectionRulesDrawerItem(
		ItemStackTileEntityRuleFlowData data,
		ItemStackTileEntityRuleDrawer parent
	) {
		super(data.getIcon(), new Position());
		this.DATA = data;
		PARENT = parent;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		ArrayList<ITextComponent> list = new ArrayList<>();
		list.add(new StringTextComponent(DATA.name));
		return list;
	}

	@Override
	public void onSelectionChanged() {
		List<UUID> next = PARENT.getChildrenRules().stream()
			.map(FlowData::getId)
			.collect(Collectors.toList());
		if (isSelected()) {
			next.add(DATA.getId());
		} else {
			next.remove(DATA.getId());
		}
		PARENT.setChildrenRules(next);
	}
}
