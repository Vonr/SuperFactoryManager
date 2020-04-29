package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowIconButton;
import ca.teamdman.sfm.common.container.ManagerContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.manager.ButtonPositionPacketC2S;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public class ManagerFlowController implements IFlowController, IFlowView {
	private static final ItemStack        asd    = new ItemStack(Blocks.PUMPKIN);
	public final         ManagerContainer CONTAINER;
	public               FlowIconButton   button = new FlowIconButton(FlowIconButton.ButtonLabel.INPUT) {
		@Override
		public void onPositionChanged() {
			PacketHandler.INSTANCE.sendToServer(new ButtonPositionPacketC2S(
					CONTAINER.windowId,
					CONTAINER.getSource().getPos(),
					0,
					this.getX(),
					this.getY()));
		}
	};

	public ManagerFlowController(ManagerContainer container) {
		this.CONTAINER = container;
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return this.button.mouseClicked(screen, mx, my, button);
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		return this.button.mouseReleased(screen, mx, my, button);
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return this.button.mouseDragged(screen, mx, my, button, dmx, dmy);
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void load() {
		this.button.setXY(CONTAINER.x, CONTAINER.y);
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		button.draw(screen, mx, my, deltaTime);
		RenderHelper.disableStandardItemLighting();
		RenderHelper.enableGUIStandardItemLighting();
		screen.getItemRenderer().renderItemAndEffectIntoGUI(asd, 25, 25);
		BufferBuilder           bb            = Tessellator.getInstance().getBuffer();
		BlockRendererDispatcher blockRenderer = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockState              state         = Blocks.PUMPKIN.getDefaultState();
		//Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(Blocks.PUMPKIN.getDefaultState())
		IBakedModel             model         = blockRenderer.getBlockModelShapes().getModel(state);
		World                   world         = CONTAINER.getSource().getWorld();

		IModelData data = model.getModelData(world,
				BlockPos.ZERO,
				state,
				ModelDataManager.getModelData(
						world,
						CONTAINER.getSource().getPos()));
		Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(
				world,
				model,
				state,
				BlockPos.ZERO,
				bb,
				true,
				new Random(),
				42,
				data
				);
		//		Minecraft.getInstance().getBlockRendererDispatcher().renderBlockBrightness(Blocks.PUMPKIN.getDefaultState(), 1);
		//		Minecraft.getInstance().worldRenderer.
		RenderHelper.enableStandardItemLighting();
	}
}
