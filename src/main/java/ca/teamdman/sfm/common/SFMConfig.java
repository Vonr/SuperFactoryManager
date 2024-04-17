package ca.teamdman.sfm.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class SFMConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final SFMConfig.Common COMMON;
    static {
        final Pair<SFMConfig.Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(SFMConfig.Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }

    public static class Common {
        public final ForgeConfigSpec.IntValue timerTriggerMinimumIntervalInTicks;
        public final ForgeConfigSpec.IntValue timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO;

        Common(ForgeConfigSpec.Builder builder) {
            timerTriggerMinimumIntervalInTicks = builder
                    .defineInRange("timerTriggerMinimumIntervalInTicks", 20, 1, Integer.MAX_VALUE);
            timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO = builder
                    .defineInRange("timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIOStatementsPresent", 1, 1, Integer.MAX_VALUE);
        }
    }

    /**
     * Get a config value in a way that doesn't fail when running tests
     */
    public static <T> T getOrDefault(ForgeConfigSpec.ConfigValue<T> configValue) {
        try {
            return configValue.get();
        } catch (Exception e) {
            return configValue.getDefault();
        }
    }

    public static void register(ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, SFMConfig.COMMON_SPEC);
    }
}
