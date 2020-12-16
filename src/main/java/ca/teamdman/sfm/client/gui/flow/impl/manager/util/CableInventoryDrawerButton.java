/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawerElement;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowTileEntity;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerToggleBlockPosSelectedC2S;
import java.util.stream.Collectors;
import net.minecraft.tileentity.TileEntity;

public abstract class CableInventoryDrawerButton<T extends FlowData & PositionHolder> extends
	DrawerButton<T> {

	public final ManagerFlowController CONTROLLER;

	public CableInventoryDrawerButton(
		ManagerFlowController CONTROLLER,
		T data,
		ButtonLabel label
	) {
		super(CONTROLLER, data, label);
		this.CONTROLLER = CONTROLLER;
		this.DRAWER.setItems(CONTROLLER.SCREEN.CONTAINER.getSource().getCableTiles()
			.map(FlowTileEntityDrawerElement::new)
			.collect(Collectors.toList()));
	}


	public class FlowTileEntityDrawerElement extends FlowTileEntity implements FlowDrawerElement {

		public FlowTileEntityDrawerElement(
			TileEntity tile
		) {
			super(tile, new Position());
		}

		@Override
		public void setSelected(boolean value, boolean notify) {
			super.setSelected(value, notify);
			if (notify) {
				PacketHandler.INSTANCE.sendToServer(new ManagerToggleBlockPosSelectedC2S(
					CONTROLLER.SCREEN.CONTAINER.windowId,
					CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
					data.getId(),
					this.TILE.getPos(),
					value
				));
			}
		}
	}

	@Override
	public void onDataChange() {
		super.onDataChange();
//		this.DRAWER.ITEMS.forEach(v -> v.setSelected(false, false));
		// todo: enable tile selection for cable adjacent tiles?
//		this.DRAWER.ITEMS.forEach(v ->
//			v.setSelected(data.getSelected().contains(v.TILE.getPos()), false));
	}
}
