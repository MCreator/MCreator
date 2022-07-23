/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.element.types.VillagerProfession;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.PointOfInterestValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.stream.Collectors;

public class VillagerProfessionGUI extends ModElementGUI<VillagerProfession> {

	private final VTextField name = new VTextField(30);
	private final VTextField displayName = new VTextField(30);
	private final MCItemHolder pointOfInterest = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
	private final SoundSelector actionSound = new SoundSelector(mcreator);
	private final JComboBox<String> hat = new JComboBox<>(new String[] { "None", "Partial", "Full" });
	private final VComboBox<String> professionTextureFile = new SearchableComboBox<>();
	private final VComboBox<String> zombifiedProfessionTextureFile = new SearchableComboBox<>();

	private final JLabel texturePreview = new JLabel();
	private final JLabel zombifiedTexturePreview = new JLabel();
	private final ValidationGroup page1group = new ValidationGroup();

	public VillagerProfessionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		professionTextureFile.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		professionTextureFile.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");
		zombifiedProfessionTextureFile.setRenderer(
				new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.ENTITY));
		zombifiedProfessionTextureFile.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(displayName, 16);
		ComponentUtils.deriveFont(hat, 16);

		JPanel subpanel = new JPanel(new GridLayout(7, 2, 0, 2));
		subpanel.setOpaque(false);

		name.setEnabled(false);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/name"),
				L10N.label("elementgui.villager_profession.name")));
		subpanel.add(name);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/display_name"),
				L10N.label("elementgui.villager_profession.display_name")));
		subpanel.add(displayName);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/profession_block"),
				L10N.label("elementgui.villager_profession.profession_block")));
		subpanel.add(PanelUtils.centerInPanel(pointOfInterest));

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/action_sound"),
				L10N.label("elementgui.villager_profession.action_sound")));
		subpanel.add(actionSound);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/hat"),
				L10N.label("elementgui.villager_profession.hat")));
		subpanel.add(hat);

		professionTextureFile.addActionListener(e -> updateProfessionTexturePreview());

		JButton importProfessionTexture = new JButton(UIRES.get("18px.add"));
		importProfessionTexture.setToolTipText(L10N.t("elementgui.villager_profession.import_profession_texture"));
		importProfessionTexture.setOpaque(false);
		importProfessionTexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY);
			professionTextureFile.removeAllItems();
			professionTextureFile.addItem("");
			mcreator.getFolderManager().getTexturesList(TextureType.ENTITY)
					.forEach(el -> professionTextureFile.addItem(el.getName()));
		});

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/profession_texture"),
				L10N.label("elementgui.villager_profession.profession_texture")));
		subpanel.add(PanelUtils.centerAndEastElement(professionTextureFile, importProfessionTexture));

		zombifiedProfessionTextureFile.addActionListener(e -> updateZombifiedProfessionTexturePreview());

		JButton importZombifiedProfessionTexture = new JButton(UIRES.get("18px.add"));
		importZombifiedProfessionTexture.setToolTipText(
				L10N.t("elementgui.villager_profession.import_zombified_profession_texture"));
		importZombifiedProfessionTexture.setOpaque(false);
		importZombifiedProfessionTexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY);
			zombifiedProfessionTextureFile.removeAllItems();
			zombifiedProfessionTextureFile.addItem("");
			mcreator.getFolderManager().getTexturesList(TextureType.ENTITY)
					.forEach(el -> zombifiedProfessionTextureFile.addItem(el.getName()));
		});

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/zombified_profession_texture"),
				L10N.label("elementgui.villager_profession.zombified_profession_texture")));
		subpanel.add(PanelUtils.centerAndEastElement(zombifiedProfessionTextureFile, importZombifiedProfessionTexture));

		page1group.addValidationElement(name);
		page1group.addValidationElement(displayName);
		page1group.addValidationElement(pointOfInterest);
		page1group.addValidationElement(actionSound.getVTextField());
		page1group.addValidationElement(professionTextureFile);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.villager_profession.profession_needs_name")));
		name.enableRealtimeValidation();
		displayName.setValidator(new TextFieldValidator(displayName,
				L10N.t("elementgui.villager_profession.profession_needs_display_name")));
		displayName.enableRealtimeValidation();
		pointOfInterest.setValidator(new PointOfInterestValidator(mcreator, pointOfInterest));
		actionSound.getVTextField().setValidator(new TextFieldValidator(actionSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null")));
		professionTextureFile.setValidator(() -> {
			if (professionTextureFile.getSelectedItem() == null || professionTextureFile.getSelectedItem().equals(""))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.villager_profession.profession_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		JPanel mainPanel = new JPanel(new BorderLayout(0, 50));
		mainPanel.add("North", subpanel);
		mainPanel.add("Center",
				PanelUtils.centerInPanel(PanelUtils.westAndEastElement(texturePreview, zombifiedTexturePreview)));
		mainPanel.setOpaque(false);

		addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(mainPanel));

		if (!isEditingMode()) {
			name.setText(modElement.getName().toUpperCase(Locale.ROOT));
			displayName.setText(StringUtils.uppercaseFirstLetter(modElement.getName().toLowerCase(Locale.ROOT)));
		}
	}

	private void updateProfessionTexturePreview() {
		if (professionTextureFile.getSelectedItem() == null)
			return;

		File professionTexture = mcreator.getFolderManager()
				.getTextureFile(professionTextureFile.getSelectedItem().replace(".png", ""), TextureType.ENTITY);

		texturePreview.setIcon(new ImageIcon(
				ImageUtils.resize(new ImageIcon(professionTexture.getAbsolutePath()).getImage(), 320, 320)));
	}

	private void updateZombifiedProfessionTexturePreview() {
		if (zombifiedProfessionTextureFile.getSelectedItem() == null)
			return;

		File zombifiedProfessionTexture = mcreator.getFolderManager()
				.getTextureFile(zombifiedProfessionTextureFile.getSelectedItem().replace(".png", ""),
						TextureType.ENTITY);

		zombifiedTexturePreview.setIcon(new ImageIcon(
				ImageUtils.resize(new ImageIcon(zombifiedProfessionTexture.getAbsolutePath()).getImage(), 320, 320)));
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(professionTextureFile, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.collect(Collectors.toList())), "");
		ComboBoxUtil.updateComboBoxContents(zombifiedProfessionTextureFile, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getTexturesList(TextureType.ENTITY).stream().map(File::getName)
						.collect(Collectors.toList())), "");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(VillagerProfession profession) {
		name.setText(modElement.getName().toUpperCase(Locale.ROOT));
		displayName.setText(profession.displayName);
		pointOfInterest.setBlock(profession.pointOfInterest);
		actionSound.setSound(profession.actionSound);
		hat.setSelectedItem(profession.hat);
		professionTextureFile.setSelectedItem(profession.professionTextureFile);
		zombifiedProfessionTextureFile.setSelectedItem(profession.zombifiedProfessionTextureFile);

		updateProfessionTexturePreview();
	}

	@Override public VillagerProfession getElementFromGUI() {
		VillagerProfession profession = new VillagerProfession(modElement);
		profession.name = StringUtils.uppercaseFirstLetter(modElement.getName().toLowerCase(Locale.ROOT));
		profession.displayName = displayName.getText();
		profession.pointOfInterest = pointOfInterest.getBlock();
		profession.actionSound = actionSound.getSound();
		profession.hat = (String) hat.getSelectedItem();
		profession.professionTextureFile = professionTextureFile.getSelectedItem();
		profession.zombifiedProfessionTextureFile = zombifiedProfessionTextureFile.getSelectedItem();
		return profession;
	}

	@Override protected void beforeGeneratableElementGenerated() {
		super.beforeGeneratableElementGenerated();
		modElement.setRegistryName(modElement.getName().toLowerCase(Locale.ROOT));
	}
}
