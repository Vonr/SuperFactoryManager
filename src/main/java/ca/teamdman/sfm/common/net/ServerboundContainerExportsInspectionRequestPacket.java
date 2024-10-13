package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.compat.SFMCompat;
import ca.teamdman.sfm.common.compat.SFMMekanismCompat;
import ca.teamdman.sfm.common.localization.LocalizationKeys;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.resourcetype.ResourceType;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public record ServerboundContainerExportsInspectionRequestPacket(
        int windowId,
        BlockPos pos
) implements CustomPacketPayload {
    public static final Type<ServerboundContainerExportsInspectionRequestPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_container_exports_inspection_request_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundContainerExportsInspectionRequestPacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundContainerExportsInspectionRequestPacket::encode,
            ServerboundContainerExportsInspectionRequestPacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundContainerExportsInspectionRequestPacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
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
            IPayloadContext context
    ) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                AbstractContainerMenu.class,
                BlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, blockEntity) -> {
                    assert blockEntity.getLevel() != null;
                    if (!(context.player() instanceof ServerPlayer player)) {
                        return;
                    }
                    String payload = buildInspectionResults(blockEntity.getLevel(), blockEntity.getBlockPos());
                    PacketDistributor.sendToPlayer(
                            player,
                            new ClientboundContainerExportsInspectionResultsPacket(
                                    msg.windowId,
                                    SFMUtils.truncate(
                                            payload,
                                            ClientboundContainerExportsInspectionResultsPacket.MAX_RESULTS_LENGTH
                                    )
                            )
                    );
                }
        );

    }


    public static String buildInspectionResults(
            Level level,
            BlockPos pos
    ) {
        StringBuilder sb = new StringBuilder();
        Direction[] dirs = Arrays.copyOf(Direction.values(), Direction.values().length + 1);
        dirs[dirs.length - 1] = null;
        for (Direction direction : dirs) {
            sb.append("-- ").append(direction).append("\n");
            int len = sb.length();
            //noinspection unchecked,rawtypes
            SFMResourceTypes.DEFERRED_TYPES
                    .entrySet()
                    .forEach(entry -> sb.append(buildInspectionResults(
                            (ResourceKey) entry.getKey(),
                            entry.getValue(),
                            level,
                            pos,
                            direction
                    )));
            if (sb.length() == len) {
                sb.append("No exports found");
            }
            sb.append("\n");
        }

        if (SFMCompat.isMekanismLoaded()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                sb.append(SFMMekanismCompat.gatherInspectionResults(be)).append("\n");
            }
        }

        return sb.toString();
    }

    public static <STACK, ITEM, CAP> String buildInspectionResults(
            ResourceKey<ResourceType<STACK, ITEM, CAP>> resourceTypeResourceKey,
            ResourceType<STACK, ITEM, CAP> resourceType,
            Level level,
            BlockPos pos,
            @Nullable
            Direction direction
    ) {
        StringBuilder sb = new StringBuilder();
        var cap = level.getCapability(resourceType.CAPABILITY_KIND, pos, direction);
        if (cap != null) {
            int slots = resourceType.getSlots(cap);
            Int2ObjectMap<STACK> slotContents = new Int2ObjectArrayMap<>(slots);
            for (int slot = 0; slot < slots; slot++) {
                STACK stack = resourceType.getStackInSlot(cap, slot);
                if (!resourceType.isEmpty(stack)) {
                    slotContents.put(slot, stack);
                }
            }

            if (!slotContents.isEmpty()) {
                slotContents.forEach((slot, stack) -> {
                    InputStatement inputStatement = SFMUtils.getInputStatementForStack(
                            resourceTypeResourceKey,
                            resourceType,
                            stack,
                            "target",
                            slot,
                            false,
                            direction
                    );
                    sb.append(inputStatement.toStringPretty()).append("\n");
                });

                List<ResourceLimit> resourceLimitList = new ArrayList<>();
                slotContents.forEach((slot, stack) -> {
                    ResourceLocation stackId = resourceType.getRegistryKey(stack);
                    ResourceIdentifier<STACK, ITEM, CAP> resourceIdentifier = new ResourceIdentifier<>(
                            resourceTypeResourceKey.location().getNamespace(),
                            resourceTypeResourceKey.location().getPath(),
                            stackId.getNamespace(),
                            stackId.getPath()
                    );
                    ResourceLimit resourceLimit = new ResourceLimit(
                            new ResourceIdSet(List.of(resourceIdentifier)),
                            Limit.MAX_QUANTITY_NO_RETENTION, With.ALWAYS_TRUE
                    );
                    resourceLimitList.add(resourceLimit);
                });
                InputStatement inputStatement = new InputStatement(
                        new LabelAccess(
                                List.of(new Label("target")),
                                new DirectionQualifier(direction == null
                                                       ? EnumSet.noneOf(Direction.class)
                                                       : EnumSet.of(direction)),
                                NumberRangeSet.MAX_RANGE,
                                RoundRobin.disabled()
                        ),
                        new ResourceLimits(
                                resourceLimitList.stream().distinct().toList(),
                                ResourceIdSet.EMPTY
                        ),
                        false
                );
                sb.append(inputStatement.toStringPretty());
            }
        }
        String result = sb.toString();
        if (!result.isBlank()) {
            BlockEntity be = level.getBlockEntity(pos);
            //noinspection DataFlowIssue
            if (be != null && direction == null && BuiltInRegistries.BLOCK_ENTITY_TYPE
                    .getKey(be.getType())
                    .getNamespace()
                    .equals("mekanism")) {
                return "-- "
                       + LocalizationKeys.CONTAINER_INSPECTOR_MEKANISM_NULL_DIRECTION_WARNING.getString()
                       + "\n"
                       + result;
            }
        }
        return result;
    }

}

