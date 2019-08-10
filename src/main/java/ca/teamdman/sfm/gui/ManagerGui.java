package ca.teamdman.sfm.gui;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.container.ManagerContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ManagerGui extends BaseGui implements IHasContainer<ManagerContainer> {
	public static final int               LEFT                 = 0;
	public static final int               MIDDLE               = 2;
	public static final int               RIGHT                = 1;
	private static final ResourceLocation BACKGROUND_LEFT      = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_1.png");
	private static final ResourceLocation BACKGROUND_RIGHT     = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_2.png");
	public final ButtonController         BUTTON_CONTROLLER    = new ButtonController(this);
	public final CommandController        COMMAND_CONTROLLER   = new CommandController(this);
	public final PositionController       POSITION_CONTROLLER  = new PositionController(this);
	public final HierarchyController      HIERARCHY_CONTROLLER = new HierarchyController(this);
	private final ManagerContainer        CONTAINER;


	public ManagerGui(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(name, 180, 160);
		this.xSize = 512;
		this.ySize = 256;
		this.CONTAINER = container;
	}

	@Override
	public ManagerContainer getContainer() {
		return CONTAINER;
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		int mx = scaleX((float) x) - guiLeft;
		int my = scaleY((float) y) - guiTop;
		for (Command c : COMMAND_CONTROLLER.getCommands()) {
			if (c.isInBounds(mx, my)) {
				if (POSITION_CONTROLLER.onMouseDown(mx, my, button, c))
					return true;
				if (HIERARCHY_CONTROLLER.onMouseDown(mx, my, button, c))
					return true;
				if (BUTTON_CONTROLLER.onMouseDown(mx, my, button, c))
					return true;
			}
		}
		return true;
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (POSITION_CONTROLLER.onMouseUp(mx, my, button))
			return true;
		if (HIERARCHY_CONTROLLER.onMouseUp(mx, my, button))
			return true;
		if (BUTTON_CONTROLLER.onMouseUp(mx, my, button))
			return true;
		return false;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;

		if (POSITION_CONTROLLER.onDrag(mx, my, button))
			return true;
		if (HIERARCHY_CONTROLLER.onDrag(mx, my, button))
			return true;
		if (BUTTON_CONTROLLER.onDrag(mx, my, button))
			return true;
		return false;
	}

	@Override
	public void draw(int x, int y, float deltaTime) {
		// Background Layer
		drawBackground();
		HIERARCHY_CONTROLLER.draw(x,y);
		COMMAND_CONTROLLER.draw();

	}

	private void drawBackground() {
		GlStateManager.color4f(1f, 1f, 1f, 1f);
		bindTexture(BACKGROUND_LEFT);
		drawTexture(0, 0, 0, 0, 256, 256);
		bindTexture(BACKGROUND_RIGHT);
		drawTexture(256, 0, 0, 0, 256, 256);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.clone"), 506, 222, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.move"), 506, 232, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.snaptogrid"), 506, 242, 0x999999);
	}


}
