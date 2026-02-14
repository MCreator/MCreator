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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.ArmorTrim;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import java.util.*;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class ArmorTrimGUI extends ModElementGUI<ArmorTrim> {
	private final ValidationGroup page1group = new ValidationGroup();
	private final VTextField name = new VTextField(17);
	private final MCItemHolder item = new MCItemHolder(this.mcreator, ElementUtil::loadBlocksAndItems);
	private final TextureComboBox armorTextureFile = new TextureComboBox(mcreator, TextureType.ARMOR, true).requireValue("elementgui.armortrim.armortrim_needs_texture");
	private final JLabel clo1 = new JLabel();
	private final JLabel clo2 = new JLabel();

	private static final int ARMOR_TEXTURE_SIZE_FACTOR = 5;

	public ArmorTrimGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane1 = new JPanel(new BorderLayout());
		pane1.setOpaque(false);
		JPanel mainPanel = new JPanel(new GridLayout(3, 2, 0, 2));
		mainPanel.setOpaque(false);

		armorTextureFile.setAddPNGExtension(false);
		armorTextureFile.getComboBox().addActionListener(e -> updateArmorTexturePreview());
		page1group.addValidationElement(armorTextureFile);
		clo1.setPreferredSize(new Dimension(64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
		clo2.setPreferredSize(new Dimension(64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));

		JPanel clop = new JPanel();
		clop.add(clo1);
		clop.add(clo2);
		clop.setOpaque(false);

		JPanel merger = new JPanel(new BorderLayout(35, 35));
		merger.setOpaque(false);

		mainPanel.add(HelpUtils.wrapWithHelpButton(withEntry("armortrim/trim_name"), L10N.label("elementgui.armortrim.name")));
		mainPanel.add(name);
		mainPanel.add(HelpUtils.wrapWithHelpButton(withEntry("armortrim/smithing_template"), L10N.label("elementgui.armortrim.smithing_template")));
		mainPanel.add(PanelUtils.join(FlowLayout.LEFT, item));
		mainPanel.add(HelpUtils.wrapWithHelpButton(withEntry("armortrim/armor_layer_texture"), L10N.label("elementgui.armortrim.layer_texture")));
		mainPanel.add(this.armorTextureFile);

		item.setValidator(new MCItemHolderValidator(item));
		page1group.addValidationElement(item);
		page1group.addValidationElement(armorTextureFile);

		name.enableRealtimeValidation();
		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.armortrim.needs_layer_texture")));
		page1group.addValidationElement(name);

		if (!this.isEditingMode()) {
			name.setText(this.modElement.getName());
		}

		merger.add("Center", mainPanel);
		merger.add("South", clop);

		pane1.add("Center", PanelUtils.totalCenterInPanel(merger));
		addPage(pane1).validate(page1group);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		armorTextureFile.reload();
	}

	private void updateArmorTexturePreview() {
		String textureName = armorTextureFile.getTextureName();
		File[] armorTextures = mcreator.getFolderManager().getArmorTextureFilesForName(textureName);
		if (!textureName.isBlank() && armorTextures[0].isFile() && armorTextures[1].isFile()) {
			ImageIcon bg1 = new ImageIcon(
					ImageUtils.resize(new ImageIcon(armorTextures[0].getAbsolutePath()).getImage(),
							64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
			ImageIcon bg2 = new ImageIcon(
					ImageUtils.resize(new ImageIcon(armorTextures[1].getAbsolutePath()).getImage(),
							64 * ARMOR_TEXTURE_SIZE_FACTOR, 32 * ARMOR_TEXTURE_SIZE_FACTOR));
			ImageIcon front1 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1());
			ImageIcon front2 = new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2());
			clo1.setIcon(ImageUtils.drawOver(bg1, front1));
			clo2.setIcon(ImageUtils.drawOver(bg2, front2));
		} else {
			clo1.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame1()));
			clo2.setIcon(new ImageIcon(MinecraftImageGenerator.Preview.generateArmorPreviewFrame2()));
		}
	}

	@Override public void openInEditingMode(ArmorTrim trim) {
		name.setText(trim.name);
		item.setBlock(trim.item);
		armorTextureFile.setTextureFromTextureName(trim.armorTextureFile);
		updateArmorTexturePreview();
	}

	@Override public ArmorTrim getElementFromGUI() {
		ArmorTrim trim = new ArmorTrim(modElement);
		trim.name = name.getText();
		trim.item = item.getBlock();
		trim.armorTextureFile = Objects.requireNonNull(armorTextureFile.getComboBox().getSelectedItem()).getTextureName();
		return trim;
	}

	@Override @Nullable public URI contextURL() throws URISyntaxException {
		return null;
	}

}
