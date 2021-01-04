/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

public abstract class FlowContainer extends FlowComponent {


	private final ArrayList<FlowComponent> children = new ArrayList<FlowComponent>() {
		@Override
		public boolean add(FlowComponent o) {
			boolean rtn = super.add(o);
			this.sort(Comparator.comparingInt(IFlowView::getZIndex));
			return rtn;
		}
	};

	/**
	 * Containers with no area are not draggable
	 */
	public FlowContainer() {
		setDraggable(false);
	}

	/**
	 * Containers with no area are not draggable
	 */
	public FlowContainer(Position pos) {
		this(pos, new Size(0, 0));
		setDraggable(false);
	}

	public FlowContainer(
		Position pos,
		Size size
	) {
		super(pos, size);
	}

	/**
	 * Adds the children to a stream in reversed order. Higher z index means that the elements
	 * render later Rendering later means they render 'on top'. Clicking an element that is on to of
	 * another expects the top element to consume click first.
	 */
	public Stream<FlowComponent> getEnabledChildrenControllers() {
		Builder<FlowComponent> builder = Stream.builder();
		ListIterator<FlowComponent> iter = children.listIterator(children.size());
		while (iter.hasPrevious()) {
			builder.add(iter.previous());
		}
		return builder.build().filter(FlowComponent::isEnabled);
	}

	@Override
	public Optional<FlowComponent> getElementUnderMouse(int mx, int my) {
		Optional<FlowComponent> rtn = children.stream()
			.filter(FlowComponent::isVisible)
			.map(c -> c.getElementUnderMouse(
				mx + getPosition().getX(),
				my + getPosition().getY()
			))
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
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.mousePressed(pmx, pmy, button))
			|| super.mousePressed(mx, my, button);
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.mouseReleased(pmx, pmy, button))
			|| super.mouseReleased(mx, my, button);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers, int mx, int my) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.charTyped(codePoint, modifiers, pmx, pmy))
			|| super.charTyped(codePoint, modifiers, mx, my);
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.mouseDragged(pmx, pmy, button, dmx, dmy))
			|| super.mouseDragged(mx, my, button, dmx, dmy);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.keyPressed(keyCode, scanCode, modifiers, pmx, pmy))
			|| super.keyPressed(keyCode, scanCode, modifiers, mx, my);
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.keyReleased(keyCode, scanCode, modifiers, pmx, pmy)
				|| super.keyReleased(keyCode, scanCode, modifiers, mx, my));
	}

	@Override
	public boolean mouseScrolled(int mx, int my, double scroll) {
		int pmx = mx - getPosition().getX();
		int pmy = my - getPosition().getY();
		return getEnabledChildrenControllers()
			.anyMatch(c -> c.mouseScrolled(pmx, pmy, scroll))
			|| super.mouseScrolled(mx, my, scroll);
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		matrixStack.push();
		matrixStack.translate(getPosition().getX(), getPosition().getY(), 0);
		for (FlowComponent c : getChildren()) {
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
		drawTooltip(screen, matrixStack, mx, my, deltaTime);
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
