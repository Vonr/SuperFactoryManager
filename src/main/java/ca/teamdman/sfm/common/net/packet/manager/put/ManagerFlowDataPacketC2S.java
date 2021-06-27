package ca.teamdman.sfm.common.net.packet.manager.put;

import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.FlowDataSerializer;
import ca.teamdman.sfm.common.net.packet.C2SContainerPacket;
import ca.teamdman.sfm.common.tile.manager.ManagerTileEntity;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public final class ManagerFlowDataPacketC2S extends
	C2SContainerPacket<ManagerTileEntity, ManagerContainer> {
	private final FlowData[] DATA;
	public ManagerFlowDataPacketC2S(
		int windowId, BlockPos pos, FlowData... data
	) {
		super(ManagerTileEntity.class, ManagerContainer.class, windowId, pos);
		this.DATA = data;
	}

	public static final class Handler extends
		C2SContainerPacketHandler<ManagerTileEntity, ManagerContainer, ManagerFlowDataPacketC2S> {

		@Override
		public void finishEncode(
			ManagerFlowDataPacketC2S msg, PacketBuffer buf
		) {
			buf.writeInt(msg.DATA.length);
			for (FlowData datum : msg.DATA) {
				FlowDataSerializer serializer = datum.getSerializer();
				buf.writeUtf(serializer.getRegistryName().toString(), 128);
				serializer.toBuffer(
					datum,
					buf
				);
			}
		}

		@Override
		public ManagerFlowDataPacketC2S finishDecode(
			int windowId, BlockPos tilePos, PacketBuffer buf
		) {
			return new ManagerFlowDataPacketC2S(
				windowId,
				tilePos,
				IntStream.range(0, buf.readInt())
					.mapToObj(__ -> FlowDataSerializer
						.getSerializer(buf.readUtf(128))
						.get()
						.fromBuffer(buf))
					.toArray(FlowData[]::new)
			);
		}

		@Override
		public void handleDetailed(
			Supplier<Context> ctx,
			ManagerFlowDataPacketC2S msg,
			ManagerTileEntity manager
		) {
			//todo: sort by dependencies?
			for (FlowData datum : msg.DATA) {
				datum.addToDataContainer(manager.getFlowDataContainer());
			}

			manager.sendPacketToListeners(windowId ->
				new ManagerFlowDataPacketS2C(
					windowId,
					msg.DATA
				)
			);
		}
	}
}
