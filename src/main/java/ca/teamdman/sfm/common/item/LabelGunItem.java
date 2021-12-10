package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.gui.screen.LabelGunScreen;
import ca.teamdman.sfm.common.block.ManagerBlock;
import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class LabelGunItem extends Item {
    public LabelGunItem() {
        super(new Properties().tab(SFMItems.TAB));
    }

    public static void setLabel(ItemStack stack, String label) {
        stack
                .getOrCreateTag()
                .putString("sfm:label", label);
    }

    public static Optional<BlockPos> getPairedLocation(ItemStack gun) {
        if (!gun.hasTag()) return Optional.empty();
        var pos = BlockPos.of(gun
                                      .getTag()
                                      .getLong("sfm:pos"));
        return Optional.of(pos);
    }

    public static void setPairedLocation(ItemStack gun, BlockPos pos) {
        gun
                .getOrCreateTag()
                .putLong("sfm:pos", pos.asLong());
    }

    public static String getLabel(ItemStack stack) {
        return !stack.hasTag()
               ? ""
               : stack
                       .getTag()
                       .getString("sfm:label");
    }

    @Override
    public InteractionResult onItemUseFirst(
            ItemStack stack, UseOnContext ctx
    ) {
        var pos   = ctx.getClickedPos();
        var level = ctx.getLevel();
        if (level
                .getBlockState(pos)
                .getBlock() instanceof ManagerBlock) {
            setPairedLocation(ctx.getItemInHand(), pos);
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void appendHoverText(
            ItemStack item, @Nullable Level level, List<Component> lines, TooltipFlag detail
    ) {
        lines.add(new TranslatableComponent("item.sfm.labelgun.tooltip.pairing").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            Minecraft
                    .getInstance()
                    .setScreen(new LabelGunScreen(stack, hand));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        if (getPairedLocation(stack).isEmpty()) {
            return new TranslatableComponent("item.sfm.labelgun.unpaired").withStyle(
                    ChatFormatting.BOLD,
                    ChatFormatting.RED
            );
        }
        var name = getLabel(stack);
        if (name.isEmpty()) return super.getName(stack);
        return new TranslatableComponent("item.sfm.labelgun.paired", name).withStyle(ChatFormatting.AQUA);
    }
}
