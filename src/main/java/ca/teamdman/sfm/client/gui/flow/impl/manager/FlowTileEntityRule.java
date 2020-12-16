/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPanel;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileEntityRuleData;
import com.mojang.blaze3d.matrix.MatrixStack;

public class FlowTileEntityRule implements IFlowController, IFlowView {

	public final FlowPanel HEADER;
	private final ManagerFlowController CONTROLLER;
	private final FlowTileEntityRuleData DATA;
	private boolean visible = false;

	public FlowTileEntityRule(
		ManagerFlowController controller, FlowTileEntityRuleData data
	) {
		this.CONTROLLER = controller;
		this.HEADER = new FlowPanel(data.getPosition(), new Size(200, 25));
		this.DATA = data;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (visible) {
			screen.drawString(
				matrixStack,
				"Tile Entity Rule",
				HEADER.getPosition().getX(),
				HEADER.getPosition().getY(),
				0x999999
			);
		}
	}

	public void notifyOwner() {
		CONTROLLER.getController(DATA.owner).ifPresent(IFlowController::onDataChange);
	}

	@Override
	public void onDataChange() {
		notifyOwner();
	}

	@Override
	public IFlowView getView() {
		return this;
	}
}
