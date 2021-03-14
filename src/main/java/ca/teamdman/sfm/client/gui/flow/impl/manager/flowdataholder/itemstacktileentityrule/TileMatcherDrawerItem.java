package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.data.FlowData;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;

class TileMatcherDrawerItem<T extends FlowData & TileMatcher> extends ItemStackFlowComponent {

	public final T DATA;
	private final CableNetwork NETWORK;
	private int tick = 0;
	private ItemRuleFlowComponent PARENT;

	public TileMatcherDrawerItem(ItemRuleFlowComponent PARENT, T data, CableNetwork network) {
		super(ItemStack.EMPTY, new Position());
		this.DATA = data;
		this.PARENT = PARENT;
		this.NETWORK = network;
		setNextIcon();
	}

	@Override
	public void tick() {
		tick++;
		if (tick % 10 == 0) {
			setNextIcon();
		}
	}

	private void setNextIcon() {
		List<ItemStack> items = DATA.getPreview(NETWORK);
		if (items.size() == 0) {
			return;
		}
		setItemStack(items.get((tick / 10) % items.size()));
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
			// delete the matcher data
			// the item rules will detect the delete and prune accordingly
			PARENT.CONTROLLER.SCREEN.sendFlowDataDeleteToServer(DATA.getId());
		}
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextProperties> rtn = new ArrayList<>(super.getTooltip());
		rtn.add(
			1,
			new StringTextComponent(DATA.getMatcherDisplayName())
				.mergeStyle(TextFormatting.GRAY)
		);
		return rtn;
	}
}
