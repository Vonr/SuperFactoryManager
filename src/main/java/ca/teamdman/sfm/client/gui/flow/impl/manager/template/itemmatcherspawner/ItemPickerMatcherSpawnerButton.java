package ca.teamdman.sfm.client.gui.flow.impl.manager.template.itemmatcherspawner;

import ca.teamdman.sfm.client.gui.flow.impl.util.ButtonLabel;
import ca.teamdman.sfm.client.gui.flow.impl.util.FlowIconButton;
import ca.teamdman.sfm.common.flow.core.Position;
import ca.teamdman.sfm.common.flow.data.ItemPickerMatcherFlowData;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemPickerMatcherSpawnerButton extends FlowIconButton {

	private final ItemMatcherSpawnerDrawer PARENT;

	public ItemPickerMatcherSpawnerButton(
		ItemMatcherSpawnerDrawer PARENT
	) {
		super(
			ButtonLabel.PICKER_MATCHER,
			new Position()
		);
		this.PARENT = PARENT;
	}

	@Override
	public List<? extends ITextProperties> getTooltip() {
		return Collections.singletonList(
			new TranslationTextComponent("gui.sfm.toolbox.add_itemstackcomparer_matcher")
		);
	}

	@Override
	public void onClicked(int mx, int my, int button) {
		PARENT.add(new ItemPickerMatcherFlowData(
			UUID.randomUUID(),
			new ItemStack(Blocks.STONE),
			0,
			false
		));
		if (!Screen.hasShiftDown()) {
			PARENT.setVisibleAndEnabled(false);
		}
	}
}
