package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.Constants;
import ca.teamdman.sfm.common.registry.SFMDataComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("UnusedReturnValue")
public record LabelPositionHolder(Map<String, HashSet<BlockPos>> labels) {
    public static final StreamCodec<FriendlyByteBuf, LabelPositionHolder> STREAM_CODEC = StreamCodec.ofMember(
            LabelPositionHolder::encode,
            LabelPositionHolder::decode
    );
    public static final MapCodec<LabelPositionHolder> CODEC =
            RecordCodecBuilder.mapCodec(
                    builder -> builder.group(
                            Codec.unboundedMap(
                                    Codec.STRING,
                                    BlockPos.CODEC.listOf().xmap(HashSet::new, ArrayList::new)
                            ).fieldOf("labels").forGetter(LabelPositionHolder::labels)
                    ).apply(builder, LabelPositionHolder::new)
            );


    private final static WeakHashMap<ItemStack, LabelPositionHolder> CACHE = new WeakHashMap<>();

    private LabelPositionHolder() {
        this(new HashMap<>());
    }

    private LabelPositionHolder(LabelPositionHolder other) {
        this();
        other.labels().forEach((key, value) -> this.labels().put(key, new HashSet<>(value)));
    }

    public static void encode(
            LabelPositionHolder labelPositionHolder,
            FriendlyByteBuf friendlyByteBuf
    ) {
        friendlyByteBuf.writeVarInt(labelPositionHolder.labels().size());
        for (Map.Entry<String, ? extends Set<BlockPos>> entry : labelPositionHolder.labels().entrySet()) {
            String label = entry.getKey();
            Set<BlockPos> positions = entry.getValue();
            friendlyByteBuf.writeUtf(label);
            friendlyByteBuf.writeVarInt(positions.size());
            positions.forEach(friendlyByteBuf::writeBlockPos);
        }
    }

    public static LabelPositionHolder decode(FriendlyByteBuf friendlyByteBuf) {
        LabelPositionHolder rtn = LabelPositionHolder.empty();
        int size = friendlyByteBuf.readVarInt();
        for (int i = 0; i < size; i++) {
            String label = friendlyByteBuf.readUtf();
            int positionsSize = friendlyByteBuf.readVarInt();
            HashSet<BlockPos> positions = new HashSet<>();
            for (int j = 0; j < positionsSize; j++) {
                positions.add(friendlyByteBuf.readBlockPos());
            }
            rtn.labels().put(label, positions);
        }
        return rtn;
    }

    /**
     * Get the label position holder for this disk.
     * <p>
     * Saves it in the cache for faster future lookups.
     * <p>
     * This mutably borrows the cache entry.
     */
    public static LabelPositionHolder from(ItemStack stack) {
        return CACHE.computeIfAbsent(stack,
                                     s -> stack.getOrDefault(
                                             SFMDataComponents.LABEL_POSITION_HOLDER,
                                             new LabelPositionHolder()
                                     )
        );
    }

    public static LabelPositionHolder empty() {
        return new LabelPositionHolder();
    }

    public LabelPositionHolder save(ItemStack stack) {
        LabelPositionHolder copy = new LabelPositionHolder(this);
        stack.set(SFMDataComponents.LABEL_POSITION_HOLDER, copy);
        CACHE.put(stack, copy);
        return this;
    }

    public static void purge(ItemStack stack) {
        stack.remove(SFMDataComponents.LABEL_POSITION_HOLDER);
        CACHE.remove(stack);
    }

    public boolean contains(
            String label,
            BlockPos pos
    ) {
        HashSet<BlockPos> positionsForLabel = this.labels().get(label);
        if (positionsForLabel == null) {
            return false;
        } else {
            return positionsForLabel.contains(pos);
        }
    }

    public LabelPositionHolder toggle(
            String label,
            BlockPos pos
    ) {
        if (contains(label, pos)) {
            remove(label, pos);
        } else {
            add(label, pos);
        }
        return this;
    }

    public Set<BlockPos> getPositions(String label) {
        return labels().getOrDefault(label, new HashSet<>());
    }

    public Set<BlockPos> getPositionsMut(String label) {
        return labels().computeIfAbsent(label, s -> new HashSet<>());
    }

    public LabelPositionHolder addAll(
            String label,
            Collection<BlockPos> positions
    ) {
        getPositionsMut(label).addAll(positions);
        return this;
    }

    public LabelPositionHolder addReferencedLabel(String label) {
        getPositionsMut(label);
        return this;
    }

    public List<Component> asHoverText() {
        var rtn = new ArrayList<Component>();
        if (labels().isEmpty()) return rtn;
        rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL_HEADER
                        .getComponent()
                        .withStyle(ChatFormatting.UNDERLINE));
        for (var entry : labels().entrySet()) {
            rtn.add(Constants.LocalizationKeys.DISK_ITEM_TOOLTIP_LABEL.getComponent(
                    entry.getKey(),
                    entry.getValue().size()
            ).withStyle(ChatFormatting.GRAY));
        }
        return rtn;
    }

    public LabelPositionHolder remove(BlockPos value) {
        labels().values().forEach(list -> list.remove(value));
        return this;
    }

    public LabelPositionHolder prune() {
        labels().entrySet().removeIf(entry -> entry.getValue().isEmpty());
        return this;
    }

    public LabelPositionHolder clear() {
        labels().clear();
        return this;
    }

    public LabelPositionHolder add(
            String label,
            BlockPos position
    ) {
        getPositionsMut(label).add(position);
        return this;
    }

    public LabelPositionHolder remove(
            String label,
            BlockPos pos
    ) {
        getPositionsMut(label).remove(pos);
        return this;
    }

    public LabelPositionHolder removeIf(BiPredicate<String, BlockPos> predicate) {
        labels().forEach((key, value) -> value.removeIf(pos -> predicate.test(key, pos)));
        return this;
    }

    public LabelPositionHolder removeIf(Predicate<String> predicate) {
        labels().keySet().removeIf(predicate);
        return this;
    }

    public LabelPositionHolder forEach(BiConsumer<String, BlockPos> consumer) {
        labels().forEach((key, value) -> value.forEach(pos -> consumer.accept(key, pos)));
        return this;
    }

    @Override
    public String toString() {
        return "LabelPositionHolder{size=" + labels().values().stream().mapToInt(Set::size).sum() + "; " +
               labels()
                       .entrySet()
                       .stream()
                       .map(entry -> entry.getKey() + "=" + entry.getValue().size())
                       .collect(Collectors.joining(", ")) +
               "}";
    }

    public LabelPositionHolder toOwned() {
        return new LabelPositionHolder(this);
    }
}
