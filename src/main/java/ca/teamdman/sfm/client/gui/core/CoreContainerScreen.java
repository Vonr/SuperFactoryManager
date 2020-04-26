package ca.teamdman.sfm.client.gui.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.CoreContainer;
import ca.teamdman.sfm.common.container.core.Relationship;
import ca.teamdman.sfm.common.container.core.Sprite;
import ca.teamdman.sfm.common.container.core.component.CommandButton;
import ca.teamdman.sfm.common.container.core.component.Line;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class CoreContainerScreen<C extends CoreContainer<?>> extends BaseScreen implements IHasContainer<C> {
	public static final  int              LEFT             = 0;
	public static final  int              MIDDLE           = 2;
	public static final  int              RIGHT            = 1;
	private static final ResourceLocation BACKGROUND_LEFT  = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_1.png");
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_2.png");
	public final         C    CONTAINER;

	public CoreContainerScreen(C container, int width, int height, PlayerInventory inv, ITextComponent name) {
		super(name, width,height);
		this.CONTAINER = container;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		int           mx      = scaleX((float) x) - guiLeft;
		int           my      = scaleY((float) y) - guiTop;
		CommandButton pressed = null;

		for (CommandButton c : CONTAINER.COMMAND_CONTROLLER.getCommands()) {
			if (c.isInBounds(mx, my)) {
				pressed = c;
				break;
			}
		}

		if (CONTAINER.POSITION_CONTROLLER.onMouseDown(mx, my, button, pressed)) {
			//LOGGER.debug("Stopped at position controller mouse down.");
			return true;
		}
		if (CONTAINER.RELATIONSHIP_CONTROLLER.onMouseDown(mx, my, button, pressed)) {
			//LOGGER.debug("Stopped at relationship controller mouse down.");
			return true;
		}
		if (CONTAINER.BUTTON_CONTROLLER.onMouseDown(mx, my, button, pressed)) {
			//LOGGER.debug("Stopped at button controller mouse down.");
			return true;
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (CONTAINER.POSITION_CONTROLLER.onMouseUp(mx, my, button)) {
			//LOGGER.debug("Stopped at position controller mouse released.");
			return true;
		}
		if (CONTAINER.RELATIONSHIP_CONTROLLER.onMouseUp(mx, my, button)) {
			//LOGGER.debug("Stopped at relationship controller mouse released.");
			return true;
		}
		if (CONTAINER.BUTTON_CONTROLLER.onMouseUp(mx, my, button)) {
			//LOGGER.debug("Stopped at button controller mouse released.");
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;

		if (CONTAINER.POSITION_CONTROLLER.onDrag(mx, my, button)) {
			//LOGGER.debug("Stopped at position controller mouse dragged.");
			return true;
		}
		if (CONTAINER.RELATIONSHIP_CONTROLLER.onDrag(mx, my, button)) {
			//LOGGER.debug("Stopped at relationship controller mouse dragged.");
			return true;
		}
		if (CONTAINER.BUTTON_CONTROLLER.onDrag(mx, my, button)) {
			//LOGGER.debug("Stopped at button controller mouse dragged.");
			return true;
		}
		return false;
	}

	@Override
	public void draw(int x, int y, float deltaTime) {
		// Background Layer
		drawBackground();
		drawRelationships();
		drawButtons();
		drawDraggingConnectionAndCheckClear(x, y);
	}

	public void drawDraggingConnectionAndCheckClear(int x, int y) {
		CONTAINER.RELATIONSHIP_CONTROLLER.getDragStart().ifPresent(start -> {
			if (hasShiftDown())
				this.drawArrow(start.getPosition().getX() + start.getWidth() / 2, start.getPosition().getY() + start.getHeight() / 2, x, y);
			else
				CONTAINER.RELATIONSHIP_CONTROLLER.clearDragStart();
		});
	}

	public void drawButtons() {
		BaseScreen.bindTexture(Sprite.SHEET);
		for (CommandButton action : CONTAINER.COMMAND_CONTROLLER.getCommands()) {
			this.drawSprite(action.getPosition().getX(), action.getPosition().getY(), action.isPressed() ? Sprite.CASE_DARK : Sprite.CASE);
			this.drawSprite(action.getPosition().getX() + 4, action.getPosition().getY() + 4, action.getSprite());
		}
	}

	public void drawRelationships() {
		for (Relationship r : CONTAINER.RELATIONSHIP_CONTROLLER.getRelationships().values()) {
			for (Line line : r.LINE_LIST) {
				if (line.getNext() == r.HEAD) {
					this.drawArrow(line);
				} else {
					this.drawLine(line);
				}
			}
		}
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
