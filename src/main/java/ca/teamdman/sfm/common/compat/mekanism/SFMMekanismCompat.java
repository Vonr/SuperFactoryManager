package ca.teamdman.sfm.common.compat.mekanism;

import ca.teamdman.sfm.common.resourcetype.*;
import net.minecraftforge.registries.DeferredRegister;

public class SFMMekanismCompat {
    public static void register(DeferredRegister<ResourceType<?, ?, ?>> types) {
        types.register(
                "gas",
                GasResourceType::new
        );
        types.register(
                "infusion",
                InfuseResourceType::new
        );

        types.register(
                "pigment",
                PigmentResourceType::new
        );
        types.register(
                "slurry",
                SlurryResourceType::new
        );
    }
}
