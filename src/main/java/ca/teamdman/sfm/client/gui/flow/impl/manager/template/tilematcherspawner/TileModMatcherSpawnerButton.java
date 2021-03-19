package ca.teamdman.sfm.client.gui.flow.impl.manager.template.tilematcherspawner;

import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.TileModMatcherFlowData;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

class TileModMatcherSpawnerButton extends FlowIconButton {

	private final TileMatcherSpawnerDrawer PARENT;

	public TileModMatcherSpawnerButton(TileMatcherSpawnerDrawer parent) {
		super(
			ButtonLabel.MODID_MATCHER,
			new Position()
		);
		PARENT = parent;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return Collections.singletonList(
			new TranslationTextComponent("gui.sfm.toolbox.add_tile_mod_matcher")
		);
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		FlowData data = new TileModMatcherFlowData(
			UUID.randomUUID(),
			"minecraft",
			false
		);
		PARENT.PARENT.getData().tileMatcherIds.add(data.getId());
		PARENT.PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(
			data,
			PARENT.PARENT.getData()
		);
		if (!Screen.hasShiftDown()) {
			PARENT.setVisibleAndEnabled(false);
		}
	}
}
