package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule.ItemStackTileEntityRuleFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

class ChildRulesDrawerItem extends ItemStackFlowComponent implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	private final ManagerFlowController CONTROLLER;
	private final FlowDrawer PARENT;
	public ItemStackTileEntityRuleFlowData data;

	public ChildRulesDrawerItem(
		ItemStackTileEntityRuleFlowData data,
		ManagerFlowController controller,
		FlowDrawer parent
	) {
		super(data.getIcon(), new Position());
		CONTROLLER = controller;
		PARENT = parent;
		setData(data);
		CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			this,
			ItemStackTileEntityRuleFlowData.class
		));
	}


	private void refreshSelection() {
		setSelected(CONTROLLER.findFirstChild(data.getId())
			.filter(FlowComponent::isVisible)
			.isPresent());
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		ArrayList<ITextComponent> list = new ArrayList<>();
		list.add(new StringTextComponent(data.name));
		return list;
	}

	@Override
	public void onSelectionChanged() {
		if (!Client.allowMultipleRuleWindows && isSelected()) {
			CONTROLLER.getChildren().stream()
				.filter(c -> c instanceof ItemStackTileEntityRuleFlowComponent)
				.map(c -> ((ItemStackTileEntityRuleFlowComponent) c))
				.forEach(c -> {
					c.setVisible(false);
					c.setEnabled(false);
				});
			PARENT.getChildren().stream()
				.filter(c -> c instanceof ChildRulesDrawerItem && c != this)
				.forEach(c -> ((ChildRulesDrawerItem) c).setSelected(false));
		}
		CONTROLLER.findFirstChild(data.getId()).ifPresent(comp -> {
			comp.setVisible(isSelected());
			comp.setEnabled(isSelected());
		});
	}

	@Override
	public ItemStackTileEntityRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(ItemStackTileEntityRuleFlowData data) {
		this.data = data;
		refreshSelection();
	}
}
