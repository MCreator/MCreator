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

package net.mcreator.workspace;

import net.mcreator.element.GeneratableElement;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ShareableZIPManager {

	private static final Logger LOG = LogManager.getLogger("Shareable ZIP manager");

	public static File importZIP(File file, File workspaceDir, Window window) {
		AtomicReference<File> retval = new AtomicReference<>();

		ProgressDialog dial = new ProgressDialog(window, L10N.t("dialog.workspace.import_from_zip.importing"));

		Thread t = new Thread(() -> {
			ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.import_from_zip.extracting"));
			dial.addProgressUnit(p1);

			ZipIO.unzip(file.getAbsolutePath(), workspaceDir.getAbsolutePath());

			retval.set(WorkspaceUtils.getWorkspaceFileForWorkspaceFolder(workspaceDir));

			if (retval.get() != null) {
				p1.markStateOk();
			} else {
				p1.markStateError();

				JOptionPane.showMessageDialog(dial, L10N.t("dialog.workspace.import_from_zip.failed_message"),
						L10N.t("dialog.workspace.import_from_zip.failed_title"), JOptionPane.ERROR_MESSAGE);

				dial.hideDialog();

				return;
			}

			ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.regenerate_and_build.progress.loading_mod_elements"));
			dial.addProgressUnit(p2);

			try (Workspace workspace = Workspace.readFromFS(retval.get(), dial)) {
				int modstoload = workspace.getModElements().size();

				int i = 0;
				// In exported ZIPs, mod element images are not stored, so we regenerate them here
				// If workspace MCR version is the same, regeneration will not run and thus ME icons will be missing
				// This is "fixed" by "preloading mod elements" here
				for (ModElement mod : workspace.getModElements()) {
					// During the loading of GeneratableElement, other MEs could be converted too, so we make sure
					// that the current ME (from list that could be outdated at this point) was not converted to
					// a different type. If this is the case, we don't preload the ME here as this will result in
					// warnings because we are trying to load ME that is already stored in GeneratableElement cache
					// as a different mod element type
					ModElement check = workspace.getModElementByName(mod.getName());
					if (check != null && check.getType() == mod.getType()) {
						GeneratableElement generatableElement = mod.getGeneratableElement();
						if (generatableElement != null) {
							// save custom mod element picture if it has one
							workspace.getModElementManager().storeModElementPicture(generatableElement);

							// we reinit the mod to load new ME icon
							generatableElement.getModElement().reinit(workspace);
						}
					} else {
						LOG.debug("Skipping preloading of mod element " + mod.getName()
								+ " as it was converted to a different type");
					}

					i++;
					p1.setPercent((int) (i / (float) modstoload * 100));
				}

				// make sure we store any potential changes made to the workspace
				workspace.markDirty();
			} catch (UnsupportedGeneratorException | MissingGeneratorFeaturesException e) {
				// Exception that already prompted user action resulting in us landing here happened before
				// So we just cancel the import at this point by returning null
				retval.set(null);
			} catch (Exception e) {
				LOG.error("Failed to import workspace", e);
			}

			p2.markStateOk();

			dial.hideDialog();
		}, "ZIPImporter");
		t.start();
		dial.setVisible(true);

		return retval.get();
	}

	public static void exportZIP(String title, File file, MCreator mcreator, boolean excludeRunDir) {
		ProgressDialog dial = new ProgressDialog(mcreator, title);
		Thread t = new Thread(() -> {
			ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
					L10N.t("dialog.workspace.export_workspace.compressing"));
			dial.addProgressUnit(p1);

			try {
				Set<String> excludes = new HashSet<>(
						Set.of(".eclipse/", ".gradle/", ".mcreator/", "build/", "gradle/", "#build.gradle", "#gradlew",
								"#gradlew.bat", "#mcreator.gradle", ".git/", "#.classpath", "#.project", ".idea/",
								".settings/"));
				if (excludeRunDir)
					excludes.addAll(Set.of("run/", "runs/"));

				ZipIO.zipDir(mcreator.getWorkspaceFolder().getAbsolutePath(), file.getAbsolutePath(),
						excludes.toArray(new String[0]));
			} catch (IOException e) {
				LOG.error("Failed to export workspace", e);
			}

			p1.markStateOk();
			dial.hideDialog();
		}, "ZIPExporter");
		t.start();
		dial.setVisible(true);
	}

}
