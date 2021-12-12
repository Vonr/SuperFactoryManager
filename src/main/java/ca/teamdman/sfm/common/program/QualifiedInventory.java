package ca.teamdman.sfm.common.program;

import ca.teamdman.sfm.common.program.LimitedSlot.LimitedExtractionSlot;
import ca.teamdman.sfm.common.program.LimitedSlot.LimitedInsertionSlot;
import ca.teamdman.sfml.ast.DirectionQualifier;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class QualifiedInventory implements Iterable<IItemHandler> {
    private final List<LazyOptional<IItemHandler>> CAPS;
    private final DirectionQualifier               DIRECTIONS;

    public QualifiedInventory(List<LazyOptional<IItemHandler>> containers, DirectionQualifier directions) {
        this.CAPS       = containers;
        this.DIRECTIONS = directions;
    }

    public List<LazyOptional<IItemHandler>> getCaps() {
        return CAPS;
    }

    @NotNull
    @Override
    public Iterator<IItemHandler> iterator() {
        return new CapabilityIterator<>(CAPS);
    }

    public Stream<LimitedExtractionSlot> asInputSlots() {
        var rtn = Stream.<LimitedExtractionSlot>builder();
        for (var inv : this) {
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                rtn.add(new LimitedExtractionSlot(inv, slot, Integer.MAX_VALUE));
            }
        }
        return rtn.build();
    }

    public Stream<LimitedInsertionSlot> asOutputSlots() {
        var rtn = Stream.<LimitedInsertionSlot>builder();
        for (var inv : this) {
            for (int slot = 0; slot < inv.getSlots(); slot++) {
                rtn.add(new LimitedInsertionSlot(inv, slot, Integer.MAX_VALUE));
            }
        }
        return rtn.build();
    }
}
