package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBlockPosPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import org.lwjgl.glfw.GLFW;

class TilesSection extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final ItemRuleFlowComponent PARENT;
	private final FlowBlockPosPicker PICKER;

	public TilesSection(ItemRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.tiles.title")
		));

		DRAWER = new FlowDrawer(new Position(0, 16), 4, 3);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

		PICKER = new FlowBlockPosPicker(
			new Position(ItemStackFlowComponent.DEFAULT_SIZE.getWidth() + 5, 15)
		) {
			@Override
			public void onPicked(BlockPos pos) {
				PARENT.getData().tilePositions.add(pos);
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
				setVisible(false);
				setEnabled(false);
			}
		};
		PICKER.setVisible(false);
		PICKER.setEnabled(false);
		addChild(PICKER);

		rebuildChildren();
	}

	public void rebuildChildren() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(new AddButton());

		ManagerTileEntity tile = PARENT.CONTROLLER.SCREEN.getContainer().getSource();
		World world = tile.getWorld();

		if (world == null) {
			return;
		}

		PARENT.getData().tilePositions.stream()
			.map(pos -> new Entry(pos, new ItemStack(world.getBlockState(pos).getBlock().asItem())))
			.forEach(DRAWER::addChild);

		CableNetworkManager.getOrRegisterNetwork(world, tile.getPos()).ifPresent(network -> {
			PICKER.setContents(
				network.getInventories().stream()
					.filter(t -> t.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent())
					.map(TileEntity::getPos)
					.filter(pos -> !PARENT.getData().tilePositions.contains(pos))
					.collect(Collectors.toList()),
				world
			);
		});

		DRAWER.update();
	}

	public void onDataChanged(ItemRuleFlowData data) {
		rebuildChildren();
	}

	private class Entry extends ItemStackFlowComponent {

		public final BlockPos POS;

		public Entry(BlockPos pos, ItemStack stack) {
			super(stack, new Position());
			this.POS = pos;
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = (List<ITextProperties>) super.getTooltip();
			rtn.add(1,
				new StringTextComponent(POS.toString())
					.mergeStyle(TextFormatting.GRAY)
			);
			return rtn;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			if (button == GLFW.GLFW_MOUSE_BUTTON_2) {
				PARENT.getData().tilePositions.remove(POS);
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
			}
		}
	}

	private class AddButton extends FlowPlusButton {

		public AddButton() {
			super(new Position(), ItemStackFlowComponent.DEFAULT_SIZE, CONST.ADD_BUTTON);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			PICKER.toggleVisibilityAndEnabled();
		}
	}
}
