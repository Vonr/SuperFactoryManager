package ca.teamdman.sfm.client.gui.flow.impl.manager.template.itemmatcherspawner;

import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.FlowData;
import ca.teamdman.sfm.common.flow.data.ItemModMatcherFlowData;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemModIdMatcherSpawnerButton extends FlowIconButton {

	private final ItemMatcherSpawnerDrawer PARENT;

	public ItemModIdMatcherSpawnerButton(
		ItemMatcherSpawnerDrawer PARENT
	) {
		super(
			ButtonLabel.MODID_MATCHER,
			new Position()
		);
		this.PARENT = PARENT;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		List<ITextProperties> rtn = new ArrayList<>();
		rtn.add(new TranslationTextComponent("gui.sfm.toolbox.add_modid_matcher"));
		return rtn;
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		FlowData data = new ItemModMatcherFlowData(
			UUID.randomUUID(),
			"minecraft",
			0,
			false
		);
		PARENT.PARENT.getData().itemMatcherIds.add(data.getId());
		PARENT.PARENT.CONTROLLER.SCREEN.sendFlowDataToServer(
			data,
			PARENT.PARENT.getData()
		);
		if (!Screen.hasShiftDown()) {
			PARENT.setVisibleAndEnabled(false);
		}
	}
}
