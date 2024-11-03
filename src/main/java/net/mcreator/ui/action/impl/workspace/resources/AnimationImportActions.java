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

/*
 * Additional permission under GNU GPL version 3 section 7
 *
 * If you modify this Program, or any covered work, by linking or combining
 * it with JBoss Forge (or a modified version of that library), containing
 * parts covered by the terms of Eclipse Public License, the licensors of
 * this Program grant you additional permission to convey the resulting work.
 */

package net.mcreator.ui.action.impl.workspace.resources;

import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.forge.roaster.ParserException;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class AnimationImportActions {

	private static final Logger LOG = LogManager.getLogger("Animation import actions");

	public static class JAVA extends BasicAction {
		public JAVA(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.resources.import_java_animation"), actionEvent -> {
				File file = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".java" });
				if (file != null)
					importJavaModelAnimation(actionRegistry.getMCreator(), file);
			});
			setIcon(UIRES.get("16px.importjavamodelanimation"));
		}

		@Override public boolean isEnabled() {
			return actionRegistry.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_animations_java")
					!= GeneratorStats.CoverageStatus.NONE;
		}
	}

	public static void importJavaModelAnimation(MCreator mcreator, File file) {
		String origCode = FileIO.readFileToString(file);

		JavaClassSource classJavaSource;

		try {
			classJavaSource = (JavaClassSource) Roaster.parse(origCode);
			classJavaSource.toString();

			if (!origCode.contains("AnimationDefinition") || !origCode.contains("AnimationChannel")) {
				throw new ParserException("Not a valid animation code");
			}

			String className = origCode.split("(?= class\\s+" + classJavaSource.getName() + ")")[1].replaceAll(
					"class\\s+", "").split("\\s+?\\{")[0].trim();

			if (!JavaConventions.isValidJavaIdentifier(className)) {
				JOptionPane.showMessageDialog(null,
						L10N.t("dialog.workspace.resources.import_java_animation.invalid_animation_name.message",
								className),
						L10N.t("dialog.workspace.resources.import_java_animation.invalid_animation_name.title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception err) {
			LOG.error("Failed to load Java model animation: {}", file, err);
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("dialog.workspace.resources.import_java_animation.invalid_animation_format.message"),
					L10N.t("dialog.workspace.resources.import_java_animation.invalid_animation_format.title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<Import> imports = classJavaSource.getImports();
		for (Import imprt : imports) {
			classJavaSource.removeImport(imprt);
		}

		classJavaSource.setDefaultPackage();

		if (!classJavaSource.getName().contains("Animation"))
			classJavaSource.setName("Animations" + classJavaSource.getName());

		if (new File(mcreator.getFolderManager().getModelAnimationsDir(),
				classJavaSource.getName() + ".java").exists()) {
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("dialog.workspace.resources.import_java_animation.animation_already_exists.message",
							classJavaSource.getName()),
					L10N.t("dialog.workspace.resources.import_java_animation.animation_already_exists.title"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		FileIO.writeStringToFile(classJavaSource.toString(),
				new File(mcreator.getFolderManager().getModelAnimationsDir(), classJavaSource.getName() + ".java"));

		mcreator.mv.resourcesPan.workspacePanelAnimations.reloadElements();
		if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI)
			((ModElementGUI<?>) mcreator.mcreatorTabs.getCurrentTab().getContent()).reloadDataLists();
	}

}
