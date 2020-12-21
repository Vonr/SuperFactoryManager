/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.flow.data.impl;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.FlowOutputButton;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.core.Position;
import ca.teamdman.sfm.common.flow.data.core.PositionHolder;
import ca.teamdman.sfm.common.flow.data.core.SelectionHolder;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;

public class FlowOutputData extends FlowData implements PositionHolder,
	SelectionHolder<BlockPos> {

	public Position position;
	public Set<BlockPos> selected;
	public FlowOutputData(UUID uuid, Position position) {
		this(uuid, position, Collections.emptyList());
	}

	public FlowOutputData(UUID uuid, Position position, Collection<BlockPos> selected) {
		super(uuid);
		this.position = position;
		this.selected = new HashSet<>(selected);
	}

	public FlowOutputData(CompoundNBT tag) {
		this(null, new Position());
		deserializeNBT(tag);
	}

	@Override
	public Set<BlockPos> getSelected() {
		return selected;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT tag = super.serializeNBT();
		tag.put("pos", position.serializeNBT());
		tag.put(
			"selected",
			new LongArrayNBT(this.selected.stream().mapToLong(BlockPos::toLong).toArray())
		);

		FlowDataFactories.OUTPUT.stampNBT(tag);
		return tag;
	}

	@Override
	public FlowComponent createController(
		FlowComponent parent
	) {
		if (!(parent instanceof ManagerFlowController)) {
			return null;
		}
		return new FlowOutputButton((ManagerFlowController) parent, this);
	}

	@Override
	public void setSelected(BlockPos pos, boolean value) {
		if (value) {
			selected.add(pos);
		} else {
			selected.remove(pos);
		}
	}

	@Override
	public Class<BlockPos> getSelectionType() {
		return BlockPos.class;
	}

	@SuppressWarnings("ConstantConditions")
	@Override
	public void deserializeNBT(CompoundNBT tag) {
		super.deserializeNBT(tag);
		this.position.deserializeNBT(tag.getCompound("pos"));
		LongArrayNBT selectedNBT = (LongArrayNBT) tag.get("selected");
		this.selected = new HashSet<>();
		selectedNBT.stream()
			.map(l -> BlockPos.fromLong(l.getLong()))
			.forEach(selected::add);
	}

	@Override
	public FlowData copy() {
		return new FlowOutputData(getId(), getPosition(), selected);
	}

	@Override
	public Position getPosition() {
		return position;
	}

	public static class FlowOutputDataFactory extends FlowDataFactory<FlowOutputData> {

		public FlowOutputDataFactory(ResourceLocation key) {
			super(key);
		}

		@Override
		public FlowOutputData fromNBT(CompoundNBT tag) {
			return new FlowOutputData(tag);
		}
	}
}
