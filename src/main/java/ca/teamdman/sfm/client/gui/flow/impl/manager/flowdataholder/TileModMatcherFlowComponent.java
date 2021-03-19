package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f.CONST;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.manager.core.ManagerFlowController;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowContainer;
import ca.teamdman.sfm.client.gui.flow.impl.util.ItemStackFlowComponent;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.FlowDataHolder;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TileModMatcherFlowData;
import ca.teamdman.sfm.common.flow.holder.FlowDataHolderObserver;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.Locale;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;

public class TileModMatcherFlowComponent extends
	FlowContainer implements
	FlowDataHolder<TileModMatcherFlowData> {

	protected final ManagerFlowController PARENT;
	private final TextAreaFlowComponent MOD_ID_INPUT;
	private TileModMatcherFlowData data;

	public TileModMatcherFlowComponent(
		ManagerFlowController parent,
		TileModMatcherFlowData data
	) {
		super(
			new Position(ItemStackFlowComponent.DEFAULT_SIZE.getWidth() + 5, 0),
			new Size(66, 24)
		);
		PARENT = parent;
		this.data = data;

		// modid text input
		this.MOD_ID_INPUT = new TextAreaFlowComponent(
			parent.SCREEN,
			data.modId,
			"minecraft",
			new Position(4, 4),
			new Size(58, 16)
		) {
			@Override
			public void clear() {
				delegate.setText("");
			}
		};
		MOD_ID_INPUT.setValidator(next ->
			Objects.nonNull(next)
				&& ResourceLocation.isResouceNameValid(next.toLowerCase(Locale.ROOT) + ":"));
		MOD_ID_INPUT.setResponder(next -> {
			if (!next.equals(data.modId)) {
				data.modId = next.toLowerCase(Locale.ROOT);
				parent.SCREEN.sendFlowDataToServer(data);
			}
		});
		addChild(MOD_ID_INPUT);

		// Add change listener
		parent.SCREEN.getFlowDataContainer().addObserver(new FlowDataHolderObserver<>(
			TileModMatcherFlowData.class, this
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
		screen.drawBorder(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			1,
			CONST.PANEL_BORDER
		);
		super.draw(screen, matrixStack, mx, my, deltaTime);
	}

	@Override
	public TileModMatcherFlowData getData() {
		return data;
	}

	@Override
	public void setData(TileModMatcherFlowData data) {
		this.data = data;
		MOD_ID_INPUT.setContent(data.modId);
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
