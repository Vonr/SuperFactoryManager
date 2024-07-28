package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = SFM.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class SFMDatagen {
    @SubscribeEvent
    public static void onGather(GatherDataEvent event) {
        if (!DatagenModLoader.isRunningDataGen()) return;
        if (event.includeServer()) {
            event.getGenerator().addProvider(event.includeClient(), new SFMBlockStatesAndModels(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMItemModels(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMBlockTags(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMLootTables(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMRecipes(event));
            event.getGenerator().addProvider(event.includeClient(), new SFMLanguageProvider(event));
        }
    }
}
