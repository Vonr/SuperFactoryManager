package ca.teamdman.sfm;

import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import ca.teamdman.sfm.common.net.packet.IWindowIdProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class SFMUtil {
	public static <T extends TileEntity> Optional<T> getServerTile(IWorldPosCallable access, Class<T> clazz) {
		return access.applyOrElse((world, pos) -> getTile(world, pos, clazz, false), Optional.empty());
	}

	public static <T extends TileEntity> Optional<T> getTile(IWorldReader world, BlockPos pos, Class<T> clazz, boolean remote) {
		if (world.isRemote() != remote)
			return Optional.empty();
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null)
			return Optional.empty();
		if (clazz.isInstance(tile))
			//noinspection unchecked
			return (Optional<T>) Optional.of(tile);
		return Optional.empty();
	}

	public static <T extends TileEntity> Optional<T> getClientTile(IWorldPosCallable access, Class<T> clazz) {
		return access.applyOrElse((world, pos) -> getTile(world, pos, clazz, true), Optional.empty());
	}

	public static <T extends TileEntity> Optional<T> getTileFromContainerPacket(IContainerTilePacket packet, Supplier<NetworkEvent.Context> ctx, Class<? extends Container> containerClass, Class<T> tileClass) {
		if (ctx == null)
			return Optional.empty();
		NetworkEvent.Context context = ctx.get();
		if (context == null)
			return Optional.empty();
		ServerPlayerEntity sender = context.getSender();
		if (sender == null)
			return Optional.empty();
		if (!containerClass.isInstance(sender.openContainer))
			return Optional.empty();
		if (sender.openContainer.windowId != packet.getWindowId())
			return Optional.empty();
		ServerWorld world = sender.getServerWorld();
		if (!world.isBlockLoaded(packet.getTilePosition()))
			return Optional.empty();
		TileEntity tile = world.getTileEntity(packet.getTilePosition());
		if (!tileClass.isInstance(tile))
			return Optional.empty();
		//noinspection unchecked
		return Optional.of((T) tile);
	}
}
