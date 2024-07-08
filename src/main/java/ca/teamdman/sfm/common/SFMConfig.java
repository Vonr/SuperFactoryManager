package ca.teamdman.sfm.common;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class SFMConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final SFMConfig.Common COMMON;
    public static final SFMConfig.Client CLIENT;
    static {
        final Pair<SFMConfig.Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(SFMConfig.Common::new);
        COMMON_SPEC = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
        final Pair<SFMConfig.Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(SFMConfig.Client::new);
        CLIENT_SPEC = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();
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
    public static class Client {
        public final ForgeConfigSpec.BooleanValue showLineNumbers;

        Client(ForgeConfigSpec.Builder builder) {
            showLineNumbers = builder
                    .define("showLineNumbers", false);
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
        context.registerConfig(ModConfig.Type.CLIENT, SFMConfig.CLIENT_SPEC);
    }
}
