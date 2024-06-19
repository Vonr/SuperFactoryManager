package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundManagerProgramPacket(
        int windowId,
        BlockPos pos,
        String program
) {

    public static void encode(ServerboundManagerProgramPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeUtf(msg.program(), Program.MAX_PROGRAM_LENGTH);
    }

    public static ServerboundManagerProgramPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerProgramPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH)
        );
    }

    public static void handle(ServerboundManagerProgramPacket msg, NetworkEvent.Context context) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> manager.setProgram(msg.program())
        );
        context.setPacketHandled(true);
    }
}
