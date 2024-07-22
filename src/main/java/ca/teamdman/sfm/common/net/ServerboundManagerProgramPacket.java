package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundManagerProgramPacket(
        int windowId,
        BlockPos pos,
        String program
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(SFM.MOD_ID, "serverbound_manager_program_packet"));

    public static final StreamCodec<ByteBuf, ServerboundManagerProgramPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            ServerboundManagerProgramPacket::windowId,
            BlockPos.STREAM_CODEC,
            ServerboundManagerProgramPacket::pos,
            ByteBufCodecs.STRING_UTF8,
            ServerboundManagerProgramPacket::program,
            ServerboundManagerProgramPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(ServerboundManagerProgramPacket msg, IPayloadContext context) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> manager.setProgram(msg.program())
        );

    }
}
