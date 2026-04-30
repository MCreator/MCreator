/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.dialogs.tools.quickrecipestool;

import net.mcreator.element.ModElementType;
import net.mcreator.element.types.Recipe;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.RecipeTemplatesLoader;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JRecipeListEntry extends JSimpleListEntry<RecipeListEntry> {

	private final VTextField name = new VTextField(15);
	private final JComboBox<String> template;
	private final MCItemHolder input, result;
	private final JCheckBox generateStonecuttingRecipe = L10N.checkbox("dialog.tools.quick_recipes.generate_stonecutting_recipe");

	public JRecipeListEntry(MCreator mcreator, JPanel parent, List<? extends JSimpleListEntry<RecipeListEntry>> entryList,
			ValidationGroup validableElements) {
		super(parent, entryList);

		template = new JComboBox<>(RecipeTemplatesLoader.getRecipeTemplates().stream().sorted().toList().toArray(new String[0]));
		input = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItemsAndTags, true).requireValue(
				"dialog.tools.quick_recipes.input_validator");
		result = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems, false).requireValue(
				"dialog.tools.quick_recipes.result_validator");

		line.add(L10N.label("dialog.tools.quick_recipes.name"));
		name.setPreferredSize(new Dimension(300, 30));
		name.setValidator(new UniqueNameValidator(L10N.t("modelement.recipe"), name::getText, () -> {
			List<String> names = new ArrayList<>();
			names.addAll(mcreator.getWorkspace().getModElements().stream()
					.filter(me -> me.getType() == ModElementType.RECIPE).map(ModElement::getGeneratableElement)
					.filter(Objects::nonNull).map(ge -> ((Recipe) ge).namespace + ":" + ((Recipe) ge).name).toList());
			names.addAll(entryList.stream().map(e -> e.getEntry().name).toList());

			return names.stream();
		}, new ModElementNameValidator(mcreator.getWorkspace(), name, name.getText())));
		name.enableRealtimeValidation();
		validableElements.addValidationElement(name);
		line.add(name);

		line.add(L10N.label("dialog.tools.quick_recipes.template"));
		template.setPreferredSize(new Dimension(125, 30));
		line.add(template);

		generateStonecuttingRecipe.setSelected(true);
		line.add(generateStonecuttingRecipe);

		line.add(L10N.label("dialog.tools.quick_recipes.input"));
		input.setValidator(new MCItemHolderValidator(input).considerAirAsEmpty());
		validableElements.addValidationElement(input);
		line.add(PanelUtils.totalCenterInPanel(input));

		line.add(L10N.label("dialog.tools.quick_recipes.result"));
		result.setValidator(new MCItemHolderValidator(result).considerAirAsEmpty());
		validableElements.addValidationElement(result);
		line.add(PanelUtils.totalCenterInPanel(result));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		name.setEnabled(enabled);
		template.setEnabled(enabled);
		generateStonecuttingRecipe.setEnabled(enabled);
		input.setEnabled(enabled);
		result.setEnabled(enabled);
	}

	@Override public RecipeListEntry getEntry() {
		RecipeListEntry entry = new RecipeListEntry();
		entry.name = name.getText();
		entry.template = (String) template.getSelectedItem();
		entry.generateStonecuttingRecipe = generateStonecuttingRecipe.isSelected();
		entry.input = input.getBlock();
		entry.result = result.getBlock();

		return entry;
	}

	@Override public void setEntry(RecipeListEntry entry) {
	}
}
