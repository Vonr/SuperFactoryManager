package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;


import java.util.function.Supplier;

public record ServerboundManagerLogDesireUpdatePacket(
        int windowId,
        BlockPos pos,
        boolean isLogScreenOpen
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_manager_log_desire_update_packet"
    ));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundManagerLogDesireUpdatePacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeBoolean(msg.isLogScreenOpen());
    }

    public static ServerboundManagerLogDesireUpdatePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerLogDesireUpdatePacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readBoolean()
        );
    }

    public static void handle(
            ServerboundManagerLogDesireUpdatePacket msg,
            IPayloadContext context
    ) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    menu.isLogScreenOpen = msg.isLogScreenOpen();
                    manager.sendUpdatePacket();
                }
        );

    }
}
