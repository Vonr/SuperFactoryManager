package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.BaseContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BaseContainerScreen<C extends BaseContainer<?>> extends BaseScreen implements
	IHasContainer<C> {


	public static final int LEFT = 0;
	public static final int MIDDLE = 2;
	public static final int RIGHT = 1;
	private static final ResourceLocation BACKGROUND_LEFT = new ResourceLocation(SFM.MOD_ID,
		"textures/gui/background_1.png");
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(SFM.MOD_ID,
		"textures/gui/background_2.png");
	public final C CONTAINER;

	public BaseContainerScreen(C container, int width, int height, PlayerInventory inv,
		ITextComponent name) {
		super(name, width, height);
		this.CONTAINER = container;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		int mx = scaleX((float) x) - guiLeft;
		int my = scaleY((float) y) - guiTop;
		return onScaledMouseClicked(mx, my, button);

	}

	public boolean onScaledMouseClicked(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		return onScaledMouseReleased(mx, my, button);
	}

	public boolean onScaledMouseReleased(int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		int dmx = scaleX(dx) - guiLeft;
		int dmy = scaleX(dy) - guiTop;
		return onScaledMouseDragged(mx, my, button, dmx, dmy);
	}

	public boolean onScaledMouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return false;
	}

	@Override
	public void draw(MatrixStack matrixStack, int mx, int my,
		float partialTicks) {
		drawBackground(matrixStack);
	}

	public void drawBackground(MatrixStack matrixStack) {
		RenderSystem.color4f(1f, 1f, 1f, 1f);
		bindTexture(BACKGROUND_LEFT);
		drawTexture(matrixStack, 0, 0, 0, 0, 256, 256);
		bindTexture(BACKGROUND_RIGHT);
		drawTexture(matrixStack, 256, 0, 0, 0, 256, 256);
		drawRightAlignedString(matrixStack, I18n.format("gui.sfm.manager.legend.chain"), 506, 212,
			0x999999);
		drawRightAlignedString(matrixStack, I18n.format("gui.sfm.manager.legend.clone"), 506, 222,
			0x999999);
		drawRightAlignedString(matrixStack, I18n.format("gui.sfm.manager.legend.move"), 506, 232,
			0x999999);
		drawRightAlignedString(matrixStack, I18n.format("gui.sfm.manager.legend.snaptogrid"), 506,
			242,
			0x999999);
	}

	@Override
	@Nonnull
	public C getContainer() {
		return CONTAINER;
	}
}
