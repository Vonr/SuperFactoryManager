package ca.teamdman.sfm.container;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class ManagerGui extends ContainerScreen<ManagerContainer> {
	public ManagerGui(ManagerContainer container, PlayerInventory inv, ITextComponent name) {
		super(container, inv, name);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawString(this.font, "asd", mouseX, mouseY, -1);
	}
}
