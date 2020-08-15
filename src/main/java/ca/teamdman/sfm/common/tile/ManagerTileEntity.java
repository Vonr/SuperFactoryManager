package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flowdata.FlowData;
import ca.teamdman.sfm.common.flowdata.FlowDataFactory;
import ca.teamdman.sfm.common.registrar.BlockRegistrar;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ManagerTileEntity extends TileEntity {

	private final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());
	private final HashSet<ServerPlayerEntity> CONTAINER_LISTENERS = new HashSet<>();
	public HashMap<UUID, FlowData> data = new HashMap<>();

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

	public Stream<ServerPlayerEntity> getContainerListeners() {
		return CONTAINER_LISTENERS.stream();
	}

	@Override
	public CompoundNBT serializeNBT() {
		SFM.LOGGER.debug(MARKER, "Saving NBT on {}, writing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", data.size());
		CompoundNBT c = new CompoundNBT();
		ListNBT list = new ListNBT();
		data.values().forEach(d -> list.add(d.serializeNBT()));
		c.put("flow_data_list", list);
		return c;
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		SFM.LOGGER.debug(MARKER, "Loading nbt on {}, replacing {} entries",
			world == null ? "null world" : world.isRemote ? "client" : "server", data.size());
		data.clear();
		compound.getList("flow_data_list", NBT.TAG_COMPOUND).forEach(c -> {
			CompoundNBT tag = (CompoundNBT) c;
			LazyOptional<FlowDataFactory<?>> factory = FlowDataFactory.getFactory(tag);
			factory.ifPresent(fac -> {
				FlowData myData = fac.fromNBT(tag);
				data.put(myData.getId(), myData);
			});
			if (!factory.isPresent()) {
				SFM.LOGGER.warn("Could not find factory for {}", tag);
			}
		});
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

	public Stream<BlockPos> getNeighbours(BlockPos pos) {
		Stream.Builder<BlockPos> builder = Stream.builder();
		Set<BlockPos> debounce = new HashSet<>();
		Deque<BlockPos> toVisit = new ArrayDeque<>();
		toVisit.add(pos);
		while (toVisit.size() > 0) {
			BlockPos p = toVisit.pop();
			builder.add(p);
			if (!isCable(p)) {
				continue;
			}
			for (Direction d : Direction.values()) {
				BlockPos dx = p.offset(d);
				if (debounce.contains(dx)) {
					continue;
				}
				debounce.add(dx);
				toVisit.add(dx);
			}
		}
		return builder.build();
	}

	public boolean isCable(BlockPos pos) {
		if (world == null || pos == null) {
			return false;
		}
		BlockState state = world.getBlockState(pos);
		Block block = state.getBlock();
		if (block == BlockRegistrar.Blocks.CABLE) {
			return true;
		}
		return block == BlockRegistrar.Blocks.MANAGER;
	}
}
