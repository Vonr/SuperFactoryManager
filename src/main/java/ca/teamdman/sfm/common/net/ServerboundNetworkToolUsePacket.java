package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.cablenetwork.CableNetwork;
import ca.teamdman.sfm.common.cablenetwork.CableNetworkManager;
import ca.teamdman.sfm.common.compat.SFMCompat;
import ca.teamdman.sfm.common.registry.SFMResourceTypes;
import ca.teamdman.sfm.common.util.SFMUtils;
import ca.teamdman.sfml.ast.DirectionQualifier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record ServerboundNetworkToolUsePacket(
        BlockPos blockPosition,
        Direction blockFace
) implements CustomPacketPayload {

    public static final Type<ServerboundNetworkToolUsePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(
            SFM.MOD_ID,
            "serverbound_network_tool_use_packet"
    ));
    public static final StreamCodec<FriendlyByteBuf, ServerboundNetworkToolUsePacket> STREAM_CODEC = StreamCodec.ofMember(
            ServerboundNetworkToolUsePacket::encode,
            ServerboundNetworkToolUsePacket::decode
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void encode(
            ServerboundNetworkToolUsePacket msg,
            FriendlyByteBuf friendlyByteBuf
    ) {
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
            ServerboundNetworkToolUsePacket msg,
            IPayloadContext context
    ) {
        // we don't know if the player has the program edit screen open from a manager or a disk in hand
        if (!(context.player() instanceof ServerPlayer player)) {
            return;
        }
        Level level = player.level();
        BlockPos pos = msg.blockPosition();
        if (!level.isLoaded(pos)) return;
        StringBuilder payload = new StringBuilder()
                .append("---- block position ----\n")
                .append(pos)
                .append("\n---- block state ----\n");
        BlockState state = level.getBlockState(pos);
        payload.append(state).append("\n");

        List<CableNetwork> foundNetworks = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos cablePosition = pos.relative(direction);
            CableNetworkManager
                    .getOrRegisterNetworkFromCablePosition(level, cablePosition)
                    .ifPresent(foundNetworks::add);
        }
        payload.append("---- cable networks ----\n");
        if (foundNetworks.isEmpty()) {
            payload.append("No networks found\n");
        } else {
            for (CableNetwork network : foundNetworks) {
                payload.append(network).append("\n");
            }
        }

        BlockEntity entity = level.getBlockEntity(pos);
        if (entity != null) {
            if (!FMLEnvironment.production) {
                payload.append("---- (dev only) block entity ----\n");
                payload.append(entity).append("\n");
            }
        }

        payload.append("---- capability directions ----\n");
        for (var cap : SFMCompat.getCapabilities()) {
            payload
                    .append(cap.name())
                    .append(": ");
            String directions = DirectionQualifier.EVERY_DIRECTION
                    .stream()
                    .filter(dir -> level.getCapability(cap, pos, dir) != null)
                    .map(dir -> dir == null ? "NULL DIRECTION" : DirectionQualifier.directionToString(dir))
                    .collect(Collectors.joining(", ", "[", "]"));
            payload.append(directions).append("\n");
        }

        payload.append("---- exports ----\n");
        int len = payload.length();
        //noinspection unchecked,rawtypes
        SFMResourceTypes.DEFERRED_TYPES
                .entrySet()
                .forEach(entry -> payload.append(ServerboundContainerExportsInspectionRequestPacket.buildInspectionResults(
                        (ResourceKey) entry.getKey(),
                        entry.getValue(),
                        level,
                        pos,
                        msg.blockFace
                )));
        if (payload.length() == len) {
            payload.append("No exports found");
        }
        payload.append("\n");


        if (entity != null) {
            if (player.hasPermissions(2)) {
                payload.append("---- (op only) nbt data ----\n");
                payload.append(entity.getPersistentData()).append("\n");
            }
        }


        PacketDistributor.sendToPlayer(
                player,

                new ClientboundInputInspectionResultsPacket(
                        SFMUtils.truncate(
                                payload.toString(),
                                ClientboundInputInspectionResultsPacket.MAX_RESULTS_LENGTH
                        ))
        );

    }
}

