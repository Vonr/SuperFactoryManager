package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flowdata.Position;
import ca.teamdman.sfm.common.flowdata.PositionProvider;
import ca.teamdman.sfm.common.flowdata.SizeProvider;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

public class FlowIconButton implements IFlowController, IFlowView, PositionProvider, SizeProvider {

	public final FlowSprite BACKGROUND;
	public final FlowSprite ICON;
	public final FlowPositionBox POS;

	public FlowIconButton(ButtonLabel type, Position pos) {
		this.BACKGROUND = createBackground(
			ButtonBackground.SPRITE_SHEET,
			ButtonBackground.NORMAL.left,
			ButtonBackground.NORMAL.top,
			ButtonBackground.NORMAL.width,
			ButtonBackground.NORMAL.height);
		this.ICON = createLabel(
			ButtonLabel.SPRITE_SHEET,
			type.left,
			type.top,
			type.width,
			type.height);
		this.POS = createPositionBox(pos, ButtonBackground.NORMAL.width,
			ButtonBackground.NORMAL.height);
	}

	public FlowIconButton(ButtonLabel type) {
		this(type, new Position(0, 0));
	}

	@Override
	public boolean isInBounds(int mx, int my) {
		if (mx < getPosition().getX()) {
			return false;
		}
		if (my < getPosition().getY()) {
			return false;
		}
		if (mx > getPosition().getX() + this.BACKGROUND.WIDTH) {
			return false;
		}
		if (my > getPosition().getY() + this.BACKGROUND.HEIGHT) {
			return false;
		}
		return true;
	}

	public FlowSprite createBackground(ResourceLocation sheet, int left, int top, int width,
		int height) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	public FlowSprite createLabel(ResourceLocation sheet, int left, int top, int width,
		int height) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	public FlowPositionBox createPositionBox(Position pos, int width, int height) {
		return new FlowPositionBox(pos,
			new Size(width, height));
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return POS.mousePressed(mx, my, button);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (POS.mouseReleased(mx, my, button)) {
			return true;
		}
		if (POS.getSize().contains(POS.getPosition(), mx, my)) {
			this.onClicked(mx, my, button);
		}
		return false;
	}

	public void onClicked(int mx, int my, int button) {

	}


	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return POS.mouseDragged(mx, my, button, dmx, dmy);
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
	public Position getCentroid() {
		return getPosition().withOffset(BACKGROUND.WIDTH / 2, BACKGROUND.HEIGHT / 2);
	}

	@Override
	public Size getSize() {
		return POS.getSize();
	}

	private enum ButtonBackground {
		NORMAL(14, 0, 22, 22),
		DEPRESSED(14, 22, 22, 22);

		static final ResourceLocation SPRITE_SHEET = new ResourceLocation(SFM.MOD_ID,
			"textures/gui/sprites.png");
		final int left, top, width, height;

		ButtonBackground(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}

	public enum ButtonLabel {
		INPUT(0, 0, 14, 14),
		OUTPUT(0, 14, 14, 14),
		ADD_INPUT(0, 28, 14, 14),
		ADD_OUTPUT(0, 42, 14, 14);

		static final ResourceLocation SPRITE_SHEET = new ResourceLocation(SFM.MOD_ID,
			"textures/gui/sprites.png");
		final int left, top, width, height;

		ButtonLabel(int left, int top, int width, int height) {
			this.left = left;
			this.top = top;
			this.width = width;
			this.height = height;
		}
	}
}
