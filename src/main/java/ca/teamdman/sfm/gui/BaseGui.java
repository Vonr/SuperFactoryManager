package ca.teamdman.sfm.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.opengl.GL11;

import javax.vecmath.Color4f;

/**
 * Credit to VSWE for lots of the rendering scaling tech
 */
public abstract class BaseGui extends Screen {
	public static final Color4f DEFAULT_LINE_COLOUR     = new Color4f(0.4f, 0.4f, 0.4f, 1);
	public static final Color4f HIGHLIGHTED_LINE_COLOUR = new Color4f(0.15686275f, 0.5294118f, 0.94509804f, 1);
	final               int     zLevel                  = 0;
	protected           int     guiLeft                 = 0;
	protected           int     guiTop                  = 0;
	protected           int     xSize                   = 176;
	protected           int     ySize                   = 166;

	public BaseGui(ITextComponent titleIn, int width, int height) {
		super(titleIn);
		this.width = width;
		this.height = height;
	}

	/**
	 * Binds a texture to be drawn
	 *
	 * @param resource Texture location
	 */
	public static void bindTexture(ResourceLocation resource) {
		Minecraft.getInstance().getTextureManager().bindTexture(resource);
	}

	public void drawString(String str, int x, int y, int color) {
		drawString(str, x, y, 1F, color);
	}


	public void drawString(String str, int x, int y, float mult, int color) {
		GlStateManager.pushMatrix();
		GlStateManager.scalef(mult, mult, 1F);
		this.font.drawString(str, (int) ((x + guiLeft) / mult), (int) ((y + guiTop) / mult), color);
		//bindTexture(getComponentResource());
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}


	public void drawRightAlignedString(String str, int x, int y, int color) {
		drawRightAlignedString(str, x, y, 1, color);
	}

	public void drawRightAlignedString(String str, int x, int y, float mult, int color) {
		drawString(
				str,
				(int) (x - fixScaledCoordinate(font.getStringWidth(str), getScale(), minecraft.mainWindow.getWidth())),
				y,
				mult,
				color
		);
	}

	/**
	 * Converts local values to screen values.
	 *
	 * @param val   Local value
	 * @param scale Scale factor
	 * @param size  Screen dimension
	 * @return Screen value
	 */
	public double fixScaledCoordinate(int val, double scale, int size) {
		double d = val / scale;
		d *= size;
		d = Math.floor(d);
		d /= size;
		d *= scale;

		return d;
	}

	/**
	 * Gets the ratio from screen to local.
	 *
	 * @return Scaling factor
	 */
	public double getScale() {
		double xFactor = (width * 0.9F) / this.xSize;
		double yFactor = (height * 0.9F) / this.ySize;
		double mult    = Math.min(xFactor, yFactor);
		mult = Math.min(1, mult);
		mult = Math.floor(mult * 1000) / 1000F;
		//		System.out.printf("xsize %d\tysize %d\twidth %d\theight %d\txfac %f\tyfac %f\tmult %f\n",xSize, ySize, width, height, xFactor, yFactor, mult);
		return mult;
	}

	/**
	 * Draws a sprite, does not bind.
	 *
	 * @param x      Local value
	 * @param y      Local value
	 * @param sprite Sprite data
	 */
	public void drawSprite(int x, int y, ISprite sprite) {
		drawTexture(x, y, sprite.getLeft(), sprite.getTop(), sprite.getWidth(), sprite.getHeight());
	}

	/**
	 * Scales and draws the currently bound texture.
	 *
	 * @param x    Local value
	 * @param y    Local value
	 * @param srcX Sprite value
	 * @param srcY Sprite value
	 * @param w    Local width
	 * @param h    Local height
	 */
	public void drawTexture(int x, int y, int srcX, int srcY, int w, int h) {
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
	 * Draws texture using screen values
	 *
	 * @param x    Screen value
	 * @param y    Screen value
	 * @param srcX Sprite value
	 * @param srcY Sprite value
	 * @param w    Screen width
	 * @param h    Screen height
	 */
	public void drawScaleFriendlyTexture(double x, double y, double srcX, double srcY, double w, double h) {
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

	/**
	 * Scales and renders main gui.
	 *
	 * @param mouseX Screen value
	 * @param mouseY Screen value
	 * @param f      Unknown?
	 */
	@Override
	public void render(int mouseX, int mouseY, float f) {
		this.renderBackground();
		startScaling();
		draw(scaleX(mouseX) - guiLeft, scaleY(mouseY) - guiTop, f);
		stopScaling();
		super.render(mouseX, mouseY, f);
	}

	/**
	 * Initializes content, centers GUI on screen
	 */
	@Override
	protected void init() {
		super.init();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
	}

	/**
	 * Converts a screen X value to a local one.
	 *
	 * @param x Screen value
	 * @return Local value
	 */
	public int scaleX(double x) {
		double scale = getScale();
		x /= scale;
		x += guiLeft;
		x -= (this.width - this.xSize * scale) / (2 * scale);
		return (int) x;
	}

	/**
	 * Converts a screen X value to a local one.
	 *
	 * @param y Screen value
	 * @return Local value
	 */
	public int scaleY(double y) {
		double scale = getScale();
		y /= scale;
		y += guiTop;
		y -= (this.height - this.ySize * scale) / (2 * scale);
		return (int) y;
	}

	/**
	 * Sets GL state to fit the current scale ratio.
	 */
	private void startScaling() {
		GlStateManager.pushMatrix();
		double scale = getScale();
		GlStateManager.scaled(scale, scale, 1);
		GlStateManager.translated(-guiLeft, -guiTop, 0.0F);
		GlStateManager.translated((this.width - this.xSize * scale) / (2 * scale), (this.height - this.ySize * scale) / (2 * scale), 0.0F);
	}

	/**
	 * Reverts GL state to normal scaling.
	 */
	private void stopScaling() {
		GlStateManager.popMatrix();
	}

	public abstract void draw(int mouseX, int mouseY, float f);

	public void drawLine(Line line) {
		drawLine(line.HEAD, line.TAIL, line.getColor());
	}

	public void drawLine(Point head, Point tail, Color4f color) {
		drawLine(head.getX(), head.getY(), tail.getX(), tail.getY(), color);
	}

	public void drawLine(int x1, int y1, int x2, int y2, Color4f color) {
		GlStateManager.pushMatrix();

		GlStateManager.disableTexture();
		GlStateManager.color4f(color.x, color.y, color.z, color.w);

		//GlStateManager.enableBlend();
		//GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_DST_COLOR);
		//GL11.glShadeModel(GL11.GL_SMOOTH);
		//GL11.glEnable(GL11.GL_LINE_SMOOTH);
		//GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
		//GL11.glLineWidth(5);
		GL11.glLineWidth(1 + 5 * this.width / 500F);

		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3f(guiLeft + x1, guiTop + y1, 0);
		GL11.glVertex3f(guiLeft + x2, guiTop + y2, 0);
		GL11.glEnd();

		GlStateManager.disableBlend();
		GlStateManager.color4f(1F, 1F, 1F, 1F);
		GlStateManager.enableTexture();
		GlStateManager.popMatrix();
	}

	public void drawLine(Point head, Point tail) {
		drawLine(head.getX(), head.getY(), tail.getX(), tail.getY());
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		drawLine(x1, y1, x2, y2, new Color4f(0.4f, 0.4f, 0.4f, 1));
	}

	public void drawArrow(Line line) {
		drawArrow(line.TAIL.getX(), line.TAIL.getY(), line.HEAD.getX(), line.HEAD.getY(), line.getColor());
	}

	public void drawArrow(int x1, int y1, int x2, int y2, Color4f color) {
		drawLine(x1, y1, x2, y2, color);
		int    lookX = x2 - x1;
		int    lookY = y2 - y1;
		double mag   = Math.sqrt((lookX * lookX) + (lookY * lookY));
		mag *= 1 / 24d;
		lookX /= mag;
		lookY /= mag;

		double ang = Math.PI * -7 / 8d;
		drawLine(
				x2,
				y2,
				x2 + (int) (Math.cos(ang) * lookX - Math.sin(ang) * lookY),
				y2 + (int) (Math.sin(ang) * lookX + Math.cos(ang) * lookY),
				color
		);

		ang = Math.PI * 7 / 8d;
		drawLine(
				x2,
				y2,
				x2 + (int) (Math.cos(ang) * lookX - Math.sin(ang) * lookY),
				y2 + (int) (Math.sin(ang) * lookX + Math.cos(ang) * lookY),
				color
		);
	}

	public void drawArrow(int x1, int y1, int x2, int y2) {
		drawArrow(x1, y1, x2, y2, DEFAULT_LINE_COLOUR);
	}
}
