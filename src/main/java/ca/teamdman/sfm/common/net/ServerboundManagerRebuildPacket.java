package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;


public record ServerboundManagerRebuildPacket(
        int windowId,
        BlockPos pos
) implements CustomPacketPayload {
    public static final Type<ServerboundManagerProgramPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_manager_rebuild_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundManagerRebuildPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundManagerRebuildPacket::encode,
            ServerboundManagerRebuildPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundManagerRebuildPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerRebuildPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerRebuildPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(
            ServerboundManagerRebuildPacket msg,
            IPayloadContext context
    ) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    // perform rebuild by unregistering the cable network
                    CableNetworkManager.purgeCableNetworkForManager(manager);
                    manager.logger.warn(x -> x.accept(Constants.LocalizationKeys.LOG_MANAGER_CABLE_NETWORK_REBUILD.get()));

                    // log it
                    String sender = "UNKNOWN SENDER";
                    if (context.player() instanceof ServerPlayer player) {
                        sender = player.getName().getString();
                    }
                    SFM.LOGGER.debug("{} performed rebuild for manager {} {}", sender, msg.pos(), manager.getLevel());
                }
        );

    }
}
