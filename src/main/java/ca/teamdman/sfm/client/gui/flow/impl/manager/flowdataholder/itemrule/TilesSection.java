package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner.TileMatcherSpawnerDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowButton;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

class TilesSection extends FlowContainer {

	protected final FlowDrawer DRAWER;
	protected final ItemRuleFlowComponent PARENT;
	private final TileMatcherSpawnerDrawer ADDER;

	public TilesSection(ItemRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		ADDER = new TileMatcherSpawnerDrawer(
			PARENT,
			new Position(ItemStackFlowButton.DEFAULT_SIZE.getWidth() + 5, 15)
		);
		ADDER.setVisibleAndEnabled(false);
		addChild(ADDER);

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.tiles.title")
		));

		DRAWER = new FlowDrawer(new Position(0, 16), 4, 3);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

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

		CableNetworkManager.getOrRegisterNetwork(world, tile.getPos())
			.ifPresent(this::getDrawerChildrenFromNetwork);

		DRAWER.update();
	}

	private void getDrawerChildrenFromNetwork(CableNetwork network) {
		PARENT.getData().tileMatcherIds.stream()
			.map(PARENT.CONTROLLER::findFirstChild)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(FlowDataHolder.class::isInstance)
			.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof TileMatcher)
			.map(c -> new TileMatcherDrawerItem(this, c, network))
			.forEach(DRAWER::addChild);
	}

	public void onDataChanged(ItemRuleFlowData data) {
		rebuildChildren();
	}

	private class AddButton extends FlowPlusButton {

		public AddButton() {
			super(new Position(), ItemStackFlowButton.DEFAULT_SIZE, CONST.ADD_BUTTON);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			ADDER.toggleVisibilityAndEnabled();
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			return Arrays.asList(
				new TranslationTextComponent("gui.sfm.flow.tooltip.add_tile_matcher_1"),
				new TranslationTextComponent("gui.sfm.flow.tooltip.add_tile_matcher_2")
					.mergeStyle(TextFormatting.GRAY)
			);
		}
	}
}
