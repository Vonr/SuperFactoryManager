package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.core.Colour3f;
import ca.teamdman.sfm.client.gui.flow.core.IFlowController;
import ca.teamdman.sfm.client.gui.flow.core.IFlowTangible;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class FlowDrawer<T extends IFlowTangible & IFlowController> implements IFlowController, IFlowView,
	IFlowTangible {

	private static final int PADDING_X = 4;
	private static final int PADDING_Y = 4;
	private static final int ITEM_MARGIN_X = 4;
	private static final int ITEM_MARGIN_Y = 4;

	public final List<T> ITEMS;
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
		onDataChange();
	}

	@Override
	public Position getPosition() {
		return POSITION;
	}

	@Override
	public Size getSize() {
		return SIZE;
	}


	@Override
	public void onDataChange() {
		this.SIZE.setSize(
			(ITEM_WIDTH + ITEM_MARGIN_X) * getItemsPerRow() + PADDING_X,
			(ITEM_HEIGHT + ITEM_MARGIN_Y) * getItemsPerColumn() + PADDING_Y
		);
		this.POSITION.setXY(PARENT.getPosition().withOffset(PARENT.getSize().getWidth() + 5, 0));
		for (int i = 0; i < ITEMS.size(); i++) {
			ITEMS.get(i).getPosition().setXY(
				getPosition().getX() + getWrappedX(i),
				getPosition().getY() + getWrappedY(i) - scroll
			);
		}
	}

	public void fixScroll() {
		this.scroll = MathHelper.clamp(
			this.scroll,
			0,
			Math.max(
				0,
				(
					(int) Math.ceil(ITEMS.size() / (float) getItemsPerRow())
						- getItemsPerColumn()
				) * (ITEM_HEIGHT + ITEM_MARGIN_Y)
			)
		);
	}

	@Override
	public boolean mouseScrolled(int mx, int my, double scroll) {
		if (scroll > 0) {
			scrollUp();
		} else {
			scrollDown();
		}
		return true;
	}

	public void scrollDown() {
		this.scroll += 7;
		this.fixScroll();
		this.onDataChange();
	}

	public void scrollUp() {
		this.scroll -= 7;
		this.fixScroll();
		this.onDataChange();
	}


	public int getItemsPerColumn() {
		return MathHelper.clamp(
			getItemRow(ITEMS.size()),
			1,
			7
		);
	}

	public int getItemsPerRow() {
		return MathHelper.clamp(ITEMS.size(), 1, 5);
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
	public boolean mousePressed(int mx, int my, int button) {
		return ITEMS.stream().anyMatch(v -> v.mousePressed(mx, my, button));
	}

	@Override
	public boolean mouseReleased(int mx, int my, int button) {
		return ITEMS.stream().anyMatch(v -> v.mouseReleased(mx, my, button));
	}

	@Override
	public boolean mouseDragged(int mx, int my, int button, int dmx, int dmy) {
		return ITEMS.stream().anyMatch(v -> v.mouseDragged(mx, my, button, dmx, dmy));
	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return ITEMS.stream().anyMatch(v -> v.keyPressed(keyCode, scanCode, modifiers, mx, my));
	}

	@Override
	public boolean keyReleased(int keyCode, int scanCode, int modifiers, int mx, int my) {
		return ITEMS.stream().anyMatch(v -> v.keyReleased(keyCode, scanCode, modifiers, mx, my));
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

		RenderSystem.pushMatrix();

		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		int x = getPosition().getX();
		int y = getPosition().getY();
		int myWidth = getSize().getWidth();
		int myHeight = getSize().getHeight();
		screen.scissorScaledArea(
			x + PADDING_X / 2,
			y + PADDING_Y / 2,
			myWidth - PADDING_X,
			myHeight - PADDING_Y
		);
		int start = scroll / (ITEM_HEIGHT + ITEM_MARGIN_Y) * getItemsPerRow();
		int lastRow = start + (myHeight / ITEM_HEIGHT) * getItemsPerRow();
		for (int i = start; i < Math.min(lastRow, ITEMS.size()); i++) {
			ITEMS.get(i).getView().draw(screen, matrixStack, mx, my, deltaTime);
		}
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		RenderSystem.popMatrix();
	}
}
