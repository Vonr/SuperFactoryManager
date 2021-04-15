package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;

public class RadioFlowButton extends SelectableFlowButton {

	private final RadioGroup GROUP;
	private final String HOVER_TEXT;
	public RadioFlowButton(
		Position pos,
		Size size,
		String text,
		RadioGroup group
	) {
		this(pos, size, text, null, group);
	}

	public RadioFlowButton(
		Position pos,
		Size size,
		String text,
		String hoverText,
		RadioGroup group
	) {
		super(pos, size, text);
		group.addMember(this);
		this.GROUP = group;
		this.HOVER_TEXT = hoverText;
	}

	@Override
	public List<ITextProperties> getTooltip() {
		List<ITextProperties> rtn = new ArrayList<>();
		if (HOVER_TEXT != null) {
			rtn.add(new StringTextComponent(HOVER_TEXT));
		}
		return rtn;
	}

	@Override
	public void onSelectionChanged() {
		if (isSelected()) {
			GROUP.setSelected(this);
		}
	}

	/**
	 * Only one member can be selected at once. Members store their selected property.
	 */
	public static class RadioGroup {

		private final HashSet<RadioFlowButton> MEMBERS = new HashSet<>();

		public void addMember(RadioFlowButton member) {
			MEMBERS.add(member);
		}

		public Optional<RadioFlowButton> getSelected() {
			return MEMBERS.stream().filter(RadioFlowButton::isSelected).findFirst();
		}

		public void setSelected(RadioFlowButton member) {
			for (RadioFlowButton button : MEMBERS) {
				if (member.equals(button)) {
					if (!member.isSelected()) {
						member.setSelected(true);
					}
				} else {
					button.setSelected(false);
				}
			}
			onSelectionChanged(member);
		}

		public void onSelectionChanged(RadioFlowButton member) {

		}
	}
}
