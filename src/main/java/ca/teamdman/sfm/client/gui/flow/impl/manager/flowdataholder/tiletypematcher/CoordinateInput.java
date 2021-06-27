package ca.teamdman.sfm.client.gui.flow.impl.manager.flowdataholder.tiletypematcher;

import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.client.gui.flow.impl.util.TextAreaFlowComponent;
import ca.teamdman.sfm.common.flow.core.Position;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

class CoordinateInput extends TextAreaFlowComponent {
	public static Pattern p = Pattern.compile("^\\s*-?\\d*\\s*$");
	public CoordinateInput(
		TileTypeMatcherFlowComponent parent,
		Supplier<Integer> getter,
		Consumer<Integer> setter,
		Position pos,
		Size size
	) {
		super(
			parent.PARENT.SCREEN,
			getter.get().toString(),
			"#",
			pos,
			size
		);

		setValidator(n -> p.matcher(n).matches());
		setResponder(n -> {
			try {
				int next = Integer.parseInt(n);
				if (getter.get() != next) {
					setter.accept(next);
					parent.PARENT.SCREEN.sendFlowDataToServer(parent.getData());
				}
			} catch (NumberFormatException ignored) {
			}
		});
	}
}
