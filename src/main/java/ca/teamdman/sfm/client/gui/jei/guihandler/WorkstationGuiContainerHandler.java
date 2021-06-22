package ca.teamdman.sfm.client.gui.jei.guihandler;

import ca.teamdman.sfm.client.gui.screen.WorkstationScreen;
import java.util.List;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;

public class WorkstationGuiContainerHandler implements
	IGuiContainerHandler<WorkstationScreen> {

	@Override
	public List<Rectangle2d> getGuiExtraAreas(WorkstationScreen containerScreen) {
		return containerScreen.getExclusionAreas();
	}


}
