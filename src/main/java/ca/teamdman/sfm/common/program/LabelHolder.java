package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfml.ast.Label;
import ca.teamdman.sfml.ast.LabelAccess;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("UnusedReturnValue")
public class LabelHolder {
    private final Map<String, List<BlockPos>> LABELS = new HashMap<>();

    private LabelHolder() {

    }

    public static LabelHolder empty() {
        return new LabelHolder();
    }

    public static LabelHolder from(ItemStack stack) {
        var labels = LabelHolder.empty();
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

    public Stream<BlockPos> getPositions(LabelAccess access) {
        return access.labels().stream()
                .map(Label::name)
                .map(this::getPositions)
                .flatMap(Collection::stream);
    }

    public boolean contains(String label, BlockPos pos) {
        return LABELS.getOrDefault(label, Collections.emptyList()).contains(pos);
    }

    public LabelHolder toggle(String label, BlockPos pos) {
        if (contains(label, pos)) {
            remove(label, pos);
        } else {
            add(label, pos);
        }
        return this;
    }

    public Map<String, List<BlockPos>> get() {
        return LABELS;
    }

    public List<BlockPos> getPositions(String label) {
        return LABELS.computeIfAbsent(label, s -> new ArrayList<>());
    }

    public LabelHolder addAll(String label, List<BlockPos> positions) {
        getPositions(label).addAll(positions);
        return this;
    }

    public LabelHolder addReferencedLabel(String label) {
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

    public LabelHolder remove(BlockPos value) {
        LABELS.values().forEach(list -> list.remove(value));
        return this;
    }

    public LabelHolder prune() {
        LABELS.entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return this;
    }

    public LabelHolder clear() {
        LABELS.clear();
        return this;
    }

    public LabelHolder add(String label, BlockPos position) {
        getPositions(label).add(position);
        return this;
    }

    public LabelHolder remove(String label, BlockPos pos) {
        getPositions(label).remove(pos);
        return this;
    }

    public LabelHolder save(ItemStack stack) {
        stack.getOrCreateTag().put("sfm:labels", serialize());
        return this;
    }

    public LabelHolder removeIf(BiPredicate<String, BlockPos> predicate) {
        LABELS.forEach((key, value) -> value.removeIf(pos -> predicate.test(key, pos)));
        return this;
    }

    public LabelHolder forEach(BiConsumer<String, BlockPos> consumer) {
        LABELS.forEach((key, value) -> value.forEach(pos -> consumer.accept(key, pos)));
        return this;
    }
}
