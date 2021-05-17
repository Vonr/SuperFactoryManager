package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationModeSwitchPacket;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationModeSwitchPacket.Mode;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class WorkstationScreen extends
	ContainerScreen<WorkstationContainer> implements
	IHasContainer<WorkstationContainer> {

	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(
		"textures/gui/container/generic_54.png");

	private final WorkstationContainer CONTAINER;
	private int inventoryRows = 3;
	private TextFieldWidget searchField;
	private ExtendedButton modeSwitchButton;

	public WorkstationScreen(
		WorkstationContainer container,
		PlayerInventory inv,
		ITextComponent name
	) {
		super(container, inv, name);
		this.CONTAINER = container;
	}

	@Override
	protected void init() {
		super.init();
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		searchField = new TextFieldWidget(
			this.font,
			i,
			j - 25,
			103,
			12,
			new TranslationTextComponent("gui.sfm.workstation.search.text")
		);
//		searchField.setCanLoseFocus(false);
		searchField.setTextColor(-1);
		searchField.setDisabledTextColour(-1);
		setFocusedDefault(searchField);

		modeSwitchButton = new ExtendedButton(
			i + 125,
			j - 25,
			50,
			15,
			new TranslationTextComponent(
				"gui.sfm.workstation.mode_switch_button.learning.text"),
			(button) -> PacketHandler.INSTANCE.sendToServer(new C2SWorkstationModeSwitchPacket(
				CONTAINER.windowId,
				Mode.LEARNING
			))
		);

		addButton(searchField);
		addButton(modeSwitchButton);
	}

	@Override
	public void render(
		MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks
	) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(
		MatrixStack matrixStack, float partialTicks, int x, int y
	) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.blit(
			matrixStack,
			i,
			j,
			0,
			0,
			this.xSize,
			this.inventoryRows * 18 + 17
		);
		this.blit(
			matrixStack,
			i,
			j + this.inventoryRows * 18 + 17,
			0,
			126,
			this.xSize,
			96
		);
	}

	@Override
	public WorkstationContainer getContainer() {
		return CONTAINER;
	}
}
