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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.elementlist.special.CompactModElementListCellRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CodeErrorDialog {

	private static final Logger LOG = LogManager.getLogger(CodeErrorDialog.class);

	/**
	 * @param mcreator     MCreator instance
	 * @param stderroutput Error log in text format
	 * @return true if this error was handled and displayed to the user
	 */
	public static boolean showCodeErrorDialog(MCreator mcreator, String stderroutput) {
		Set<File> problematicFiles = new HashSet<>();

		mcreator.setCursor(new Cursor(Cursor.WAIT_CURSOR));

		stderroutput.lines().forEach(line -> {
			if (line.contains(".java:") && line.contains(": error:")) {
				File fileWithError = new File(line.split(":\\d+: error:")[0].replaceAll("build[/|\\\\]sources", "src"));
				problematicFiles.add(fileWithError);
			}
		});

		Set<ModElement> problematicMods = new HashSet<>();

		List<File> moddefinitionfiles = mcreator.getGenerator().getModBaseGeneratorTemplatesList(false).stream()
				.map(GeneratorTemplate::getFile).collect(Collectors.toList());
		boolean moddefinitionfileerrors = false;

		for (File problematicFile : problematicFiles) {
			ModElement modElementWithError = mcreator.getGenerator().getModElementThisFileBelongsTo(problematicFile);
			if (modElementWithError != null) {
				problematicMods.add(modElementWithError);
				modElementWithError.setCompiles(false);
				mcreator.getWorkspace().markDirty();
			} else if (FileIO.isFileOnFileList(moddefinitionfiles, problematicFile)) {
				moddefinitionfileerrors = true;
			} else {
				LOG.warn("[ForgeGradleUtil] Error from non MCreator generated class!");
			}
		}

		mcreator.setCursor(Cursor.getDefaultCursor());

		if (moddefinitionfileerrors) { // first we try to fix mod definition errors
			Object[] options = { L10N.t("dialog.code_error.regenerate_code"),
					L10N.t("dialog.code_error.ignore_error") };
			int n = JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.code_error.compilation_desc"),
					L10N.t("dialog.code_error.compilation_title"), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.WARNING_MESSAGE, null, options, options[0]);
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
		problematicModsList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		problematicModsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		problematicModsList.setFixedCellHeight(40);
		problematicModsList.setFixedCellWidth(200);
		problematicModsList.setVisibleRowCount(-1);
		problematicModsList.setCellRenderer(new CompactModElementListCellRenderer());

		JScrollPane sp = new JScrollPane(problematicModsList);
		sp.setPreferredSize(new Dimension(150, 140));
		sp.setBackground(Theme.current().getSecondAltBackgroundColor());
		problematicModsList.setBackground(Theme.current().getSecondAltBackgroundColor());

		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add("North", L10N.label("dialog.code_error.compilation_list"));
		wrapper.add("Center", sp);

		Object[] options = { L10N.t("dialog.code_error.show_in_workspace"), L10N.t("dialog.code_error.show_build_log"),
				L10N.t("gradle.errors.do_nothing"), L10N.t("action.support") };
		int n = JOptionPane.showOptionDialog(mcreator, wrapper, L10N.t("dialog.code_error.title"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (n == 0) {
			mcreator.mcreatorTabs.showTab(mcreator.workspaceTab);
			mcreator.mv.search.setText("f:err");
		} else if (n == 1) {
			mcreator.mcreatorTabs.showTab(mcreator.consoleTab);
		} else if (n == 3) {
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/support");
		}

		return true;
	}

}
