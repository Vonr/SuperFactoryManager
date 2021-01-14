package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowBlockPosPicker;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemStackTileEntityRuleFlowData;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

class TilesSection extends FlowContainer {

	private final FlowDrawer DRAWER;
	private final ItemStackTileEntityRuleFlowComponent PARENT;
	private final FlowBlockPosPicker PICKER;

	public TilesSection(ItemStackTileEntityRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.tiles.title")
		));

		DRAWER = new FlowDrawer(new Position(0, 16), 4, 2);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

		PICKER = new FlowBlockPosPicker(
			new Position(ItemStackFlowComponent.DEFAULT_SIZE.getWidth() + 15, 15)
		) {
			@Override
			public void onPicked(BlockPos pos) {
				PARENT.getData().tilePositions.add(pos);
				PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
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
		World world = PARENT.CONTROLLER.SCREEN.getContainer().getSource().getWorld();
		if (world == null) {
			return;
		}
		PARENT.getData().tilePositions.stream()
			.map(pos -> new Entry(pos, new ItemStack(world.getBlockState(pos).getBlock().asItem())))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	public void onDataChanged(ItemStackTileEntityRuleFlowData data) {
		rebuildChildren();
	}

	private class Entry extends ItemStackFlowComponent {

		public final BlockPos POS;

		public Entry(BlockPos pos, ItemStack stack) {
			super(stack, new Position());
			this.POS = pos;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			super.onClicked(mx, my, button);
		}
	}

	private class AddButton extends FlowPlusButton {

		public AddButton() {
			super(new Position(), ItemStackFlowComponent.DEFAULT_SIZE, CONST.ADD_BUTTON);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			PICKER.setVisible(!PICKER.isVisible());
			PICKER.setEnabled(PICKER.isVisible());
		}
	}
}
