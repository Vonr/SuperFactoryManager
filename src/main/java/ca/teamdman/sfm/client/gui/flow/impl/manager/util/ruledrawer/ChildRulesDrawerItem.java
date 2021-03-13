package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

class ChildRulesDrawerItem extends ItemStackFlowComponent implements
	FlowDataHolder<ItemRuleFlowData> {

	private final ItemStackTileEntityRuleDrawer PARENT;
	private ItemRuleFlowData data;

	public ChildRulesDrawerItem(
		ItemStackTileEntityRuleDrawer parent,
		ItemRuleFlowData data
	) {
		super(data.getIcon(), new Position());
		PARENT = parent;
		this.data = data;
		setData(data);
		PARENT.CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemRuleFlowData.class, this
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
		PARENT.CONTROLLER.findFirstChild(data.getId())
			.ifPresent(c -> c.setVisible(value));
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
	public ItemRuleFlowData getData() {
		return data;
	}

	@Override
	public void setData(ItemRuleFlowData data) {
		this.data = data;
	}
}
