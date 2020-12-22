/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowTileEntity;
import ca.teamdman.sfm.common.flow.data.core.Position;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public abstract class CableInventoryDrawerButton extends
	DrawerButton {

	public final ManagerFlowController CONTROLLER;

	public CableInventoryDrawerButton(
		ManagerFlowController CONTROLLER,
		Position pos,
		ButtonLabel label
	) {
		super(CONTROLLER, pos, label);
		this.CONTROLLER = CONTROLLER;
		CONTROLLER.SCREEN.CONTAINER.getSource().getCableTiles()
			.map(FlowTileEntityDrawerElement::new)
			.forEach(DRAWER::addChild);
	}

	public abstract void setSelected(BlockPos tilePos, boolean value);

	public class FlowTileEntityDrawerElement extends FlowTileEntity implements
		IFlowController, IFlowTangible {

		public FlowTileEntityDrawerElement(
			TileEntity tile
		) {
			super(tile, new Position());
		}

		@Override
		public void setSelected(boolean value, boolean notify) {
			super.setSelected(value, notify);
			if (notify) {
				CableInventoryDrawerButton.this.setSelected(this.TILE.getPos(), value);
			}
		}
	}
}
