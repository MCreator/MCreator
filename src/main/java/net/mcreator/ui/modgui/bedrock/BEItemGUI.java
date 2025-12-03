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

package net.mcreator.ui.modgui.bedrock;

import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class BEItemGUI extends ModElementGUI<BEItem> {

	private TextureSelectionButton texture;
	private final JCheckBox hasGlint = L10N.checkbox("elementgui.common.enable");

	private final VTextField name = new VTextField(20).requireValue("elementgui.item.error_item_needs_name")
			.enableRealtimeValidation();
	private final JSpinner stackSize = new JSpinner(new SpinnerNumberModel(64, 1, 99, 1));
	private final JSpinner maxDurability = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 1));
	private final JSpinner useDuration = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JSpinner damageVsEntity = new JSpinner(new SpinnerNumberModel(0, 0, 128000, 0.1));
	private final JCheckBox enableMeleeDamage = new JCheckBox();

	private final JCheckBox isFood = L10N.checkbox("elementgui.common.enable");
	private final JSpinner foodNutritionalValue = new JSpinner(new SpinnerNumberModel(4, -1000, 1000, 1));
	private final JSpinner foodSaturation = new JSpinner(new SpinnerNumberModel(0.3, -1000, 1000, 0.1));
	private final JCheckBox foodCanAlwaysEat = L10N.checkbox("elementgui.common.enable");

	private final ValidationGroup page1group = new ValidationGroup();

	public BEItemGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel visualsPanel = new JPanel(new BorderLayout(10, 10));
		visualsPanel.setOpaque(false);
		JPanel propertiesPanel = new JPanel(new BorderLayout(10, 10));
		propertiesPanel.setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM)).requireValue(
				"elementgui.item.error_item_needs_texture");
		texture.setOpaque(false);

		JPanel visualProperties = new JPanel(new GridLayout(1, 2, 5, 5));
		visualProperties.setOpaque(false);

		visualProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/has_glint"),
				L10N.label("elementgui.beitem.has_glint")));
		visualProperties.add(hasGlint);
		hasGlint.setOpaque(false);

		visualsPanel.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndSouthElement(
				ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture")), visualProperties)));

		JPanel basicProperties = new JPanel(new GridLayout(4, 2, 65, 5));
		basicProperties.setOpaque(false);

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
		enableMeleeDamage.addActionListener(e -> updateMeleeDamage());
		updateMeleeDamage();
		enableMeleeDamage.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/number_of_uses"),
				L10N.label("elementgui.item.number_of_uses")));
		basicProperties.add(maxDurability);

		basicProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.common.page_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		JPanel foodProperties = new JPanel(new GridLayout(5, 2, 65, 5));
		foodProperties.setOpaque(false);

		foodProperties.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/is_food"), L10N.label("elementgui.item.is_food")));
		foodProperties.add(isFood);
		isFood.addActionListener(e -> {
			updateFoodPanel();
			if (!isEditingMode()) {
				useDuration.setValue(1.6);
			}
		});
		updateFoodPanel();
		isFood.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/nutritional_value"),
				L10N.label("elementgui.item.nutritional_value")));
		foodProperties.add(foodNutritionalValue);
		foodNutritionalValue.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/saturation"),
				L10N.label("elementgui.item.saturation")));
		foodProperties.add(foodSaturation);
		foodSaturation.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/always_edible"),
				L10N.label("elementgui.item.is_edible")));
		foodProperties.add(foodCanAlwaysEat);
		foodCanAlwaysEat.setOpaque(false);

		foodProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/use_duration"),
				L10N.label("elementgui.beitem.use_duration")));
		foodProperties.add(useDuration);

		foodProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item.food_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		propertiesPanel.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.centerAndEastElement(PanelUtils.pullElementUp(basicProperties),
						PanelUtils.pullElementUp(foodProperties))));

		page1group.addValidationElement(texture);

		addPage(L10N.t("elementgui.common.page_visual"), visualsPanel).validate(page1group);
		addPage(L10N.t("elementgui.common.page_properties"), propertiesPanel).validate(name);
	}

	private void updateFoodPanel() {
		if (isFood.isSelected()) {
			foodNutritionalValue.setEnabled(true);
			foodSaturation.setEnabled(true);
			foodCanAlwaysEat.setEnabled(true);
			useDuration.setEnabled(true);
		} else {
			foodNutritionalValue.setEnabled(false);
			foodSaturation.setEnabled(false);
			foodCanAlwaysEat.setEnabled(false);
			useDuration.setEnabled(false);
		}
	}

	private void updateMeleeDamage() {
		damageVsEntity.setEnabled(enableMeleeDamage.isSelected());
	}

	@Override protected void openInEditingMode(BEItem item) {
		texture.setTexture(item.texture);
		name.setText(item.name);
		stackSize.setValue(item.stackSize);
		useDuration.setValue(item.useDuration);
		maxDurability.setValue(item.maxDurability);
		hasGlint.setSelected(item.hasGlint);
		damageVsEntity.setValue(item.damageVsEntity);
		enableMeleeDamage.setSelected(item.enableMeleeDamage);
		isFood.setSelected(item.isFood);
		foodCanAlwaysEat.setSelected(item.foodCanAlwaysEat);
		foodNutritionalValue.setValue(item.foodNutritionalValue);
		foodSaturation.setValue(item.foodSaturation);

		updateFoodPanel();
		updateMeleeDamage();
	}

	@Override public BEItem getElementFromGUI() {
		BEItem item = new BEItem(modElement);

		item.texture = texture.getTextureHolder();
		item.name = name.getText();
		item.stackSize = (int) stackSize.getValue();
		item.useDuration = (double) useDuration.getValue();
		item.maxDurability = (int) maxDurability.getValue();
		item.hasGlint = hasGlint.isSelected();
		item.damageVsEntity = (double) damageVsEntity.getValue();
		item.enableMeleeDamage = enableMeleeDamage.isSelected();
		item.isFood = isFood.isSelected();
		item.foodNutritionalValue = (int) foodNutritionalValue.getValue();
		item.foodSaturation = (double) foodSaturation.getValue();
		item.foodCanAlwaysEat = foodCanAlwaysEat.isSelected();

		return item;
	}

	@Nullable @Override public URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-item");
	}
}
