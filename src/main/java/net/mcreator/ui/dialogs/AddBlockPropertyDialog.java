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

import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.PropertyDataWithValue;
import net.mcreator.ui.minecraft.states.block.BlockStatePropertyUtils;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class AddBlockPropertyDialog {

	@Nullable
	public static PropertyDataWithValue<?> showCreateDialog(MCreator mcreator, List<PropertyData<?>> currentEntries,
			Supplier<Collection<String>> nonUserProvidedProperties) {
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("elementgui.block.custom_properties.add.title"),
				true);

		VTextField name = new VTextField(24);
		name.setValidator(new UniqueNameValidator(L10N.t("elementgui.block.custom_properties.add.input"), name::getText,
				() -> currentEntries.stream().map(BlockStatePropertyUtils::propertyRegistryName),
				nonUserProvidedProperties.get(), new RegistryNameValidator(name,
				L10N.t("elementgui.block.custom_properties.add.input"))).setIsPresentOnList(false));
		name.enableRealtimeValidation();
		JComboBox<String> type = new JComboBox<>(new String[] { "Logic", "Integer", "Enum" });

		JMinMaxSpinner integerBounds = new JMinMaxSpinner(0, 1, 0, Integer.MAX_VALUE, 1);
		JStringListField stringBounds = new JStringListField(mcreator, e -> new RegistryNameValidator(e,
				L10N.t("elementgui.block.custom_properties.add.value"))).setUniqueEntries(true);

		CardLayout cards = new CardLayout();
		JPanel bounds = new JPanel(cards);
		bounds.setPreferredSize(new Dimension(0, 34));
		bounds.add("Logic", new JEmptyBox());
		bounds.add("Integer", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				integerBounds));
		bounds.add("Enum", PanelUtils.gridElements(1, 0, 2, 0, L10N.label("elementgui.block.custom_property.values"),
				stringBounds));
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
				} else if ("Enum".equals(type.getSelectedItem())) {
					List<String> textList = stringBounds.getTextList();
					if (textList.size() > 1) {
						result.set(new PropertyDataWithValue<>(
								new PropertyData.StringType(propertyName, textList.toArray(String[]::new)), null));
						dialog.setVisible(false);
					} else {
						JOptionPane.showMessageDialog(dialog,
								L10N.t("elementgui.block.custom_properties.add.error_invalid_values"),
								L10N.t("elementgui.block.custom_properties.add.error_invalid_values.title"),
								JOptionPane.ERROR_MESSAGE);
					}
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

	public static PropertyDataWithValue<?> showImportDialog(MCreator mcreator, List<PropertyData<?>> currentEntries,
			Supplier<Collection<String>> nonUserProvidedProperties) {
		DataListEntry property = DataListSelectorDialog.openSelectorDialog(mcreator,
				w -> DataListLoader.loadDataList("blockstateproperties").stream()
						.filter(e -> e.isSupportedInWorkspace(mcreator.getWorkspace())).toList(),
				L10N.t("elementgui.block.custom_properties.add_existing"),
				L10N.t("elementgui.block.custom_properties.add_existing.message"));
		if (property == null || !(property.getOther() instanceof Map<?, ?> other) || other.get("registry_name") == null)
			return null;

		String registryName = (String) other.get("registry_name");
		for (PropertyData<?> p : currentEntries) {
			if (registryName.equals(BlockStatePropertyUtils.propertyRegistryName(p))) {
				JOptionPane.showMessageDialog(mcreator,
						L10N.t("elementgui.block.custom_properties.add.error_duplicate"),
						L10N.t("elementgui.block.custom_properties.add.error_duplicate.title"),
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		for (String s : nonUserProvidedProperties.get()) {
			if (registryName.equals(s)) {
				JOptionPane.showMessageDialog(mcreator,
						L10N.t("elementgui.block.custom_properties.add.error_duplicate"),
						L10N.t("elementgui.block.custom_properties.add.error_duplicate.title"),
						JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}

		return BlockStatePropertyUtils.fromDataListEntry(property);
	}

}
