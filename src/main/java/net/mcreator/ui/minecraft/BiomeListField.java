/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft;

import net.mcreator.element.parts.BiomeEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JItemListField;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.DataListSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.ResourceLocationValidator;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BiomeListField extends JItemListField<BiomeEntry> {

	public BiomeListField(MCreator mcreator) {
		super(mcreator, false);
	}

	public BiomeListField(MCreator mcreator, boolean allowTags) {
		super(mcreator, false, allowTags);
	}

	@Override protected List<BiomeEntry> getElementsToAdd() {
		return DataListSelectorDialog.openMultiSelectorDialog(mcreator, ElementUtil::loadAllBiomes,
						L10N.t("dialog.list_field.biome_list_title"), L10N.t("dialog.list_field.biome_list_message")).stream()
				.map(e -> new BiomeEntry(mcreator.getWorkspace(), e)).toList();
	}

	@Override protected List<BiomeEntry> getTagsToAdd() {
		List<BiomeEntry> tags = new ArrayList<>();

		VComboBox<String> tagName = new VComboBox<>();

		tagName.setValidator(new ResourceLocationValidator<>(L10N.t("modelement.tag"), tagName, true));

		tagName.addItem("");
		tagName.addItem("is_overworld");
		tagName.addItem("is_nether");
		tagName.addItem("is_end");
		tagName.addItem("is_badlands");
		tagName.addItem("is_beach");
		tagName.addItem("is_cave");
		tagName.addItem("is_cold/end");
		tagName.addItem("is_cold/overworld");
		tagName.addItem("is_cold");
		tagName.addItem("is_coniferous");
		tagName.addItem("is_deep_ocean");
		tagName.addItem("is_dense/overworld");
		tagName.addItem("is_dense");
		tagName.addItem("is_desert");
		tagName.addItem("is_dry/end");
		tagName.addItem("is_dry/nether");
		tagName.addItem("is_dry/overworld");
		tagName.addItem("is_dry");
		tagName.addItem("is_end");
		tagName.addItem("is_forest");
		tagName.addItem("is_hill");
		tagName.addItem("is_hot/nether");
		tagName.addItem("is_hot/overworld");
		tagName.addItem("is_hot");
		tagName.addItem("is_jungle");
		tagName.addItem("is_lush");
		tagName.addItem("is_mountain");
		tagName.addItem("is_mountain");
		tagName.addItem("is_mushroom");
		tagName.addItem("is_nether");
		tagName.addItem("is_ocean");
		tagName.addItem("is_overworld");
		tagName.addItem("is_peak");
		tagName.addItem("is_plains");
		tagName.addItem("is_plateau");
		tagName.addItem("is_rare");
		tagName.addItem("is_river");
		tagName.addItem("is_sandy");
		tagName.addItem("is_savanna");
		tagName.addItem("is_slope");
		tagName.addItem("is_snowy");
		tagName.addItem("is_sparse/overworld");
		tagName.addItem("is_sparse");
		tagName.addItem("is_spooky");
		tagName.addItem("is_swamp");
		tagName.addItem("is_taiga");
		tagName.addItem("is_underground");
		tagName.addItem("is_void");
		tagName.addItem("is_wasteland");
		tagName.addItem("is_water");
		tagName.addItem("is_wet/overworld");
		tagName.addItem("is_wet");

		tagName.setEditable(true);
		tagName.setOpaque(false);
		tagName.setForeground(Color.white);
		ComponentUtils.deriveFont(tagName, 16);

		tagName.enableRealtimeValidation();

		int result = JOptionPane.showConfirmDialog(mcreator,
				PanelUtils.northAndCenterElement(L10N.label("dialog.item_selector.enter_tag_name"), tagName),
				L10N.t("dialog.item_selector.use_tag"), JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			if (tagName.getValidationStatus().getValidationResultType()
					!= Validator.ValidationResultType.ERROR) {
				String selectedItem = tagName.getSelectedItem();
				if (selectedItem != null) {
					tags.add(new BiomeEntry(mcreator.getWorkspace(), "#" + selectedItem));
				}
			} else {
				JOptionPane.showMessageDialog(mcreator, tagName.getValidationStatus().getMessage(),
						L10N.t("dialog.item_selector.error_invalid_tag_name_title"), JOptionPane.ERROR_MESSAGE);
			}
		}

		return tags;
	}

}
