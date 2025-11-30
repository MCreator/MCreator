/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.BEItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class BEItemGUI extends ModElementGUI<BEItem> {

	private TextureSelectionButton texture;
	private final JCheckBox isGlowing = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(20).requireValue("elementgui.item.error_item_needs_name")
			.enableRealtimeValidation();
	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 1, 99, 1));
	private final JSpinner damageCount = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 1));
	private final JSpinner useDuration = new JSpinner(new SpinnerNumberModel(0, -100, 128000, 1));
	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private final JCheckBox isFood = L10N.checkbox("elementgui.common.enable");
	private final JSpinner nutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner saturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));
	private final JCheckBox isMeat = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox isAlwaysEdible = L10N.checkbox("elementgui.common.enable");

	private final ValidationGroup page1group = new ValidationGroup();

	public BEItemGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel visualsPanel = new JPanel(new BorderLayout(10, 10));
		visualsPanel.setOpaque(false);
		JPanel propertiesPanel= new JPanel(new BorderLayout(10, 10));
		propertiesPanel.setOpaque(false);
		JPanel foodPanel = new JPanel(new BorderLayout(10, 10));
		foodPanel.setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM)).requireValue(
				"elementgui.item.error_item_needs_texture");
		texture.setOpaque(false);

		JPanel visualProperties = new JPanel(new GridLayout( 1, 2, 5, 5));
		visualProperties.setOpaque(false);

		visualProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/is_glowing"),
				L10N.label("elementgui.beitem.is_glowing")));
		visualProperties.add(isGlowing);
		isGlowing.setOpaque(false);

		visualsPanel.add("Center", ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture")));

		JPanel basicProperties = new JPanel(new GridLayout(5, 2, 65, 5));

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		basicProperties.add(name);
		ComponentUtils.deriveFont(name, 16);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/stack_size"),
				L10N.label("elementgui.common.max_stack_size")));
		basicProperties.add(stackSize);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/damage_vs_entity"),
				L10N.label("elementgui.item.damage_vs_entity")));
		basicProperties.add(PanelUtils.westAndCenterElement(enableMeleeDamage, damageVsEntity));
		enableMeleeDamage.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.item.number_of_uses")));
		basicProperties.add(damageCount);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/use_duration"),
				L10N.label("elementgui.item.use_duration")));
		basicProperties.add(useDuration);

		JPanel foodProperties = new JPanel(new GridLayout(6, 2, 2, 2));
		foodProperties.setOpaque(false);

		isFood.setOpaque(false);
		isMeat.setOpaque(false);
		isAlwaysEdible.setOpaque(false);
		nutritionalValue.setOpaque(false);
		saturation.setOpaque(false);

		isFood.addActionListener(e -> {
			updateFoodPanel();
			if (!isEditingMode()) {
				useDuration.setValue(32);
			}
		});

		updateFoodPanel();

		foodProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_food"), L10N.label("elementgui.item.is_food")));
		foodProperties.add(isFood);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/nutritional_value"),
				L10N.label("elementgui.item.nutritional_value")));
		foodProperties.add(nutritionalValue);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/saturation"),
				L10N.label("elementgui.item.saturation")));
		foodProperties.add(saturation);

		foodProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_meat"), L10N.label("elementgui.item.is_meat")));
		foodProperties.add(isMeat);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/always_edible"),
				L10N.label("elementgui.item.is_edible")));
		foodProperties.add(isAlwaysEdible);

		foodPanel.add("Center", PanelUtils.totalCenterInPanel(foodProperties));
		foodPanel.setOpaque(false);

		page1group.addValidationElement(texture);

		addPage(L10N.t("elementgui.common.page_visual"), visualsPanel).validate(page1group);
		addPage(L10N.t("elementgui.common.page_properties"), propertiesPanel).validate(name);
		addPage(L10N.t("elementgui.item.food_properties"), foodPanel);
	}

	private void updateFoodPanel() {
		if (isFood.isSelected()) {
			nutritionalValue.setEnabled(true);
			saturation.setEnabled(true);
			isMeat.setEnabled(true);
			isAlwaysEdible.setEnabled(true);
		} else {
			nutritionalValue.setEnabled(false);
			saturation.setEnabled(false);
			isMeat.setEnabled(false);
			isAlwaysEdible.setEnabled(false);
		}
	}

	@Override protected void openInEditingMode(BEItem item) {
		texture.setTexture(item.texture);
		name.setText(item.name);
		stackSize.setValue(item.stackSize);
		useDuration.setValue(item.useDuration);
		damageCount.setValue(item.damageCount);
		isGlowing.setSelected(item.isGlowing);
		damageVsEntity.setValue(item.damageVsEntity);
		enableMeleeDamage.setSelected(item.enableMeleeDamage);
		isFood.setSelected(item.isFood);
		isMeat.setSelected(item.isMeat);
		isAlwaysEdible.setSelected(item.isAlwaysEdible);
		nutritionalValue.setValue(item.nutritionalValue);
		saturation.setValue(item.saturation);

		updateFoodPanel();
	}

	@Override public BEItem getElementFromGUI() {
		BEItem item = new BEItem(modElement);

		item.texture = texture.getTextureHolder();
		item.name = name.getText();
		item.stackSize = (int) stackSize.getValue();
		item.useDuration = (int) useDuration.getValue();
		item.damageCount = (int) damageCount.getValue();
		item.isGlowing = isGlowing.isSelected();
		item.damageVsEntity = (double) damageVsEntity.getValue();
		item.enableMeleeDamage = enableMeleeDamage.isSelected();
		item.isFood = isFood.isSelected();
		item.nutritionalValue = (int) nutritionalValue.getValue();
		item.saturation = (double) saturation.getValue();
		item.isMeat = isMeat.isSelected();
		item.isAlwaysEdible = isAlwaysEdible.isSelected();

		return item;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-item");
	}
}
