package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.containermenu.ManagerContainerMenu;
import ca.teamdman.sfm.common.registry.SFMPackets;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.function.Supplier;

public record ServerboundManagerProgramPacket(
        int windowId,
        BlockPos pos,
        String program
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_manager_program_packet");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void encode(ServerboundManagerProgramPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.windowId());
        friendlyByteBuf.writeBlockPos(msg.pos());
        friendlyByteBuf.writeUtf(msg.program(), Program.MAX_PROGRAM_LENGTH);
    }

    public static ServerboundManagerProgramPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ServerboundManagerProgramPacket(
                friendlyByteBuf.readVarInt(),
                friendlyByteBuf.readBlockPos(),
                friendlyByteBuf.readUtf(Program.MAX_PROGRAM_LENGTH)
        );
    }

    public static void handle(ServerboundManagerProgramPacket msg, PlayPayloadContext context) {
        SFMPackets.handleServerboundContainerPacket(
                context,
                ManagerContainerMenu.class,
                ManagerBlockEntity.class,
                msg.pos,
                msg.windowId,
                (menu, manager) -> manager.setProgram(msg.program())
        );
        
    }
}
