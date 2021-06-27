package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tiletypematcher;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TileTypeMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.awt.TextArea;
import net.minecraft.util.math.BlockPos;

public class TileTypeMatcherFlowComponent extends FlowContainer implements
	FlowDataHolder<TileTypeMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	protected final Picker PICKER;
	private final CoordinateInput TEXT_INPUT, Y_INPUT, Z_INPUT;
	private final Icon ICON;
	private TileTypeMatcherFlowData data;

	public TileTypeMatcherFlowComponent(
		ManagerFlowController parent,
		TileTypeMatcherFlowData data
	) {
		super(
			new Position(0, 0),
			new Size(136, 26)
		);
		this.PARENT = parent;
		this.data = data;

		ICON = new Icon(this, new Position(3, 3));
		addChild(ICON);

		PICKER = new Picker(data, parent, new Position(25, 0));
		PICKER.setVisibleAndEnabled(false);
		addChild(PICKER);

		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TileTypeMatcherFlowData.class, this
		));
	}

	@Override
	public TileTypeMatcherFlowData getData() {
		return data;
	}

	@Override
	public void setData(TileTypeMatcherFlowData data) {
		this.data = data;
		ICON.cycleItemStack();
		PICKER.rebuildSuggestions();
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
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
			CONST.PANEL_BACKGROUND_NORMAL
		);
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			CONST.PANEL_BACKGROUND_DARK
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public boolean isVisible() {
		return data.open;
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
