/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.FlowData;
import ca.teamdman.sfm.common.flow.data.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;

public abstract class FlowContainer extends FlowComponent {


	private final ArrayList<FlowComponent> children = new ArrayList<FlowComponent>() {
		@Override
		public boolean add(FlowComponent o) {
			boolean rtn = super.add(o);
			this.sort(Comparator.comparingInt(IFlowView::getZIndex));
			return rtn;
		}
	};

	public FlowContainer() {
	}

	public FlowContainer(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public FlowContainer(
		Position pos,
		Size size
	) {
		super(pos, size);
	}

	@Override
	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		Optional<FlowComponent> rtn = children.stream()
			.map(c -> c.getElementUnderMouse(mx, my))
			.filter(Optional::isPresent)
			.map(Optional::get)
			.findFirst();
		if (rtn.isPresent()) {
			return rtn;
		}
		return super.getElementUnderMouse(mx, my);
	}

	public ArrayList<FlowComponent> getChildren() {
		return children;
	}

	public Optional<FlowComponent> findFirstChild(FlowData data) {
		return findFirstChild(data.getId());
	}

	public Optional<FlowComponent> findFirstChild(UUID id) {
		return getChildren().stream().filter(c -> c instanceof FlowDataHolder
			&& ((FlowDataHolder) c).getData().getId().equals(id)).findFirst();
	}

	public void addChild(FlowComponent c) {
		this.children.add(c);
	}

	public boolean removeChild(FlowComponent c) {
		return this.children.remove(c);
	}

	@Override
	public boolean mousePressed(int mx, int my, int button) {
		if (super.mousePressed(mx, my, button)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.mousePressed(pmx, pmy, button));
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		if (super.mouseReleased(mx, my, button)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.mouseReleased(pmx, pmy, button));
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		if (super.mouseDragged(mx, my, button, dmx, dmy)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.mouseDragged(pmx, pmy, button, dmx, dmy));
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (super.keyPressed(keyCode, scanCode, modifiers, mx, my)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.keyPressed(keyCode, scanCode, modifiers, pmx, pmy));
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		if (super.keyReleased(keyCode, scanCode, modifiers, mx, my)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.keyReleased(keyCode, scanCode, modifiers, pmx, pmy));
	}

	@Override
	public boolean mouseScrolled(int mx, int my, double scroll) {
		if (super.mouseScrolled(mx, my, scroll)) {
			return true;
		}
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return children.stream()
			.filter(FlowComponent::isEnabled)
			.anyMatch(c -> c.mouseScrolled(pmx, pmy, scroll));
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		matrixStack.push();
		matrixStack.translate(getPosition().getX(), getPosition().getY(), 0);
		for (FlowComponent c : children) {
			if (c.isVisible()) {
				c.draw(
					screen,
					matrixStack,
					mx - getPosition().getX(),
					my - getPosition().getY(),
					deltaTime
				);
			}
		}
		matrixStack.pop();
	}

	@Override
	public void drawGhost(
		BaseScreen screen,
		MatrixStack matrixStack,
		int mx,
		int my,
		float deltaTime
	) {
		matrixStack.push();
		matrixStack.translate(getPosition().getX(), getPosition().getY(), 0);
		for (FlowComponent c : children) {
			if (c.isVisible()) {
				c.drawGhost(
					screen,
					matrixStack,
					mx - getPosition().getX(),
					my - getPosition().getY(),
					deltaTime
				);
			}
		}
		matrixStack.pop();
	}
}
