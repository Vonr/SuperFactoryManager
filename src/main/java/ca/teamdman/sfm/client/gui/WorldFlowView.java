package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.AbstractChunkRenderContainer;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WorldFlowView implements IFlowView {

	final BlockPos POSITION;
	final World WORLD;

	public WorldFlowView(World world, BlockPos pos) {
		this.WORLD = world;
		this.POSITION = pos;
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		Minecraft mc = Minecraft.getInstance();
		GameRenderer gr = mc.gameRenderer;
		ActiveRenderInfo ar = gr.getActiveRenderInfo();
		WorldRenderer wr = mc.worldRenderer;

		GlStateManager.enableCull();

		{ // setup camera
			float farPlaneDistance = (float) (mc.gameSettings.renderDistanceChunks * 16);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
//			if (this.cameraZoom != 1.0D) {
//				GlStateManager.translatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
//				GlStateManager.scaled(this.cameraZoom, this.cameraZoom, 1.0D);
//			}
//			GlStateManager.translatef(0.3f,0,0);
			GlStateManager.multMatrix(Matrix4f
				.perspective(120, (float) mc.mainWindow.getFramebufferWidth() / (float) mc.mainWindow
					.getFramebufferHeight(), 0.05F, farPlaneDistance * MathHelper.SQRT_2));
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();
		}
		{ // clear screen
			GlStateManager.viewport(0, 0, mc.mainWindow.getFramebufferWidth(),
				mc.mainWindow.getFramebufferHeight());
			GlStateManager.clear(16640, Minecraft.IS_RUNNING_ON_MAC);
		}

		ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
		ICamera cam = new Frustum(clippingHelper);
		double d0 = ar.getProjectedView().x;
		double d1 = ar.getProjectedView().y;
		double d2 = ar.getProjectedView().z;
		cam.setPosition(d0, d1, d2);

		mc.getTextureManager().bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();

//		wr.setupTerrain(ar, cam, 1, mc.player.isSpectator());

//		wr.updateChunks(0);

		{ // terrain
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			GlStateManager.disableAlphaTest();
			wr.renderBlockLayer(BlockRenderLayer.SOLID, ar);
			GlStateManager.enableAlphaTest();
			mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.setBlurMipmap(false,
					mc.gameSettings.mipmapLevels
						> 0); // FORGE: fix flickering leaves when mods mess up the blurMipmap settings
			wr.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, ar);
			{
				AbstractChunkRenderContainer crc = new RenderList();
				crc.addRenderChunk();
			}
			mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.restoreLastBlurMipmap();
			mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.setBlurMipmap(false, false);
			wr.renderBlockLayer(BlockRenderLayer.CUTOUT, ar);
			mc.getTextureManager().getTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE)
				.restoreLastBlurMipmap();
			GlStateManager.shadeModel(7424);
			GlStateManager.alphaFunc(516, 0.1F);
			GlStateManager.matrixMode(5888);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			RenderHelper.enableStandardItemLighting();
		}
	}
}
