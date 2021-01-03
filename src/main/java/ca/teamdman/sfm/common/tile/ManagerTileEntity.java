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
import ca.teamdman.sfm.common.flow.data.core.FlowDataSerializer;
import ca.teamdman.sfm.common.flow.data.impl.RelationshipFlowData;
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
		getData(dataId)
			.ifPresent(data -> {
				consumer.accept(data);
				markAndNotify();
				changeNotifier.run();
			});
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
		SFM.LOGGER.debug(SFMUtil.getMarker(getClass()), "Loading nbt on {}, replacing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", getDataCount()
		);
		clearData();
		compound.getList("flow_data_list", NBT.TAG_COMPOUND).stream()
			.map(c -> ((CompoundNBT) c))
			.map(c -> {
				Optional<FlowData> data = FlowDataSerializer.getSerializer(c)
					.map(serializer -> serializer.fromNBT(c));
				if (!data.isPresent()) {
					SFM.LOGGER.warn("Could not find factory for {}", c);
				}
				return data;
			})
			.filter(Optional::isPresent)
			.map(Optional::get)
			.sorted(Comparator.comparing(a -> a instanceof RelationshipFlowData))
			.forEach(this::addData);
	}

	public int getDataCount() {
		return graph.getNodeCount();
	}

	public void addRelationship(RelationshipFlowData data) {
		if (Objects.equals(data.from, data.to)) {
			return;
		}
		if (graph.getEdge(data.from, data.to).isPresent()) {
			return;
		}
		if (graph.getAncestors(data.from).anyMatch(node -> node.getId().equals(data.to))) {
			return;
		}
		//todo: prevent LineNode from allowing duplicate connections to an element
		graph.addNode(data);
		graph.putEdge(
			data.getId(),
			data.from,
			data.to
		);
	}

	@Override
	public CompoundNBT serializeNBT() {
		SFM.LOGGER.debug(SFMUtil.getMarker(getClass()), "Saving NBT on {}, writing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", getDataCount()
		);
		CompoundNBT c = new CompoundNBT();
		ListNBT list = new ListNBT();
		getData().forEach(d -> list.add(d.getSerializer().toNBT(d)));
		c.put("flow_data_list", list);
		return c;
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("data", serializeNBT());
		return compound;
	}

	@Override
	public Stream<FlowData> getData() {
		return graph.getData();
	}

	@Override
	public Optional<FlowData> getData(UUID id) {
		return graph.getData(id);
	}

	@Override
	public void removeData(UUID id) {
		graph.removeNode(id);
	}

	@Override
	public void addData(FlowData data) {
		Optional<FlowData> existing = graph.getData(data.getId());
		if (existing.isPresent()) {
			existing.get().merge(data);
		} else {
			if (data instanceof RelationshipFlowData) {
				addRelationship(((RelationshipFlowData) data));
			} else {
				graph.addNode(data);
			}
			markAndNotify();
		}
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

	public Stream<TileEntity> getCableTiles() {
		if (world == null) {
			return Stream.empty();
		}
		return getCableNeighbours()
			.distinct()
			.map(pos -> world.getTileEntity(pos))
			.filter(Objects::nonNull);
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
