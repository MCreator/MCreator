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
import net.mcreator.java.ClassFinder;
import net.mcreator.java.debug.Breakpoint;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.Gutter;
import org.fife.ui.rtextarea.GutterIconInfo;
import org.fife.ui.rtextarea.IconRowHeader;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class BreakpointHandler {

	private static final Logger LOG = LogManager.getLogger("BreakpointHandler");

	private final Map<Integer, GutterBreakpointInfo> breakpoints = new HashMap<>();

	public BreakpointHandler(CodeEditorView cev, RTextScrollPane sp, RSyntaxTextArea te, JavaParser parser) {
		try {
			Field field = Gutter.class.getDeclaredField("iconArea");
			field.setAccessible(true);
			IconRowHeader iconRowHeader = (IconRowHeader) field.get(sp.getGutter());
			iconRowHeader.addMouseListener(new MouseAdapter() {

				private int viewToModelLine(Point p) throws BadLocationException {
					int offs = te.viewToModel2D(p);
					return offs > -1 ? te.getLineOfOffset(offs) : -1;
				}

				@Override public void mousePressed(MouseEvent e) {
					// TODO: for now only allow breakpoints while in debug session
					// meaning new breakpoints can't be added while not debugging
					// we also need to handle breakpoint that were added while debugging and are still present after debugging
					if (cev.getMCreator().getGradleConsole().getDebugClient() == null)
						return;

					try {
						int line = viewToModelLine(e.getPoint());
						if (line > -1) {
							if (breakpoints.containsKey(line)) {
								sp.getGutter().removeTrackingIcon(breakpoints.get(line).getGutterIconInfo());
								if (breakpoints.get(line).getBreakpoint() != null
										&& cev.getMCreator().getGradleConsole().getDebugClient() != null) {
									cev.getMCreator().getGradleConsole().getDebugClient()
											.removeBreakpoint(breakpoints.get(line).getBreakpoint());
								}
								breakpoints.remove(line);
							} else {
								AtomicReference<GutterIconInfo> gutterIconInfo = new AtomicReference<>(
										sp.getGutter().addLineTrackingIcon(line, UIRES.get("16px.sound")));
								GutterBreakpointInfo gutterBreakpointInfo = new GutterBreakpointInfo(
										gutterIconInfo.get());
								if (cev.getMCreator().getGradleConsole().getDebugClient() != null) {
									try {
										Breakpoint breakpoint = new Breakpoint(ClassFinder.getCurrentFQDN(parser), line + 1,
												new Breakpoint.BreakpointListener() {
													@Override public void breakpointLoaded() {
														try {
															sp.getGutter().removeTrackingIcon(gutterIconInfo.get());
															gutterIconInfo.set(sp.getGutter().addLineTrackingIcon(line,
																	UIRES.get("16px.breakpoint")));
															gutterBreakpointInfo.setGutterIconInfo(
																	gutterIconInfo.get());
														} catch (BadLocationException ignored) {
														}
													}

													@Override
													public boolean breakpointHit(BreakpointEvent breakpointEvent) {
														MCreatorTabs.Tab existing = cev.getMCreator().mcreatorTabs.showTabOrGetExisting(
																cev.fileWorkingOn);
														CodeEditorView bpCev;
														if (existing != null) {
															bpCev = (CodeEditorView) existing.getContent();
															if (bpCev == cev) {
																bpCev.getMCreator().mcreatorTabs.showTab(existing);
																try {
																	int startOffset = bpCev.te.getLineStartOffset(
																			breakpointEvent.location().lineNumber());
																	bpCev.te.setCaretPosition(startOffset);
																} catch (BadLocationException ignored) {
																}
															}

															return false;
														} else {
															return true;
														}
													}
												});
										cev.getMCreator().getGradleConsole().getDebugClient().addBreakpoint(breakpoint);
										gutterBreakpointInfo.setBreakpoint(breakpoint);
									} catch (Exception ex) {
										LOG.warn("Failed to add breakpoint", ex);
										sp.getGutter().removeTrackingIcon(gutterIconInfo.get());
										return;
									}
								}
								breakpoints.put(line, gutterBreakpointInfo);
							}
						}
					} catch (BadLocationException ignored) {
					}
				}
			});
		} catch (NoSuchFieldException | IllegalAccessException ignored) {
		}
	}

}
