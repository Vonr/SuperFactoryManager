package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.HashSet;
import java.util.Optional;

public class RadioFlowButton extends SelectableFlowButton {

	private final RadioGroup GROUP;

	public RadioFlowButton(
		Position pos,
		Size size,
		String text,
		RadioGroup group
	) {
		super(pos, size, text);
		group.addMember(this);
		this.GROUP = group;
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
				if (!member.equals(button)) {
					button.setSelected(false);
				}
			}
			onSelectionChanged(member);
		}

		public void onSelectionChanged(RadioFlowButton member) {

		}
	}
}
