/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.template;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.CloneController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.AdvancedTileInputFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.AdvancedTileInputFlowData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class AdvancedInputSpawnerFlowButton extends FlowIconButton {

	private final ManagerFlowController CONTROLLER;

	public AdvancedInputSpawnerFlowButton(
		ManagerFlowController controller
	) {
		super(ButtonLabel.ADD_INPUT, new Position(25, 50));
		this.CONTROLLER = controller;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextComponent> list = new ArrayList<>();
		list.add(new TranslationTextComponent("gui.sfm.flow.tooltip.advanced_input_spawner"));
		return list;
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		// override mousePressed instead of onClicked because of custom hover logic
		boolean rtn = super.mousePressed(mx, my, button);
		if (clicking) {
			clicking=false;
			CONTROLLER.findFirstChild(CloneController.class).ifPresent(cloner -> {
				AdvancedTileInputFlowData data = new AdvancedTileInputFlowData(
					UUID.randomUUID(),
					new Position(),
					Collections.emptyList()
				);
				AdvancedTileInputFlowButton comp = new AdvancedTileInputFlowButton(CONTROLLER, data);
				cloner.setCloning(comp);
			});
			return true;
		}
		return rtn;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
	}
}
