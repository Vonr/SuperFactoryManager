package ca.teamdman.sfm.common.net;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.item.DiskItem;
import ca.teamdman.sfml.ast.Program;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;


import java.util.function.Supplier;

public record ServerboundDiskItemSetProgramPacket(
        String programString,
        InteractionHand hand
) implements CustomPacketPayload {
    @Override
    public void write(FriendlyByteBuf friendlyByteBuf) {
        encode(this, friendlyByteBuf);
    }

    public static final ResourceLocation ID = new ResourceLocation(SFM.MOD_ID, "serverbound_disk_item_set_program_packet");
    @Override
    public ResourceLocation id() {
        return ID;
    }

    public static void encode(ServerboundDiskItemSetProgramPacket msg, FriendlyByteBuf buf) {
        buf.writeUtf(msg.programString, Program.MAX_PROGRAM_LENGTH);
        buf.writeEnum(msg.hand);
    }

    public static ServerboundDiskItemSetProgramPacket decode(
            FriendlyByteBuf buf
    ) {
        return new ServerboundDiskItemSetProgramPacket(
                buf.readUtf(Program.MAX_PROGRAM_LENGTH),
                buf.readEnum(InteractionHand.class)
        );
    }

    public static void handle(
            ServerboundDiskItemSetProgramPacket msg, PlayPayloadContext context
    ) {
        context.workHandler().submitAsync(() -> {
            if (!(context.player().orElse(null) instanceof ServerPlayer sender)) {
                return;
            }
            var stack = sender.getItemInHand(msg.hand);
            if (stack.getItem() instanceof DiskItem) {
                DiskItem.setProgram(stack, msg.programString);
                DiskItem.compileAndUpdateAttributes(stack, null);
            }

        });
        
    }
}
