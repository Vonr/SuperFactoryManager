package ca.teamdman.sfm.common.registry;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.net.*;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SFMPackets {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(SFM.MOD_ID)
                .versioned("1.0.0");
        registrar.playToServer(
                ServerboundManagerProgramPacket.TYPE,
                ServerboundManagerProgramPacket.STREAM_CODEC,
                ServerboundManagerProgramPacket::handle
        );
        registrar.playToServer(
                ServerboundManagerResetPacket.TYPE,
                ServerboundManagerResetPacket.STREAM_CODEC,
                ServerboundManagerResetPacket::handle
        );
        registrar.playToServer(
                ServerboundManagerFixPacket.TYPE,
                ServerboundManagerFixPacket.STREAM_CODEC,
                ServerboundManagerFixPacket::handle
        );

        registrar.playToClient(
                ClientboundManagerGuiUpdatePacket.TYPE,
                ClientboundManagerGuiUpdatePacket.STREAM_CODEC,
                ClientboundManagerGuiUpdatePacket::handle
        );
        registrar.playToServer(
                ServerboundManagerSetLogLevelPacket.TYPE,
                ServerboundManagerSetLogLevelPacket.STREAM_CODEC,
                ServerboundManagerSetLogLevelPacket::handle
        );
        registrar.playToServer(
                ServerboundManagerClearLogsPacket.TYPE,
                ServerboundManagerClearLogsPacket.STREAM_CODEC,
                ServerboundManagerClearLogsPacket::handle
        );
        registrar.playToServer(
                ServerboundManagerLogDesireUpdatePacket.TYPE,
                ServerboundManagerLogDesireUpdatePacket.STREAM_CODEC,
                ServerboundManagerLogDesireUpdatePacket::handle
        );
        registrar.playToClient(
                ClientboundManagerLogsPacket.TYPE,
                ClientboundManagerLogsPacket.STREAM_CODEC,
                ClientboundManagerLogsPacket::handle
        );
        registrar.playToServer(
                ServerboundManagerRebuildPacket.TYPE,
                ServerboundManagerRebuildPacket.STREAM_CODEC,
                ServerboundManagerRebuildPacket::handle
        );
        registrar.playToClient(
                ClientboundManagerLogLevelUpdatedPacket.TYPE,
                ClientboundManagerLogLevelUpdatedPacket.STREAM_CODEC,
                ClientboundManagerLogLevelUpdatedPacket::handle
        );

        registrar.playToServer(
                ServerboundLabelGunUpdatePacket.TYPE,
                ServerboundLabelGunUpdatePacket.STREAM_CODEC,
                ServerboundLabelGunUpdatePacket::handle
        );
        registrar.playToServer(
                ServerboundLabelGunPrunePacket.TYPE,
                ServerboundLabelGunPrunePacket.STREAM_CODEC,
                ServerboundLabelGunPrunePacket::handle
        );
        registrar.playToServer(
                ServerboundLabelGunClearPacket.TYPE,
                ServerboundLabelGunClearPacket.STREAM_CODEC,
                ServerboundLabelGunClearPacket::handle
        );
        registrar.playToServer(
                ServerboundLabelGunUsePacket.TYPE,
                ServerboundLabelGunUsePacket.STREAM_CODEC,
                ServerboundLabelGunUsePacket::handle
        );

        registrar.playToServer(
                ServerboundDiskItemSetProgramPacket.TYPE,
                ServerboundDiskItemSetProgramPacket.STREAM_CODEC,
                ServerboundDiskItemSetProgramPacket::handle
        );

        registrar.playToServer(
                ServerboundContainerExportsInspectionRequestPacket.TYPE,
                ServerboundContainerExportsInspectionRequestPacket.STREAM_CODEC,
                ServerboundContainerExportsInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundContainerExportsInspectionResultsPacket.TYPE,
                ClientboundContainerExportsInspectionResultsPacket.STREAM_CODEC,
                ClientboundContainerExportsInspectionResultsPacket::handle
        );
        registrar.playToServer(
                ServerboundLabelInspectionRequestPacket.TYPE,
                ServerboundLabelInspectionRequestPacket.STREAM_CODEC,
                ServerboundLabelInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundLabelInspectionResultsPacket.TYPE,
                ClientboundLabelInspectionResultsPacket.STREAM_CODEC,
                ClientboundLabelInspectionResultsPacket::handle
        );
        registrar.playToServer(
                ServerboundInputInspectionRequestPacket.TYPE,
                ServerboundInputInspectionRequestPacket.STREAM_CODEC,
                ServerboundInputInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundInputInspectionResultsPacket.TYPE,
                ClientboundInputInspectionResultsPacket.STREAM_CODEC,
                ClientboundInputInspectionResultsPacket::handle
        );
        registrar.playToServer(
                ServerboundOutputInspectionRequestPacket.TYPE,
                ServerboundOutputInspectionRequestPacket.STREAM_CODEC,
                ServerboundOutputInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundOutputInspectionResultsPacket.TYPE,
                ClientboundOutputInspectionResultsPacket.STREAM_CODEC,
                ClientboundOutputInspectionResultsPacket::handle
        );
        registrar.playToServer(
                ServerboundNetworkToolUsePacket.TYPE,
                ServerboundNetworkToolUsePacket.STREAM_CODEC,
                ServerboundNetworkToolUsePacket::handle
        );
        registrar.playToServer(
                ServerboundIfStatementInspectionRequestPacket.TYPE,
                ServerboundIfStatementInspectionRequestPacket.STREAM_CODEC,
                ServerboundIfStatementInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundIfStatementInspectionResultsPacket.TYPE,
                ClientboundIfStatementInspectionResultsPacket.STREAM_CODEC,
                ClientboundIfStatementInspectionResultsPacket::handle
        );
        registrar.playToServer(
                ServerboundBoolExprStatementInspectionRequestPacket.TYPE,
                ServerboundBoolExprStatementInspectionRequestPacket.STREAM_CODEC,
                ServerboundBoolExprStatementInspectionRequestPacket::handle
        );
        registrar.playToClient(
                ClientboundBoolExprStatementInspectionResultsPacket.TYPE,
                ClientboundBoolExprStatementInspectionResultsPacket.STREAM_CODEC,
                ClientboundBoolExprStatementInspectionResultsPacket::handle
        );
    }

    public static <MENU extends AbstractContainerMenu, BE extends BlockEntity> void handleServerboundContainerPacket(
            @Nullable IPayloadContext context,
            Class<MENU> menuClass,
            Class<BE> blockEntityClass,
            BlockPos pos,
            int containerId,
            BiConsumer<MENU, BE> callback
    ) {
        if (context == null) return;
        // TODO: log return cases about invalid packet received
        {
            if (!(context.player() instanceof ServerPlayer sender)) {
                return;
            }
            if (sender.isSpectator()) return; // ignore packets from spectators

            var menu = sender.containerMenu;
            if (!menuClass.isInstance(menu)) return;
            if (menu.containerId != containerId) return;

            var level = sender.level();
            //noinspection ConstantValue
            if (level == null) return;
            if (!level.isLoaded(pos)) return;

            var blockEntity = level.getBlockEntity(pos);
            if (!blockEntityClass.isInstance(blockEntity)) return;
            //noinspection unchecked
            callback.accept((MENU) menu, (BE) blockEntity);
        }
    }
}
