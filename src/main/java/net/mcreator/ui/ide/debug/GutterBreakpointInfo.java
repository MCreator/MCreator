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
import net.mcreator.ui.component.MonacoEditorPanel;

import javax.annotation.Nullable;

public class GutterBreakpointInfo {

	private int line;

	@Nullable private Breakpoint breakpoint;

	public GutterBreakpointInfo(int line) {
		this.line = line;
	}

	public int getCurrentLine(MonacoEditorPanel te) {
		return line;
	}

	@Nullable public Breakpoint getBreakpoint() {
		return breakpoint;
	}

	public void setBreakpoint(@Nullable Breakpoint breakpoint) {
		this.breakpoint = breakpoint;
	}
}