package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.Constants;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public class LabelPositionHolder {
    private final Map<String, Set<BlockPos>> LABELS = new HashMap<>();

    private LabelPositionHolder() {

    }

    public static LabelPositionHolder empty() {
        return new LabelPositionHolder();
    }

    public static LabelPositionHolder from(ItemStack stack) {
        var labels = LabelPositionHolder.empty();
        var tag = stack.getOrCreateTag().getCompound("sfm:labels");
        for (var label : tag.getAllKeys()) {
            // old: storing BlockPos as long
            labels.addAll(label, tag.getList(label, Tag.TAG_LONG).stream()
                    .map(LongTag.class::cast)
                    .mapToLong(LongTag::getAsLong)
                    .mapToObj(BlockPos::of).collect(Collectors.toList()));

            // new: storing BlockPos as compound
            labels.addAll(label, tag.getList(label, Tag.TAG_COMPOUND).stream()
                    .map(CompoundTag.class::cast)
                    .map(NbtUtils::readBlockPos)
                    .collect(Collectors.toList()));
        }
        return labels;
    }

    public CompoundTag serialize() {
        var tag = new CompoundTag();
        for (var label : get().keySet()) {
            var list = new ListTag();
            list.addAll(LABELS.get(label)
                                .stream()
                                .map(NbtUtils::writeBlockPos)
                                .toList());
            tag.put(label, list);
        }
        return tag;
    }

    public boolean contains(String label, BlockPos pos) {
        return LABELS.getOrDefault(label, Collections.emptySet()).contains(pos);
    }

    public LabelPositionHolder toggle(String label, BlockPos pos) {
        if (contains(label, pos)) {
            remove(label, pos);
        } else {
            add(label, pos);
        }
        return this;
    }

    public Map<String, Set<BlockPos>> get() {
        return LABELS;
    }

    public Set<BlockPos> getPositions(String label) {
        return LABELS.computeIfAbsent(label, s -> new HashSet<>());
    }

    public LabelPositionHolder addAll(String label, Collection<BlockPos> positions) {
        getPositions(label).addAll(positions);
        return this;
    }

    public LabelPositionHolder addReferencedLabel(String label) {
        getPositions(label);
        return this;
    }

    public List<Component> asHoverText() {
        var rtn = new ArrayList<Component>();
        if (LABELS.isEmpty()) return rtn;
        rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL_HEADER
                        .getComponent()
                        .withStyle(ChatFormatting.UNDERLINE));
        for (var entry : LABELS.entrySet()) {
            rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL.getComponent(
                    entry.getKey(),
                    entry.getValue().size()
            ).withStyle(ChatFormatting.GRAY));
        }
        return rtn;
    }

    public LabelPositionHolder remove(BlockPos value) {
        LABELS.values().forEach(list -> list.remove(value));
        return this;
    }

    public LabelPositionHolder prune() {
        LABELS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return this;
    }

    public LabelPositionHolder clear() {
        LABELS.clear();
        return this;
    }

    public LabelPositionHolder add(String label, BlockPos position) {
        getPositions(label).add(position);
        return this;
    }

    public LabelPositionHolder remove(String label, BlockPos pos) {
        getPositions(label).remove(pos);
        return this;
    }

    public LabelPositionHolder save(ItemStack stack) {
        stack.getOrCreateTag().put("sfm:labels", serialize());
        return this;
    }

    public LabelPositionHolder removeIf(BiPredicate<String, BlockPos> predicate) {
        LABELS.forEach((key, value) -> value.removeIf(pos -> predicate.test(key, pos)));
        return this;
    }

    public LabelPositionHolder removeIf(Predicate<String> predicate) {
        LABELS.keySet().removeIf(predicate);
        return this;
    }

    public LabelPositionHolder forEach(BiConsumer<String, BlockPos> consumer) {
        LABELS.forEach((key, value) -> value.forEach(pos -> consumer.accept(key, pos)));
        return this;
    }
}
