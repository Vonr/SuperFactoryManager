package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ServerboundManagerFixPacket(
        int windowId,
        BlockPos pos
) {
    public static void encode(ServerboundManagerFixPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerFixPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerFixPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(ServerboundManagerFixPacket msg, NetworkEvent.Context context) {
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
                                .ifPresent(program -> program.fixWarnings(disk, manager)))
        );
        context.setPacketHandled(true);
    }
}
