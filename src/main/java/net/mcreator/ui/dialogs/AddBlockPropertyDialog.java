/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AddBlockPropertyDialog {

	@Nullable
	public static PropertyDataWithValue<?> showDialog(MCreator mcreator, List<PropertyData<?>> currentEntries,
			Supplier<Collection<String>> nonUserProvidedProperties) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("elementgui.block.custom_properties.add.title"),
				true);

		VTextField name = new VTextField(24);
		name.setValidator(new UniqueNameValidator(L10N.t("elementgui.block.custom_properties.add.input"), name::getText,
				() -> currentEntries.stream().map(PropertyData::getName), nonUserProvidedProperties.get(),
				new RegistryNameValidator(name, L10N.t("elementgui.block.custom_properties.add.input"))));
		name.enableRealtimeValidation();
		JComboBox<String> type = new JComboBox<>(new String[] { "Logic", "Integer" });

		JMinMaxSpinner integerBounds = new JMinMaxSpinner(0, 1, 0, Integer.MAX_VALUE, 1);

		CardLayout cards = new CardLayout();
		JPanel bounds = new JPanel(cards);
		bounds.setPreferredSize(new Dimension(0, 28));
		bounds.add("Logic", new JEmptyBox());
		bounds.add("Integer", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				integerBounds));
		type.addActionListener(e -> cards.show(bounds, (String) type.getSelectedItem()));

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		AtomicReference<PropertyDataWithValue<?>> result = new AtomicReference<>(null);
		ok.addActionListener(e -> {
			if (name.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
				String propertyName = "CUSTOM:" + name.getText();
				if ("Logic".equals(type.getSelectedItem())) {
					result.set(new PropertyDataWithValue<>(new PropertyData.LogicType(propertyName), null));
					dialog.setVisible(false);
				} else if ("Integer".equals(type.getSelectedItem())) {
					result.set(new PropertyDataWithValue<>(
							new PropertyData.IntegerType(propertyName, integerBounds.getIntMinValue(),
									integerBounds.getIntMaxValue()), null));
					dialog.setVisible(false);
				}
			}
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		JPanel main = new JPanel(new GridLayout(0, 1, 0, 2));
		main.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.name"), name));
		main.add(PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.type"), type));
		main.add(bounds);

		dialog.getContentPane().add("Center", main);
		dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
		dialog.pack();
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return result.get();
	}

}
