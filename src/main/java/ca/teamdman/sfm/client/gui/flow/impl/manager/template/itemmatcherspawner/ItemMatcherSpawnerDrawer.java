package ca.teamdman.sfm.client.gui.flow.impl.manager.template.itemmatcherspawner;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.MovementMatcher;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;

public class ItemMatcherSpawnerDrawer extends FlowDrawer {

	@SuppressWarnings("rawtypes")
	private final Consumer ACTION;

	public <T extends FlowData & MovementMatcher> ItemMatcherSpawnerDrawer(
		Consumer<T> action,
		Position pos
	) {
		super(pos, 3, 3);
		this.ACTION = action;
		addChild(new ItemPickerMatcherSpawnerButton(this));
		addChild(new ItemModMatcherSpawnerButton(this));
		update();
	}

	@Override
	public int getZIndex() {
		return super.getZIndex() + 100;
	}

	@Override
	public void draw(
		BaseScreen screen, MatrixStack matrixStack, int mx, int my, float deltaTime
	) {
		screen.pauseScissor();
		super.draw(screen, matrixStack, mx, my, deltaTime);
		screen.resumeScissor();
	}

	protected <T extends FlowData & MovementMatcher> void add(T data) {
		//noinspection unchecked
		ACTION.accept(data);
	}
}
