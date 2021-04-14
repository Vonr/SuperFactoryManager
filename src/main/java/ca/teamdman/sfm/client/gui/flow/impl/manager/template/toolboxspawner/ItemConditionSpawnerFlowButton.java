/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.template.toolboxspawner;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.CloneController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.ItemConditionFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemConditionFlowData;
import ca.teamdman.sfm.common.flow.data.ItemConditionRuleFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemConditionSpawnerFlowButton extends FlowIconButton {

	private final ManagerFlowController CONTROLLER;

	public ItemConditionSpawnerFlowButton(
		ManagerFlowController controller
	) {
		super(ButtonLabel.CONDITIONAL, new Position());
		this.CONTROLLER = controller;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextComponent> list = new ArrayList<>();
		list.add(new TranslationTextComponent("gui.sfm.flow.tooltip.item_condition_spawner"));
		return list;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		// override mousePressed instead of onClicked because of custom hover logic
		boolean rtn = super.mousePressed(mx, my, button);
		if (clicking) {
			clicking = false;
			CONTROLLER.findFirstChild(CloneController.class).ifPresent(cloner -> {
				BasicFlowDataContainer container = new BasicFlowDataContainer();

				// create default rule data
				ItemConditionRuleFlowData ruleData = new ItemConditionRuleFlowData();
				container.put(ruleData);

				// create button data
				ItemConditionFlowData buttonData = new ItemConditionFlowData(
					UUID.randomUUID(),
					new Position(),
					ruleData.getId()
				);

				// create rule button component
				FlowComponent comp = new ItemConditionFlowButton(CONTROLLER, buttonData, ruleData);

				// set component as cloning
				cloner.startCloning(comp, container);
			});
			return true;
		}
		return rtn;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
	}
}
