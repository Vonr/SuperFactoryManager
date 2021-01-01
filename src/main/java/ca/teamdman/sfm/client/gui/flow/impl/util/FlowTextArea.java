/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. */
package ca.teamdman.sfm.client.gui.flow.impl.util;

import ca.teamdman.sfm.client.gui.flow.core.FlowComponent;
import ca.teamdman.sfm.client.gui.flow.core.IFlowView;
import ca.teamdman.sfm.client.gui.flow.core.Size;
import ca.teamdman.sfm.common.flow.data.core.Position;

/**
 * @see net.minecraft.client.gui.widget.TextFieldWidget
 * @see net.minecraft.client.gui.fonts.TextInputUtil
 * @see net.minecraft.util.text.TextFormatting
 */
public class FlowTextArea extends FlowComponent {
	private String content;
//	private final TextFieldWidget delegate;

	public FlowTextArea(String content, Position pos, Size size) {
		super(pos, size);
		this.content = content;
//		this.delegate = new TextFieldWidget()
	}

	public void focus() {

	}

	@Override
	public IFlowView getView() {
		return this;
	}

	@Override
	public Position getPosition() {
		return null;
	}

	@Override
	public Size getSize() {
		return null;
	}
}
