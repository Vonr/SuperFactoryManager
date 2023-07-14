package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.client.ClientStuff;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public record ClientboundInspectExportsResultPacket(
        int windowId,
        String results
) {
    public static final int MAX_RESULTS_LENGTH = 10240;

    public static void encode(
            ClientboundInspectExportsResultPacket msg, FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.results(), MAX_RESULTS_LENGTH);
    }

    public static ClientboundInspectExportsResultPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundInspectExportsResultPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(MAX_RESULTS_LENGTH)
        );
    }

    public static void handle(
            ClientboundInspectExportsResultPacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> ClientStuff.showProgramEditScreen(msg.results, next -> {
        }));
        contextSupplier.get().setPacketHandled(true);
    }
}
