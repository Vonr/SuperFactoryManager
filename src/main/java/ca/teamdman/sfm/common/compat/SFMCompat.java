package ca.teamdman.sfm.common.compat;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class SFMCompat {
    public static List<Capability<?>> getCapabilities() {
        return List.of(
                ForgeCapabilities.ITEM_HANDLER,
                ForgeCapabilities.FLUID_HANDLER,
                ForgeCapabilities.ENERGY
        );
    }
}
