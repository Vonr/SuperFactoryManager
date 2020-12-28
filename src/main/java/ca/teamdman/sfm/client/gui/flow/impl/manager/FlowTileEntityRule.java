/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.impl.TileEntityRuleFlowData;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowTileEntityRule extends FlowContainer implements FlowDataHolder {

	private final ManagerFlowController CONTROLLER;
	private final TileEntityRuleFlowData DATA;
	private String name = "Tile Entity Rule";

	public FlowTileEntityRule(
		ManagerFlowController controller, TileEntityRuleFlowData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.DATA = data;
		setVisible(false);
		setEnabled(false);
	}


	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		super.draw(screen, matrixStack, mx, my, deltaTime);
		screen.drawString(
			matrixStack,
			"Tile Entity Rule",
			5,
			5,
			0x999999
		);
	}

	@Override
	public FlowData getData() {
		return DATA;
	}

	@Override
	public void onDataChanged() {

	}
}
