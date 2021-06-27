package ca.teamdman.sfm.client.gui.screen;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.WorkstationContainer;
import ca.teamdman.sfm.common.net.PacketHandler;
import ca.teamdman.sfm.common.net.packet.workstation.C2SWorkstationAutoLearnChangedPacket;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

public class WorkstationScreen extends
	ContainerScreen<WorkstationContainer> implements
	IHasContainer<WorkstationContainer> {

	private static final ResourceLocation MAIN_TEXTURE = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/container/workstation_main.png"
	);
	private static final ResourceLocation SIDE_TEXTURE = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/container/workstation_side.png"
	);
//	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation(
//		"textures/gui/container/generic_54.png");

	private final WorkstationContainer CONTAINER;
	private int inventoryRows = 3;
	private TextFieldWidget searchField;
	private ExtendedButton learnButton;
	private List<Rectangle2d> exclusionAreas;

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
		int i = (this.width - this.getXSize()) / 2;
		int j = (this.height - this.getYSize()) / 2;
//		searchField = new TextFieldWidget(
//			this.font,
//			i,
//			j - 25,
//			103,
//			12,
//			new TranslationTextComponent("gui.sfm.workstation.search.text")
//		);
////		searchField.setCanLoseFocus(false);
//		searchField.setTextColor(-1);
//		searchField.setDisabledTextColour(-1);
//		setFocusedDefault(searchField);

		learnButton = new ExtendedButton(
			i + 108,
			j + 7,
			60,
			15,
			new TranslationTextComponent(
				"gui.sfm.workstation.button.auto_learn.text")
				.withStyle(CONTAINER.getSource().isAutoLearnEnabled()
					? TextFormatting.GREEN : TextFormatting.RED),
			(button) -> {
				PacketHandler.INSTANCE.sendToServer(
					new C2SWorkstationAutoLearnChangedPacket(
						getMenu().containerId,
						CONTAINER.getSource().getBlockPos(),
						!CONTAINER.getSource().isAutoLearnEnabled()
					)
				);
			}
		);
		addButton(learnButton);

		exclusionAreas = Arrays.asList(
			new Rectangle2d(i - 104, j, 100, 182)
		);
//		addButton(searchField);
	}


	@Override
	public void render(
		MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks
	) {
		this.renderBackground(matrixStack);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
		this.renderTooltip(matrixStack, mouseX, mouseY);
	}

	@Override
	protected void renderBg(
		MatrixStack matrixStack, float partialTicks, int x, int y
	) {
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.minecraft.getTextureManager().bind(MAIN_TEXTURE);
		int i = this.leftPos;
		int j = (this.height - this.getYSize()) / 2;
		this.blit(matrixStack, i, j, 0, 0, this.getXSize(), this.getYSize());
		this.minecraft.getTextureManager().bind(SIDE_TEXTURE);
		this.blit(matrixStack, i - 104, j, 0, 0, 97, 182);
		this.blit(matrixStack, i - 7, j, 169, 0, 10, 182);
	}

	@Override
	public WorkstationContainer getMenu() {
		return CONTAINER;
	}

	public List<Rectangle2d> getExclusionAreas() {
		return exclusionAreas;
	}
}
