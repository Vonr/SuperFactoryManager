package ca.teamdman.sfm;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.Optional;

public class SFMUtil {
	public static <T extends TileEntity> Optional<T> getTile(IWorldReader world, BlockPos pos, Class<T> clazz, boolean remote) {
		if (world.isRemote() != remote)
			return Optional.empty();
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return Optional.empty();
		if (clazz.isInstance(tile))
			return (Optional<T>) Optional.of(tile);
		return Optional.empty();
	}
	public static <T extends TileEntity> Optional<T> getServerTile(IWorldPosCallable access, Class<T> clazz) {
		return access.applyOrElse((world, pos) -> getTile(world, pos, clazz, false), Optional.empty());
	}

	public static <T extends TileEntity> Optional<T> getClientTile(IWorldPosCallable access, Class<T> clazz) {
		return access.applyOrElse((world, pos) -> getTile(world, pos, clazz, true), Optional.empty());
	}
}
