/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.dialogs;

import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.io.FileIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.impl.workspace.RegenerateCodeAction;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeErrorDialog {

	private static final Logger LOG = LogManager.getLogger("Code Error Parser");

	/**
	 * @param mcreator     MCreator instance
	 * @param stderroutput Error log in text format
	 * @return true if this error was handled and displayed to the user
	 */
	public static boolean showCodeErrorDialog(MCreator mcreator, String stderroutput) {
		Set<File> problematicFiles = new HashSet<>();

		mcreator.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		String[] lines = stderroutput.split("\n");
		for (String line : lines) {
			if (line.contains(".java:") && line.contains(": error:")) {
				File fileWithError = new File(line.split(":\\d+: error:")[0].replaceAll("build[/|\\\\]sources", "src"));
				problematicFiles.add(fileWithError);
			}
		}

		Set<ModElement> problematicMods = new HashSet<>();
		boolean moddefinitionfileerrors = false;

		for (File problematicFile : problematicFiles) {
			ModElement modElementWithError = mcreator.getWorkspace().getGenerator()
					.getModElementThisFileBelongsTo(problematicFile);
			if (modElementWithError != null) {
				problematicMods.add(modElementWithError);
				modElementWithError.setCompiles(false);
				mcreator.getWorkspace().updateModElement(modElementWithError);
			} else if (FileIO.isFileOnFileList(
					mcreator.getWorkspace().getGenerator().getModBaseGeneratorTemplatesList(false).stream()
							.map(GeneratorTemplate::getFile).collect(Collectors.toList()), problematicFile)) {
				moddefinitionfileerrors = true;
			} else {
				LOG.warn("[ForgeGradleUtil] Error from non MCreator generated class!");
			}

			if (problematicMods.size() > 10)
				break;
		}

		mcreator.setCursor(Cursor.getDefaultCursor());

		if (moddefinitionfileerrors) { // first we try to fix mod definition errors
			Object[] options = { "Regenerate code", "Ignore error" };
			int n = JOptionPane.showOptionDialog(mcreator,
					"<html><b color=#ffc40d>Your main mod file did not compile properly.</b><br><br>"
							+ "This is usually caused by inconsistencies in the mod code during builds.<br>"
							+ "MCreator can solve this issue for you by regenerating entire mod's code."
							+ "<br><br>If you have locked mod elements or mod base files, they can cause this error too.</small><br><br>"
							+ "Click <b>Regenerate code</b> button to proceed with this action.<br>",
					"Main mod file compilation errors", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE,
					null, options, options[0]);
			if (n == 0) {
				RegenerateCodeAction.regenerateCode(mcreator, true, true);
			}
			return true;
		}

		if (problematicMods.isEmpty()) { // if list is empty, there are no mod elements to show
			if (stderroutput.contains("see the compiler error output for details")) {
				mcreator.mcreatorTabs.showTab(mcreator.consoleTab);
				return true;
			}
			return false;
		}

		JList<ModElement> problematicModsList = new JList<>(problematicMods.toArray(new ModElement[0]));
		JScrollPane sp = new JScrollPane(problematicModsList);
		sp.setPreferredSize(new Dimension(150, 140));

		sp.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		problematicModsList.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		JPanel list = new JPanel(new BorderLayout());
		list.add("North", new JLabel(
				"<html><b color=#ffc40d>Your workspace contains mod elements that don't compile properly.</b><br>"
						+ "<br>This was most likely caused by the last mod element you added or edited. Try altering the settings of mod elements<br>"
						+ "that cause build errors (such elements are listed below) to try to fix this problem.<br>"
						+ "<br>If you can't resolve this problem, export the workspace to shareable ZIP file from the <i>File</i> menu and send it to us via<br>"
						+ "our Issue tracker on our website so we can try to help you.<br>"
						+ "<br><b>List of mod elements that cause compilation errors (trimmed to up to 10 elements):<br><br>"));

		list.add("Center", sp);

		Object[] options = { "OK", "Show console tab", "Support page" };
		int n = JOptionPane.showOptionDialog(mcreator, list, "Some mod elements cause compilation errors",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (n == 1) {
			mcreator.mcreatorTabs.showTab(mcreator.consoleTab);
		} else if (n == 2) {
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/support");
		}

		return true;
	}

}
