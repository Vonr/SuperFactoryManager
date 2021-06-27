package ca.teamdman.sfm.client.gui.jei.recipetransferhandler;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationLearnRecipePacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Collections;
import javax.annotation.Nullable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.helpers.IStackHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.transfer.BasicRecipeTransferHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;

public class WorkstationRecipeTransferHandler extends
	BasicRecipeTransferHandler<WorkstationContainer> {

	public WorkstationRecipeTransferHandler(
		IStackHelper stackHelper,
		IRecipeTransferHandlerHelper handlerHelper,
		IRecipeTransferInfo<WorkstationContainer> transferHelper
	) {
		super(
			stackHelper,
			handlerHelper,
			transferHelper
		);
	}

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
		if (doTransfer && Screen.hasControlDown() && recipe instanceof ICraftingRecipe) {
			PacketHandler.INSTANCE.sendToServer(new C2SWorkstationLearnRecipePacket(
				container.containerId,
				container.getSource().getBlockPos(),
				((ICraftingRecipe) recipe).getId()
			));
			return new KeepOpenError();
		}
		if (!doTransfer) {
			return null;
		}
		return super.transferRecipe(
			container,
			recipe,
			recipeLayout,
			player,
			maxTransfer,
			doTransfer
		);
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
				Minecraft.getInstance().getWindow().getGuiScaledWidth(),
				Minecraft.getInstance().getWindow().getGuiScaledHeight(),
				150,
				Minecraft.getInstance().font
			);
		}
	}
}
