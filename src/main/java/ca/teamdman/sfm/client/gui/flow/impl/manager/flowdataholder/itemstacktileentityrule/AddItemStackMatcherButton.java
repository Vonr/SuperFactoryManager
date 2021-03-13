package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itemstacktileentityrule;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.FlowToolbox;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemRuleFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackComparerMatcherFlowData;
import ca.teamdman.sfm.common.flow.data.ItemStackModIdMatcherFlowData;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

class AddItemStackMatcherButton extends FlowPlusButton {

	private ManagerFlowController CONTROLLER;
	private ItemRuleFlowData data;
	private final List<FlowComponent> TOOLBOX_BUTTONS = Lists.newArrayList(
		new AddItemStackComparerMatcherToolboxButton(),
		new AddModIdMatcher()
	);

	public AddItemStackMatcherButton(
		ManagerFlowController CONTROLLER, ItemRuleFlowData data,
		Position pos
	) {
		super(pos, ItemStackFlowComponent.DEFAULT_SIZE, CONST.ADD_BUTTON);
		this.CONTROLLER = CONTROLLER;
		this.data = data;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		CONTROLLER.findFirstChild(FlowToolbox.class)
			.ifPresent(t -> t.setDrawerChildren(TOOLBOX_BUTTONS));
	}

	private class AddItemStackComparerMatcherToolboxButton extends FlowIconButton {
		public AddItemStackComparerMatcherToolboxButton() {
			super(ButtonLabel.COMPARER_MATCHER);
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = new ArrayList<>();
			rtn.add(new TranslationTextComponent("gui.sfm.toolbox.add_itemstackcomparer_matcher"));
			return rtn;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			FlowData matcher = new ItemStackComparerMatcherFlowData(
				UUID.randomUUID(),
				new ItemStack(Blocks.STONE),
				0,
				false
			);
			data.matcherIds.add(matcher.getId());
			CONTROLLER.SCREEN.sendFlowDataToServer(matcher, data);
		}
	}

	private class AddModIdMatcher extends FlowIconButton {
		public AddModIdMatcher() {
			super(ButtonLabel.MODID_MATCHER);
		}

		@Override
		public List<? extends ITextProperties> getTooltip() {
			List<ITextProperties> rtn = new ArrayList<>();
			rtn.add(new TranslationTextComponent("gui.sfm.toolbox.add_modid_matcher"));
			return rtn;
		}

		@Override
		public void onClicked(int mx, int my, int button) {
			FlowData matcher = new ItemStackModIdMatcherFlowData(
				UUID.randomUUID(),
				"minecraft",
				0,
				false
			);
			data.matcherIds.add(matcher.getId());
			CONTROLLER.SCREEN.sendFlowDataToServer(matcher, data);
		}
	}
}
