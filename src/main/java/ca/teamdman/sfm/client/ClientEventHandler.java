package ca.teamdman.sfm.client;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.model.BakedModelDelegate;
import ca.teamdman.sfm.common.item.CraftingContractItemStackTileEntityRenderer;
import ca.teamdman.sfm.common.registrar.SFMItems;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = SFM.MOD_ID, bus = Bus.MOD)
public class ClientEventHandler {

	@SubscribeEvent
	public static void onModelBake(ModelBakeEvent event) {
		event.getModelRegistry().computeIfPresent(
			new ModelResourceLocation(
				SFMItems.CRAFTING_CONTRACT.getId().toString() + "#inventory"),
			(key, value) -> new BakedModelDelegate(value) {
				@Override
				public boolean isCustomRenderer() {
					// use ISTSR when holding shift to show the recipe output
					return !CraftingContractItemStackTileEntityRenderer.debounce;
				}
			}
		);
	}
}
