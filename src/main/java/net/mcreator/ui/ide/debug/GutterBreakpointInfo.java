/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.ide.debug;

import net.mcreator.java.debug.Breakpoint;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.GutterIconInfo;

import javax.annotation.Nullable;
import javax.swing.text.BadLocationException;

public class GutterBreakpointInfo {

	private GutterIconInfo gutterIconInfo;

	@Nullable private Breakpoint breakpoint;

	public GutterBreakpointInfo(GutterIconInfo gutterIconInfo) {
		this.gutterIconInfo = gutterIconInfo;
	}

	public GutterIconInfo getGutterIconInfo() {
		return gutterIconInfo;
	}

	public int getCurrentLine(RSyntaxTextArea te) {
		try {
			return te.getLineOfOffset(gutterIconInfo.getMarkedOffset());
		} catch (BadLocationException e) {
			return -1;
		}
	}

	@Nullable public Breakpoint getBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(@Nullable Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}

	public void setGutterIconInfo(GutterIconInfo gutterIconInfo) {
		this.gutterIconInfo = gutterIconInfo;
	}
}
