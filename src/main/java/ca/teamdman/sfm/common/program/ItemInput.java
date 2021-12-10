package ca.teamdman.sfm.common.program;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class ItemInput {
    private final List<LazyOptional<IItemHandler>> CAPS;

    public ItemInput(List<LazyOptional<IItemHandler>> containers) {
        this.CAPS = containers;
    }

    public List<LazyOptional<IItemHandler>> getCaps() {
        return CAPS;
    }
}
