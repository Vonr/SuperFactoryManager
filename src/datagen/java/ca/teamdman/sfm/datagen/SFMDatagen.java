package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMDatagen {
    @SubscribeEvent
    public static void onGather(GatherDataEvent event) {
        if (!DatagenModLoader.isRunningDataGen()) return;
        var gen = event.getGenerator();
        if (event.includeServer()) {
            gen.addProvider(
                    event.includeClient(),
                    new SFMBlockStates(gen.getPackOutput(), event.getExistingFileHelper())
            );
            gen.addProvider(
                    event.includeClient(),
                    new SFMItemModels(gen.getPackOutput(), event.getExistingFileHelper())
            );
            gen.addProvider(
                    event.includeClient(),
                    new SFMBlockTags(gen.getPackOutput(),
                                     event.getLookupProvider(),
                                     event.getExistingFileHelper()
                    )
            );
            gen.addProvider(event.includeClient(), new SFMLootTables(gen.getPackOutput()));
            gen.addProvider(event.includeClient(), new SFMRecipes(gen.getPackOutput()));
        }
    }
}
