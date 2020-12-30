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

import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.MtlWriter;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.io.Transliteration;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.ActionRegistry;
import net.mcreator.ui.action.BasicAction;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.dialogs.JavaModelAnimationEditorDialog;
import net.mcreator.ui.dialogs.TextureMappingDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.resources.TexturedModel;
import org.jboss.forge.roaster.Roaster;
import org.jboss.forge.roaster.model.source.Import;
import org.jboss.forge.roaster.model.source.JavaClassSource;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModelImportActions {

	public static class JAVA extends BasicAction {
		public JAVA(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.resources.import_java_model"), actionEvent -> {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.resources.import_java_model.version_notice.message"),
						L10N.t("dialog.workspace.resources.import_java_model.version_notice.title"),
						JOptionPane.INFORMATION_MESSAGE);
				File file = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".java" });
				if (file != null)
					importJavaModel(actionRegistry.getMCreator(), file);
			});
		}

		@Override public boolean isEnabled() {
			return actionRegistry.getMCreator().getGeneratorStats().getBaseCoverageInfo()
					.get("model_java") != GeneratorStats.CoverageStatus.NONE;
		}
	}

	public static void importJavaModel(MCreator mcreator, File file) {
		String origCode = FileIO.readFileToString(file).replace("public class", "public static class")
				.replace("RendererModel ", "ModelRenderer ").replace("RendererModel(", "ModelRenderer(")
				.replace("ModelRenderer ;", "");

		if (origCode.contains("software.bernie.geckolib.animation.model.AnimatedEntityModel") && !mcreator
				.getWorkspaceSettings().getMCreatorDependencies().contains("geckolib")) {
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("dialog.workspace.resources.import_java_model.geckolib_needed.message"),
					L10N.t("dialog.workspace.resources.import_java_model.geckolib_needed.title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JavaClassSource classJavaSource;

		try {
			if (origCode.contains("software.bernie.geckolib.animation.model.AnimatedEntityModel")) {
				origCode = origCode.replaceAll("AnimatedEntityModel<[\\w\\d$_€]+>", "AnimatedEntityModel");
			}

			classJavaSource = (JavaClassSource) Roaster.parse(origCode);
			classJavaSource.toString();

			String className = origCode.split("(?= class\\s+" + classJavaSource.getName() + ")")[1]
					.replaceAll("class\\s+", "").split("( extends )|(<\\w+? extends)")[0].trim();

			if (!JavaConventions.isValidJavaIdentifier(className)) {
				JOptionPane.showMessageDialog(null,
						L10N.t("dialog.workspace.resources.import_java_model.invalid_model_name.message", className),
						L10N.t("dialog.workspace.resources.import_java_model.invalid_model_name.title"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		} catch (Exception err) {
			err.printStackTrace();
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("dialog.workspace.resources.import_java_model.invalid_model_format.message"),
					L10N.t("dialog.workspace.resources.import_java_model.invalid_model_format.title"),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		List<Import> imports = classJavaSource.getImports();
		for (Import imprt : imports) {
			classJavaSource.removeImport(imprt);
		}
		classJavaSource.setDefaultPackage();
		if (!classJavaSource.getName().startsWith("Model"))
			classJavaSource.setName("Model" + classJavaSource.getName());

		String finalModelCode = JavaModelAnimationEditorDialog
				.openAnimationEditorDialog(mcreator, classJavaSource.toString());

		if (finalModelCode == null)
			finalModelCode = classJavaSource.toString();

		if (new File(mcreator.getFolderManager().getModelsDir(), classJavaSource.getName() + ".java")
				.exists()) {
			JOptionPane.showMessageDialog(mcreator,
					L10N.t("dialog.workspace.resources.import_java_model.model_already_exists.message",
							classJavaSource.getName()),
					L10N.t("dialog.workspace.resources.import_java_model.model_already_exists.title"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		FileIO.writeStringToFile(finalModelCode.replace("setRotationAngles(f, f1, f2, f3, f4, f5);",
				"setRotationAngles(f, f1, f2, f3, f4, f5, entity);"),
				new File(mcreator.getFolderManager().getModelsDir(),
						classJavaSource.getName() + ".java"));
		mcreator.mv.resourcesPan.workspacePanelModels.reloadElements();
		if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI)
			((ModElementGUI) mcreator.mcreatorTabs.getCurrentTab().getContent()).reloadDataLists();
	}

	public static class JSON extends BasicAction {
		public JSON(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.resources.import_json_model"), actionEvent -> {
				File json = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".json" });
				if (json != null)
					importJSONModel(actionRegistry.getMCreator(), json);
			});
		}

		@Override public boolean isEnabled() {
			return actionRegistry.getMCreator().getGeneratorStats().getBaseCoverageInfo()
					.get("model_json") != GeneratorStats.CoverageStatus.NONE;
		}
	}

	public static void importJSONModel(MCreator mcreator, File file) {
		// find the textures
		String cmodel = FileIO.readFileToString(file);
		HashSet<String> txs = new HashSet<>();
		Pattern pattern = Pattern.compile("\"#(.*?)\"");
		Matcher matcher = pattern.matcher(cmodel);
		while (matcher.find())
			txs.add(matcher.group(1));

		newTextureMapDialog(mcreator, txs, file, true);

		mcreator.mv.resourcesPan.workspacePanelModels.reloadElements();
		if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI)
			((ModElementGUI) mcreator.mcreatorTabs.getCurrentTab().getContent()).reloadDataLists();
	}

	public static class OBJ extends BasicAction {
		public OBJ(ActionRegistry actionRegistry) {
			super(actionRegistry, L10N.t("action.workspace.resources.import_obj_mtl_model"), actionEvent -> {
				JOptionPane.showMessageDialog(actionRegistry.getMCreator(),
						L10N.t("dialog.workspace.resources.import_obj_mtl_model.message"),
						L10N.t("dialog.workspace.resources.import_obj_mtl_model.title"),
						JOptionPane.INFORMATION_MESSAGE);
				File obj = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".obj" });
				if (obj != null) {
					File mtl = FileDialogs.getOpenDialog(actionRegistry.getMCreator(), new String[] { ".mtl" });
					if (mtl != null)
						importOBJModel(actionRegistry.getMCreator(), obj, mtl);
				}
			});
		}

		@Override public boolean isEnabled() {
			return actionRegistry.getMCreator().getGeneratorStats().getBaseCoverageInfo()
					.get("model_obj") != GeneratorStats.CoverageStatus.NONE;
		}
	}

	public static void importOBJModel(MCreator mcreator, File obj, File mtl) {
		if (obj == null && mtl == null)
			return;

		if (mtl == null) {
			mtl = new File(obj.getAbsolutePath().replace(".obj", ".mtl"));
			if (!mtl.isFile())
				return;
		}

		if (obj == null) {
			obj = new File(mtl.getAbsolutePath().replace(".mtl", ".obj"));
			if (!obj.isFile())
				return;
		}

		String modelName = Transliteration.transliterateString(obj.getName()).toLowerCase(Locale.ENGLISH).trim()
				.replace(" ", "_").replace(":", "");
		File objFile = new File(mcreator.getFolderManager().getModelsDir(), modelName);
		FileIO.copyFile(obj, objFile);
		File mtlFile = new File(mcreator.getFolderManager().getModelsDir(),
				modelName.substring(0, modelName.lastIndexOf('.')) + ".mtl");
		FileIO.copyFile(mtl, mtlFile);

		try {
			List<Mtl> mtlList = MtlReader.read(new FileInputStream(mtlFile));
			HashSet<String> txs = new HashSet<>();
			for (Mtl mtlElement : mtlList)
				if (mtlElement.getMapKd() != null) {
					txs.add(mtlElement.getName());
					mtlElement.setMapKd(
							null); // we remove inline textures, they are defined in json or redefined by generator
				}
			if (!txs.isEmpty()) {
				newTextureMapDialog(mcreator, txs, objFile, false);
				MtlWriter.write(mtlList, new FileOutputStream(mtlFile));
			}
		} catch (Exception ignore) {
		}
		mcreator.mv.resourcesPan.workspacePanelModels.reloadElements();
		if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI)
			((ModElementGUI) mcreator.mcreatorTabs.getCurrentTab().getContent()).reloadDataLists();
	}

	private static void newTextureMapDialog(MCreator mcreator, HashSet<String> txs, File modelFile,
			boolean supportMultiple) {
		Map<String, TexturedModel.TextureMapping> textureMappingMap = new TextureMappingDialog(null)
				.openMappingDialog(mcreator, txs, supportMultiple);
		if (textureMappingMap != null) {
			String data = TexturedModel.getJSONForTextureMapping(textureMappingMap);
			FileIO.writeStringToFile(data, new File(mcreator.getFolderManager().getModelsDir(),
					Transliteration.transliterateString(modelFile.getName()).toLowerCase(Locale.ENGLISH).trim()
							.replace(":", "").replace(" ", "_") + ".textures"));
			// copy the actual model
			FileIO.copyFile(modelFile, new File(mcreator.getFolderManager().getModelsDir(),
					Transliteration.transliterateString(modelFile.getName()).toLowerCase(Locale.ENGLISH).trim()
							.replace(":", "").replace(" ", "_")));
		}
	}

}
