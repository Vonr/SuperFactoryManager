package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.IModelData;

import java.util.Random;

public class WorldFlowView implements IFlowView {
	final BlockPos POSITION;
	final World    WORLD;

	public WorldFlowView(World world, BlockPos pos) {
		this.WORLD = world;
		this.POSITION = pos;
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		Minecraft mc = Minecraft.getInstance();
		BufferBuilder           bb    = Tessellator.getInstance().getBuffer();
		BlockRendererDispatcher brd   = Minecraft.getInstance().getBlockRendererDispatcher();
		BlockState              state = WORLD.getBlockState(POSITION);
		IBakedModel model = brd.getModelForState(state);

		ActiveRenderInfo info = new ActiveRenderInfo();
//		info.update(WORLD, Minecraft.getInstance().player, false, false, 0);
		info.update(mc.world, (Entity)(mc.getRenderViewEntity() == null ? mc.player : mc.getRenderViewEntity()), mc.gameSettings.thirdPersonView > 0, mc.gameSettings.thirdPersonView == 2, partialTicks);

		for (BlockRenderLayer layer : BlockRenderLayer.values()) {

			Minecraft.getInstance().worldRenderer.renderBlockLayer(layer, info);
		}
//		brd.renderBlock(state, POSITION, WORLD, bb, new Random(), net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
	}
}
