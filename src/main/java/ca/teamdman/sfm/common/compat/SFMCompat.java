package ca.teamdman.sfm.common.compat;

import net.minecraftforge.fml.ModList;

public class SFMCompat {
    public static boolean isMekanismLoaded() {
        return ModList.get().getModContainerById("mekanism").isPresent();
    }
}
