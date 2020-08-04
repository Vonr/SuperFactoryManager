package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.ResourceLocation;

public class FlowIconButton implements IFlowController, IFlowView, PositionProvider, SizeProvider {
	private final FlowSprite         BACKGROUND;
	private final FlowSprite         ICON;
	private final FlowRepositionable POS;

	public FlowIconButton(ButtonLabel type, Position pos) {
		this.BACKGROUND = new FlowSprite(
				ButtonBackground.SPRITE_SHEET,
				ButtonBackground.NORMAL.left,
				ButtonBackground.NORMAL.top,
				ButtonBackground.NORMAL.width,
				ButtonBackground.NORMAL.height);

		this.ICON = new FlowSprite(
				ButtonLabel.SPRITE_SHEET,
				type.left,
				type.top,
				type.width,
				type.height);

		this.POS = new FlowRepositionable(pos, new Size(ButtonBackground.NORMAL.width, ButtonBackground.NORMAL.height));
	}

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return POS.mouseClicked(screen, mx, my, button);
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		if (!POS.mouseReleased(screen, mx, my, button))
			return false;
		this.onPositionChanged();
		return true;
	}

	public void onPositionChanged() {
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return POS.mouseDragged(screen, mx, my, button, dmx, dmy);
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime) {
		Position p = POS.getPosition();
		BACKGROUND.drawAt(screen, matrixStack, p);
		ICON.drawAt(screen, matrixStack, p.getX() + 4, p.getY() + 4);
	}

	@Override
	public Position getPosition() {
		return POS.getPosition();
	}

	@Override
	public Size getSize() {
		return POS.getSize();
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
