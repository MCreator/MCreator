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
import net.mcreator.ui.MCreatorApplication;
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
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class VillagerProfessionGUI extends ModElementGUI<VillagerProfession> {

	private final VTextField name = new VTextField(30);
	private final MCItemHolder pointOfInterest = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
	private final SoundSelector actionSound = new SoundSelector(mcreator);
	private final VComboBox<String> professionTextureFile = new SearchableComboBox<>();

	private final int fact = 64 * 5;

	private final JLabel clo = new JLabel();
	private final ValidationGroup page1group = new ValidationGroup();

	public VillagerProfessionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		professionTextureFile.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));
		professionTextureFile.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");

		JPanel panel2 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(name, 16);

		JPanel subpanel = new JPanel(new GridLayout(4, 2, 0, 2));
		subpanel.setOpaque(false);

		name.setEditable(true);

		ComponentUtils.deriveFont(name, 16);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofessions/name"),
				L10N.label("elementgui.villagerprofession.name")));
		subpanel.add(name);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofessions/point_of_interest"),
				L10N.label("elementgui.villagerprofession.point_of_interest")));
		subpanel.add(PanelUtils.centerInPanel(pointOfInterest));

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofessions/action_sound"),
				L10N.label("elementgui.villagerprofession.action_sound")));
		subpanel.add(actionSound);

		professionTextureFile.addActionListener(e -> updateProfessionTexturePreview());

		JButton importProfessionTexture = new JButton(UIRES.get("18px.add"));
		importProfessionTexture.setToolTipText(L10N.t("elementgui.villagerprofession.import_profession_texture"));
		importProfessionTexture.setOpaque(false);
		importProfessionTexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.OTHER);
			professionTextureFile.removeAllItems();
			professionTextureFile.addItem("");
			mcreator.getFolderManager().getTexturesList(TextureType.OTHER)
					.forEach(el -> professionTextureFile.addItem(el.getName()));
		});

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofessions/profession_texture"),
				L10N.label("elementgui.villagerprofession.profession_texture")));
		subpanel.add(PanelUtils.centerAndEastElement(professionTextureFile, importProfessionTexture));

		page1group.addValidationElement(name);
		page1group.addValidationElement(pointOfInterest);
		page1group.addValidationElement(actionSound.getVTextField());
		page1group.addValidationElement(professionTextureFile);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.villagerprofession.profession_needs_name")));
		name.enableRealtimeValidation();
		pointOfInterest.setValidator(new MCItemHolderValidator(pointOfInterest));
		actionSound.getVTextField().setValidator(new TextFieldValidator(actionSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null")));
		professionTextureFile.setValidator(() -> {
			if (professionTextureFile.getSelectedItem() == null || professionTextureFile.getSelectedItem().equals(""))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t("elementgui.villagerprofession.profession_needs_texture"));
			return Validator.ValidationResult.PASSED;
		});

		panel2.add(PanelUtils.totalCenterInPanel(subpanel));
		panel2.setOpaque(false);

		clo.setPreferredSize(new Dimension(fact, fact));

		JPanel clop = new JPanel();
		clop.setOpaque(false);
		clop.add(clo);

		JPanel panel = new JPanel(new BorderLayout(35, 35));
		panel.add("Center", panel2);
		panel.add("South", clop);
		panel.setOpaque(false);

		addPage(L10N.t("elementgui.common.page_properties"), panel);

		if (isEditingMode()) {
			name.setEnabled(false);
		}
	}

	private void updateProfessionTexturePreview() {
		File professionTexture = mcreator.getFolderManager()
				.getVillagerProfessionTextureFileForName(professionTextureFile.getSelectedItem());
		if (professionTexture.isFile()) {
			ImageIcon bg = new ImageIcon(
					ImageUtils.resize(new ImageIcon(professionTexture.getAbsolutePath()).getImage(), fact, fact));
			clo.setIcon(ImageUtils.drawOver(bg));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		List<File> others = mcreator.getFolderManager().getTexturesList(TextureType.OTHER);
		List<String> othersParts = new ArrayList<>();
		for (File texture : others)
			if (texture.getName().endsWith(".png"))
				othersParts.add(texture.getName().replace(".png", ""));
		ComboBoxUtil.updateComboBoxContents(professionTextureFile,
				ListUtils.merge(Collections.singleton(""), othersParts));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(VillagerProfession profession) {
		name.setText(profession.name.toUpperCase(Locale.ENGLISH));
		pointOfInterest.setBlock(profession.pointOfInterest);
		actionSound.setSound(profession.actionSound);
		professionTextureFile.setSelectedItem(profession.professionTextureFile);

		updateProfessionTexturePreview();
	}

	@Override public VillagerProfession getElementFromGUI() {
		VillagerProfession profession = new VillagerProfession(modElement);
		profession.name = name.getText();
		profession.pointOfInterest = pointOfInterest.getBlock();
		profession.actionSound = actionSound.getSound();
		profession.professionTextureFile = professionTextureFile.getSelectedItem();
		return profession;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-villager-profession");
	}
}
