package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public record ServerboundManagerSetLogLevelPacket(
        int windowId,
        BlockPos pos,
        String logLevel
) {
    public static final int MAX_LOG_LEVEL_NAME_LENGTH = 64;

    public static void encode(ServerboundManagerSetLogLevelPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeUtf(msg.logLevel(), MAX_LOG_LEVEL_NAME_LENGTH);
    }

    public static ServerboundManagerSetLogLevelPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerSetLogLevelPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readUtf(MAX_LOG_LEVEL_NAME_LENGTH)
        );
    }

    public static void handle(ServerboundManagerSetLogLevelPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
        SFMPackets.handleServerboundContainerPacket(
                contextSupplier,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> {
                    Level logLevelObj = Level.getLevel(msg.logLevel());
                    manager.logger.setLogLevel(logLevelObj);
                    manager.logger.info(x -> x.accept(Constants.LocalizationKeys.LOGS_GUI_SET_LOG_LEVEL_BUTTON_PACKET_RECEIVED.get(
                            msg.logLevel())));
                    String sender = "UNKNOWN SENDER";
                    if (contextSupplier.get().getSender() != null) {
                        sender = contextSupplier.get().getSender().getName().getString();
                    }
                    SFM.LOGGER.debug("{} updated manager {} log level to {}", sender, msg.pos(), msg.logLevel());
                }
        );
    }
}
