package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
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

public record ServerboundInspectExportsPacket(
        int windowId,
        BlockPos pos
) {
    public static void encode(ServerboundInspectExportsPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
    }

    public static ServerboundInspectExportsPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundInspectExportsPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos()
        );
    }

    public static void handle(ServerboundInspectExportsPacket msg, Supplier<NetworkEvent.Context> contextSupplier) {
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
                            new ClientboundInspectExportsResultPacket(
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

        return sb.toString();
    }

    public static String directionToString(@Nullable Direction direction) {
        if (direction == null) return "";
        return switch (direction) {
            case UP -> "TOP";
            case DOWN -> "BOTTOM";
            case NORTH -> "NORTH";
            case SOUTH -> "SOUTH";
            case EAST -> "EAST";
            case WEST -> "WEST";
        };
    }

    private static <STACK, ITEM, CAP> String buildInspectionResults(
            ResourceKey<ResourceType<STACK, ITEM, CAP>> typeKey,
            ResourceType<STACK, ITEM, CAP> type,
            BlockEntity be,
            @Nullable
            Direction direction
    ) {
        StringBuilder sb = new StringBuilder();
        be.getCapability(type.CAPABILITY, direction).ifPresent(cap -> {
            int slots = type.getSlots(cap);
            Int2ObjectMap<STACK> slotContents = new Int2ObjectArrayMap<>(slots);
            for (int slot = 0; slot < slots; slot++) {
                STACK stack = type.getStackInSlot(cap, slot);
                if (!type.isEmpty(stack)) {
                    slotContents.put(slot, stack);
                }
            }

            if (!slotContents.isEmpty()) {
                sb.append("-- ").append(typeKey.location()).append("\n");
                slotContents.forEach((slot, stack) -> {
                    // example:
                    // INPUT 5 item:minecraft:iron_ingot FROM target TOP SIDE SLOTS 0
                    sb
                            .append("INPUT ")
                            .append(type.getCount(stack))
                            .append(" ");
                    if (typeKey.equals(SFMResourceTypes.ITEM.getKey())) {
                        sb.append(type.getRegistryKey(stack));
                    } else if (typeKey.equals(SFMResourceTypes.FORGE_ENERGY.getKey())) {
                        sb.append("forge_energy::");
                    } else {
                        sb.append(typeKey.location().toString().replaceFirst("^sfm:", ""))
                                .append(":")
                                .append(type.getRegistryKey(stack));
                    }
                    sb.append(" FROM target ");
                    if (direction != null) {
                        sb.append(directionToString(direction))
                                .append(" SIDE ");
                    }
                    sb.append("SLOTS ")
                            .append(slot)
                            .append("\n");
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
                slotContents.forEach((slot, stack) -> {
                    if (typeKey.equals(SFMResourceTypes.ITEM.getKey())) {
                        sb.append("    ").append(type.getRegistryKey(stack)).append(",\n");
                    } else if (typeKey.equals(SFMResourceTypes.FORGE_ENERGY.getKey())) {
                        sb.append("    forge_energy::").append(",\n");
                    } else {
                        sb.append("    ").append(typeKey.location().toString().replaceFirst("^sfm:", ""))
                                .append(":")
                                .append(type.getRegistryKey(stack))
                                .append(",\n");
                    }
                });
                if (direction == null) {
                    sb.append("FROM target");
                } else {
                    sb.append("FROM target ").append(directionToString(direction)).append(" SIDE");
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
