package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class SlotsSection extends FlowContainer {

	private final ItemStackTileEntityRuleFlowComponent PARENT;

	public SlotsSection(
		ItemStackTileEntityRuleFlowComponent PARENT,
		Position pos
	) {
		super(pos);
		this.PARENT = PARENT;

		addChild(new SectionHeader(
			new Position(0, 0),
			new Size(35, 12),
			I18n.format("gui.sfm.manager.tile_entity_rule.slots.title")
		) {
			@Override
			public List<? extends ITextProperties> getTooltip() {
				return Arrays.asList(
					new StringTextComponent("Slots examples:")
						.mergeStyle(TextFormatting.WHITE),
					new StringTextComponent("0, 1, 2, 3")
						.mergeStyle(TextFormatting.GRAY),
					new StringTextComponent("0-3")
						.mergeStyle(TextFormatting.GRAY),
					new StringTextComponent("0-3, 9, 10, 11, 12")
						.mergeStyle(TextFormatting.GRAY),
					new StringTextComponent("0-3, 9-12, 16+")
						.mergeStyle(TextFormatting.GRAY)
				);
			}
		});

		addChild(new TextAreaFlowComponent(
			PARENT.CONTROLLER.SCREEN,
			"",
			"",
			new Position(0, 15),
			new Size(70,10)
		));
	}
}
