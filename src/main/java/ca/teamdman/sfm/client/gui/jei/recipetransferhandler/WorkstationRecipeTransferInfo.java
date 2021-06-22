package ca.teamdman.sfm.client.gui.jei.recipetransferhandler;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import java.util.List;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;

public class WorkstationRecipeTransferInfo implements
	IRecipeTransferInfo<WorkstationContainer> {

	@Override
	public Class<WorkstationContainer> getContainerClass() {
		return WorkstationContainer.class;
	}

	@Override
	public ResourceLocation getRecipeCategoryUid() {
		return VanillaRecipeCategoryUid.CRAFTING;
	}

	@Override
	public boolean canHandle(WorkstationContainer container) {
		return true;
	}

	@Override
	public List<Slot> getRecipeSlots(WorkstationContainer workstationContainer) {
		return workstationContainer.inventorySlots.subList(2, 10+1);
	}

	@Override
	public List<Slot> getInventorySlots(WorkstationContainer workstationContainer) {
		return workstationContainer.inventorySlots.subList(56, 91+1);
	}
}
