package ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner;

import ca.teamdman.sfm.client.gui.flow.core.BaseScreen;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowDrawer;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.core.TileMatcher;
import ca.teamdman.sfm.common.flow.data.FlowData;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Consumer;

public class TileMatcherSpawnerDrawer extends FlowDrawer {

	@SuppressWarnings("rawtypes")
	private final Consumer ACTION;

	public <T extends FlowData & TileMatcher> TileMatcherSpawnerDrawer(
		Consumer<T> action,
		Position pos
	) {
		super(pos, 3, 3);
		ACTION = action;
		addChild(new TilePositionMatcherSpawnerButton(this));
		addChild(new TileModMatcherSpawnerButton(this));
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

	public <T extends FlowData & TileMatcher> void add(T matcher) {
		//noinspection unchecked
		ACTION.accept(matcher);
	}
}
