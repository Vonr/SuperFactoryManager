package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.itempickermatcher;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowButton;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemPickerMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Objects;

public class ItemPickerMatcherFlowComponent extends
	FlowContainer implements
	FlowDataHolder<ItemPickerMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	private final StackIconButton ICON;
	private final TextAreaFlowComponent QUANTITY_INPUT;
	private ItemPickerMatcherFlowData data;

	public ItemPickerMatcherFlowComponent(
		ManagerFlowController parent,
		ItemPickerMatcherFlowData data
	) {
		super(
			new Position(ItemStackFlowButton.DEFAULT_SIZE.getWidth() + 5, 0),
			new Size(100, 24)
		);
		PARENT = parent;
		this.data = data;

		// Quantity input box
		this.QUANTITY_INPUT = new TextAreaFlowComponent(
			parent.SCREEN,
			data.getDisplayQuantity(),
			"#",
			new Position(4, 4),
			new Size(38, 16)
		){
			@Override
			public void clear() {
				delegate.setValue("0");
			}
		};
		QUANTITY_INPUT.setValidator(next -> Objects.nonNull(next) && next.matches("\\d*"));
		QUANTITY_INPUT.setResponder(next -> {
			try {
				int nextVal = Integer.parseInt(next);
				if (nextVal != data.quantity) {
					data.quantity = nextVal;
					parent.SCREEN.sendFlowDataToServer(data);
				}
			} catch (NumberFormatException ignored) {
			}
		});
		addChild(QUANTITY_INPUT);

		// "x" separator
		addChild(new FlowComponent(new Position(45, 8), new Size(0, 0)) {
			@Override
			public void draw(
				BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
			) {
				screen.drawString(
					matrixStack,
					"x",
					getPosition().getX(),
					getPosition().getY(),
					CONST.TEXT_DARK
				);
			}
		});

		// Display itemstack
//		this.ICON = new ItemStackFlowButton(data.stack, new Position(54, 2));
		this.ICON = new StackIconButton(this, new Position(54, 2));
		addChild(ICON);

		// Add change listener
		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			ItemPickerMatcherFlowData.class, this
		));
	}

	@Override
	public void draw(BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime) {
		screen.clearRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight()
		);
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			CONST.PANEL_BACKGROUND_LIGHT
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public ItemPickerMatcherFlowData getData() {
		return data;
	}	@Override
	public boolean isVisible() {
		return data.open;
	}

	@Override
	public void setData(ItemPickerMatcherFlowData data) {
		this.data = data;
		ICON.BUTTON.setItemStack(data.stack);
		QUANTITY_INPUT.setContent(Integer.toString(data.quantity));
	}

	@Override
	public boolean isEnabled() {
		return isVisible();
	}

	@Override
	public void setVisible(boolean visible) {
		if (data.open != visible) {
			data.open = visible;
			PARENT.SCREEN.sendFlowDataToServer(data);
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		setVisible(enabled);
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 120;
	}

}
