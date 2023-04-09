package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.registry.SFMItems;
import ca.teamdman.sfm.common.util.SFMLabelNBTHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
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
                    ctx.getPlayer().sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PULLED.getComponent());
                } else {
                    SFMLabelNBTHelper.copyLabels(stack, disk);
                    manager.rebuildProgramAndUpdateDisk();
                    manager.setChanged();
                    ctx.getPlayer().sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PUSHED.getComponent());
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
        lines.add(Constants.LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_1.getComponent().withStyle(ChatFormatting.GRAY));
        lines.add(Constants.LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_2.getComponent().withStyle(ChatFormatting.GRAY));
        lines.addAll(SFMLabelNBTHelper.getHoverText(stack));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(
            Level level,
            Player player,
            InteractionHand hand
    ) {
        var stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            ClientStuff.showLabelGunScreen(stack, hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
    }

    @Override
    public Component getName(ItemStack stack) {
        var name = getLabel(stack);
        if (name.isEmpty()) return super.getName(stack);
        return Constants.LocalizationKeys.LABEL_GUN_ITEM_NAME_WITH_LABEL
                .getComponent(name)
                .withStyle(ChatFormatting.AQUA);
    }
}
