package ca.teamdman.sfm.datagen;

import ca.teamdman.sfm.SFM;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DatagenModLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = Bus.MOD)
public class SFMDatagen {

	@SubscribeEvent
	public static void onGather(GatherDataEvent event) {
		if (!DatagenModLoader.isRunningDataGen()) return;
		DataGenerator generator = event.getGenerator();
		if (event.includeServer()) {
			generator.addProvider(new LootTables(generator));
			generator.addProvider(new BlockStates(
				generator,
				event.getExistingFileHelper()
			));
			generator.addProvider(new ItemModels(
				generator,
				event.getExistingFileHelper()
			));
			generator.addProvider(new Recipes(generator));
		}

	}
}
