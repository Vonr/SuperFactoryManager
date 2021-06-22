package ca.teamdman.sfm.client.gui.jei.recipetransferhandler;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class WorkstationRecipeTransferHandler implements
	IRecipeTransferHandler<WorkstationContainer> {

	@Override
	public Class<WorkstationContainer> getContainerClass() {
		return WorkstationContainer.class;
	}

	@Nullable
	@Override
	public IRecipeTransferError transferRecipe(
		WorkstationContainer container,
		Object recipe,
		IRecipeLayout recipeLayout,
		PlayerEntity player,
		boolean maxTransfer,
		boolean doTransfer
	) {
		if (Screen.hasControlDown()) {
			System.out.println("Learning!");
			return new KeepOpenError();
		} else {
//			return null;
		}
		System.out.println(recipe.getClass().getName());
		return null;
	}

	private static class KeepOpenError implements IRecipeTransferError {

		@Override
		public Type getType() {
			return Type.USER_FACING;
		}

		@Override
		public void showError(
			MatrixStack matrixStack,
			int mouseX,
			int mouseY,
			IRecipeLayout recipeLayout,
			int recipeX,
			int recipeY
		) {
			GuiUtils.drawHoveringText(
				matrixStack,
				Collections.singletonList(new TranslationTextComponent(
					"gui.sfm.tooltip.workstation.learned"
				)),
				mouseX,
				mouseY,
				Minecraft.getInstance().getMainWindow().getScaledWidth(),
				Minecraft.getInstance().getMainWindow().getScaledHeight(),
				150,
				Minecraft.getInstance().fontRenderer
			);
		}
	}
}
