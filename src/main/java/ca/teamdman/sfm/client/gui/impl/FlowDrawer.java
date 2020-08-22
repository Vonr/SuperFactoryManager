package ca.teamdman.sfm.client.gui.impl;

import ca.teamdman.sfm.client.gui.core.BaseScreen;
import ca.teamdman.sfm.client.gui.core.Colour3f;
import ca.teamdman.sfm.client.gui.core.IFlowController;
import ca.teamdman.sfm.client.gui.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.core.IFlowView;
import ca.teamdman.sfm.client.gui.core.Size;
import ca.teamdman.sfm.common.flowdata.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import it.unimi.dsi.fastutil.ints.Int2BooleanOpenHashMap;
import java.util.List;
import net.minecraft.util.math.MathHelper;

public class FlowDrawer<T extends IFlowTangible & IFlowView> implements IFlowController, IFlowView,
	IFlowTangible {

	private static final int PADDING_X = 4;
	private static final int PADDING_Y = 4;
	private static final int ITEM_MARGIN_X = 4;
	private static final int ITEM_MARGIN_Y = 4;

	private final List<T> ITEMS;
	private final Int2BooleanOpenHashMap SELECTED;
	private final int ITEM_WIDTH;
	private final int ITEM_HEIGHT;
	private final boolean open = false;
	private final IFlowTangible PARENT;
	private final Position POSITION = new Position();
	private final Size SIZE = new Size(0, 0);
	private int scroll = 0;

	public FlowDrawer(IFlowTangible parent, List<T> items, int itemWidth, int itemHeight) {
		this.PARENT = parent;
		this.ITEMS = items;
		this.ITEM_WIDTH = itemWidth;
		this.ITEM_HEIGHT = itemHeight;
		this.SELECTED = new Int2BooleanOpenHashMap(items.size());
		updatePositionsAndSize();
	}

	@Override
	public Position getPosition() {
		return POSITION;
	}

	@Override
	public Size getSize() {
		return SIZE;
	}

	public void updatePositionsAndSize() {
		this.SIZE.setSize(
			(ITEM_WIDTH + ITEM_MARGIN_X) * getItemsPerRow() + PADDING_X,
			(ITEM_HEIGHT + ITEM_MARGIN_Y) * getItemsPerColumn() + PADDING_Y
		);
		this.POSITION.setXY(PARENT.getPosition().withOffset(PARENT.getSize().getWidth() + 5, 0));
		for (int i = 0; i < ITEMS.size(); i++) {
			ITEMS.get(i).getPosition().setXY(
				getPosition().getX() + getWrappedX(i),
				getPosition().getY() + getWrappedY(i)
			);
		}
	}

	public void fixScroll() {
		this.scroll = MathHelper.clamp(
			this.scroll,
			0,
			ITEMS.size() / getItemsPerRow() * ITEM_HEIGHT
		);
	}

	public void scrollDown() {
		this.scroll++;
		this.fixScroll();
		this.updatePositionsAndSize();
	}

	public void scrollUp() {
		this.scroll--;
		this.fixScroll();
		this.updatePositionsAndSize();
	}


	public int getItemsPerColumn() {
		return MathHelper.clamp(
			getItemRow(ITEMS.size()),
			1,
			7
		);
	}

	public int getItemsPerRow() {
		return 5;
	}

	public int getWrappedX(int index) {
		return getItemColumn(index)
			* (FlowItemStack.ITEM_TOTAL_WIDTH + ITEM_MARGIN_X)
			+ ITEM_MARGIN_X / 2
			+ PADDING_X / 2;
	}

	public int getItemColumn(int index) {
		return index % getItemsPerRow();
	}

	public int getItemRow(int index) {
		return (int) Math.floor(index / (float) getItemsPerRow());
	}

	public int getWrappedY(int index) {
		return getItemRow(index)
			* (FlowItemStack.ITEM_TOTAL_HEIGHT + ITEM_MARGIN_Y)
			+ ITEM_MARGIN_Y / 2
			+ PADDING_Y / 2;
	}


	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.drawRect(
			matrixStack,
			getPosition().getX(),
			getPosition().getY(),
			getSize().getWidth(),
			getSize().getHeight(),
			Colour3f.PANEL_BACKGROUND
		);
		for (int i = 0; i < ITEMS.size(); i++) {
			if (getItemRow(i) >= getItemsPerColumn()) {
				return;
			}
			ITEMS.get(i).draw(screen, matrixStack, mx, my, deltaTime);
		}
	}
}
