package ca.teamdman.sfm.common.tile;

import ca.teamdman.sfm.common.registrar.BlockRegistrar;
import ca.teamdman.sfm.common.registrar.TileEntityRegistrar;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class ManagerTileEntity extends TileEntity {
	public int x, y;

	public ManagerTileEntity() {
		this(TileEntityRegistrar.Tiles.MANAGER);
	}

	public ManagerTileEntity(final TileEntityType<?> type) {
		super(type);
	}

	@Override
	public void read(CompoundNBT compound) {
		this.x = compound.getInt("x");
		this.y = compound.getInt("y");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putInt("x", x);
		compound.putInt("y", y);
		return compound;
	}

	public Stream<BlockPos> getNeighbours(BlockPos pos) {
		Stream.Builder<BlockPos> builder  = Stream.builder();
		Set<BlockPos>            debounce = new HashSet<>();
		Deque<BlockPos>          toVisit  = new ArrayDeque<>();
		toVisit.add(pos);
		while (toVisit.size() > 0) {
			BlockPos p = toVisit.pop();
			builder.add(p);
			if (!isCable(p))
				continue;
			for (Direction d : Direction.values()) {
				BlockPos dx = p.offset(d);
				if (debounce.contains(dx))
					continue;
				debounce.add(dx);
				toVisit.add(dx);
			}
		}
		return builder.build();
	}

	public boolean isCable(BlockPos pos) {
		if (world == null || pos == null)
			return false;
		BlockState state = world.getBlockState(pos);
		Block      block = state.getBlock();
		if (block == BlockRegistrar.Blocks.CABLE)
			return true;
		if (block == BlockRegistrar.Blocks.MANAGER)
			return true;
		return false;
	}
}
