package ca.teamdman.sfm.gui;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.container.ManagerContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import com.sun.jna.platform.KeyboardUtils;
import net.java.games.input.Keyboard;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.List;

import static ca.teamdman.sfm.gui.MouseButton.LEFT;
import static ca.teamdman.sfm.gui.Sprite.*;

public class ManagerGui extends BaseGui implements IHasContainer<ManagerContainer> {
	private final ManagerContainer container;

	private static final ResourceLocation BACKGROUND_LEFT  = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_1.png");
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_2.png");
	private final        List<FlowAction> actionList       = new ArrayList<>();
	private              FlowAction       focusedAction    = null;
	private boolean dragging = false;
	private int dragOffsetX = 0;
	private int dragOffsetY = 0;

	{
		actionList.add(new FlowAction(50, 50, INPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("left");
		}));
		actionList.add(new FlowAction(150, 50, OUTPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("center");
		}));
		actionList.add(new FlowAction(250, 50, INPUT, new TranslationTextComponent("woot"), () -> {
			System.out.println("right");
		}));
	}

	public ManagerGui(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(name, 180, 160);
		this.xSize = 512;
		this.ySize = 256;
		this.container = container;
	}

	@Override
	public ManagerContainer getContainer() {
		return container;
	}

	private void drawBackground() {
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		bindTexture(BACKGROUND_LEFT);
		drawTexture(0, 0, 0, 0, 256, 256);
		bindTexture(BACKGROUND_RIGHT);
		drawTexture(256, 0, 0, 0, 256, 256);
		drawRightAlignedString(
				this.font,
				I18n.format("gui.sfm.manager.legend.move"),
				(int) fixScaledCoordinate(512, getScale(), minecraft.mainWindow.getWidth()) - 4,
				(int) fixScaledCoordinate(256, getScale(), minecraft.mainWindow.getHeight())- 4,
				16777215
		);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		double sx = scaleX((float) x);
		double sy = scaleY((float) y);
		//		super.mouseClicked(sx, sy, button);
		if (button != LEFT.ordinal())
			return false;

		int mx = (int) (sx - guiLeft);
		int my = (int) (sy - guiTop);
		for (FlowAction action : actionList) {
			if (action.isInBounds(mx, my)) {
				if (hasAltDown()) {
					dragging = true;
					dragOffsetX = action.getX() - mx;
					dragOffsetY = action.getY() - my;
				} else {
					action.setPressed(true);
				}
				this.focusedAction = action;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (dragging) {
			if (!hasAltDown())
				return false;
			if (focusedAction == null)
				return false;
			if (hasShiftDown()) {
				mx-=(mx+dragOffsetX)%10;
				my-=(my+dragOffsetY)%10;
			}
			focusedAction.setX(mx + dragOffsetX);
			focusedAction.setY(my + dragOffsetY);
		} else {
			if (button != LEFT.ordinal())
				return false;
			if (focusedAction == null)
				return false;
			if (focusedAction.isInBounds(mx, my) == focusedAction.isPressed())
				return false;

			focusedAction.setPressed(!focusedAction.isPressed());
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		dragging = false;
		if (button != LEFT.ordinal())
			return false;
		if (focusedAction == null)
			return false;

		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (!focusedAction.isInBounds(mx, my) || !focusedAction.isPressed())
			return false;

		focusedAction.click();
		focusedAction.setPressed(false);
		this.focusedAction = null;
		return true;
	}

	private void drawActions() {
		bindTexture(SHEET);
		for (FlowAction action : actionList) {
			drawSprite(action.getX(), action.getY(), action.isPressed() ? CASE_DARK : CASE);
			drawSprite(action.getX() + 4, action.getY() + 4, action.getSprite());
		}
	}


	@Override
	public void draw(int mouseX, int p_render_2_, float p_render_3_) {
		// Background Layer
		drawBackground();
		drawActions();

	}


}
