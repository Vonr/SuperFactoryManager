package ca.teamdman.sfm.common.program;

import ca.teamdman.sfml.ast.DirectionQualifier;
import ca.teamdman.sfml.ast.Matchers;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class InventoryTracker implements Iterable<IItemHandler> {
    private final List<LazyOptional<IItemHandler>> CAPS;
    private final DirectionQualifier               DIRECTIONS;
    private final Matchers                         MATCHERS;

    public InventoryTracker(
            List<LazyOptional<IItemHandler>> containers,
            Matchers matchers,
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

    public Stream<LimitedInputSlot> streamInputSlots() {
        var rtn      = Stream.<LimitedInputSlot>builder();
        var matchers = MATCHERS.createInputMatchers();
        for (var inv : this) {
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                for (var matcher : matchers) {
                    rtn.add(new LimitedInputSlot(inv, slot, matcher));
                }
            }
        }
        return rtn.build();
    }

    public Stream<LimitedOutputSlot> streamOutputSlots() {
        var rtn      = Stream.<LimitedOutputSlot>builder();
        var matchers = MATCHERS.createOutputMatchers();
        for (var inv : this) {
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                for (var matcher : matchers) {
                    rtn.add(new LimitedOutputSlot(inv, slot, matcher));
                }
            }
        }
        return rtn.build();
    }
}
