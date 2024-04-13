package ca.teamdman.sfm.common.compat;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import net.minecraftforge.fml.ModList;
import java.util.List;
import net.neoforged.fml.ModList;

public class SFMCompat {
    public static boolean isMekanismLoaded() {
        return ModList.get().getModContainerById("mekanism").isPresent();
    }

    public static List<Capability<?>> getCapabilities() {
        return List.of(
                ForgeCapabilities.ITEM_HANDLER,
                ForgeCapabilities.FLUID_HANDLER,
                ForgeCapabilities.ENERGY
        );
    }
}
