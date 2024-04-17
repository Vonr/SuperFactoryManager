package ca.teamdman.sfm.common;


import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class SFMConfig {
    public static final ModConfigSpec COMMON_SPEC;
    public static final SFMConfig.Common COMMON;
    static {
        final Pair<Common, ModConfigSpec> commonSpecPair = new ModConfigSpec.Builder().configure(SFMConfig.Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }

    public static class Common {
        public final ModConfigSpec.IntValue timerTriggerMinimumIntervalInTicks;
        public final ModConfigSpec.IntValue timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO;

        Common(ModConfigSpec.Builder builder) {
            timerTriggerMinimumIntervalInTicks = builder
                    .defineInRange("timerTriggerMinimumIntervalInTicks", 20, 1, Integer.MAX_VALUE);
            timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO = builder
                    .defineInRange("timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIOStatementsPresent", 1, 1, Integer.MAX_VALUE);
        }
    }

    /**
     * Get a config value in a way that doesn't fail when running tests
     */
    public static <T> T getOrDefault(ModConfigSpec.ConfigValue<T> configValue) {
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
