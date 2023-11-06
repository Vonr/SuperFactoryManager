package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.program.InputResourceTracker;
import ca.teamdman.sfm.common.program.LimitedInputSlot;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfml.ast.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;

public record ServerboundNetworkToolUsePacket(
        BlockPos blockPosition,
        Direction blockFace
) {
    public static void encode(ServerboundNetworkToolUsePacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(msg.blockPosition);
        friendlyByteBuf.writeEnum(msg.blockFace);
    }

    public static ServerboundNetworkToolUsePacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundNetworkToolUsePacket(
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readEnum(Direction.class)
        );
    }

    public static void handle(
            ServerboundNetworkToolUsePacket msg, Supplier<NetworkEvent.Context> contextSupplier
    ) {
        contextSupplier.get().enqueueWork(() -> {
            // we don't know if the player has the program edit screen open from a manager or a disk in hand
            ServerPlayer player = contextSupplier.get().getSender();
            if (player == null) return;
            Level level = player.getLevel();
            BlockPos pos = msg.blockPosition();
            if (!level.isLoaded(pos)) return;
            StringBuilder payload = new StringBuilder()
                    .append("---- block position ----\n")
                    .append(pos)
                    .append("\n---- block state ----\n");
            BlockState state = level.getBlockState(pos);
            payload.append(state).append("\n");

            CableNetworkManager.getNetworkFromPosition(level, pos).ifPresent(net -> {
                payload.append("---- cable network ----\n");
                payload.append(net).append("\n");
            });

            BlockEntity entity = level.getBlockEntity(pos);
            if (entity != null) {
                if (!FMLEnvironment.production) {
                    payload.append("---- (dev only) block entity ----\n");
                    payload.append(entity).append("\n");
                }
                entity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(itemHandler -> {
                    payload.append("---- item handler ----\n").append(itemHandler).append("\n------ contents ------\n");
                    LabelAccess labelAccess = new LabelAccess(
                            List.of(new Label("target")),
                            new DirectionQualifier(EnumSet.of(msg.blockFace)),
                            NumberRangeSet.MAX_RANGE,
                            RoundRobin.disabled()
                    );
                    InputResourceTracker<ItemStack, Item, IItemHandler> tracker = new InputResourceTracker<>(
                            new ResourceLimit<>(new ResourceIdentifier<>(".*"), Limit.MAX_QUANTITY_NO_RETENTION),
                            ResourceIdSet.EMPTY,
                            new AtomicLong(0),
                            new AtomicLong(0)
                    );
                    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
                        LimitedInputSlot<ItemStack, Item, IItemHandler> inputSlot = new LimitedInputSlot<>(
                                itemHandler,
                                slot,
                                tracker
                        );
                        SFMUtil
                                .getInputStatementForSlot(
                                        inputSlot,
                                        labelAccess
                                )
                                .ifPresent(is -> payload
                                        .append(is.toStringPretty())
                                        .append("\n"));
                    }
                });
                if (player.hasPermissions(2)) {
                    payload.append("---- (op only) nbt data ----\n");
                    payload.append(entity.serializeNBT()).append("\n");
                }
            }


            if (payload.length()
                > ClientboundInputInspectionResultsPacket.MAX_RESULTS_LENGTH) {
                SFM.LOGGER.info("Payload too big! (len={})", payload.length());
                String truncationMsg = "\n...truncated";
                SFMPackets.INSPECTION_CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new ClientboundInputInspectionResultsPacket(
                                payload.substring(
                                        0,
                                        ClientboundInputInspectionResultsPacket.MAX_RESULTS_LENGTH
                                        - truncationMsg.length()
                                ) + truncationMsg)
                );
            } else {
                SFMPackets.INSPECTION_CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new ClientboundInputInspectionResultsPacket(
                                payload.toString())
                );
            }
        });
    }
}
