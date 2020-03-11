package ca.teamdman.sfm.common.container.factory;

import ca.teamdman.sfm.SFMUtil;
import ca.teamdman.sfm.common.container.CrafterContainer;
import ca.teamdman.sfm.common.tile.CrafterTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class CrafterContainerProvider implements INamedContainerProvider {
	private final IWorldPosCallable ACCESS;

	public CrafterContainerProvider(IWorldPosCallable access) {
		this.ACCESS = access;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TranslationTextComponent("container.sfm.crafter");
	}

	@Nullable
	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
		return SFMUtil.getServerTile(ACCESS, CrafterTileEntity.class)
				.map(tile -> new CrafterContainer(windowId, playerInv, tile)).orElse(null);
	}

	public void openGui(PlayerEntity player) {
		if (player instanceof ServerPlayerEntity)
			SFMUtil.getServerTile(ACCESS, CrafterTileEntity.class)
					.ifPresent(tile -> NetworkHooks.openGui((ServerPlayerEntity) player, this, data -> {
						data.writeBlockPos(tile.getPos());
					}));
	}

	public static class CrafterContainerFactory implements IContainerFactory<CrafterContainer> {
		@Override
		public CrafterContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
			return SFMUtil.getClientTile(IWorldPosCallable.of(inv.player.world, data.readBlockPos()), CrafterTileEntity.class)
					.map(crafterTileEntity -> new CrafterContainer(windowId, inv, crafterTileEntity)).orElse(null);
		}
	}
}
