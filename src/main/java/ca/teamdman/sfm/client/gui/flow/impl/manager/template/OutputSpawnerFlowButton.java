/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.template;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowOutputDataPacketC2S;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class OutputSpawnerFlowButton extends FlowIconButton {

	private final ManagerFlowController managerFlowController;

	public OutputSpawnerFlowButton(
		ManagerFlowController managerFlowController
	) {
		super(ButtonLabel.ADD_OUTPUT, new Position(25, 75));
		setDraggable(false);
		this.managerFlowController = managerFlowController;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextComponent> list = new ArrayList<>();
		list.add(new TranslationTextComponent("gui.sfm.flow.tooltip.output_spawner"));
		return list;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowOutputDataPacketC2S(
			managerFlowController.SCREEN.getContainer().windowId,
			managerFlowController.SCREEN.getContainer().getSource().getPos(),
			UUID.randomUUID(),
			new Position(0, 0),
			Collections.emptyList()
		));
	}
}
