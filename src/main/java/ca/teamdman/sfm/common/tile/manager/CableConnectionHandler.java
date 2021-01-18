package ca.teamdman.sfm.common.tile.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.block.ICable;
import ca.teamdman.sfm.common.util.SFMUtil;
import java.util.HashMap;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

public class CableConnectionHandler {

	@CapabilityInject(IItemHandler.class)
	public static Capability<IItemHandler> ITEM_HANDLER_CAPABILITY = null;

	private final ManagerTileEntity TILE;
	private final HashMap<BlockPos, TileEntity> TILE_CACHE = new HashMap<>();
	private boolean shouldRebuildCache = true;

	public CableConnectionHandler(ManagerTileEntity TILE) {
		this.TILE = TILE;
	}

	public void recalculateConnectedInventories() {
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Recalculating connected inventories, previously {} entries",
			TILE_CACHE.size()
		);
		getTiles().forEach(tile -> {
			LazyOptional<IItemHandler> cap = tile.getCapability(ITEM_HANDLER_CAPABILITY);
			cap.ifPresent(handler -> {
				TILE_CACHE.put(tile.getPos(), tile);
				cap.addListener(_cap -> TILE_CACHE.remove(tile.getPos()));
			});
		});
		SFM.LOGGER.debug(
			SFMUtil.getMarker(getClass()),
			"Recalculation finished, now {} entries",
			TILE_CACHE.size()
		);
		shouldRebuildCache = false;
	}

	public boolean shouldRebuildCache() {
		return shouldRebuildCache;
	}

	public void invalidateCache() {
		shouldRebuildCache = true;
	}

	public Stream<TileEntity> getCachedTiles() {
		if (!shouldRebuildCache()) {
			recalculateConnectedInventories();
		}
		return TILE_CACHE.values().stream();
	}

	/**
	 * More expensive, should instead use {@link #getCachedTiles}
	 * @return Tiles that are adjacent to a cable that is connected to the manager
	 */
	public Stream<TileEntity> getTiles() {
		if (TILE.getWorld() == null) {
			return Stream.empty();
		}
		return getCableNeighbours()
			.distinct()
			.map(pos -> TILE.getWorld().getTileEntity(pos))
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
		}, TILE.getPos());
	}

	public boolean isCable(BlockPos pos) {
		if (TILE.getWorld() == null || pos == null) {
			return false;
		}
		BlockState state = TILE.getWorld().getBlockState(pos);
		Block block = state.getBlock();
		if (block instanceof ICable) {
			return ((ICable) block).isCableEnabled(state, TILE.getWorld(), pos);
		}
		return false;
	}

	public void tick() {
	}
}
