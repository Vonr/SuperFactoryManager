package ca.teamdman.sfm.client.gui.flow.impl.manager.util.ruledrawer;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.glfw.GLFW;

class GlobalRulesDrawerItem extends ItemStackFlowComponent {

	public ItemStackTileEntityRuleFlowData DATA;
	private final ItemStackTileEntityRuleDrawer PARENT;

	public GlobalRulesDrawerItem(
		ItemStackTileEntityRuleFlowData data,
		ItemStackTileEntityRuleDrawer parent
	) {
		super(data.getIcon(), new Position());
		this.DATA = data;
		PARENT = parent;
	}


	@Override
	public void onClicked(int mx, int my, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_1) {
			super.onClicked(mx, my, button);
		} else if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
			// right click, remove item globally
			PARENT.CONTROLLER.SCREEN.sendFlowDataDeleteToServer(DATA.getId());
		}
	}
	@Override
	public List<? extends ITextProperties> getTooltip() {
		ArrayList<ITextComponent> list = new ArrayList<>();
		list.add(new StringTextComponent(DATA.name));
		list.add(new StringTextComponent(""));
		list.add(new TranslationTextComponent( "gui.sfm.associatedrulesdrawer.selection.remove_hint")
				.mergeStyle(TextFormatting.GRAY));
		return list;
	}

	@Override
	public void onSelectionChanged() {
		List<UUID> next = PARENT.getChildrenRuleIds();
		if (isSelected()) {
			next.add(DATA.getId());
		} else {
			next.remove(DATA.getId());
		}
		PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getDataWithNewChildren(next));
	}
}
