/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.timertrigger;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TimerTriggerFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class TimerTriggerFlowComponent extends FlowContainer implements
	FlowDataHolder<TimerTriggerFlowData> {

	final ManagerFlowController CONTROLLER;
	final Button BUTTON;
	final EditWindow WINDOW;
	TimerTriggerFlowData data;

	public TimerTriggerFlowComponent(ManagerFlowController controller, TimerTriggerFlowData data) {
		super();
		this.data = data;
		this.CONTROLLER = controller;

		this.BUTTON = new Button(this){
			@Override
			public List<? extends ITextProperties> getTooltip() {
				List<ITextProperties> rtn = new ArrayList<>();
				rtn.add(new TranslationTextComponent(
					"gui.sfm.flow.tooltip.timer_interval",
					TimerTriggerFlowComponent.this.data.interval,
					TimerTriggerFlowComponent.this.data.interval/20.0
				));
				return rtn;
			}
		};
		addChild(BUTTON);

		this.WINDOW = new EditWindow(this);
		addChild(WINDOW);

		controller.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TimerTriggerFlowData.class, this
		));
	}

	@Override
	public TimerTriggerFlowData getData() {
		return data;
	}

	@Override
	public void setData(TimerTriggerFlowData data) {
		this.data = data;
		BUTTON.onDataChanged();
		WINDOW.onDataChanged();
	}

	@Override
	public boolean isDeletable() {
		return true;
	}

	@Override
	public boolean isCloneable() {
		return true;
	}

	@Override
	public Position getCentroid() {
		return BUTTON.getCentroid();
	}

	@Override
	public Position snapToEdge(Position outside) {
		return BUTTON.snapToEdge(outside);
	}

	@Override
	public Optional<FlowComponent> getElementUnderMouse(
		int mx, int my
	) {
		return super.getElementUnderMouse(mx, my).map(__ -> this);
	}

}
