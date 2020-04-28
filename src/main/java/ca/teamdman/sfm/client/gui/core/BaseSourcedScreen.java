package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.CoreContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class BaseSourcedScreen<C extends CoreContainer<?>> extends BaseScreen implements IHasContainer<C> {
	public static final  int              LEFT             = 0;
	public static final  int              MIDDLE           = 2;
	public static final  int              RIGHT            = 1;
	private static final ResourceLocation BACKGROUND_LEFT  = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_1.png");
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_2.png");
	public final         C    CONTAINER;

	public BaseSourcedScreen(C container, int width, int height, PlayerInventory inv, ITextComponent name) {
		super(name, width,height);
		this.CONTAINER = container;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		int           mx      = scaleX((float) x) - guiLeft;
		int           my      = scaleY((float) y) - guiTop;
		CONTAINER.getControllers().forEach(c -> c.mouseClicked(this, mx, my, button));
		return true;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		CONTAINER.getControllers().forEach(c -> c.mouseReleased(this, mx, my, button));
		return false;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		int dmx = scaleX(dx) - guiLeft;
		int dmy = scaleX(dy) - guiTop;
		CONTAINER.getControllers().forEach(c -> c.mouseDragged(this, mx, my, button, dmx, dmy));
		return false;
	}

	@Override
	public void draw(int mx, int my, float deltaTime) {
//		int mx = scaleX(x) - guiLeft;
//		int my = scaleY(y) - guiTop;
		drawBackground();
		CONTAINER.getViews().forEach(v -> v.draw(this, mx, my, deltaTime));
	}

	public void drawBackground() {
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		bindTexture(BACKGROUND_LEFT);
		drawTexture(0, 0, 0, 0, 256, 256);
		bindTexture(BACKGROUND_RIGHT);
		drawTexture(256, 0, 0, 0, 256, 256);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.chain"), 506, 212, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.clone"), 506, 222, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.move"), 506, 232, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.snaptogrid"), 506, 242, 0x999999);
	}

	@Override
	@Nonnull
	public C getContainer() {
		return CONTAINER;
	}
}
