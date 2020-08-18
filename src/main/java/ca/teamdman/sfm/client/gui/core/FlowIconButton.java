package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.flowdata.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.ResourceLocation;

public class FlowIconButton implements IFlowController, IFlowView, IFlowTangible {

	public final FlowSprite BACKGROUND;
	public final FlowSprite ICON;
	public final FlowPositionBox POS;

	public FlowIconButton(ButtonBackground background, ButtonLabel label, Position pos) {
		this.BACKGROUND = createBackground(
			ButtonBackground.SPRITE_SHEET,
			background.LEFT,
			background.TOP,
			background.WIDTH,
			background.HEIGHT
		);
		this.ICON = createLabel(
			ButtonLabel.SPRITE_SHEET,
			label.LEFT,
			label.TOP,
			label.WIDTH,
			label.HEIGHT
		);
		this.POS = createPositionBox(pos, background.WIDTH, background.HEIGHT);
	}

	public FlowIconButton(ButtonLabel type, Position pos) {
		this(ButtonBackground.NORMAL, type, pos);
	}

	public FlowIconButton(ButtonBackground background, ButtonLabel label) {
		this(background, label, new Position(0, 0));
	}

	public FlowIconButton(ButtonLabel type) {
		this(type, new Position(0, 0));
	}

	@Override
	public boolean isInBounds(int mx, int my) {
		return POS.isInBounds(mx, my);
	}

	public FlowSprite createBackground(
		ResourceLocation sheet, int left, int top, int width,
		int height
	) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	public FlowSprite createLabel(
		ResourceLocation sheet, int left, int top, int width,
		int height
	) {
		return new FlowSprite(sheet, left, top, width, height);
	}

	public FlowPositionBox createPositionBox(Position pos, int width, int height) {
		return new FlowPositionBox(
			pos,
			new Size(width, height)
		);
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
		if (isInBounds(mx, my)) {
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
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx,
		int my, float deltaTime
	) {
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

	@Override
	public Position snapToEdge(Position outside) {
		return POS.snapToEdge(outside);
	}

	public enum ButtonBackground {
		NORMAL(14, 0, 22, 22),
		DEPRESSED(14, 22, 22, 22),
		LINE_NODE(36, 0, 8, 8);

		public static final ResourceLocation SPRITE_SHEET = new ResourceLocation(
			SFM.MOD_ID,
			"textures/gui/sprites.png"
		);
		public final int LEFT, TOP, WIDTH, HEIGHT;

		ButtonBackground(int left, int top, int width, int height) {
			this.LEFT = left;
			this.TOP = top;
			this.WIDTH = width;
			this.HEIGHT = height;
		}
	}

	public enum ButtonLabel {
		INPUT(0, 0, 14, 14),
		OUTPUT(0, 14, 14, 14),
		ADD_INPUT(0, 28, 14, 14),
		ADD_OUTPUT(0, 42, 14, 14),
		NONE(0, 0, 0, 0);

		public static final ResourceLocation SPRITE_SHEET = new ResourceLocation(
			SFM.MOD_ID,
			"textures/gui/sprites.png"
		);
		public final int LEFT, TOP, WIDTH, HEIGHT;

		ButtonLabel(int left, int top, int width, int height) {
			this.LEFT = left;
			this.TOP = top;
			this.WIDTH = width;
			this.HEIGHT = height;
		}
	}
}
