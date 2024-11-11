package ca.teamdman.sfm.common.net;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public interface SFMPacketDaddy<T extends SFMPacket> {
    Class<T> getPacketClass();

    void encode(
            T msg,
            FriendlyByteBuf friendlyByteBuf
    );

    T decode(FriendlyByteBuf friendlyByteBuf);

    void handle(
            T msg,
            SFMPacketHandlingContext context
    );

    default void handleOuter(
            T msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        SFMPacketHandlingContext context = new SFMPacketHandlingContext(contextSupplier);
        context.enqueueAndFinish(() -> handle(msg, context));
    }
}
