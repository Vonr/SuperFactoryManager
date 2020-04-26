package ca.teamdman.sfm.client.gui;

import ca.teamdman.sfm.client.gui.core.CoreContainerScreen;
import ca.teamdman.sfm.common.container.CoreContainer;
import ca.teamdman.sfm.common.container.ManagerContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerScreen extends CoreContainerScreen<ManagerContainer> {


	public ManagerScreen(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, 180, 160, inv, name);
		this.xSize = 512;
		this.ySize = 256;
	}


}
