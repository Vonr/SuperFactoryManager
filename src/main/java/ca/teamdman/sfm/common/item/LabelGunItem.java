package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.client.ClientStuff;
import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.blockentity.ManagerBlockEntity;
import ca.teamdman.sfm.common.program.LabelHolder;
import ca.teamdman.sfm.common.registry.SFMItems;
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
import java.util.Comparator;
import java.util.List;

public class LabelGunItem extends Item {
    public LabelGunItem() {
        super(new Properties().stacksTo(1));
    }

    public static void setActiveLabel(ItemStack gun, String label) {
        if (label.isEmpty()) return;
        LabelHolder.from(gun).addReferencedLabel(label).save(gun);
        gun.getOrCreateTag().putString("sfm:active_label", label);
    }

    public static String getActiveLabel(ItemStack stack) {
        //noinspection DataFlowIssue
        return !stack.hasTag() ? "" : stack.getTag().getString("sfm:active_label");
    }

    public static String getNextLabel(ItemStack gun, int change) {
        var labels = LabelHolder.from(gun).get().keySet().stream().sorted(Comparator.naturalOrder()).toList();
        if (labels.size() == 0) return "";
        var currentLabel = getActiveLabel(gun);

        int currentLabelIndex = 0;
        for (int i = 0; i < labels.size(); i++) {
            if (labels.get(i).equals(currentLabel)) {
                currentLabelIndex = i;
                break;
            }
        }

        int nextLabelIndex = currentLabelIndex + change;
        // ensure going negative wraps around
        nextLabelIndex = ((nextLabelIndex % labels.size()) + labels.size()) % labels.size();

        return labels.get(nextLabelIndex);
    }

    @Override
    public InteractionResult onItemUseFirst(
            ItemStack gun, UseOnContext ctx
    ) {
        var level = ctx.getLevel();
        if (level.isClientSide) return InteractionResult.SUCCESS;

        var gunLabels = LabelHolder.from(gun);
        var pos = ctx.getClickedPos();

        if (level.getBlockEntity(pos) instanceof ManagerBlockEntity manager) {
            manager.getDisk().ifPresent(disk -> {
                Player player = ctx.getPlayer();
                if (player != null && player.isShiftKeyDown()) {
                    // copy labels from disk to gun and also add referenced labels from scripts
                    var diskLabels = LabelHolder.from(disk);
                    manager.getReferencedLabels().forEach(diskLabels::addReferencedLabel);
                    diskLabels.save(gun);
                    player.sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PULLED.getComponent());
                } else {
                    // copy labels from gun to disk
                    gunLabels.save(disk);
                    manager.rebuildProgramAndUpdateDisk();
                    manager.setChanged();
                    if (player != null) {
                        player.sendSystemMessage(Constants.LocalizationKeys.LABEL_GUN_CHAT_PUSHED.getComponent());
                    }
                }
            });
            return InteractionResult.CONSUME;
        }

        var label = getActiveLabel(gun);
        if (label.isEmpty()) return InteractionResult.SUCCESS;

        if (ctx.getPlayer() != null && ctx.getPlayer().isShiftKeyDown())
            gunLabels.remove(pos);
        else
            gunLabels.toggle(label, pos);
        gunLabels.save(gun);

        return InteractionResult.CONSUME;
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag detail
    ) {
        lines.add(Constants.LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_1.getComponent().withStyle(ChatFormatting.GRAY));
        lines.add(Constants.LocalizationKeys.LABEL_GUN_ITEM_TOOLTIP_2.getComponent().withStyle(ChatFormatting.GRAY));
        lines.addAll(LabelHolder.from(stack).asHoverText());
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
        var name = getActiveLabel(stack);
        if (name.isEmpty()) return super.getName(stack);
        return Constants.LocalizationKeys.LABEL_GUN_ITEM_NAME_WITH_LABEL
                .getComponent(name)
                .withStyle(ChatFormatting.AQUA);
    }
}
