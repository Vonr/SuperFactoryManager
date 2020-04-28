package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import net.minecraft.util.ResourceLocation;

public class FlowIconButton implements IFlowController, IFlowView {
	private final FlowSprite       BUTTON_BACKGROUND;
	private final FlowSprite       BUTTON_LABEL;
	private final FlowPositionable BUTTON_POSITION;

	public FlowIconButton(ButtonLabel type) {
		this(type, 5, 5);
	}


	public FlowIconButton(ButtonLabel type, int x, int y) {
		this.BUTTON_BACKGROUND = new FlowSprite(
				ButtonBackground.SPRITE_SHEET,
				ButtonBackground.NORMAL.left,
				ButtonBackground.NORMAL.top,
				ButtonBackground.NORMAL.width,
				ButtonBackground.NORMAL.height);

		this.BUTTON_LABEL = new FlowSprite(
				ButtonLabel.SPRITE_SHEET,
				type.left,
				type.top,
				type.width,
				type.height);

		this.BUTTON_POSITION = new FlowPositionable(x, y, ButtonBackground.NORMAL.width, ButtonBackground.NORMAL.height) {
			@Override
			public void setX(int x) {
				super.setX(x);
				this.trackPosition();
			}

			@Override
			public void setY(int y) {
				super.setY(y);
				this.trackPosition();
			}

			private void trackPosition() {
				BUTTON_BACKGROUND.setX(this.getX());
				BUTTON_BACKGROUND.setY(this.getY());
				BUTTON_LABEL.setX(this.getX() + 4);
				BUTTON_LABEL.setY(this.getY() + 4);
			}
		};
	}

	public void setXY(int x, int y) {
		this.BUTTON_POSITION.setXY(x, y);
	}

	public int getX() {
		return this.BUTTON_POSITION.getX();
	}

	public void setX(int x) {
		this.BUTTON_POSITION.setX(x);
	}

	public int getY() {
		return this.BUTTON_POSITION.getY();
	}

	public void setY(int y) {
		this.BUTTON_POSITION.setY(y);
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return BUTTON_POSITION.mouseClicked(screen, mx, my, button);
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		if (!BUTTON_POSITION.mouseReleased(screen, mx, my, button))
			return false;
		this.onPositionChanged();
		return true;
	}

	public void onPositionChanged() {
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return BUTTON_POSITION.mouseDragged(screen, mx, my, button, dmx, dmy);
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, int mx, int my, float deltaTime) {
		BUTTON_BACKGROUND.getView().draw(screen, my, my, deltaTime);
		BUTTON_LABEL.getView().draw(screen, my, my, deltaTime);
	}

	private enum ButtonBackground {
		NORMAL(14, 0, 22, 22),
		DEPRESSED(14, 22, 22, 22);

		static final ResourceLocation SPRITE_SHEET = new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png");
		final        int              left, top, width, height;

		ButtonBackground(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}

	public enum ButtonLabel {
		INPUT(0, 0, 14, 14),
		OUTPUT(0, 14, 14, 14);

		static final ResourceLocation SPRITE_SHEET = new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png");
		final        int              left, top, width, height;

		ButtonLabel(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}
}
