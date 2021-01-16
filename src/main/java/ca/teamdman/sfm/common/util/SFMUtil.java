/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.common.net.packet.IContainerTilePacket;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class SFMUtil {

	/**
	 * The length of a UUID once stringified
	 */
	public static final int UUID_STRING_LENGTH = 36;

	/**
	 * Writes a UUID to a buffer
	 *
	 * @param id  UUID
	 * @param buf Buffer
	 */
	public static void writeUUID(UUID id, PacketBuffer buf) {
		buf.writeString(id.toString(), UUID_STRING_LENGTH);
	}

	/**
	 * Gets the marker used for logging purposes
	 *
	 * @param clazz The class used for naming the marker
	 * @return Logging marker
	 */
	public static Marker getMarker(Class clazz) {
		String[] x = clazz.getName().split("\\.");
		return MarkerManager.getMarker(x[x.length - 1]);
	}

	/**
	 * Gets a tile entity from the server side
	 *
	 * @param access World
	 * @param clazz  Tile class
	 * @param <T>    Tile entity type
	 * @return Tile of type T
	 */
	public static <T extends TileEntity> Optional<T> getServerTile(
		IWorldPosCallable access,
		Class<T> clazz
	) {
		return access
			.applyOrElse((world, pos) -> getTile(world, pos, clazz, false), Optional.empty());
	}

	/**
	 * Gets a tile entity from the world
	 *
	 * @param world  World
	 * @param pos    Tile position
	 * @param clazz  Tile class
	 * @param remote isRemote
	 * @param <T>    Tile entity type
	 * @return Tile of type T
	 */
	public static <T extends TileEntity> Optional<T> getTile(
		IWorldReader world, BlockPos pos,
		Class<T> clazz, boolean remote
	) {
		if (world.isRemote() != remote) {
			return Optional.empty();
		}
		TileEntity tile = world.getTileEntity(pos);
		if (tile == null) {
			return Optional.empty();
		}
		if (clazz.isInstance(tile))
		//noinspection unchecked
		{
			return (Optional<T>) Optional.of(tile);
		}
		return Optional.empty();
	}

	/**
	 * Gets a tile entity on the client side
	 *
	 * @param access World
	 * @param clazz  Tile class
	 * @param <T>    Tile entity type
	 * @return Tile of type T
	 */
	public static <T extends TileEntity> Optional<T> getClientTile(
		IWorldPosCallable access,
		Class<T> clazz
	) {
		return access
			.applyOrElse((world, pos) -> getTile(world, pos, clazz, true), Optional.empty());
	}

	/**
	 * Gets a tile entity from a packet with container info
	 *
	 * @param packet         Packet
	 * @param ctx            Network context
	 * @param containerClass Container
	 * @param tileClass      Tile class
	 * @param <T>            Tile entity type
	 * @return Tile of type T
	 */
	public static <T extends TileEntity> Optional<T> getTileFromContainerPacket(
		IContainerTilePacket packet, Supplier<NetworkEvent.Context> ctx,
		Class<? extends Container> containerClass, Class<T> tileClass
	) {
		if (ctx == null) {
			return Optional.empty();
		}
		NetworkEvent.Context context = ctx.get();
		if (context == null) {
			return Optional.empty();
		}
		ServerPlayerEntity sender = context.getSender();
		if (sender == null) {
			return Optional.empty();
		}
		if (!containerClass.isInstance(sender.openContainer)) {
			return Optional.empty();
		}
		if (sender.openContainer.windowId != packet.getWindowId()) {
			return Optional.empty();
		}
		ServerWorld world = sender.getServerWorld();
		//noinspection deprecation
		if (!world.isBlockLoaded(packet.getTilePosition())) {
			return Optional.empty();
		}
		TileEntity tile = world.getTileEntity(packet.getTilePosition());
		if (!tileClass.isInstance(tile)) {
			return Optional.empty();
		}
		//noinspection unchecked
		return Optional.of((T) tile);
	}

	/**
	 * Checks if a key is being held down
	 *
	 * @param key GLFW key
	 * @return true if key is being held down
	 */
	@OnlyIn(Dist.CLIENT)
	public static boolean isKeyDown(int key) {
		return InputMappings
			.isKeyDown(Minecraft.getInstance().getMainWindow().getHandle(), key);

	}

	/**
	 * Gets a stream using a self-feeding mapping function. Prevents the re-traversal of elements
	 * that have been visited before.
	 *
	 * @param operator Consumes queue elements to build the result set and append the next queue
	 *                 elements
	 * @param first    Initial value, not checked against the filter
	 * @param <T>      Type that the mapper consumes and produces
	 * @return Stream result after termination of the recursive mapping process
	 */
	public static <T> Stream<T> getRecursiveStream(
		RecursiveBuilder<T> operator, T first
	) {
		Stream.Builder<T> builder = Stream.builder();
		Set<T> debounce = new HashSet<>();
		Deque<T> toVisit = new ArrayDeque<>();
		toVisit.add(first);
		debounce.add(first);
		while (toVisit.size() > 0) {
			T current = toVisit.pop();
			operator.accept(current, next -> {
				if (!debounce.contains(next)) {
					debounce.add(next);
					toVisit.add(next);
				}
			}, builder::add);
		}
		return builder.build();
	}

	/**
	 * Gets shortest distance between a point and a line segment https://stackoverflow.com/a/6853926/11141271
	 * https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line#Line_defined_by_two_points
	 *
	 * @param x  point x
	 * @param y  point y
	 * @param x1 line start x
	 * @param y1 line start y
	 * @param x2 line end x
	 * @param y2 line end y
	 * @return distance
	 */
	public static double getDistanceFromLine(
		int x, int y, int x1, int y1, int x2, int y2
	) {
		int A = x - x1;
		int B = y - y1;
		int C = x2 - x1;
		int D = y2 - y1;

		int dot = A * C + B * D;
		int len_sq = C * C + D * D;
		double param = -1;
		if (len_sq != 0) //in case of 0 length line
		{
			param = dot / (double) len_sq;
		}

		double xx, yy;

		if (param < 0) {
			xx = x1;
			yy = y1;
		} else if (param > 1) {
			xx = x2;
			yy = y2;
		} else {
			xx = x1 + param * C;
			yy = y1 + param * D;
		}

		double dx = x - xx;
		double dy = y - yy;
		return Math.sqrt(dx * dx + dy * dy);
	}

	/**
	 * Reads a UUID from a packet buffer Will throw an error if unable to pop a string from the
	 * buffer Will throw an error if the string is malformed
	 *
	 * @return UUID
	 */
	public static UUID readUUID(PacketBuffer buf) {
		return UUID.fromString(buf.readString(UUID_STRING_LENGTH));
	}

	public interface RecursiveBuilder<T> {

		void accept(T next, Consumer<T> nextQueue, Consumer<T> resultBuilder);
	}

}
