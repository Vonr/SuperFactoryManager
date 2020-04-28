package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import net.minecraft.util.ResourceLocation;

public class FlowSprite implements IFlowController {
	private final ResourceLocation SHEET;
	private final int LEFT, TOP, WIDTH, HEIGHT;
	private int x, y;

	public FlowSprite(ResourceLocation sheet, int x, int y, int left, int top, int width, int height) {
		this.SHEET = sheet;
		this.LEFT = left;
		this.TOP = top;
		this.WIDTH = width;
		this.HEIGHT = height;
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		return false;
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return false;
	}

	@SuppressWarnings("AccessStaticViaInstance")
	@Override
	public IFlowView getView() {
		return (screen, mx, my, deltaTime) -> {
			screen.bindTexture(SHEET);
			screen.drawSprite(x, y, LEFT, TOP, WIDTH, HEIGHT);
		};
	}
}
