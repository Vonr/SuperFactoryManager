/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileEntityRuleData;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowTileEntityRule extends FlowContainer {

	private final ManagerFlowController CONTROLLER;
	private final FlowTileEntityRuleData DATA;
	private String name = "Tile Entity Rule";

	public FlowTileEntityRule(
		ManagerFlowController controller, FlowTileEntityRuleData data
	) {
		super(data.getPosition(), new Size(200, 200));
		this.CONTROLLER = controller;
		this.DATA = data;
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
}
