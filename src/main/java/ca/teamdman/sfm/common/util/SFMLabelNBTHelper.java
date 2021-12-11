package ca.teamdman.sfm.common.util;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

import java.util.stream.Stream;

public class SFMLabelNBTHelper {
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

    public static Stream<BlockPos> getLabelPositions(ItemStack stack, String label) {
        var dict = stack
                .getOrCreateTag()
                .getCompound("sfm:labels");
        return getPositions(dict.getList(label, Tag.TAG_LONG));
    }

    private static Stream<BlockPos> getPositions(ListTag list) {
        return list.stream()
                .map(LongTag.class::cast)
                .mapToLong(LongTag::getAsLong)
                .mapToObj(BlockPos::of);
    }

    public static Multimap<String, BlockPos> getLabelPositions(ItemStack stack) {
        var rtn = HashMultimap.<String, BlockPos>create();
        var dict = stack
                .getOrCreateTag()
                .getCompound("sfm:labels");
        for (var key : dict.getAllKeys()) {
            getPositions(dict.getList(key, Tag.TAG_LONG)).forEach(pos -> rtn.put(key, pos));
        }
        return rtn;
    }

    public static Multimap<BlockPos, String> getPositionLabels(ItemStack stack) {
        var rtn  = HashMultimap.<BlockPos, String>create();
        var dict = stack.getOrCreateTag().getCompound("sfm:labels");
        for (var key : dict.getAllKeys()) {
            getPositions(dict.getList(key, Tag.TAG_LONG)).forEach(pos -> rtn.put(pos, key));
        }
        return rtn;
    }

    public static void copyLabels(ItemStack source, ItemStack destination) {
        destination.getOrCreateTag().put("sfm:labels", source.getOrCreateTag().getCompound("sfm:labels"));
    }
}
