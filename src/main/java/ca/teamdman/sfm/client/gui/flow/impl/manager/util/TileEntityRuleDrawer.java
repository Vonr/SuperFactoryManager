/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.util;

import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawerElement;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowItemStack;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowPlusButton;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.impl.FlowTileEntityRuleData;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ManagerCreateTileEntityRulePacketC2S;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class TileEntityRuleDrawer<T extends FlowData & PositionHolder> extends
	DrawerButton<T> {

	public final ManagerFlowController CONTROLLER;
	private final AddTileEntityRuleButton ADD_BUTTON;


	public TileEntityRuleDrawer(
		ManagerFlowController CONTROLLER,
		T data,
		ButtonLabel label
	) {
		super(CONTROLLER, data, label);
		this.CONTROLLER = CONTROLLER;
		this.ADD_BUTTON = new AddTileEntityRuleButton(
			new Position(data.getPosition()), new Size(24, 24));
	}

	@Override
	public void onDataChange() {
		super.onDataChange();
		rebuildList();
	}

	public void rebuildList() {
		ArrayList<FlowDrawerElement> items = new ArrayList<>();
		items.add(ADD_BUTTON);
		CONTROLLER.SCREEN.getData(FlowTileEntityRuleData.class)
			.filter(data -> data.owner.equals(this.data.getId()))
			.map(TileEntityRuleDrawerElement::new)
			.collect(Collectors.toCollection(() -> items));
		this.DRAWER.setItems(items);
		this.DRAWER.onDataChange();
	}

	public static class TileEntityRuleDrawerElement extends FlowItemStack implements FlowDrawerElement {

		public TileEntityRuleDrawerElement(
			FlowTileEntityRuleData data
		) {
			super(data.getIcon(), new Position());
		}

		@Override
		public boolean mouseReleased(int mx, int my, int button) {
			boolean rtn = super.mouseReleased(mx, my, button);
			System.out.printf("Pressed rule %s\n", getItemStack().getItem().toString());
			return rtn;
		}
	}

	private class AddTileEntityRuleButton extends FlowPlusButton implements FlowDrawerElement {

		public AddTileEntityRuleButton(Position pos, Size size) {
			super(pos, size, new Colour3f(0.4f, 0.8f, 0.4f));
		}

		@Override
		public void onClicked() {
			PacketHandler.INSTANCE.sendToServer(new ManagerCreateTileEntityRulePacketC2S(
				CONTROLLER.SCREEN.CONTAINER.windowId,
				CONTROLLER.SCREEN.CONTAINER.getSource().getPos(),
				TileEntityRuleDrawer.this.data.getId(),
				new Position(0, 0)
			));
		}
	}
}
