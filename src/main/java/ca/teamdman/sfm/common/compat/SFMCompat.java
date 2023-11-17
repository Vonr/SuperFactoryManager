package ca.teamdman.sfm.common.compat;

import ca.teamdman.sfm.common.resourcetype.GasResourceType;
import ca.teamdman.sfm.common.resourcetype.InfuseResourceType;
import ca.teamdman.sfm.common.resourcetype.PigmentResourceType;
import ca.teamdman.sfm.common.resourcetype.SlurryResourceType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class SFMCompat {
    public static boolean isMekanismLoaded() {
        return ModList.get().getModContainerById("mekanism").isPresent();
    }

    public static List<Capability<?>> getCapabilities() {
        if (isMekanismLoaded()) {
            return List.of(
                    ForgeCapabilities.ITEM_HANDLER,
                    ForgeCapabilities.FLUID_HANDLER,
                    ForgeCapabilities.ENERGY,
                    GasResourceType.CAP,
                    InfuseResourceType.CAP,
                    PigmentResourceType.CAP,
                    SlurryResourceType.CAP
            );
        } else {
            return List.of(
                    ForgeCapabilities.ITEM_HANDLER,
                    ForgeCapabilities.FLUID_HANDLER,
                    ForgeCapabilities.ENERGY
            );
        }
    }
}
