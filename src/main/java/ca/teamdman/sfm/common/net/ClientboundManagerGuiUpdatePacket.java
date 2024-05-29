package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.logging.TranslatableLogEvent;
import ca.teamdman.sfm.common.logging.TranslatableLogger;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public record ClientboundManagerGuiUpdatePacket(
        int windowId,
        String program,
        ManagerBlockEntity.State state,
        long[] tickTimes,
        List<TranslatableLogEvent> logs
) {

    public static void encode(
            ClientboundManagerGuiUpdatePacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.program(), Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeEnum(msg.state());
        friendlyByteBuf.writeLongArray(msg.tickTimes());
        TranslatableLogger.encode(msg.logs(), friendlyByteBuf);
    }

    public static ClientboundManagerGuiUpdatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundManagerGuiUpdatePacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readEnum(ManagerBlockEntity.State.class),
                friendlyByteBuf.readLongArray(),
                TranslatableLogger.decode(friendlyByteBuf)
        );
    }

    public static void handle(
            ClientboundManagerGuiUpdatePacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> ClientStuff.updateMenu(msg));
        contextSupplier.get().setPacketHandled(true);
    }

    public ClientboundManagerGuiUpdatePacket cloneWithWindowId(int windowId) {
        return new ClientboundManagerGuiUpdatePacket(windowId, program(), state(), tickTimes(), logs());
    }
}
