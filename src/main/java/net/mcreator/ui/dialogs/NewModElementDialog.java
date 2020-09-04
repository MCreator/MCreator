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
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.java.JavaConventions;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.TiledImageCache;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class NewModElementDialog {

	public static void showNameDialog(MCreator mcreator, ModElementType type) {
		JLabel regName = new JLabel("Registry name: <enter mod element name first>");
		regName.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		regName.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

		String modName = VOptionPane.showInputDialog(mcreator,
				"<html><font style=\"font-size: 13px;\">Enter the name of " + type.getReadableName() + ":</font><br>"
						+ "<font style=\"font-size: 8px;\">"
						+ "Do not use existing vanilla names (Dirt, Coal, Diamond, ...) for better compatibility.<br>"
						+ "Use upper-camel-cased names if possible (eg. CustomGem)<br>"
						+ "Avoid using type names in element names (eg. CustomGemItem, CustomTab, ...)",
				"Enter " + type.getReadableName() + " name", TiledImageCache.getModTypeIcon(type),
				new OptionPaneValidatior() {
					@Override public Validator.ValidationResult validate(JComponent component) {
						regName.setText("Registry name: " + RegistryNameFixer
								.fromCamelCase(((VTextField) component).getText()));
						return new ModElementNameValidator(mcreator.getWorkspace(), (VTextField) component).validate();
					}
				}, "Create new " + type.getReadableName(), "Cancel", null, regName);

		if (modName != null && !modName.equals("")) {
			modName = JavaConventions.convertToValidClassName(modName);

			ModElement element = new ModElement(mcreator.getWorkspace(), modName, type);
			ModElementGUI newGUI = ModElementTypeRegistry.REGISTRY.get(type).getModElement(mcreator, element, false);
			if (newGUI != null) {
				newGUI.showView();
				mcreator.getApplication().getAnalytics().async(() -> mcreator.getApplication().getAnalytics()
						.trackEvent(AnalyticsConstants.EVENT_NEW_MOD_ELEMENT, type.getReadableName(), null, null));
			}
		}
	}

}
