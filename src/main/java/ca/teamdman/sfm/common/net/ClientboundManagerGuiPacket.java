package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundManagerGuiPacket(
        int windowId,
        String program,
        ManagerBlockEntity.State state,
        long[] tickTimes
) {

    public static void encode(
            ClientboundManagerGuiPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.program(), Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeEnum(msg.state());
        friendlyByteBuf.writeLongArray(msg.tickTimes());
    }

    public static ClientboundManagerGuiPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundManagerGuiPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readEnum(ManagerBlockEntity.State.class),
                friendlyByteBuf.readLongArray()
        );
    }

    public static void handle(
            ClientboundManagerGuiPacket msg, NetworkEvent.Context context
    ) {
        context.enqueueWork(() -> ClientStuff.updateMenu(msg));
        context.setPacketHandled(true);
    }
}
