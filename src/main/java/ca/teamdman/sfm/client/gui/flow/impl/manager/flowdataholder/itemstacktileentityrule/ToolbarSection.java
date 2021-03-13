package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import net.minecraft.client.resources.I18n;

public class ToolbarSection extends FlowContainer {

	private ItemRuleFlowComponent PARENT;
	private TextAreaFlowComponent TITLE;

	public ToolbarSection(ItemRuleFlowComponent parent, Position pos) {
		super(pos);
		PARENT = parent;

		TITLE = new TextAreaFlowComponent(
			parent.CONTROLLER.SCREEN,
			parent.getData().name,
			I18n.format("gui.sfm.flow.placeholder.default_rule_name"),
			new Position(1,1),
			new Size(160, 12)
		);
		TITLE.setResponder(name -> {
			if (name == null || name.equals(PARENT.getData().name)) return;
			if (name.isEmpty()) name = I18n.format("gui.sfm.flow.placeholder.default_rule_name");
			PARENT.getData().name = name;
			PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(PARENT.getData());
		});
		addChild(TITLE);

		addChild(new MinimizeButton(
			PARENT,
			new Position(195, 1),
			new Size(10, 10)
		));
	}
}
