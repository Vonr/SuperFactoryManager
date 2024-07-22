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
        registrar.play(
                ServerboundManagerResetPacket.ID,
                ServerboundManagerResetPacket::decode,
                ServerboundManagerResetPacket::handle
        );
        registrar.play(
                ServerboundManagerFixPacket.ID,
                ServerboundManagerFixPacket::decode,
                ServerboundManagerFixPacket::handle
        );

        registrar.play(
                ClientboundManagerGuiUpdatePacket.ID,
                ClientboundManagerGuiUpdatePacket::decode,
                ClientboundManagerGuiUpdatePacket::handle
        );
        registrar.play(
                ServerboundManagerSetLogLevelPacket.ID,
                ServerboundManagerSetLogLevelPacket::decode,
                ServerboundManagerSetLogLevelPacket::handle
        );
        registrar.play(
                ServerboundManagerClearLogsPacket.ID,
                ServerboundManagerClearLogsPacket::decode,
                ServerboundManagerClearLogsPacket::handle
        );
        registrar.play(
                ServerboundManagerLogDesireUpdatePacket.ID,
                ServerboundManagerLogDesireUpdatePacket::decode,
                ServerboundManagerLogDesireUpdatePacket::handle
        );
        registrar.play(
                ClientboundManagerLogsPacket.ID,
                ClientboundManagerLogsPacket::decode,
                ClientboundManagerLogsPacket::handle
        );
        registrar.play(
                ServerboundManagerRebuildPacket.ID,
                ServerboundManagerRebuildPacket::decode,
                ServerboundManagerRebuildPacket::handle
        );
        registrar.play(
                ClientboundManagerLogLevelUpdatedPacket.ID,
                ClientboundManagerLogLevelUpdatedPacket::decode,
                ClientboundManagerLogLevelUpdatedPacket::handle
        );

        registrar.play(
                ServerboundLabelGunUpdatePacket.ID,
                ServerboundLabelGunUpdatePacket::decode,
                ServerboundLabelGunUpdatePacket::handle
        );
        registrar.play(
                ServerboundLabelGunPrunePacket.ID,
                ServerboundLabelGunPrunePacket::decode,
                ServerboundLabelGunPrunePacket::handle
        );
        registrar.play(
                ServerboundLabelGunClearPacket.ID,
                ServerboundLabelGunClearPacket::decode,
                ServerboundLabelGunClearPacket::handle
        );
        registrar.play(
                ServerboundLabelGunUsePacket.ID,
                ServerboundLabelGunUsePacket::decode,
                ServerboundLabelGunUsePacket::handle
        );

        registrar.play(
                ServerboundDiskItemSetProgramPacket.ID,
                ServerboundDiskItemSetProgramPacket::decode,
                ServerboundDiskItemSetProgramPacket::handle
        );

        registrar.play(
                ServerboundContainerExportsInspectionRequestPacket.ID,
                ServerboundContainerExportsInspectionRequestPacket::decode,
                ServerboundContainerExportsInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundContainerExportsInspectionResultsPacket.ID,
                ClientboundContainerExportsInspectionResultsPacket::decode,
                ClientboundContainerExportsInspectionResultsPacket::handle
        );
        registrar.play(
                ServerboundLabelInspectionRequestPacket.ID,
                ServerboundLabelInspectionRequestPacket::decode,
                ServerboundLabelInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundLabelInspectionResultsPacket.ID,
                ClientboundLabelInspectionResultsPacket::decode,
                ClientboundLabelInspectionResultsPacket::handle
        );
        registrar.play(
                ServerboundInputInspectionRequestPacket.ID,
                ServerboundInputInspectionRequestPacket::decode,
                ServerboundInputInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundInputInspectionResultsPacket.ID,
                ClientboundInputInspectionResultsPacket::decode,
                ClientboundInputInspectionResultsPacket::handle
        );
        registrar.play(
                ServerboundOutputInspectionRequestPacket.ID,
                ServerboundOutputInspectionRequestPacket::decode,
                ServerboundOutputInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundOutputInspectionResultsPacket.ID,
                ClientboundOutputInspectionResultsPacket::decode,
                ClientboundOutputInspectionResultsPacket::handle
        );
        registrar.play(
                ServerboundNetworkToolUsePacket.ID,
                ServerboundNetworkToolUsePacket::decode,
                ServerboundNetworkToolUsePacket::handle
        );
        registrar.play(
                ServerboundIfStatementInspectionRequestPacket.ID,
                ServerboundIfStatementInspectionRequestPacket::decode,
                ServerboundIfStatementInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundIfStatementInspectionResultsPacket.ID,
                ClientboundIfStatementInspectionResultsPacket::decode,
                ClientboundIfStatementInspectionResultsPacket::handle
        );
        registrar.play(
                ServerboundBoolExprStatementInspectionRequestPacket.ID,
                ServerboundBoolExprStatementInspectionRequestPacket::decode,
                ServerboundBoolExprStatementInspectionRequestPacket::handle
        );
        registrar.play(
                ClientboundBoolExprStatementInspectionResultsPacket.ID,
                ClientboundBoolExprStatementInspectionResultsPacket::decode,
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
