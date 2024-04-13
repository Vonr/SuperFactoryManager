package ca.teamdman.sfm.common.compat;

import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.common.capabilities.Capability;

import java.util.List;

public class SFMCompat {
    public static boolean isMekanismLoaded() {
        return ModList.get().getModContainerById("mekanism").isPresent();
    }

    public static List<Capability<?>> getCapabilities() {
        return List.of(
                Capabilities.ITEM_HANDLER,
                Capabilities.FLUID_HANDLER,
                Capabilities.ENERGY
        );
    }
}
