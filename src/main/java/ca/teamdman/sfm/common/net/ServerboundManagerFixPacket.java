package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.ProgramLinter;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ServerboundManagerFixPacket(
        int windowId,
        BlockPos pos
) implements CustomPacketPayload {

    public static final Type<ServerboundManagerFixPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_manager_fix_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundManagerFixPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundManagerFixPacket::encode,
            ServerboundManagerFixPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundManagerFixPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerFixPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerFixPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(
            ServerboundManagerFixPacket msg,
            IPayloadContext context
    ) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> manager
                        .getDisk()
                        .ifPresent(disk -> manager
                                .getProgram()
                                .ifPresent(program -> ProgramLinter.fixWarningsByRemovingBadLabelsFromDisk(
                                        manager,
                                        disk,
                                        program
                                )))
        );

    }
}
