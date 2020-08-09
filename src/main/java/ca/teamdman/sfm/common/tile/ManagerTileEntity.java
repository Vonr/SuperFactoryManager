package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flowdata.FlowDataFactory;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.registrar.BlockRegistrar;
import ca.teamdman.sfm.common.registrar.FlowDataFactoryRegistrar.FlowDataFactories;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class ManagerTileEntity extends TileEntity {

	public final Marker MARKER = MarkerManager.getMarker(getClass().getSimpleName());

	public ArrayList<IFlowData> data = new ArrayList<>();

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT serializeNBT() {
		SFM.LOGGER.debug(MARKER, "Saving NBT on {}, writing {} entries", world == null ? "null world" : world.isRemote ? "client" : "server", data.size());
		CompoundNBT c = new CompoundNBT();
		ListNBT list = new ListNBT();
		data.forEach(d -> list.add(d.serializeNBT()));
		c.put("flow_data_list", list);
		return c;
	}

	@Override
	public void deserializeNBT(CompoundNBT compound) {
		SFM.LOGGER.debug(MARKER, "Loading nbt on {}, replacing {} entries", world == null ? "null world" : world.isRemote ? "client" : "server", data.size());
		data.clear();
		compound.getList("flow_data_list", NBT.TAG_COMPOUND).forEach(c -> {
			CompoundNBT tag = (CompoundNBT) c;
			FlowDataFactory.getFactory(tag).ifPresent(fac -> data.add(fac.fromNBT(tag)));
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
		if (block == BlockRegistrar.Blocks.MANAGER) {
			return true;
		}
		return false;
	}
}
