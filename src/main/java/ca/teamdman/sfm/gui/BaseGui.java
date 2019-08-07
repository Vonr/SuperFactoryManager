package ca.teamdman.sfm.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

/**
 * Credit to VSWE for lots of the rendering scaling tech
 */
public abstract class BaseGui extends Screen {
	protected int zLevel = 0;
	protected int guiLeft = 0;
	protected int guiTop = 0;
	protected int xSize = 176;
	protected int ySize = 166;

	public BaseGui(ITextComponent titleIn, int width, int height) {
		super(titleIn);
		this.width = width;
		this.height = height;
	}


	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.xSize)/2;
		this.guiTop = (this.height - this.ySize)/2;
	}

	/**
	 * Scales and draws the currently bound texture.
	 */
	protected void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
		double scale = getScale();

		drawScaleFriendlyTexture(
				fixScaledCoordinate(guiLeft + x, scale, minecraft.mainWindow.getWidth()),
				fixScaledCoordinate(guiTop + y, scale, minecraft.mainWindow.getHeight()),
				fixScaledCoordinate(srcX, scale, 256),
				fixScaledCoordinate(srcY, scale, 256),
				fixScaledCoordinate(w, scale, minecraft.mainWindow.getWidth()),
				fixScaledCoordinate(h, scale, minecraft.mainWindow.getHeight())
		);
	}

	/**
	 * Draws the sprite, does not bind.
	 */
	protected void drawSprite(int x, int y, ISprite sprite) {
		drawTexture(x, y, sprite.getLeft(), sprite.getTop(), sprite.getWidth(), sprite.getHeight());
	}

	private double fixScaledCoordinate(int val, double scale, int size) {
		double d = val / scale;
		d *= size;
		d = Math.floor(d);
		d /= size;
		d *= scale;

		return d;
	}

	protected void drawScaleFriendlyTexture(double x, double y, double srcX, double srcY, double w, double h) {
		float         f             = 0.00390625F;
		float         f1            = 0.00390625F;
		Tessellator   tessellator   = Tessellator.getInstance();
		BufferBuilder worldRenderer = tessellator.getBuffer();
		worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
		worldRenderer.pos(x + 0, y + h, this.zLevel).tex((srcX + 0) * f, (srcY + h) * f1).endVertex();
		worldRenderer.pos(x + w, y + h, this.zLevel).tex((srcX + w) * f, (srcY + h) * f1).endVertex();
		worldRenderer.pos(x + w, y + 0, this.zLevel).tex((srcX + w) * f, (srcY + 0) * f1).endVertex();
		worldRenderer.pos(x + 0, y + 0, this.zLevel).tex((srcX + 0) * f, (srcY + 0) * f1).endVertex();
		tessellator.draw();
	}

	public double getScale() {
		double xFactor = (width * 0.9F) / this.xSize;
		double yFactor = (height * 0.9F) / this.ySize;
		double mult = Math.min(xFactor, yFactor);
		mult = Math.min(1,mult);
		mult = (double) Math.floor(mult*1000) / 1000F;
//		System.out.printf("xsize %d\tysize %d\twidth %d\theight %d\txfac %f\tyfac %f\tmult %f\n",xSize, ySize, width, height, xFactor, yFactor, mult);
		return mult;
	}

	protected int scaleX(double x) {
		double scale = getScale();
		x /= scale;
		x += guiLeft;
		x -= (this.width - this.xSize * scale) / (2 * scale);
		return (int) x;
	}

	protected int scaleY(double y) {
		double scale = getScale();
		y /= scale;
		y += guiTop;
		y -= (this.height - this.ySize * scale) / (2 * scale);
		return (int) y;
	}


	protected static void bindTexture(ResourceLocation resource) {
		Minecraft.getInstance().getTextureManager().bindTexture(resource);
	}

	private void startScaling() {
		GlStateManager.pushMatrix();
		double scale = getScale();
		GlStateManager.scaled(scale, scale, 1);
		GlStateManager.translated(-guiLeft, -guiTop, 0.0F);
		GlStateManager.translated((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
	}

	private void stopScaling() {
		GlStateManager.popMatrix();
	}

	@Override
	public void render(int mouseX, int mouseY, float f) {
		this.renderBackground();
		startScaling();
		draw(scaleX(mouseX), scaleY(mouseY), f);
		stopScaling();
		super.render(mouseX, mouseY, f);
	}

	protected abstract void draw(int mouseX, int mouseY, float f);
}
