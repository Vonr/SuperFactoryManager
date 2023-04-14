package ca.teamdman.sfm.common.registry.compat;

import ca.teamdman.sfm.common.resourcetype.*;
import net.minecraftforge.registries.DeferredRegister;

public class SFMMekanismCompat {
    public static void register(DeferredRegister<ResourceType<?, ?, ?>> types) {
        var gases = types.register(
                "gas",
                GasResourceType::new
        );
        var infusions = types.register(
                "infusion",
                InfuseResourceType::new
        );

        var pigments = types.register(
                "pigment",
                PigmentResourceType::new
        );

        var slurries = types.register(
                "slurry",
                SlurryResourceType::new
        );
    }
}
