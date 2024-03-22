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

package net.mcreator.ui.debug;

import com.sun.jdi.event.BreakpointEvent;
import net.mcreator.blockly.java.blocks.DebugMarkerBlock;
import net.mcreator.io.FileIO;
import net.mcreator.java.debug.Breakpoint;
import net.mcreator.java.debug.JVMDebugClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DebugMarkersHandler {

	private static final Logger LOG = LogManager.getLogger("DebugMarkersHandler");

	public static void handleDebugMarkers(DebugPanel debugPanel) {
		FileUtils.listFiles(debugPanel.getMCreator().getGenerator().getGeneratorPackageRoot(), new IOFileFilter() {
			@Override public boolean accept(File file) {
				return FilenameUtils.isExtension(file.getName(), "java");
			}

			@Override public boolean accept(File dir, String name) {
				return FilenameUtils.isExtension(name);
			}
		}, TrueFileFilter.INSTANCE).forEach(file -> {
			String code = FileIO.readFileToString(file);
			if (code.contains(DebugMarkerBlock.CODE_START)) {
				Map<Integer, String> lines = findLinesWithMarker(code);
				try {
					lines.forEach((lineNumber, line) -> {
						JVMDebugClient debugClient = debugPanel.getDebugClient();
						if (debugClient != null) {
							SwingUtilities.invokeLater(() -> {
								try {
									String fqdn = file.getCanonicalPath().substring(
													debugPanel.getMCreator().getGenerator().getSourceRoot().getCanonicalPath()
															.length() + 1).replace("/", ".").replace("\\", ".")
											.replace(".java", "");
									DebugMarker debugMarker = new DebugMarker(debugPanel,
											line.replace(DebugMarkerBlock.CODE_START, "").trim());
									debugPanel.addMarker(debugMarker);
									debugClient.addBreakpoint(
											new Breakpoint(fqdn, lineNumber, new Breakpoint.BreakpointListener() {
												@Override public void breakpointLoaded(Breakpoint breakpoint) {
													debugMarker.loaded();
												}

												@Override
												public boolean breakpointHit(Breakpoint breakpoint,
														BreakpointEvent event) {
													debugMarker.reportHit(event);
													return true;
												}
											}));
								} catch (Exception e) {
									LOG.warn("Failed to add breakpoint for " + file, e);
								}
							});
						}
					});
				} catch (Exception e) {
					LOG.warn("Failed to find FQDN for " + file, e);
				}
			}
		});
	}

	private static Map<Integer, String> findLinesWithMarker(String input) {
		Map<Integer, String> resultMap = new HashMap<>();
		String[] lines = input.split("\n");
		for (int lineNumber = 1; lineNumber <= lines.length; lineNumber++) {
			String line = lines[lineNumber - 1];
			if (line.contains(DebugMarkerBlock.CODE_START)) {
				resultMap.put(lineNumber, line);
			}
		}
		return resultMap;
	}

}
