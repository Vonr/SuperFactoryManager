/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.flow.core.ComponentScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketC2S;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.UUID;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerScreen extends ComponentScreen<ManagerFlowController> implements
	IHasContainer<ManagerContainer> {
	private final ManagerFlowController CONTROLLER;
	private final ManagerContainer CONTAINER;

	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(name, 512, 256);
		this.CONTAINER = container;
		this.CONTROLLER = new ManagerFlowController(this);
		reloadFromManagerTileEntity();
	}

	@Override
	public void closeScreen() {
		getFlowDataContainer().notifyGuiClosed();
		super.closeScreen();
	}

	public void reloadFromManagerTileEntity() {
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Loading {} data entries from tile",
			CONTAINER.getSource().getFlowDataContainer().size()
		);
		getComponent().rebuildChildren();
	}

	@Override
	public ManagerFlowController getComponent() {
		return CONTROLLER;
	}

	public BasicFlowDataContainer getFlowDataContainer() {
		return getContainer().getSource().getFlowDataContainer();
	}

	public void sendFlowDataToServer(FlowData... data) {
		PacketHandler.INSTANCE.sendToServer(new ManagerFlowDataPacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			data
		));
	}

	@Override
	public ManagerContainer getContainer() {
		return CONTAINER;
	}

	public void sendFlowDataDeleteToServer(UUID id) {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.getContainer().windowId,
			CONTROLLER.SCREEN.getContainer().getSource().getPos(),
			id
		));
	}
}
