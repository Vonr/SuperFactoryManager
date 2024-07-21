package ca.teamdman.sfm.common.compat;

import net.minecraft.core.Direction;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SFMCompat {
    public static boolean isMekanismLoaded() {
        return ModList.get().getModContainerById("mekanism").isPresent();
    }

    public static List<BlockCapability<?, @Nullable Direction>> getCapabilities() {
        return List.of(
                Capabilities.ItemHandler.BLOCK,
                Capabilities.FluidHandler.BLOCK,
                Capabilities.EnergyStorage.BLOCK
        );
    }
}
