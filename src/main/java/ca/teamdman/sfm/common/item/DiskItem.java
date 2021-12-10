package ca.teamdman.sfm.common.item;

import ca.teamdman.sfm.common.registry.SFMItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class DiskItem extends Item {
    public DiskItem() {
        super(new Item.Properties().tab(SFMItems.TAB));
    }

    public static void toggleLabel(ItemStack stack, String label, BlockPos pos) {
        if (hasLabel(stack, label, pos)) {
            removeLabel(stack, label, pos);
        } else {
            addLabel(stack, label, pos);
        }
    }

    public static boolean hasLabel(ItemStack stack, String label, BlockPos pos) {
        return stack
                .getOrCreateTag()
                .getCompound("sfm:labels")
                .getList(label, Tag.TAG_LONG)
                .contains(LongTag.valueOf(pos.asLong()));
    }

    public static void addLabel(ItemStack stack, String label, BlockPos position) {
        var tag  = stack.getOrCreateTag();
        var dict = tag.getCompound("sfm:labels");
        var list = dict.getList(label, Tag.TAG_LONG);
        list.add(LongTag.valueOf(position.asLong()));
        dict.put(label, list);
        tag.put("sfm:labels", dict);
    }

    public static void removeLabel(ItemStack stack, String label, BlockPos pos) {
        var tag  = stack.getOrCreateTag();
        var dict = tag.getCompound("sfm:labels");
        var list = dict.getList(label, Tag.TAG_LONG);
        list.removeIf(LongTag.valueOf(pos.asLong())::equals);
        dict.put(label, list);
        tag.put("sfm:labels", dict);
    }

    public static String getProgram(ItemStack stack) {
        return stack
                .getOrCreateTag()
                .getString("sfm:program");
    }

    public static void setProgram(ItemStack stack, String program) {
        if (stack.getItem() instanceof DiskItem) {
            stack
                    .getOrCreateTag()
                    .putString("sfm:program", program);
        }
    }

    public static Component getLabelCount(ItemStack stack) {
        var dict = stack
                .getOrCreateTag()
                .getCompound("sfm:labels");
        var labelCount = dict
                .getAllKeys()
                .size();
        var blockCount = dict
                .getAllKeys()
                .stream()
                .map(key -> dict.getList(key, Tag.TAG_LONG))
                .mapToInt(ListTag::size)
                .sum();
        return new TranslatableComponent("item.sfm.disk.tooltip.labels", labelCount, blockCount).withStyle(
                ChatFormatting.GRAY);
    }

    @Override
    public void appendHoverText(
            ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag detail
    ) {
        if (stack.hasTag()) {
            list.add(getLabelCount(stack));
        }
    }
}
