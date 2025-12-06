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

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Block;
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidator;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.CompoundValidator;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.workspace.breadcrumb.WorkspaceFolderBreadcrumb;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.util.Objects;
import java.util.Optional;

public class NewModElementDialog {

	public static void showNameDialog(MCreator mcreator, ModElementType<?> type) {
		JLabel regName = L10N.label("dialog.new_modelement.registry_name",
				L10N.t("dialog.new_modelement.registry_name.empty"));
		regName.setForeground(Theme.current().getAltForegroundColor());
		regName.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		WorkspaceFolderBreadcrumb.Small breadcrumb = new WorkspaceFolderBreadcrumb.Small(mcreator);

		String modName = VOptionPane.showInputDialog(mcreator,
				L10N.t("dialog.new_modelement.desc", type.getReadableName()),
				L10N.t("dialog.new_modelement.title_window", type.getReadableName()), type.getIcon(),
				new OptionPaneValidator() {
					@Override public ValidationResult validate(JComponent component) {
						String elementName = ((VTextField) component).getText();
						String regNameString = RegistryNameFixer.fromCamelCase(elementName);
						regName.setText(L10N.t("dialog.new_modelement.registry_name",
								regNameString == null || regNameString.isEmpty() ?
										L10N.t("dialog.new_modelement.registry_name.empty") :
										regNameString));
						return new CompoundValidator(
								new ModElementNameValidator(mcreator.getWorkspace(), (VTextField) component,
										L10N.t("common.mod_element_name")),
								// Avoid collision between new mod element and existing signs
								() -> {
									Optional<ModElement> conflictingElement = mcreator.getWorkspace().getModElements()
											.stream()
											.filter(me -> me.getType() == ModElementType.BLOCK && "Sign".equals(
													((Block) Objects.requireNonNull(
															me.getGeneratableElement())).blockBase))
											.filter(me -> ("Wall" + me.getName()).equals(elementName)).findFirst();
									return conflictingElement.map(
											modElement -> new ValidationResult(ValidationResult.Type.ERROR,
													L10N.t("dialog.new_modelement.error_name_conflict", elementName,
															modElement.getName()))).orElse(ValidationResult.PASSED);
								}).validate();
					}
				}, L10N.t("dialog.new_modelement.create_new", type.getReadableName()),
				UIManager.getString("OptionPane.cancelButtonText"), null, breadcrumb.getInScrollPane(), regName);

		if (modName != null && !modName.isEmpty()) {
			modName = JavaConventions.convertToValidClassName(modName);

			ModElement element = new ModElement(mcreator.getWorkspace(), modName, type);

			ModElementGUI<?> newGUI = type.getModElementGUI(mcreator, element, false);
			if (newGUI != null) {
				newGUI.setTargetFolder(breadcrumb.getCurrentFolder());
				newGUI.showView();
			}
		}
	}

}
