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
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BreakpointHandler {

	private static final Logger LOG = LogManager.getLogger("BreakpointHandler");

	private final List<GutterBreakpointInfo> breakpointsList = new ArrayList<>();

	private final CodeEditorView cev;
	private final RTextScrollPane sp;
	private final JavaParser parser;

	public BreakpointHandler(CodeEditorView cev, RTextScrollPane sp, JavaParser parser) {
		this.cev = cev;
		this.sp = sp;
		this.parser = parser;

		try {
			Field field = Gutter.class.getDeclaredField("iconArea");
			field.setAccessible(true);
			IconRowHeader iconRowHeader = (IconRowHeader) field.get(sp.getGutter());
			iconRowHeader.addMouseListener(new MouseAdapter() {

				private int viewToModelLine(Point p) throws BadLocationException {
					int offs = cev.te.viewToModel2D(p);
					return offs > -1 ? cev.te.getLineOfOffset(offs) : -1;
				}

				@Override public void mousePressed(MouseEvent e) {
					try {
						int line = viewToModelLine(e.getPoint());
						if (line < 0)
							return;

						JVMDebugClient debugClient = cev.getMCreator().getGradleConsole().getDebugClient();

						GutterBreakpointInfo anyMatch = null;
						for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
							if (breakpointInfo.getCurrentLine(cev.te) == line) {
								anyMatch = breakpointInfo;
								break;
							}
						}

						if (anyMatch != null) {
							sp.getGutter().removeTrackingIcon(anyMatch.getGutterIconInfo());
							breakpointsList.remove(anyMatch);

							// if active debug session, remove breakpoint from the client too
							if (anyMatch.getBreakpoint() != null && debugClient != null) {
								debugClient.removeBreakpoint(anyMatch.getBreakpoint());
							}
						} else {
							GutterIconInfo gutterIconInfo = sp.getGutter()
									.addLineTrackingIcon(line, UIRES.get("16px.breakpoint_na"));
							GutterBreakpointInfo gutterBreakpointInfo = new GutterBreakpointInfo(gutterIconInfo);
							breakpointsList.add(gutterBreakpointInfo);

							if (debugClient != null) {
								registerBreakpointWithDebugClient(debugClient, gutterBreakpointInfo);
							}
						}
					} catch (BadLocationException ignored) {
					}
				}
			});
		} catch (NoSuchFieldException | IllegalAccessException e) {
			LOG.error("Failed to add breakpoint handler to the gutter", e);
		}
	}

	public void newDebugClient(JVMDebugClient debugClient) {
		// register all current breakpoints to the debug client
		for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
			registerBreakpointWithDebugClient(debugClient, breakpointInfo);
		}
	}

	private void replaceGutterIcon(RTextScrollPane sp, GutterBreakpointInfo gutterBreakpointInfo, ImageIcon newIcon) {
		try {
			GutterIconInfo currentGutterIconInfo = gutterBreakpointInfo.getGutterIconInfo();
			int line = gutterBreakpointInfo.getCurrentLine(cev.te);
			sp.getGutter().removeTrackingIcon(currentGutterIconInfo);
			GutterIconInfo newGutterIconInfo = sp.getGutter().addLineTrackingIcon(line, newIcon);
			gutterBreakpointInfo.setGutterIconInfo(newGutterIconInfo);
		} catch (BadLocationException ignored) {
		}
	}

	private void registerBreakpointWithDebugClient(@Nonnull JVMDebugClient debugClient,
			GutterBreakpointInfo gutterBreakpointInfo) {
		debugClient.addEventListener((vm, eventSet, resumed) -> {
			for (Event event : eventSet) {
				if (event instanceof VMDisconnectEvent) {
					for (GutterBreakpointInfo breakpointInfo : breakpointsList) {
						// mark breakpoints as not active
						replaceGutterIcon(sp, gutterBreakpointInfo, UIRES.get("16px.breakpoint_na"));

						// remove breakpoints from debug client
						if (breakpointInfo.getBreakpoint() != null) {
							debugClient.removeBreakpoint(breakpointInfo.getBreakpoint());
							breakpointInfo.setBreakpoint(null);
						}
					}
				}
			}
		});

		try {
			Breakpoint breakpoint = new Breakpoint(ClassFinder.getCurrentFQDN(parser),
					gutterBreakpointInfo.getCurrentLine(cev.te) + 1, new Breakpoint.BreakpointListener() {
				@Override public void breakpointLoaded(Breakpoint breakpoint) {
					replaceGutterIcon(sp, gutterBreakpointInfo, UIRES.get("16px.breakpoint"));
				}

				@Override public boolean breakpointHit(Breakpoint breakpoint, BreakpointEvent breakpointEvent) {
					MCreatorTabs.Tab existing = cev.getMCreator().mcreatorTabs.showTabOrGetExisting(cev.fileWorkingOn);
					if (existing != null) {
						SwingUtilities.invokeLater(() -> {
							CodeEditorView bpCev = (CodeEditorView) existing.getContent();
							if (bpCev == cev) {
								bpCev.getMCreator().mcreatorTabs.showTab(existing);
								try {
									int breakpointLine = gutterBreakpointInfo.getCurrentLine(cev.te);
									int startOffset = bpCev.te.getLineStartOffset(breakpointLine);
									bpCev.te.setCaretPosition(startOffset);
									bpCev.te.setActiveLineRange(breakpointLine, breakpointLine + 1);
								} catch (BadLocationException ignored) {
								}
								bpCev.te.requestFocusInWindow();
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
			sp.getGutter().removeTrackingIcon(gutterBreakpointInfo.getGutterIconInfo());
		}
	}

}
