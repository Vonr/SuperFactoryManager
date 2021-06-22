package ca.teamdman.sfm.common.net.packet.workstation;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.tile.WorkstationTileEntity;
import java.util.function.Supplier;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class C2SWorkstationLearnRecipePacket extends C2SContainerPacket<WorkstationTileEntity, WorkstationContainer> {
	public final ResourceLocation RECIPE_ID;

	public C2SWorkstationLearnRecipePacket(
		int WINDOW_ID,
		BlockPos TILE_POSITION,
		ResourceLocation recipeId
	) {
		super(WorkstationTileEntity.class, WorkstationContainer.class, WINDOW_ID, TILE_POSITION);
		RECIPE_ID = recipeId;
	}

	public static final class Handler extends C2SContainerPacketHandler<WorkstationTileEntity, WorkstationContainer, C2SWorkstationLearnRecipePacket> {

		@Override
		public void finishEncode(
			C2SWorkstationLearnRecipePacket msg,
			PacketBuffer buf
		) {
			buf.writeResourceLocation(msg.RECIPE_ID);
		}

		@Override
		public C2SWorkstationLearnRecipePacket finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new C2SWorkstationLearnRecipePacket(
				windowId,
				tilePos,
				buf.readResourceLocation()
			);
		}

		@Override
		public void handleDetailed(
			Supplier<Context> ctx,
			C2SWorkstationLearnRecipePacket msg,
			WorkstationTileEntity workstationTileEntity
		) {
			System.out.println("Learning " + msg.RECIPE_ID);
		}
	}
}
