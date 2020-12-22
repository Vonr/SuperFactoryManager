/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowTimerTrigger;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class FlowTimerTriggerData extends FlowData implements PositionHolder {

	public Position position;
	public int interval;

	public FlowTimerTriggerData(UUID uuid, Position position, int interval) {
		super(uuid);
		this.position = position;
		this.interval = interval;
	}

	public FlowTimerTriggerData(CompoundNBT tag) {
		this(null, new Position(), 20);
		deserializeNBT(tag);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		tag.putInt("interval", interval);
		FlowDataFactories.TIMER_TRIGGER.stampNBT(tag);
		return tag;
	}

	@Override
	public void merge(FlowData other) {
		if (other instanceof FlowTimerTriggerData) {
			position = ((FlowTimerTriggerData) other).position;
			interval = ((FlowTimerTriggerData) other).interval;
		}
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowTimerTrigger((ManagerFlowController) parent, this);
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
		this.interval = tag.getInt("interval");
	}

	@Override
	public FlowData copy() {
		return new FlowTimerTriggerData(getId(), getPosition(), interval);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowTimerTriggerDataFactory extends FlowDataFactory<FlowTimerTriggerData> {

		public FlowTimerTriggerDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowTimerTriggerData fromNBT(CompoundNBT tag) {
			return new FlowTimerTriggerData(tag);
		}
	}
}
