package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = SFM.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SFMDatagen {
    @SubscribeEvent
    public static void onGather(GatherDataEvent event) {
        if (!DatagenModLoader.isRunningDataGen()) return;
        var gen = event.getGenerator();
        if (event.includeServer()) {
            gen.addProvider(new SFMBlockStates(gen, event.getExistingFileHelper()));
            gen.addProvider(new SFMItemModels(gen, event.getExistingFileHelper()));
            gen.addProvider(new SFMBlockTags(gen, event.getExistingFileHelper()));
            gen.addProvider(new SFMLootTables(gen));
            gen.addProvider(new SFMRecipes(gen));
        }
    }
}
