/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.tile.manager;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.data.CursorFlowData;
import ca.teamdman.sfm.common.flow.holder.BasicFlowDataContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.registrar.SFMTiles;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class ManagerTileEntity extends TileEntity implements ITickableTileEntity,
	INamedContainerProvider {

	private final BasicFlowDataContainer FLOW_DATA_CONTAINER = new BasicFlowDataContainer();
	private final FlowExecutor EXECUTOR;
	private final int NBT_SCHEMA_VERSION = 2;
	private final String NBT_SCHEMA_VERSION_KEY = "__version";
	private final String NBT_SCHEMA_DATA_KEY = "__data";
	private final Map<ServerPlayerEntity, Integer> CONTAINER_LISTENERS = new WeakHashMap<>();


	public ManagerTileEntity() {
		this(SFMTiles.MANAGER.get());
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
		EXECUTOR = new FlowExecutor(this);
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
		ManagerContainer managerContainer = new ManagerContainer(windowId, this, false);
		return managerContainer;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.sfm.manager");
	}

	public void addContainerListener(ServerPlayerEntity player, int windowId) {
		CONTAINER_LISTENERS.put(player, windowId);
		pruneCursors();
	}

	/**
	 * Remove any CursorFlowData instances that don't belong to a listener
	 */
	public void pruneCursors() {
		Set<UUID> listeners = getContainerListeners()
			.map(Entry::getKey)
			.map(ServerPlayerEntity::getUniqueID)
			.collect(Collectors.toSet());
		getFlowDataContainer().removeIf(data ->
			data instanceof CursorFlowData && !listeners.contains(data.getId()));
	}


	public Stream<Entry<ServerPlayerEntity, Integer>> getContainerListeners() {
		// get non-weak reference to all items in the list to avoid CMEs
		return new ArrayList<>(CONTAINER_LISTENERS.entrySet()).stream();
	}

	public BasicFlowDataContainer getFlowDataContainer() {
		return FLOW_DATA_CONTAINER;
	}

	public void removeContainerListener(ServerPlayerEntity player) {
		CONTAINER_LISTENERS.remove(player);
		pruneCursors();
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

	public <MSG> void sendPacketToListeners(Function<Integer, MSG> packetFunc) {
		getContainerListeners()
			.forEach(entry -> {
				ServerPlayerEntity player = entry.getKey();
				MSG packet = packetFunc.apply(entry.getValue());
				PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
			});
	}

	public void closeGuiForAllListeners() {
		getContainerListeners()
			.map(Entry::getKey)
			.forEach(ServerPlayerEntity::closeScreen);
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		if (tag.contains("data", NBT.TAG_COMPOUND)) {
			// "data" not present on first load on client
			deserializeNBT(tag.getCompound("data"));
		}
	}

	@Override
	public void deserializeNBT(CompoundNBT tag) {
		// log debug info
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Loading nbt on {}, replacing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server",
			getFlowDataContainer().size()
		);

		// apply schema updates
		upgradeSavedData(tag);
		if (tag.getInt(NBT_SCHEMA_VERSION_KEY) != NBT_SCHEMA_VERSION) {
			throw new IllegalArgumentException("tag not using latest schema after upgrade");
		}

		// load data
		getFlowDataContainer().clear();
		getFlowDataContainer().deserializeNBT(tag.getCompound(NBT_SCHEMA_DATA_KEY));
	}

	private void upgradeSavedData(CompoundNBT tag) {
		if (tag.contains("version", NBT.TAG_INT)) {
			tag.putInt(NBT_SCHEMA_VERSION_KEY, tag.getInt("version"));
			tag.remove("version");
		}

		int version = tag.getInt(NBT_SCHEMA_VERSION_KEY);
		if (version != NBT_SCHEMA_VERSION) {
			SFM.LOGGER.debug(
				SFMUtil.getMarker(getClass()),
				"Updating schema from version {} to {}",
				version,
				NBT_SCHEMA_VERSION
			);
		}

		switch (version) {
			case 1:
				ListNBT data = tag.getList("flow_data_list", NBT.TAG_COMPOUND);
				tag.remove("flow_data_list");
				CompoundNBT dataHolder = new CompoundNBT();
				dataHolder.putInt("__version", 1);
				dataHolder.put("__data", data);
				tag.put("__data", dataHolder);
				tag.putInt(NBT_SCHEMA_VERSION_KEY, 2);
		}
	}

	@Override
	public CompoundNBT serializeNBT() {
		pruneCursors();

		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Saving NBT on {}, writing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server",
			getFlowDataContainer().size()
		);
		CompoundNBT c = new CompoundNBT();
		c.putInt(NBT_SCHEMA_VERSION_KEY, NBT_SCHEMA_VERSION);
		c.put(NBT_SCHEMA_DATA_KEY, getFlowDataContainer().serializeNBT());
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
