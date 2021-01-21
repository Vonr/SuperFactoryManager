/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.template;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class FlowTimerTriggerSpawnerButton extends FlowIconButton {

	private final ManagerFlowController managerFlowController;

	public FlowTimerTriggerSpawnerButton(
		ManagerFlowController managerFlowController
	) {
		super(ButtonLabel.ADD_TIMER_TRIGGER, new Position(25, 25));
		this.managerFlowController = managerFlowController;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextComponent> list = new ArrayList<>();
		list.add(new TranslationTextComponent("gui.sfm.flow.tooltip.trigger_spawner"));
		return list;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		managerFlowController.SCREEN.sendFlowDataToServer(
			new TimerTriggerFlowData(
				UUID.randomUUID(),
				getPosition().withOffset(getSize().getWidth() + 10, 0),
				20
			)
		);
	}
}
