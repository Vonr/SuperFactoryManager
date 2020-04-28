package ca.teamdman.sfm.client.gui.manager;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.impl.FlowPositionable;
import ca.teamdman.sfm.client.gui.impl.FlowSprite;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.util.ResourceLocation;

public class ManagerFlowController implements IFlowController {
	final FlowPositionable POSITION          = new FlowPositionable(new Rectangle2d(5, 5, 20, 20)) {
		@Override
		public void setArea(Rectangle2d area) {
			super.setArea(area);
			BUTTON_BACKGROUND.setX(area.getX());
			BUTTON_BACKGROUND.setY(area.getY());
			BUTTON_LABEL.setX(area.getX()+4);
			BUTTON_LABEL.setY(area.getY()+4);
		}
	};
	final FlowSprite       BUTTON_BACKGROUND = new FlowSprite(
			new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png"),
			POSITION.getArea().getX(), POSITION.getArea().getY(),
			14, 0, 22, 22);
	final FlowSprite       BUTTON_LABEL      = new FlowSprite(
			new ResourceLocation(SFM.MOD_ID, "textures/gui/sprites.png"),
			POSITION.getArea().getX()+4, POSITION.getArea().getY()+4,
			0, 0, 14, 14);

	@Override
	public boolean mouseClicked(BaseScreen screen, int mx, int my, int button) {
		return POSITION.mouseClicked(screen, mx, my, button);
	}

	@Override
	public boolean mouseReleased(BaseScreen screen, int mx, int my, int button) {
		return POSITION.mouseReleased(screen, mx, my, button);
	}

	@Override
	public boolean mouseDragged(BaseScreen screen, int mx, int my, int button, int dmx, int dmy) {
		return POSITION.mouseDragged(screen, mx, my, button, dmx, dmy);
	}

	@Override
	public IFlowView getView() {
		return (screen, mx, my, deltaTime) -> {
			BUTTON_BACKGROUND.getView().draw(screen, my, my, deltaTime);
			BUTTON_LABEL.getView().draw(screen, my, my, deltaTime);
		};
	}
}
