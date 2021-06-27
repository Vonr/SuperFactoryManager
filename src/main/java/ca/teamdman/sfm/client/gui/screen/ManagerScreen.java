/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.flow.core.ComponentScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.config.Config.Client;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.core.FlowDialog;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.delete.ManagerDeletePacketC2S;
import ca.teamdman.sfm.common.net.packet.manager.put.ManagerFlowDataPacketC2S;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
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
	}

	@Override
	public void onClose() {
		getFlowDataContainer().notifyGuiClosed();
		super.onClose();
	}

	public BasicFlowDataContainer getFlowDataContainer() {
		return getMenu().getSource().getFlowDataContainer();
	}

	@Override
	public ManagerContainer getMenu() {
		return CONTAINER;
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

	@Override
	protected void init() {
		super.init();
		CONTROLLER.init();
	}

	/**
	 * Open a dialog, and close other dialogs if config set
	 */
	public <T extends FlowData & FlowDialog> void setDialogVisibility(T dialog, boolean visible) {
		// track changed data
		Set<FlowData> changed = new HashSet<>();

		// only toggle if visibility changed
		if (dialog.isOpen() != visible) {
			dialog.setOpen(visible);
			changed.add(dialog);
		}

		// hide other dialogs
		if (visible && !Client.allowMultipleRuleWindows) {
			getFlowDataContainer().stream()
				.filter(((Predicate<FlowData>) dialog::equals).negate())
				.filter(FlowDialog.class::isInstance)
				.map(FlowDialog.class::cast)
				// update opening position
				.peek(other -> dialog.getPosition().setXY(other.getPosition()))
				// hide
				.peek(other -> other.setOpen(false))
				.map(FlowData.class::cast)
				// mark updated
				.forEach(changed::add);
		}

		// distribute changes
		CONTROLLER.SCREEN.sendFlowDataToServer(changed);
	}

	public void sendFlowDataToServer(Collection<FlowData> data) {
		if (data.size() > 0) {
			sendFlowDataToServer(data.toArray(new FlowData[0]));
		}
	}

	public void sendFlowDataToServer(FlowData... data) {
		if (data.length > 0) {
			PacketHandler.INSTANCE.sendToServer(new ManagerFlowDataPacketC2S(
				CONTROLLER.SCREEN.getMenu().containerId,
				CONTROLLER.SCREEN.getMenu().getSource().getBlockPos(),
				data
			));
		}
	}

	public void sendFlowDataDeleteToServer(UUID id) {
		PacketHandler.INSTANCE.sendToServer(new ManagerDeletePacketC2S(
			CONTROLLER.SCREEN.getMenu().containerId,
			CONTROLLER.SCREEN.getMenu().getSource().getBlockPos(),
			id
		));
	}
}
