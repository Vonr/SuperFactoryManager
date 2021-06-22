package ca.teamdman.sfm.client.gui.jei;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.jei.guihandler.WorkstationGuiContainerHandler;
import ca.teamdman.sfm.client.gui.jei.recipetransferhandler.WorkstationRecipeTransferInfo;
import ca.teamdman.sfm.client.gui.screen.WorkstationScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

	private static final ResourceLocation PLUGIN_ID = new ResourceLocation(
		SFM.MOD_ID,
		"main"
	);

	@Override
	public ResourceLocation getPluginUid() {
		return PLUGIN_ID;
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addGuiContainerHandler(
			WorkstationScreen.class,
			new WorkstationGuiContainerHandler()
		);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
//		registration.addRecipeTransferHandler(
//			new WorkstationRecipeTransferHandler(),
//			VanillaRecipeCategoryUid.CRAFTING
//		);
		registration.addRecipeTransferHandler(new WorkstationRecipeTransferInfo());
	}
}
