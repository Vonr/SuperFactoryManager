/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.tile;

import static net.minecraftforge.common.util.Constants.BlockFlags.BLOCK_UPDATE;
import static net.minecraftforge.common.util.Constants.BlockFlags.NOTIFY_NEIGHBORS;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.block.ICable;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataContainer;
import ca.teamdman.sfm.common.flow.data.core.FlowDataFactory;
import ca.teamdman.sfm.common.flow.data.impl.FlowRelationshipData;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipGraph;
import ca.teamdman.sfm.common.flow.execution.ManagerFlowExecutionController;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.network.PacketDistributor;

public class ManagerTileEntity extends TileEntity implements FlowDataContainer,
	ITickableTileEntity {

	public final RelationshipGraph graph = new RelationshipGraph();
	private final ManagerFlowExecutionController CONTROLLER = new ManagerFlowExecutionController(
		this);
	private final HashSet<ServerPlayerEntity> CONTAINER_LISTENERS = new HashSet<>();

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public Stream<FlowData> getData() {
		return graph.getData();
	}

	public int getDataCount() {
		return graph.getNodeCount();
	}

	public Optional<UUID> addRelationship(UUID fromId, UUID toId) {
		if (Objects.equals(fromId, toId)) {
			return Optional.empty();
		}
		if (graph.getEdge(fromId, toId).isPresent()) {
			return Optional.empty();
		}
		if (graph.getAncestors(fromId).anyMatch(node -> node.getId().equals(toId))) {
			return Optional.empty();
		}
		//todo: prevent LineNode from allowing duplicate connections to an element
		UUID relationshipId = UUID.randomUUID();
		FlowRelationshipData data = new FlowRelationshipData(
			relationshipId,
			fromId,
			toId
		);
		addData(data);
		markAndNotify();
		return Optional.of(relationshipId);
	}

	@Override
	public void addData(FlowData data) {
		Optional<FlowData> existing = graph.getData(data.getId());
		if (existing.isPresent()) {
			existing.get().merge(data);
		} else {
			graph.addNode(data);
			if (data instanceof FlowRelationshipData) {
				graph.putEdge(
					data.getId(),
					((FlowRelationshipData) data).from,
					((FlowRelationshipData) data).to
				);
			}
		}
	}

	@Override
	public void removeData(UUID id) {
		graph.removeNode(id);
	}

	@Override
	public void clearData() {
		graph.clear();
	}

	@Override
	public void notifyChanged(
		UUID id, ChangeType type
	) {

	}

	@Override
	public void onChange(
		UUID id, BiConsumer<FlowData, ChangeType> callback
	) {

	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return graph.getData(id);
	}


	public void addContainerListener(ServerPlayerEntity player) {
		CONTAINER_LISTENERS.add(player);
	}

	public void removeContainerListener(ServerPlayerEntity player) {
		CONTAINER_LISTENERS.remove(player);
	}

	public Stream<ServerPlayerEntity> getContainerListeners() {
		return CONTAINER_LISTENERS.stream();
	}


	public void mutateManagerData(
		UUID dataId,
		Consumer<FlowData> consumer, Runnable changeNotifier
	) {
		getData(dataId)
			.ifPresent(data -> {
				consumer.accept(data);
				markAndNotify();
				changeNotifier.run();
			});
	}

	public <MSG> void sendPacketToListeners(MSG packet) {
		getContainerListeners().forEach(player ->
			PacketHandler.INSTANCE.send(
				PacketDistributor.PLAYER.with(() -> player),
				packet
			));
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

	@Override
	public CompoundNBT serializeNBT() {
		SFM.LOGGER.debug(SFMUtil.getMarker(getClass()), "Saving NBT on {}, writing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", getDataCount()
		);
		CompoundNBT c = new CompoundNBT();
		ListNBT list = new ListNBT();
		getData().forEach(d -> list.add(d.serializeNBT()));
		c.put("flow_data_list", list);
		return c;
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		SFM.LOGGER.debug(SFMUtil.getMarker(getClass()), "Loading nbt on {}, replacing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", getDataCount()
		);
		clearData();
		compound.getList("flow_data_list", NBT.TAG_COMPOUND).stream()
			.map(c -> ((CompoundNBT) c))
			.map(c -> {
				Optional<FlowData> data = FlowDataFactory.getFactory(c)
					.map(factory -> factory.fromNBT(c));
				if (!data.isPresent()) {
					SFM.LOGGER.warn("Could not find factory for {}", c);
				}
				return data;
			})
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted(Comparator.comparing(a -> a instanceof FlowRelationshipData))
			.forEach(this::addData);
	}


	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		deserializeNBT(tag.getCompound("data"));
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("data", serializeNBT());
		return compound;
	}

	public Stream<BlockPos> getCableNeighbours() {
		return SFMUtil.getRecursiveStream((current, next, results) -> {
			for (Direction d : Direction.values()) {
				BlockPos offset = current.offset(d);
				if (isCable(offset)) {
					next.accept(offset);
				} else {
					results.accept(offset);
				}
			}
		}, getPos());
	}

	public Stream<TileEntity> getCableTiles() {
		if (world == null) {
			return Stream.empty();
		}
		return getCableNeighbours()
			.distinct()
			.map(pos -> world.getTileEntity(pos))
			.filter(Objects::nonNull);
	}

	public boolean isCable(BlockPos pos) {
		if (world == null || pos == null) {
			return false;
		}
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof ICable) {
			return ((ICable) block).isCableEnabled(state, world, pos);
		}
		return false;
	}

	@Override
	public void tick() {
		CONTROLLER.tick();
	}
}
