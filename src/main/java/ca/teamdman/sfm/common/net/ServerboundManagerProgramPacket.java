package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.menu.ManagerMenu;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

public class ServerboundManagerProgramPacket extends MenuPacket {
    private final String PROGRAM;

    public ServerboundManagerProgramPacket(int windowId, BlockPos pos, String program) {
        super(windowId, pos);
        PROGRAM = program;
    }

    public static class ResetPacketHandler extends MenuPacketHandler<ManagerMenu, ManagerBlockEntity, ServerboundManagerProgramPacket> {
        public ResetPacketHandler() {
            super(ManagerMenu.class, ManagerBlockEntity.class);
        }

        @Override
        public void encode(
                ServerboundManagerProgramPacket msg, FriendlyByteBuf buf
        ) {
            buf.writeUtf(msg.PROGRAM, Program.MAX_PROGRAM_LENGTH);
        }

        @Override
        public ServerboundManagerProgramPacket decode(int containerId, BlockPos pos, FriendlyByteBuf buf) {
            return new ServerboundManagerProgramPacket(containerId, pos, buf.readUtf(Program.MAX_PROGRAM_LENGTH));
        }

        @Override
        public void handle(
                ServerboundManagerProgramPacket msg,
                ManagerMenu menu,
                ManagerBlockEntity blockEntity
        ) {
            blockEntity.setProgram(msg.PROGRAM);
        }
    }
}
