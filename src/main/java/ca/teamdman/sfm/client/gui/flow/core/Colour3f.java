/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.core;

public class Colour3f {

	public final float RED, GREEN, BLUE;

	public Colour3f(float RED, float GREEN, float BLUE) {
		this.RED = RED;
		this.GREEN = GREEN;
		this.BLUE = BLUE;
	}

	public Colour3f(int RED, int GREEN, int BLUE) {
		this.RED = RED / 255f;
		this.GREEN = GREEN / 255f;
		this.BLUE = BLUE / 255f;
	}

	public Colour3f(int color) {
		this.RED = (color >> 16 & 0xFF) / 255f;
		this.GREEN = (color >> 8 & 0xFF) / 255f;
		this.BLUE = (color & 0xFF) / 255f;
	}

	public int toInt() {
		return (((int) (RED * 255)) << 16) | (((int) (GREEN * 255)) << 8) | (((int) (BLUE * 255)));
	}

	public static class CONST {

		public static final Colour3f BUTTON_BACKGROUND_SELECTED = new Colour3f(0xafabab);
		public static final Colour3f BUTTON_BACKGROUND_NORMAL = new Colour3f(0xdbdbdb);
		public static final Colour3f BUTTON_BORDER_SELECTED = new Colour3f(0xFF0000);
		public static final Colour3f BUTTON_BORDER_NORMAL = new Colour3f(0xd0d0d0);
		public static final Colour3f BUTTON_BACKGROUND_NORMAL_HOVER = new Colour3f(0x6666cc);
		public static final Colour3f BUTTON_BORDER_NORMAL_HOVER = new Colour3f(0x6060c0);
		public static final Colour3f BUTTON_BACKGROUND_SELECTED_HOVER = new Colour3f(0x6666cc);
		public static final Colour3f BUTTON_BORDER_SELECTED_HOVER = new Colour3f(0x6060c0);
		public static final Colour3f BUTTON_TEXT_NORMAL = new Colour3f(0x44546a);
		public static final Colour3f CHECKBOX_BACKGROUND = new Colour3f(0.3f, 0.3f, 0.3f);
		public static final Colour3f GREEN = new Colour3f(0x00FF00);
		public static final Colour3f HIGHLIGHT = new Colour3f(0.4f, 0.4f, 0.8f);
		public static final Colour3f MINIMIZE = new Colour3f(0.3f, 0.3f, 1);
		public static final Colour3f PANEL_BACKGROUND_DARK = new Colour3f(0.2f, 0.2f, 0.2f);
		public static final Colour3f PANEL_BACKGROUND_LIGHT = new Colour3f(0.8f, 0.8f, 0.8f);
		public static final Colour3f PANEL_BACKGROUND_NORMAL = new Colour3f(0.4f, 0.4f, 0.4f);
		public static final Colour3f PANEL_BORDER = new Colour3f(0.3f, 0.3f, 0.3f);
		public static final Colour3f SCREEN_BACKGROUND = new Colour3f(0.78f, 0.78f, 0.78f);
		public static final Colour3f SELECTED = new Colour3f(0.4f, 0.8f, 0.4f);
		public static final Colour3f TEXT_LIGHT = new Colour3f(0.8f, 0.8f, 0.8f);
		public static final Colour3f TEXT_NORMAL = new Colour3f(0.4f, 0.4f, 0.4f);
		public static final Colour3f WHITE = new Colour3f(0xFFFFFF);
		public static final Colour3f TEXT_DEBUG = new Colour3f(0x2222BB);
	}
}
