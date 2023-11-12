package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.item.LabelGunItem;
import ca.teamdman.sfm.common.program.LabelPositionHolder;
import ca.teamdman.sfm.common.util.SFMUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record ServerboundLabelGunUsePacket(
        InteractionHand hand,
        BlockPos pos,
        boolean isCtrlKeyDown,
        boolean isShiftKeyDown
) {

    public static void encode(ServerboundLabelGunUsePacket msg, FriendlyByteBuf buf) {
        buf.writeEnum(msg.hand);
        buf.writeBlockPos(msg.pos);
        buf.writeBoolean(msg.isCtrlKeyDown);
        buf.writeBoolean(msg.isShiftKeyDown);
    }

    public static ServerboundLabelGunUsePacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundLabelGunUsePacket(
                buf.readEnum(InteractionHand.class),
                buf.readBlockPos(),
                buf.readBoolean(),
                buf.readBoolean()
        );
    }

    public static void handle(
            ServerboundLabelGunUsePacket msg, Supplier<NetworkEvent.Context> ctx
    ) {
        ctx.get().enqueueWork(() -> {
            var sender = ctx.get().getSender();
            if (sender == null) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            var level = sender.getLevel();
            if (!(stack.getItem() instanceof LabelGunItem)) {
                return;
            }

            var gunLabels = LabelPositionHolder.from(stack);
            var pos = msg.pos;

            // target is a manager, perform push or pull action
            if (level.getBlockEntity(pos) instanceof ManagerBlockEntity manager) {
                manager.getDisk().ifPresent(disk -> {
                    if (msg.isShiftKeyDown) {
                        // start with labels from disk
                        var newLabels = LabelPositionHolder.from(disk);
                        // ensure script-referenced labels are included
                        manager.getReferencedLabels().forEach(newLabels::addReferencedLabel);
                        // save to gun
                        newLabels.save(stack);
                        // give feedback to player
                        sender.sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PULLED.getComponent());
                    } else {
                        // save gun labels to disk
                        gunLabels.save(disk);
                        // rebuild program
                        manager.rebuildProgramAndUpdateDisk();
                        // mark manager dirty
                        manager.setChanged();
                        // give feedback to player
                        sender.sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PUSHED.getComponent());
                    }
                });
                return;
            }

            // target is not a manager, we will perform label toggle
            var activeLabel = LabelGunItem.getActiveLabel(stack);
            if (activeLabel.isEmpty()) return;

            if (msg.isCtrlKeyDown) {
                // find all connected inventories of the same block type and toggle the label on all of them
                // if any of them don't have it, apply it, otherwise strip from all

                // find all cable positions so that we only include inventories adjacent to a cable
                Set<BlockPos> cablePositions = CableNetworkManager
                        .getNetworksForLevel(level)
                        .flatMap(CableNetwork::getCablePositions)
                        .collect(Collectors.toSet());

                // get positions of all connected blocks of the same type
                Block targetBlock = level.getBlockState(pos).getBlock();
                List<BlockPos> positions = SFMUtils.getRecursiveStream((current, nextQueue, results) -> {
                    results.accept(current);
                    for (var d : Direction.values()) {
                        var offset = current.offset(d.getNormal());
                        if (level.getBlockState(offset).getBlock() == targetBlock) {
                            // this is the block we are looking for
                            // ensure it is also adjacent to a cable
                            if (Arrays
                                    .stream(Direction.values())
                                    .anyMatch(d2 -> cablePositions.contains(offset.offset(d2.getNormal())))) {
                                nextQueue.accept(offset);
                            }
                        }
                    }
                }, pos).toList();

                // check if any of the positions are missing the label
                var existing = new HashSet<>(gunLabels.getPositions(activeLabel));
                boolean anyMissing = positions.stream().anyMatch(p -> !existing.contains(p));

                // apply or strip label from all positions
                if (anyMissing) {
                    gunLabels.addAll(activeLabel, positions);
                } else {
                    positions.forEach(p -> gunLabels.remove(activeLabel, p));
                }
            } else {
                // normal behaviour - operate on a single position
                if (msg.isShiftKeyDown) {
                    // clear all labels from this position
                    gunLabels.remove(pos);
                } else {
                    // toggle the active label for this position
                    gunLabels.toggle(activeLabel, pos);
                }
            }

            // write changes to label gun stack
            gunLabels.save(stack);
        });
        ctx.get().setPacketHandled(true);
    }
}
