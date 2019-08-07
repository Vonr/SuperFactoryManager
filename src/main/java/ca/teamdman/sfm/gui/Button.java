package ca.teamdman.sfm.gui;

import net.minecraft.util.text.TranslationTextComponent;

public class Button extends Component {
	private TranslationTextComponent label;
	private Runnable onClick;
	private int width, height;
	public TranslationTextComponent getLabel() {
		return label;
	}

	public void click() {
		onClick.run();
	}

	public boolean isInBounds(int mx, int my) {
		return mx >= x && mx <= x+width && my >= y && my <= y+height;
	}

	public Button(int x, int y, int width, int height, TranslationTextComponent label, Runnable onClick) {
		super(x, y);
		this.width = width;
		this.height = height;
		this.label = label;
		this.onClick = onClick;
	}


}
