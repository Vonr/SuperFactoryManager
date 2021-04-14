package ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner;

import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.TilePositionMatcherFlowData;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

class TilePositionMatcherSpawnerButton extends FlowIconButton {

	private final TileMatcherSpawnerDrawer PARENT;

	public TilePositionMatcherSpawnerButton(TileMatcherSpawnerDrawer parent) {
		super(
			ButtonLabel.PICKER_MATCHER,
			new Position()
		);
		PARENT = parent;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return Collections.singletonList(
			new TranslationTextComponent("gui.sfm.toolbox.add_tile_position_matcher")
		);
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.add(new TilePositionMatcherFlowData(
			UUID.randomUUID(),
			BlockPos.ZERO,
			false
		));
		if (!Screen.hasShiftDown()) {
			PARENT.setVisibleAndEnabled(false);
		}
	}
}
