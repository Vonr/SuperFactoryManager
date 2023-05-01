package ca.teamdman.sfm.common.util;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfml.ast.Label;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        return getLabelDict(stack)
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

    public static void addLabels(ItemStack stack, Collection<String> labels) {
        var tag  = stack.getOrCreateTag();
        var dict = tag.getCompound("sfm:labels");
        for (String label : labels) {
            var list = dict.getList(label, Tag.TAG_LONG);
            dict.put(label, list);
        }
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

    public static void removeLabel(ItemStack stack, String label) {
        var tag  = stack.getOrCreateTag();
        var dict = tag.getCompound("sfm:labels");
        dict.remove(label);
        tag.put("sfm:labels", dict);
    }

    public static List<Component> getHoverText(ItemStack stack) {
        var rtn = new ArrayList<Component>();
        var dict = getLabelDict(stack);
        if (dict.isEmpty()) return rtn;
        rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL_HEADER
                        .getComponent()
                        .withStyle(ChatFormatting.UNDERLINE));
        for (var label : dict.getAllKeys()) {
            rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL.getComponent(
                    label,
                    dict.getList(label, Tag.TAG_LONG).size()
            ).withStyle(ChatFormatting.GRAY));
        }
        return rtn;
    }

    public static Stream<BlockPos> getLabelPositions(ItemStack stack, String label) {
        var dict = getLabelDict(stack);
        return getPositions(dict, label);
    }

    public static Stream<BlockPos> getPositions(ItemStack stack, List<Label> labels) {
        var dict = getLabelDict(stack);
        return labels
                .stream()
                .map(Label::name)
                .flatMap(label -> getPositions(dict, label))
                .distinct();
    }

    private static Stream<BlockPos> getPositions(CompoundTag tag, String label) {
        return tag.getList(label, Tag.TAG_LONG).stream()
                .map(LongTag.class::cast)
                .mapToLong(LongTag::getAsLong)
                .mapToObj(BlockPos::of);
    }

    public static Multimap<BlockPos, String> getPositionLabels(ItemStack stack) {
        var rtn  = HashMultimap.<BlockPos, String>create();
        var dict = getLabelDict(stack);
        for (var key : dict.getAllKeys()) {
            getPositions(dict, key).forEach(pos -> rtn.put(pos, key));
        }
        return rtn;
    }

    @NotNull
    private static CompoundTag getLabelDict(ItemStack stack) {
        return stack.getOrCreateTag().getCompound("sfm:labels");
    }

    public static void copyLabels(ItemStack source, ItemStack destination) {
        destination.getOrCreateTag().put("sfm:labels", getLabelDict(source).copy());
    }

    public static void clearLabels(ItemStack gun, BlockPos pos) {
        var dict = getLabelDict(gun);
        for (String key : dict.getAllKeys()) {
            dict.getList(key, Tag.TAG_LONG).removeIf(p -> ((LongTag) p).getAsLong() == pos.asLong());
        }
    }

    /**
     * Offsets all positions in the disk by the given diff
     * Used by game tests to fix label positions since structures use relative positioning
     */
    public static void offsetPositions(ItemStack disk, BlockPos diff) {
        var dict = getLabelDict(disk);
        for (String key : dict.getAllKeys()) {
            var list = dict.getList(key, Tag.TAG_LONG);
            for (int i = 0; i < list.size(); i++) {
                var tag = (LongTag) list.get(i);
                list.set(i, LongTag.valueOf(BlockPos.of(tag.getAsLong()).offset(diff).asLong()));
            }
        }
    }
}
