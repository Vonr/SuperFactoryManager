package ca.teamdman.sfm.client.gui.flow.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public abstract class ControllerScreen<T extends IFlowController & IFlowView> extends
	BaseScreen implements
	IHasController<T> {

	public ControllerScreen(
		ITextComponent titleIn,
		int scaledWidth,
		int scaledHeight
	) {
		super(titleIn, scaledWidth, scaledHeight);
	}

	@Override
	public void drawScaled(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		super.drawScaled(matrixStack, mx, my, partialTicks);
		getController().draw(this, matrixStack, mx, my, partialTicks);
	}

	@Override
	public boolean mouseClickedScaled(int mx, int my, int button) {
		return getController().mousePressed(mx, my, button);
	}

	@Override
	public boolean keyPressedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return getController().keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleasedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return getController().keyReleased(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean mouseReleasedScaled(int mx, int my, int button) {
		return getController().mouseReleased(mx, my, button);
	}

	@Override
	public boolean onMouseDraggedScaled(int mx, int my, int button, int dmx, int dmy) {
		return getController().mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseScrolledScaled(int mx, int my, double scroll) {
		return getController().mouseScrolled(mx, my, scroll);
	}

	@Override
	public boolean charTypedScaled(char codePoint, int modifiers, int mx, int my) {
		return getController().charTyped(codePoint, modifiers, mx, my);
	}
}
