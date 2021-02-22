package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

class ChildRulesDrawerItem extends ItemStackFlowComponent implements
	FlowDataHolder<ItemStackTileEntityRuleFlowData> {

	private final ItemStackTileEntityRuleDrawer PARENT;
	private ItemStackTileEntityRuleFlowData data;

	public ChildRulesDrawerItem(
		ItemStackTileEntityRuleDrawer parent,
		ItemStackTileEntityRuleFlowData data
	) {
		super(data.getIcon(), new Position());
		PARENT = parent;
		this.data = data;
		setData(data);
		PARENT.CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemStackTileEntityRuleFlowData.class, this
		));
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			toggleSelected();
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
			// right click, remove item from list
			if (Screen.hasShiftDown()) {
				// delete globally
				PARENT.CONTROLLER.SCREEN.sendFlowDataDeleteToServer(data.getId());
			} else {
				// only unassociate
				List<UUID> next = PARENT.getChildrenRuleIds();
				next.remove(data.getId());
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getDataWithNewChildren(next));
			}
		}
	}

	@Override
	public boolean isSelected() {
		return data.open;
	}

	@Override
	public void setSelected(boolean value) {
		List<FlowData> changes = new ArrayList<>();
		changes.add(data);
		if (!Client.allowMultipleRuleWindows && value) {
			AtomicReference<Position> pos = new AtomicReference<>(data.position);
			PARENT.CONTROLLER.SCREEN.getFlowDataContainer()
				.get(ItemStackTileEntityRuleFlowData.class)
				.filter(d -> !d.equals(data))
				.filter(d -> d.open)
				.forEach(d -> {
					pos.set(d.position); // track open window position
					d.open = false; // close it
					changes.add(d); // mark as changed
				});
			data.position.setXY(pos.get()); // position opening window to match previously open
		}
		data.open = value; // only open after position adjusted if only one window open allowed
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(changes.toArray(new FlowData[0]));
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		ArrayList<ITextComponent> list = new ArrayList<>();
		list.add(new StringTextComponent(data.name));
		list.add(new StringTextComponent(""));
		list.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.children.remove_hint1")
			.mergeStyle(TextFormatting.GRAY));
		list.add(new TranslationTextComponent("gui.sfm.associatedrulesdrawer.children.remove_hint2")
			.mergeStyle(TextFormatting.GRAY));
		return list;
	}

	@Override
	public ItemStackTileEntityRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(ItemStackTileEntityRuleFlowData data) {
		this.data = data;
	}
}
