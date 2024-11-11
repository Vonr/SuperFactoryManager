package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.program.ProgramLinter;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public record ServerboundManagerFixPacket(
        int windowId,
        BlockPos pos
) implements SFMPacket {
    public static class Daddy implements SFMPacketDaddy<ServerboundManagerFixPacket> {
        @Override
        public void encode(
                ServerboundManagerFixPacket msg,
                FriendlyByteBuf friendlyByteBuf
        ) {
            friendlyByteBuf.writeVarInt(msg.windowId());
            friendlyByteBuf.writeBlockPos(msg.pos());
        }

        @Override
        public ServerboundManagerFixPacket decode(FriendlyByteBuf friendlyByteBuf) {
            return new ServerboundManagerFixPacket(
                    friendlyByteBuf.readVarInt(),
                    friendlyByteBuf.readBlockPos()
            );
        }

        @Override
        public void handle(
                ServerboundManagerFixPacket msg,
                SFMPacketHandlingContext context
        ) {
            context.handleServerboundContainerPacket(
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

        @Override
        public Class<ServerboundManagerFixPacket> getPacketClass() {
            return ServerboundManagerFixPacket.class;
        }
    }
}
