package ca.teamdman.sfm.gui;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.container.ManagerContainer;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManagerGui extends BaseGui implements IHasContainer<ManagerContainer> {
	private final ManagerContainer container;

	private static final ResourceLocation  BACKGROUND_LEFT   = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_1.png");
	private static final ResourceLocation  BACKGROUND_RIGHT  = new ResourceLocation(SFM.MOD_ID, "textures/gui/background_2.png");
	public final        DragController    dragController    = new DragController(this);
	public final        ButtonController  buttonController  = new ButtonController(this);
	public final        CommandController commandController = new CommandController(this);


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
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.clone"), 506, 222, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.move"), 506, 232, 0x999999);
		drawRightAlignedString(I18n.format("gui.sfm.manager.legend.snaptogrid"), 506, 242, 0x999999);
	}

	@Override
	public boolean mouseClicked(double x, double y, int button) {
		int mx = scaleX((float) x) - guiLeft;
		int my = scaleY((float) y) - guiTop;
		for (Command c : commandController.getCommands()) {
			if (c.isInBounds(mx,my)) {
				if (dragController.onMouseDown(mx, my, button, c))
					return true;
				if (buttonController.onMouseDown(mx, my, button, c))
					return true;
			}
		}
		return true;
	}

	@Override
	public boolean mouseDragged(double x, double y, int button, double dx, double dy) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (dragController.onDrag(mx, my, button))
			return true;
		return buttonController.onDrag(mx, my, button);
	}

	@Override
	public boolean mouseReleased(double x, double y, int button) {
		int mx = scaleX(x) - guiLeft;
		int my = scaleY(y) - guiTop;
		if (dragController.onMouseUp(mx, my, button))
			return true;
		return buttonController.onMouseUp(mx, my, button);
	}

	@Override
	public void draw(int mouseX, int p_render_2_, float p_render_3_) {
		// Background Layer
		drawBackground();
		commandController.draw();
	}


}
