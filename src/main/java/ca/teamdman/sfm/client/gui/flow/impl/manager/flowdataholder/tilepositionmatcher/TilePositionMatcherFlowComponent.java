package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tilepositionmatcher;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.math.BlockPos;

public class TilePositionMatcherFlowComponent extends FlowContainer implements
	FlowDataHolder<TilePositionMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	protected final Picker PICKER;
	private final CoordinateInput X_INPUT, Y_INPUT, Z_INPUT;
	private final Icon ICON;
	private TilePositionMatcherFlowData data;

	public TilePositionMatcherFlowComponent(
		ManagerFlowController parent,
		TilePositionMatcherFlowData data
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

		X_INPUT = new CoordinateInput(
			this,
			() -> getData().position.getX(),
			n -> getData().position = new BlockPos(
				n, getData().position.getY(), getData().position.getZ()
			),
			new Position(30, 4),
			new Size(30, 17)
		);
		addChild(X_INPUT);
		Y_INPUT = new CoordinateInput(
			this,
			() -> getData().position.getY(),
			n -> getData().position = new BlockPos(
				getData().position.getX(), n, getData().position.getZ()
			),
			new Position(65, 4),
			new Size(30, 17)
		);
		addChild(Y_INPUT);
		Z_INPUT = new CoordinateInput(
			this,
			() -> getData().position.getZ(),
			n -> getData().position = new BlockPos(
				getData().position.getX(), getData().position.getY(), n
			),
			new Position(100, 4),
			new Size(30, 17)
		);
		addChild(Z_INPUT);

		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TilePositionMatcherFlowData.class, this
		));
	}

	@Override
	public TilePositionMatcherFlowData getData() {
		return data;
	}

	@Override
	public void setData(TilePositionMatcherFlowData data) {
		this.data = data;
		ICON.cycleItemStack();
		PICKER.rebuildSuggestions();
		X_INPUT.setContent(Integer.toString(data.position.getX()));
		Y_INPUT.setContent(Integer.toString(data.position.getY()));
		Z_INPUT.setContent(Integer.toString(data.position.getZ()));
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
