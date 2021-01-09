package ca.teamdman.sfm.client.gui.flow.core;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

public abstract class ComponentScreen<T extends FlowComponent> extends BaseScreen {

	public ComponentScreen(
		ITextComponent titleIn,
		int scaledWidth,
		int scaledHeight
	) {
		super(titleIn, scaledWidth, scaledHeight);
	}

	@Override
	public void drawScaled(MatrixStack matrixStack, int mx, int my, float partialTicks) {
		super.drawScaled(matrixStack, mx, my, partialTicks);
		getComponent().draw(this, matrixStack, mx, my, partialTicks);
	}

	@Override
	public boolean mouseClickedScaled(int mx, int my, int button) {
		return getComponent().mousePressed(mx, my, button);
	}

	@Override
	public boolean keyPressedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return getComponent().keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleasedScaled(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return getComponent().keyReleased(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean mouseReleasedScaled(int mx, int my, int button) {
		return getComponent().mouseReleased(mx, my, button);
	}

	@Override
	public boolean mouseMovedScaled(int mx, int my) {
		return getComponent().mouseMoved(mx, my, false);
	}

	@Override
	public boolean onMouseDraggedScaled(int mx, int my, int button, int dmx, int dmy) {
		return getComponent().mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseScrolledScaled(int mx, int my, double scroll) {
		return getComponent().mouseScrolled(mx, my, scroll);
	}

	@Override
	public boolean charTypedScaled(char codePoint, int modifiers, int mx, int my) {
		return getComponent().charTyped(codePoint, modifiers, mx, my);
	}

	@Override
	public void tick() {
		getComponent().tick();
	}

	public abstract T getComponent();
}
