/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonBackground;
import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ConditionLineNodeFlowData;
import ca.teamdman.sfm.common.flow.data.ConditionLineNodeFlowData.Responsibility;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class ConditionLineNodeFlowComponent extends FlowIconButton implements
	FlowDataHolder<ConditionLineNodeFlowData> {

	public final ManagerFlowController CONTROLLER;
	private ConditionLineNodeFlowData data;

	@Override
	public int getZIndex() {
		return super.getZIndex() + 11;
	}

	public ConditionLineNodeFlowComponent(ManagerFlowController controller, ConditionLineNodeFlowData data) {
		super(
			ButtonBackground.LINE_NODE,
			ButtonBackground.LINE_NODE,
			ButtonLabel.NONE,
			data.getPosition().copy()
		);
		this.data = data;
		this.CONTROLLER = controller;
		this.CONTROLLER.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ConditionLineNodeFlowData.class, this
		));
		setDraggable(true);
	}

	@Override
	public void onDragFinished(int dx, int dy, int mx, int my) {
		data.position = getPosition();
		CONTROLLER.SCREEN.sendFlowDataToServer(data);
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		if (getData().responsibility == Responsibility.ACCEPTED) {
			RenderSystem.color4f(0.5f, 0.9f, 0.5f, 1f);
		} else if (getData().responsibility == Responsibility.REJECTED) {
			RenderSystem.color4f(0.9f, 0.5f, 0.5f, 1f);
		}
		super.draw(screen, matrixStack, mx, my, deltaTime);
		RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public Position getPosition() {
		return getData().getPosition();
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		ArrayList<ITextProperties> rtn = new ArrayList<>();
		rtn.add(new TranslationTextComponent(getData().responsibility.DISPLAY_NAME));
		return rtn;
	}

	@Override
	public ConditionLineNodeFlowData getData() {
		return data;
	}

	@Override
	public void setData(ConditionLineNodeFlowData data) {
		this.data = data;
	}

	@Override
	public void onClicked(int mx, int my, int button) {

	}
}
