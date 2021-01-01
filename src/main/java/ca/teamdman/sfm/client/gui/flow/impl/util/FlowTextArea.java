/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

/**
 * @see net.minecraft.client.gui.widget.TextFieldWidget
 * @see net.minecraft.client.gui.fonts.TextInputUtil
 * @see net.minecraft.util.text.TextFormatting
 */
public class FlowTextArea extends FlowComponent {

	private final TextFieldWidget delegate;
	private String content;

	public FlowTextArea(BaseScreen screen, String content, Position pos, Size size) {
		super(new WidgetPositionDelegate(pos), new WidgetSizeDelegate(size));
		this.content = content;
		this.delegate = new PatchedTextFieldWidget(
			screen.getFontRenderer(),
			pos.getX(),
			pos.getY(),
			size.getWidth(),
			size.getHeight(),
			new StringTextComponent(content)
		);
		((WidgetPositionDelegate) getPosition()).setWidget(delegate);
		((WidgetSizeDelegate) getSize()).setWidget(delegate);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return delegate.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return delegate.keyReleased(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers, int mx, int my) {
		return delegate.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		return delegate.mouseClicked(mx, my, button);
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return delegate.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		return delegate.mouseReleased(mx, my, button);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		delegate.render(matrixStack, mx, my, deltaTime);
	}

	private static class PatchedTextFieldWidget extends TextFieldWidget {

		MatrixStack matrixStack;

		public PatchedTextFieldWidget(
			FontRenderer p_i232260_1_,
			int p_i232260_2_,
			int p_i232260_3_,
			int p_i232260_4_,
			int p_i232260_5_,
			ITextComponent p_i232260_6_
		) {
			super(
				p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
		}

		@Override
		public void renderButton(
			MatrixStack matrixStack,
			int mouseX,
			int mouseY,
			float partialTicks
		) {
			this.matrixStack = matrixStack;
			super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
		}

		@Override
		protected void drawSelectionBox(int startX, int startY, int endX, int endY) {
			RenderSystem.pushMatrix();
			RenderSystem.multMatrix(matrixStack.getLast().getMatrix());
			super.drawSelectionBox(startX, startY, endX, endY);
			RenderSystem.popMatrix();
		}
	}
}
