/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.tile.manager;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.HashSet;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class ManagerTileEntity extends TileEntity implements ITickableTileEntity {

	private final BasicFlowDataContainer FLOW_DATA_CONTAINER = new BasicFlowDataContainer();
	private final HashSet<ServerPlayerEntity> CONTAINER_LISTENERS = new HashSet<>();
	private final FlowExecutor EXECUTOR;

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
		EXECUTOR = new FlowExecutor(this);
	}

	public void addContainerListener(ServerPlayerEntity player) {
		CONTAINER_LISTENERS.add(player);
	}

	public void removeContainerListener(ServerPlayerEntity player) {
		CONTAINER_LISTENERS.remove(player);
	}

	public void mutateManagerData(
		UUID dataId,
		Consumer<FlowData> consumer, Runnable changeNotifier
	) {
		getFlowDataContainer().get(dataId)
			.ifPresent(data -> {
				consumer.accept(data);
				markAndNotify();
				changeNotifier.run();
			});
	}

	public BasicFlowDataContainer getFlowDataContainer() {
		return FLOW_DATA_CONTAINER;
	}

	public void markAndNotify() {
		if (getWorld() == null) {
			return;
		}
		markDirty();
		getWorld().notifyBlockUpdate(
			getPos(),
			getBlockState(),
			getBlockState(),
			BLOCK_UPDATE & NOTIFY_NEIGHBORS
		);
	}

	public <MSG> void sendPacketToListeners(MSG packet) {
		getContainerListeners().forEach(player ->
			PacketHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(() -> player),
				packet
			));
	}

	public Stream<ServerPlayerEntity> getContainerListeners() {
		return CONTAINER_LISTENERS.stream();
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		deserializeNBT(tag.getCompound("data"));
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Loading nbt on {}, replacing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server",
			getFlowDataContainer().size()
		);
		getFlowDataContainer().clear();
		getFlowDataContainer().deserializeNBT(
			compound.getList("flow_data_list", NBT.TAG_COMPOUND)
		);
	}

	@Override
	public CompoundNBT serializeNBT() {
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Saving NBT on {}, writing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server",
			getFlowDataContainer().size()
		);
		CompoundNBT c = new CompoundNBT();
		c.put("flow_data_list", getFlowDataContainer().serializeNBT());
		return c;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("data", serializeNBT());
		return compound;
	}


	@Override
	public void tick() {
		EXECUTOR.tick();
	}
}
