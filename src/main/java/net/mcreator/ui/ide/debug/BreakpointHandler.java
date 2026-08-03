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

import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMDisconnectEvent;
import net.mcreator.java.ClassFinder;
import net.mcreator.java.debug.Breakpoint;
import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class BreakpointHandler {

	private static final Logger LOG = LogManager.getLogger("BreakpointHandler");

	private final List<GutterBreakpointInfo> breakpointsList = new ArrayList<>();

	private final CodeEditorView cev;

	public BreakpointHandler(CodeEditorView cev) {
		this.cev = cev;
	}

	public void newDebugClient(JVMDebugClient debugClient) {
		for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
			registerBreakpointWithDebugClient(debugClient, breakpointInfo);
		}
	}

	public void toggleBreakpoint(int line) {
		JVMDebugClient debugClient = cev.getMCreator().getGradleConsole().getDebugClient();

		GutterBreakpointInfo anyMatch = null;
		for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
			if (breakpointInfo.getCurrentLine(cev.te) == line) {
				anyMatch = breakpointInfo;
				break;
			}
		}

		if (anyMatch != null) {
			breakpointsList.remove(anyMatch);
			if (anyMatch.getBreakpoint() != null && debugClient != null) {
				debugClient.removeBreakpoint(anyMatch.getBreakpoint());
			}
		} else {
			GutterBreakpointInfo gutterBreakpointInfo = new GutterBreakpointInfo(line);
			breakpointsList.add(gutterBreakpointInfo);

			if (debugClient != null) {
				registerBreakpointWithDebugClient(debugClient, gutterBreakpointInfo);
			}
		}

		updateMonacoBreakpoints();
	}

	private void updateMonacoBreakpoints() {
		int[] lines = new int[breakpointsList.size()];
		for (int i = 0; i < breakpointsList.size(); i++) {
			lines[i] = breakpointsList.get(i).getCurrentLine(cev.te);
		}
		cev.te.setBreakpoints(lines);
	}

	private void registerBreakpointWithDebugClient(@Nonnull JVMDebugClient debugClient,
			GutterBreakpointInfo gutterBreakpointInfo) {
		debugClient.addEventListener((vm, eventSet, resumed) -> {
			for (Event event : eventSet) {
				if (event instanceof VMDisconnectEvent) {
					for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
						if (breakpointInfo.getBreakpoint() != null) {
							debugClient.removeBreakpoint(breakpointInfo.getBreakpoint());
							breakpointInfo.setBreakpoint(null);
						}
					}
				}
			}
		});

		try {
			Breakpoint breakpoint = new Breakpoint(ClassFinder.getCurrentFQDN(cev.te.getText()),
					gutterBreakpointInfo.getCurrentLine(cev.te) + 1, new Breakpoint.BreakpointListener() {
				@Override public void breakpointLoaded(Breakpoint breakpoint) {
				}

				@Override public boolean breakpointHit(Breakpoint breakpoint, BreakpointEvent breakpointEvent) {
					MCreatorTabs.Tab existing = cev.getMCreator().getTabs().showTabOrGetExisting(cev.fileWorkingOn);
					if (existing != null) {
						SwingUtilities.invokeLater(() -> {
							CodeEditorView bpCev = (CodeEditorView) existing.getContent();
							if (bpCev == cev) {
								bpCev.getMCreator().getTabs().showTab(existing);
								int breakpointLine = gutterBreakpointInfo.getCurrentLine(cev.te);
								bpCev.te.jumpToLine(breakpointLine);
								bpCev.getMCreator().toFront();
								bpCev.getMCreator().requestFocus();
							}
						});

						return false;
					} else {
						return true;
					}
				}
			});
			debugClient.addBreakpoint(breakpoint);
			gutterBreakpointInfo.setBreakpoint(breakpoint);
		} catch (Exception ex) {
			LOG.warn("Failed to add breakpoint", ex);
		}
	}

}