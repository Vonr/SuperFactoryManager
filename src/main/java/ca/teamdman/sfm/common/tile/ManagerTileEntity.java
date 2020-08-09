package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.flowdata.FlowDataFactory;
import ca.teamdman.sfm.common.flowdata.IFlowData;
import ca.teamdman.sfm.common.registrar.BlockRegistrar;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
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
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ManagerTileEntity extends TileEntity {

	public ArrayList<IFlowData> data = new ArrayList<>();

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT c = new CompoundNBT();
		ListNBT list = new ListNBT();
		data.forEach(d -> list.add(d.serializeNBT()));
		c.put("flow_data_list", list);
		return c;
	}

	final LazyOptional<Collection<FlowDataFactory>> factories = LazyOptional.of(()->GameRegistry.findRegistry(FlowDataFactory.class).getValues());
	@Override
	public void deserializeNBT(CompoundNBT compound) {
		compound.getList("flow_data_list", NBT.TAG_COMPOUND).forEach(c -> {
			CompoundNBT tag = (CompoundNBT) c;
			factories.ifPresent(fs -> fs.stream()
				.filter(f -> f.matches(tag))
				.findFirst()
				.ifPresent(fac -> data.add(fac.fromNBT(tag))));
		});
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
