package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ClientboundManagerGuiUpdatePacket(
        int windowId,
        String program,
        ManagerBlockEntity.State state,
        long[] tickTimes
) implements CustomPacketPayload {
    public static final Type<ClientboundManagerGuiUpdatePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "clientbound_manager_gui_update_packet"
    ));

    public static final StreamCodec<FriendlyByteBuf, ClientboundManagerGuiUpdatePacket> STREAM_CODEC = StreamCodec.ofMember(
            ClientboundManagerGuiUpdatePacket::encode,
            ClientboundManagerGuiUpdatePacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ClientboundManagerGuiUpdatePacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeUtf(msg.program(), Program.MAX_PROGRAM_LENGTH);
        friendlyByteBuf.writeEnum(msg.state());
        friendlyByteBuf.writeLongArray(msg.tickTimes());
    }

    public static ClientboundManagerGuiUpdatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ClientboundManagerGuiUpdatePacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH),
                friendlyByteBuf.readEnum(ManagerBlockEntity.State.class),
                friendlyByteBuf.readLongArray()
        );
    }

    public static void handle(
            ClientboundManagerGuiUpdatePacket msg,
            IPayloadContext context
    ) {
        msg.handleInner();

    }

    public ClientboundManagerGuiUpdatePacket cloneWithWindowId(int windowId) {
        return new ClientboundManagerGuiUpdatePacket(windowId, program(), state(), tickTimes());
    }

    public void handleInner() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null
            || !(player.containerMenu instanceof ManagerContainerMenu menu)
            || menu.containerId != this.windowId()) {
            SFM.LOGGER.error("Invalid logs packet received, ignoring.");
            return;
        }
        menu.tickTimeNanos = this.tickTimes();
        menu.state = this.state();
        menu.program = this.program();
    }
}
