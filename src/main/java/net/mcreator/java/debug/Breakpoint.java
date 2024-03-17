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

	private final String classname;
	private final int line;

	private boolean loaded = false;

	@Nullable private BreakpointListener listener;

	@Nullable private BreakpointRequest breakpointRequest = null;

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

	protected int getLine() {
		return line;
	}

	@Nullable public BreakpointRequest getBreakpointRequest() {
		return breakpointRequest;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Breakpoint that = (Breakpoint) o;

		if (line != that.line)
			return false;
		return classname.equals(that.classname);
	}

	@Override public int hashCode() {
		int result = classname.hashCode();
		result = 31 * result + line;
		return result;
	}

	public void setListener(@Nullable BreakpointListener listener) {
		this.listener = listener;
	}

	@Nullable public BreakpointListener getListener() {
		return listener;
	}

	public interface BreakpointListener {

		void breakpointLoaded(Breakpoint breakpoint);

		boolean breakpointHit(Breakpoint breakpoint, BreakpointEvent event);

	}

	protected boolean isLoaded() {
		return loaded;
	}

	protected void setLoaded(boolean loaded) {
		if (!this.loaded && loaded) {
			if (listener != null) {
				listener.breakpointLoaded(this);
			}
		}

		this.loaded = loaded;
	}

}
