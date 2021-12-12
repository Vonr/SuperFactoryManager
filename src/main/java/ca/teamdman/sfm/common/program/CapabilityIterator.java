package ca.teamdman.sfm.common.program;

import net.minecraftforge.common.util.LazyOptional;

import java.util.Iterator;
import java.util.List;

public class CapabilityIterator<T> implements Iterator<T> {
    private final List<LazyOptional<T>> CAPS;
    private       int                   index = -1;
    private       T                     next  = null;

    public CapabilityIterator(List<LazyOptional<T>> caps) {
        this.CAPS = caps;
    }

    @Override
    public boolean hasNext() {
        this.next = null;
        var nextIndex = index;
        while (this.next == null) {
            nextIndex++;
            if (nextIndex >= CAPS.size()) return false;
            var next = CAPS.get(nextIndex);
            if (!next.isPresent()) continue;
            this.next = next.orElse(null);
            if (this.next == null) continue;
            return true;
        }
        return false;
    }

    @Override
    public T next() {
        hasNext();
        index++;
        return next;
    }
}
