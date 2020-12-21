/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowLineNode;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowLineNodeData extends FlowData implements PositionHolder {

	public Position position;

	public FlowLineNodeData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public FlowLineNodeData(CompoundNBT tag) {
		this(null, new Position());
		deserializeNBT(tag);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		FlowDataFactories.LINE_NODE.stampNBT(tag);
		return tag;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowLineNode((ManagerFlowController) parent, this);
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		return new FlowLineNodeData(getId(), getPosition());
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class LineNodeFlowDataFactory extends FlowDataFactory<FlowLineNodeData> {

		public LineNodeFlowDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowLineNodeData fromNBT(CompoundNBT tag) {
			return new FlowLineNodeData(tag);
		}
	}
}
