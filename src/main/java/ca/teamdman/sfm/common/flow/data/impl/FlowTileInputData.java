/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowInputButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowTileInputData extends FlowData implements PositionHolder {

	public Position position;

	public FlowTileInputData(UUID uuid, Position position) {
		super(uuid);
		this.position = position;
	}

	public FlowTileInputData(CompoundNBT tag) {
		this(null, new Position());
		deserializeNBT(tag);
	}


	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		FlowDataFactories.INPUT.stampNBT(tag);
		return tag;
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
	}

	@Override
	public FlowData copy() {
		return new FlowTileInputData(getId(), getPosition());
	}

	@Override
	public Position getPosition() {
		return position;
	}

	@Override
	public IFlowController createController(
		IFlowController parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowInputButton((ManagerFlowController) parent, this);
	}

	public static class FlowInputDataFactory extends FlowDataFactory<FlowTileInputData> {

		public FlowInputDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowTileInputData fromNBT(CompoundNBT tag) {
			return new FlowTileInputData(tag);
		}
	}
}
