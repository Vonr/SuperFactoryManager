package ca.teamdman.sfm.common.config;

import ca.teamdman.sfm.SFM;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

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

    @Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class Common {
        private int revision = 0;
        public final ForgeConfigSpec.BooleanValue disableProgramExecution;
        public final ForgeConfigSpec.IntValue timerTriggerMinimumIntervalInTicks;
        public final ForgeConfigSpec.IntValue timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO;
        public final ForgeConfigSpec.IntValue maxIfStatementsInTriggerBeforeSimulationIsntAllowed;
        public final ForgeConfigSpec.ConfigValue<List<?  extends String>> disallowedResourceTypesForTransfer;
        public final ForgeConfigSpec.EnumValue<LevelsToShards> levelsToShards;

        public enum LevelsToShards {
            JustOne,
            EachOne,
            SumLevels,
            SumLevelsScaledExponentially,
        }

        Common(ForgeConfigSpec.Builder builder) {
            builder.comment("This config is shown to clients, don't put anything secret in here");
            disableProgramExecution = builder
                    .comment("Prevents factory managers from compiling and running code (for emergencies)")
                    .define("disableProgramExecution", false);
            timerTriggerMinimumIntervalInTicks = builder
                    .defineInRange("timerTriggerMinimumIntervalInTicks", 20, 1, Integer.MAX_VALUE);
            timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIO = builder
                    .defineInRange(
                            "timerTriggerMinimumIntervalInTicksWhenOnlyForgeEnergyIOStatementsPresent",
                            1,
                            1,
                            Integer.MAX_VALUE
                    );
            maxIfStatementsInTriggerBeforeSimulationIsntAllowed = builder
                    .comment(
                            "The number of scenarios to check is 2^n where n is the number of if statements in a trigger")
                    .defineInRange("maxIfStatementsInTriggerBeforeSimulationIsntAllowed", 10, 0, Integer.MAX_VALUE);
            disallowedResourceTypesForTransfer = builder
                    .comment("What resource types should SFM not be allowed to move")
                    .defineListAllowEmpty(
                            List.of("disallowedResourceTypesForTransfer"),
                            List::of,
                            String.class::isInstance
                    );
            levelsToShards = builder
                    .comment("How to convert Enchanted Books to Experience Shards", "JustOne = always produces 1 shard regardless of enchantments", "EachOne = produces 1 shard per enchantment on the book.", "SumLevels = produces a number of shards equal to the sum of the enchantments' levels", "SumLevelsScaledExponentially = produces a number of shards equal to the sum of 2 to the power of each enchantment's level (1 -> 1 shard, 2 -> 4 shards, 3 -> 8 shards, etc)")
                    .defineEnum("levelsToShards", LevelsToShards.JustOne);
        }

        public int getRevision() {
            return revision;
        }

        @SubscribeEvent
        public static void onConfigChanged(ModConfigEvent.Reloading event) {
            if (event.getConfig().getSpec() == COMMON_SPEC) {
                SFMConfig.COMMON.revision++;
                SFM.LOGGER.info("SFM config reloaded, now on revision {}", SFMConfig.COMMON.revision);
            }
        }
    }

    public static class Client {
        public final ForgeConfigSpec.BooleanValue showLineNumbers;

        Client(ForgeConfigSpec.Builder builder) {
            showLineNumbers = builder
                    .define("showLineNumbers", false);
        }
    }
}
