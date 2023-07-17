package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.compat.SFMCompat;
import ca.teamdman.sfm.common.compat.SFMMekanismCompat;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMUtil;
import ca.teamdman.sfml.ast.DirectionQualifier;
import ca.teamdman.sfml.ast.InputStatement;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public record ServerboundContainerExportsInspectionRequestPacket(
        int windowId,
        BlockPos pos
) {
    public static void encode(ServerboundContainerExportsInspectionRequestPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundContainerExportsInspectionRequestPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundContainerExportsInspectionRequestPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(
            ServerboundContainerExportsInspectionRequestPacket msg,
            Supplier<NetworkEvent.Context> contextSupplier
    ) {
        SFMPackets.handleServerboundContainerPacket(
                contextSupplier,
                AbstractContainerMenu.class,
                BlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, blockEntity) -> {
                    String payload = buildInspectionResults(blockEntity);
                    SFMPackets.INSPECTION_CHANNEL.send(
                            PacketDistributor.PLAYER.with(contextSupplier.get()::getSender),
                            new ClientboundContainerExportsInspectionResultsPacket(
                                    msg.windowId,
                                    payload
                            )
                    );
                }
        );
    }


    private static String buildInspectionResults(BlockEntity be) {
        StringBuilder sb = new StringBuilder();
        Direction[] dirs = Arrays.copyOf(Direction.values(), Direction.values().length + 1);
        dirs[dirs.length - 1] = null;
        for (Direction direction : dirs) {
            sb.append("-- ").append(direction).append("\n");
            //noinspection unchecked,rawtypes
            SFMResourceTypes.DEFERRED_TYPES
                    .get()
                    .getEntries()
                    .forEach(entry -> sb.append(buildInspectionResults(
                            (ResourceKey) entry.getKey(),
                            entry.getValue(),
                            be,
                            direction
                    ).indent(4)));
            sb.append("\n");
        }

        if (SFMCompat.isMekanismLoaded()) {
            sb.append(SFMMekanismCompat.gatherInspectionResults(be)).append("\n");
        }

        return sb.toString();
    }

    private static <STACK, ITEM, CAP> String buildInspectionResults(
            ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey,
            ResourceType<STACK, ITEM, CAP> resourceType,
            BlockEntity be,
            @Nullable
            Direction direction
    ) {
        StringBuilder sb = new StringBuilder();
        be.getCapability(resourceType.CAPABILITY, direction).ifPresent(cap -> {
            int slots = resourceType.getSlots(cap);
            Int2ObjectMap<STACK> slotContents = new Int2ObjectArrayMap<>(slots);
            for (int slot = 0; slot < slots; slot++) {
                STACK stack = resourceType.getStackInSlot(cap, slot);
                if (!resourceType.isEmpty(stack)) {
                    slotContents.put(slot, stack);
                }
            }

            if (!slotContents.isEmpty()) {
                sb.append("-- ").append(resourceTypeResourceKey.location()).append("\n");
                slotContents.forEach((slot, stack) -> {
                    InputStatement inputStatement = SFMUtil.getInputStatementForStack(
                            resourceTypeResourceKey,
                            resourceType,
                            stack,
                            "target",
                            slot,
                            false,
                            direction
                    );
                    sb.append(inputStatement.toStringCondensed()).append("\n");
                });
                sb.append("\n");
                /*
                then show all items at once with no counts for convenience

                INPUT
                    iron_ingot,
                    gold_ingot
                FROM target TOP SIDE
                 */
                sb.append("INPUT\n");
                StringBuilder lines = new StringBuilder();
                slotContents.forEach((slot, stack) -> {
                    if (resourceTypeResourceKey.equals(SFMResourceTypes.ITEM.getKey())) {
                        lines.append("    ").append(resourceType.getRegistryKey(stack)).append(",\n");
                    } else if (resourceTypeResourceKey.equals(SFMResourceTypes.FORGE_ENERGY.getKey())) {
                        lines.append("    forge_energy::").append(",\n");
                    } else {
                        lines
                                .append("    ")
                                .append(resourceTypeResourceKey.location().toString().replaceFirst("^sfm:", ""))
                                .append(":")
                                .append(resourceType.getRegistryKey(stack))
                                .append(",\n");
                    }
                });
                // remove duplicate lines and append
                sb.append(lines.toString().lines().distinct().sorted().collect(Collectors.joining("\n"))).append("\n");

                if (direction == null) {
                    sb.append("FROM target");
                } else {
                    sb.append("FROM target ").append(DirectionQualifier.directionToString(direction)).append(" SIDE");
                }
            }
            sb.append("\n");

        });
        String result = sb.toString();
        if (!result.isBlank()) {
            //noinspection DataFlowIssue
            if (direction == null && ForgeRegistries.BLOCK_ENTITY_TYPES
                    .getKey(be.getType())
                    .getNamespace()
                    .equals("mekanism")) {
                return "-- "
                       + Constants.LocalizationKeys.CONTAINER_INSPECTOR_MEKANISM_NULL_DIRECTION_WARNING.getString()
                       + "\n"
                       + result;
            }
        }
        return result;
    }

}
