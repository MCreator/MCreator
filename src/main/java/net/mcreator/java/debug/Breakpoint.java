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

package net.mcreator.java.debug;

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.request.BreakpointRequest;

import javax.annotation.Nullable;

public class Breakpoint {

	public final String classname;
	public final int line;

	@Nullable BreakpointListener listener = null;

	@Nullable BreakpointRequest breakpointRequest = null;

	public Breakpoint(String classname, int line, @Nullable BreakpointListener listener) {
		this.classname = classname;
		this.line = line;
		this.listener = listener;
	}

	public void setBreakpointRequest(@Nullable BreakpointRequest breakpointRequest) {
		this.breakpointRequest = breakpointRequest;
	}

	public String getClassname() {
		return classname;
	}

	public int getLine() {
		return line;
	}

	@Nullable public BreakpointRequest getBreakpointRequest() {
		return breakpointRequest;
	}

	public interface BreakpointListener {

		void breakpointHit(BreakpointEvent event);

	}

}
