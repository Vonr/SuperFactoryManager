package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


public record ServerboundManagerRebuildPacket(
        int windowId,
        BlockPos pos
) implements CustomPacketPayload {
    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_manager_rebuild_packet");

    @Override
    public ResourceLocation id() {
        return ID;
    }
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static void encode(ServerboundManagerRebuildPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundManagerRebuildPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerRebuildPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(ServerboundManagerRebuildPacket msg, PlayPayloadContext context) {
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
                    if (context.player().orElse(null) instanceof ServerPlayer player) {
                        sender = player.getName().getString();
                    }
                    SFM.LOGGER.debug("{} performed rebuild for manager {} {}", sender, msg.pos(), manager.getLevel());
                }
        );
        
    }
}
