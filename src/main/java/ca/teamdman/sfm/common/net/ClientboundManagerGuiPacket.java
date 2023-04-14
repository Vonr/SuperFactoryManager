package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

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
            ClientboundManagerGuiPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            var container = Minecraft.getInstance().player.containerMenu;
            if (container instanceof ManagerContainerMenu menu && container.containerId == msg.windowId()) {
                menu.tickTimeNanos = msg.tickTimes();
                menu.state = msg.state();
                menu.program = msg.program();
            }
        });
        contextSupplier.get().setPacketHandled(true);
    }
}
