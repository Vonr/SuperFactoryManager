package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.DirectionQualifier;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class InventoryTracker implements Iterable<IItemHandler> {
    private final List<LazyOptional<IItemHandler>> CAPS;
    private final DirectionQualifier               DIRECTIONS;
    private final List<ItemMatcher>                MATCHERS;

    public InventoryTracker(
            List<LazyOptional<IItemHandler>> containers,
            List<ItemMatcher> matchers,
            DirectionQualifier directions
    ) {
        this.CAPS       = containers;
        this.DIRECTIONS = directions;
        this.MATCHERS   = matchers;
    }

    @NotNull
    @Override
    public Iterator<IItemHandler> iterator() {
        return new CapabilityIterator<>(CAPS);
    }

    public Stream<LimitedSlot> streamSlots() {
        var rtn = Stream.<LimitedSlot>builder();
        for (var inv : this) {
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                for (var matcher : MATCHERS) {
                    rtn.add(new LimitedSlot(inv, slot, matcher));
                }
            }
        }
        return rtn.build();
    }
}
