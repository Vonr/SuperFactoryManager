package ca.teamdman.sfm.common.program;

import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import java.util.List;

public class ItemOutput {
    private final List<LazyOptional<IItemHandler>> CAPS;

    public ItemOutput(List<LazyOptional<IItemHandler>> containers) {
        this.CAPS = containers;
    }

    public void tick(ProgramContext context) {
        for (var input : context.getInputs()) {
            for (LazyOptional<IItemHandler> inputCap : input.getCaps()) {
                if (!inputCap.isPresent()) continue;
                var inputItemHandler = inputCap.orElse(null);
                if (inputItemHandler == null) continue;
                pull:
                for (int inSlot = 0; inSlot < inputItemHandler.getSlots(); inSlot++) {
                    var potential = inputItemHandler.extractItem(inSlot, 64, false);
                    for (LazyOptional<IItemHandler> outputCap : CAPS) {
                        if (!outputCap.isPresent()) continue;
                        var outputItemHandler = outputCap.orElse(null);
                        if (outputItemHandler == null) continue;
                        for (int outSlot = 0; outSlot < outputItemHandler.getSlots(); outSlot++) {
                            if (potential.isEmpty()) continue pull;
                            potential = outputItemHandler.insertItem(outSlot, potential, false);
                        }
                    }
                    inputItemHandler.insertItem(inSlot, potential, false);
                }
            }
        }
    }
}
