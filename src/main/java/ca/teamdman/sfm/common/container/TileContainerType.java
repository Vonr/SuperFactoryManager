package ca.teamdman.sfm.common.container;

import ca.teamdman.sfm.common.util.SFMUtil;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class TileContainerType<C extends BaseContainer<T>, T extends TileEntity> extends
	ContainerType<C> {

	private final Class<T> TILE_CLASS;

	public TileContainerType(Class<T> tileClass) {
		super(null);
		this.TILE_CLASS = tileClass;
	}

	@Override
	public C create(
		int windowId, PlayerInventory player
	) {
		throw new UnsupportedOperationException("not sure");
	}

	@Override
	public C create(
		int windowId, PlayerInventory playerInv, PacketBuffer extraData
	) {
		return SFMUtil.getClientTile(
			IWorldPosCallable.of(
				playerInv.player.world,
				extraData.readBlockPos()
			),
			TILE_CLASS
		)
			.map(tile -> createClientContainer(
				windowId,
				tile,
				playerInv,
				extraData
			))
			.orElse(null);
	}

	protected abstract C createClientContainer(
		int windowId, T tile,
		PlayerInventory playerInv,
		PacketBuffer buffer
	);

	public void openGui(PlayerEntity player, World world, BlockPos pos) {
		if (player instanceof ServerPlayerEntity) {
			SFMUtil.getServerTile(IWorldPosCallable.of(world, pos), TILE_CLASS)
				.ifPresent(tile -> openGui(
					((ServerPlayerEntity) player),
					tile
				));
		}
	}

	public void openGui(ServerPlayerEntity player, T tile) {
		NetworkHooks.openGui(
			player,
			new INamedContainerProvider() {
				@Override
				public ITextComponent getDisplayName() {
					return TileContainerType.this.getDisplayName();
				}

				@Nullable
				@Override
				public Container createMenu(
					int windowId,
					PlayerInventory playerInventory,
					PlayerEntity playerEntity
				) {
					return createServerContainer(windowId, tile, player);
				}
			},
			data -> {
				data.writeBlockPos(tile.getPos());
				prepareClientContainer(tile, data);
			}
		);
	}

	public abstract ITextComponent getDisplayName();

	public abstract C createServerContainer(
		int windowId,
		T tile,
		ServerPlayerEntity player
	);

	protected abstract void prepareClientContainer(T tile, PacketBuffer buffer);
}
