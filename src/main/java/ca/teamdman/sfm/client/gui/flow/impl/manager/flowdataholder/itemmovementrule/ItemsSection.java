package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemmovementrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.template.itemmatcherspawner.ItemMatcherSpawnerDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowButton;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.ItemMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemMovementRuleFlowData;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

class ItemsSection extends FlowContainer {

	protected final FlowDrawer DRAWER;
	protected ItemMovementRuleFlowComponent PARENT;
	private final ItemMatcherSpawnerDrawer ADDER;

	public ItemsSection(ItemMovementRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.items.title")
		));

		ADDER = new ItemMatcherSpawnerDrawer(
			PARENT::addItemMatcher,
			new Position(ItemStackFlowButton.DEFAULT_SIZE.getWidth() + 5, 15)
		);
		ADDER.setVisibleAndEnabled(false);
		addChild(ADDER);

		DRAWER = new FlowDrawer(new Position(0, 16), 4, 3);
		DRAWER.setShrinkToFit(false);
		addChild(DRAWER);

		onDataChanged(PARENT.getData());
	}

	public void onDataChanged(
		ItemMovementRuleFlowData data
	) {
		rebuildChildren();
	}

	public void rebuildChildren() {
		DRAWER.getChildren().clear();
		DRAWER.addChild(new AddItemMatcherButton());

		PARENT.getData().itemMatcherIds.stream()
			.map(PARENT.CONTROLLER::findFirstChild)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.filter(FlowDataHolder.class::isInstance)
			.filter(c -> ((FlowDataHolder<?>) c).getData() instanceof ItemMatcher)
			.map(c -> new ItemMatcherDrawerItem(this, c))
			.forEach(DRAWER::addChild);
		DRAWER.update();
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 5;
	}

	class AddItemMatcherButton extends FlowPlusButton {

		public AddItemMatcherButton(
		) {
			super(
				new Position(),
				ItemStackFlowButton.DEFAULT_SIZE,
				CONST.ADD_BUTTON
			);
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			ADDER.toggleVisibilityAndEnabled();
		}


		@Override
		public List<? extends ITextProperties> getTooltip() {
			return Arrays.asList(
				new TranslationTextComponent("gui.sfm.flow.tooltip.add_item_matcher_1"),
				new TranslationTextComponent("gui.sfm.flow.tooltip.add_item_matcher_2")
					.mergeStyle(TextFormatting.GRAY)
			);
		}
	}
}
