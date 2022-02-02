package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
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

public class LabelGunItem extends Item {
    public LabelGunItem() {
        super(new Properties().tab(SFMItems.TAB));
    }

    public static void setLabel(ItemStack stack, String label) {
        stack
                .getOrCreateTag()
                .putString("sfm:label", label);
    }

    public static String getLabel(ItemStack stack) {
        return !stack.hasTag() ? "" : stack.getTag().getString("sfm:label");
    }

    public static String getNextLabel(ItemStack gun, int change) {
        var dict = gun.getOrCreateTag().getCompound("sfm:labels");
        var keys = dict.getAllKeys().toArray(String[]::new);
        if (keys.length == 0) return "";
        var currentLabel = getLabel(gun);

        int currentLabelIndex = 0;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(currentLabel)) {
                currentLabelIndex = i;
                break;
            }
        }

        int nextLabelIndex = currentLabelIndex + change;
        nextLabelIndex = ((nextLabelIndex % keys.length) + keys.length) % keys.length;

        return keys[nextLabelIndex];
    }

    @Override
    public InteractionResult onItemUseFirst(
            ItemStack stack, UseOnContext ctx
    ) {
        var pos   = ctx.getClickedPos();
        var level = ctx.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (level.getBlockEntity(pos) instanceof ManagerBlockEntity manager) {
            manager.getDisk().ifPresent(disk -> {
                if (ctx.getPlayer().isShiftKeyDown()) {
                    SFMLabelNBTHelper.copyLabels(disk, stack);
                    var scriptLabels = manager.getReferencedLabels();
                    SFMLabelNBTHelper.addLabels(stack, scriptLabels);
                    ctx.getPlayer().sendMessage(new TranslatableComponent("chat.sfm.labelgun.pull"), Util.NIL_UUID);
                } else {
                    SFMLabelNBTHelper.copyLabels(stack, disk);
                    manager.setChanged();
                    ctx.getPlayer().sendMessage(new TranslatableComponent("chat.sfm.labelgun.push"), Util.NIL_UUID);
                }
            });
            return InteractionResult.CONSUME;
        }

        var label = getLabel(stack);
        if (label.isEmpty()) return InteractionResult.SUCCESS;
        if (ctx.getPlayer().isShiftKeyDown())
            SFMLabelNBTHelper.clearLabels(stack, pos);
        else
            SFMLabelNBTHelper.toggleLabel(stack, label, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag detail
    ) {
        lines.add(new TranslatableComponent("item.sfm.labelgun.tooltip.1").withStyle(ChatFormatting.GRAY));
        lines.add(new TranslatableComponent("item.sfm.labelgun.tooltip.2").withStyle(ChatFormatting.GRAY));
        lines.addAll(SFMLabelNBTHelper.getHoverText(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);
        SFM.PROXY.showLabelGunScreen(stack, hand);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = getLabel(stack);
        if (name.isEmpty()) return super.getName(stack);
        return new TranslatableComponent("item.sfm.labelgun.with_label", name).withStyle(ChatFormatting.AQUA);
    }
}
