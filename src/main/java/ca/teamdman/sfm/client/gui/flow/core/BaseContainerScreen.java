/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

import ca.teamdman.sfm.SFM;
import ca.teamdman.sfm.common.container.BaseContainer;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class BaseContainerScreen<C extends BaseContainer<?>> extends BaseScreen implements
	IHasContainer<C> {


	public static final int LEFT = 0;
	public static final int MIDDLE = 2;
	public static final int RIGHT = 1;
	private static final ResourceLocation BACKGROUND_LEFT = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/background_1.png"
	);
	private static final ResourceLocation BACKGROUND_RIGHT = new ResourceLocation(
		SFM.MOD_ID,
		"textures/gui/background_2.png"
	);
	public final C CONTAINER;

	public BaseContainerScreen(
		C container, int width, int height, PlayerInventory inv,
		ITextComponent name
	) {
		super(name, width, height);
		this.CONTAINER = container;
	}


	@Override
	public void drawScaled(
		MatrixStack matrixStack, int mx, int my,
		float partialTicks
	) {
		drawBackground(matrixStack);
	}

	public void drawBackground(MatrixStack matrixStack) {
		RenderSystem.color4f(1f, 1f, 1f, 1f);
		bindTexture(BACKGROUND_LEFT);
		drawTexture(matrixStack, 0, 0, 0, 0, 256, 256);
		bindTexture(BACKGROUND_RIGHT);
		drawTexture(matrixStack, 256, 0, 0, 0, 256, 256);
	}

	@Override
	@Nonnull
	public C getContainer() {
		return CONTAINER;
	}
}
