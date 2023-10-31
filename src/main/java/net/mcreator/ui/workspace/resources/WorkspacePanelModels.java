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

package net.mcreator.ui.workspace.resources;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.FileIO;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.JavaModelAnimationEditorDialog;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.TextureMappingDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkspacePanelModels extends AbstractResourcePanel<Model> {

	WorkspacePanelModels(WorkspacePanel workspacePanel) {
		super(workspacePanel, new ResourceFilterModel<>(workspacePanel,
						(item, query) -> item.getReadableName().toLowerCase(Locale.ENGLISH).contains(query) || item.getType()
								.name().toLowerCase(Locale.ENGLISH).contains(query), Model::getReadableName), new Render(),
				JList.HORIZONTAL_WRAP);

		elementList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					Model model = elementList.getSelectedValue();
					if (model.getType() == Model.Type.JAVA) {
						editSelectedModelAnimations();
					} else {
						editSelectedModelTextureMappings();
					}
				}
			}
		});

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_java")
				!= GeneratorStats.CoverageStatus.NONE)
			addToolBarButton("action.workspace.resources.import_java_model", UIRES.get("16px.importjavamodel"),
					e -> workspacePanel.getMCreator().actionRegistry.importJavaModel.doAction());

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_json")
				!= GeneratorStats.CoverageStatus.NONE)
			addToolBarButton("action.workspace.resources.import_json_model", UIRES.get("16px.importjsonmodel"),
					e -> workspacePanel.getMCreator().actionRegistry.importJSONModel.doAction());

		if (workspacePanel.getMCreator().getGeneratorStats().getBaseCoverageInfo().get("model_obj")
				!= GeneratorStats.CoverageStatus.NONE)
			addToolBarButton("action.workspace.resources.import_obj_mtl_model", UIRES.get("16px.importobjmodel"),
					e -> workspacePanel.getMCreator().actionRegistry.importOBJModel.doAction());

		addToolBarButton("workspace.3dmodels.edit_texture_mappings", UIRES.get("16px.edit.gif"),
				e -> editSelectedModelTextureMappings());
		addToolBarButton("workspace.3dmodels.redefine_animations", UIRES.get("16px.edit.gif"),
				e -> editSelectedModelAnimations());
		addToolBarButton("common.delete_selected", UIRES.get("16px.delete.gif"),
				e -> deleteCurrentlySelected());
	}

	@Override void deleteCurrentlySelected() {
		List<Model> elements = elementList.getSelectedValuesList();
		if (!elements.isEmpty()) {
			int n = JOptionPane.showConfirmDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.3dmodels.delete_confirm_message"), L10N.t("common.confirmation"),
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null);

			if (n == 0) {
				elements.forEach(model -> Arrays.stream(model.getFiles()).forEach(File::delete));
				reloadElements();
			}
		}
	}

	@Override public void reloadElements() {
		filterModel.removeAllElements();
		Model.getModels(workspacePanel.getMCreator().getWorkspace()).forEach(filterModel::addElement);
		refilterElements();
	}

	private void editSelectedModelAnimations() {
		Model model = elementList.getSelectedValue();
		if (model.getType() == Model.Type.JAVA) {
			File file = model.getFile();
			String code = FileIO.readFileToString(file);
			code = JavaModelAnimationEditorDialog.openAnimationEditorDialog(workspacePanel.getMCreator(), code);
			if (code != null) {
				FileIO.writeStringToFile(code, file);

				ProgressDialog dial = new ProgressDialog(workspacePanel.getMCreator(),
						L10N.t("workspace.3dmodels.regenerating_code"));
				Thread t = new Thread(() -> {
					ProgressDialog.ProgressUnit p0 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.3dmodels.regenerating_entity_code"));
					dial.addProgress(p0);

					AtomicInteger i = new AtomicInteger();
					// this model might be in use, we need to regenerate code of mobs
					workspacePanel.getMCreator().getWorkspace().getModElements().forEach(e -> {
						if (e.getType() == ModElementType.LIVINGENTITY && !e.isCodeLocked()) {
							GeneratableElement generatableElement = e.getGeneratableElement();
							if (generatableElement != null) {
								// generate mod element
								workspacePanel.getMCreator().getGenerator().generateElement(generatableElement);
							}
						}

						i.getAndIncrement();
						p0.setPercent((int) (((float) i.get() / (float) workspacePanel.getMCreator().getWorkspace()
								.getModElements().size()) * 100.0f));
						dial.refreshDisplay();
					});

					p0.ok();
					dial.refreshDisplay();

					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("workspace.3dmodels.rebuilding_workspace"));
					dial.addProgress(p2);
					workspacePanel.getMCreator().actionRegistry.buildWorkspace.doAction();
					p2.ok();
					dial.refreshDisplay();

					dial.hideAll();
				}, "WorkspaceModelsReload");
				t.start();
				dial.setVisible(true);
			}
		} else {
			JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.3dmodels.animation_unsupported_message"),
					L10N.t("workspace.3dmodels.animation_unsupported_title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	private void editSelectedModelTextureMappings() {
		Model model = elementList.getSelectedValue();
		Map<String, TexturedModel.TextureMapping> textureMappingMap = TexturedModel.getTextureMappingsForModel(model);
		if (textureMappingMap != null) {
			textureMappingMap = new TextureMappingDialog(textureMappingMap).openMappingDialog(
					workspacePanel.getMCreator(), null, model.getType() == Model.Type.JSON);
			if (textureMappingMap != null) {
				String data = TexturedModel.getJSONForTextureMapping(textureMappingMap);
				FileIO.writeStringToFile(data, new File(workspacePanel.getMCreator().getFolderManager().getModelsDir(),
						model.getFile().getName() + ".textures"));
			}
		} else {
			JOptionPane.showMessageDialog(workspacePanel.getMCreator(),
					L10N.t("workspace.3dmodels.mappings_unsupported_message"),
					L10N.t("workspace.3dmodels.mappings_unsupported_title"), JOptionPane.WARNING_MESSAGE);
		}
	}

	static class Render extends JLabel implements ListCellRenderer<Model> {

		@Override
		public JLabel getListCellRendererComponent(JList<? extends Model> list, Model ma, int index, boolean isSelected,
				boolean cellHasFocus) {
			setOpaque(isSelected);
			setBackground(isSelected ?
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") :
					(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setText(StringUtils.abbreviateString(ma.getReadableName(), 13));
			setToolTipText(ma.getReadableName());
			ComponentUtils.deriveFont(this, 11);
			setVerticalTextPosition(BOTTOM);
			setHorizontalTextPosition(CENTER);
			setHorizontalAlignment(CENTER);
			setIcon(UIRES.get("model." + ma.getType().name().toLowerCase(Locale.ENGLISH)));
			setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			return this;
		}

	}

}
