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

package net.mcreator.ui.modgui;

import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.BannerPattern;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;

public class BannerPatternGUI extends ModElementGUI<BannerPattern> {

	private TextureHolder bannerTexture;
	private TextureHolder shieldTexture;
	private TextureHolder texture;

	private final VTextField title = new VTextField(28);
	private final VTextField description = new VTextField(28);
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	public BannerPatternGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		bannerTexture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		bannerTexture.setOpaque(false);
		shieldTexture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		shieldTexture.setOpaque(false);
		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));
		texture.setOpaque(false);

		JComponent textureComponent = PanelUtils.centerInPanel(PanelUtils.centerAndEastElement(
				ComponentUtils.squareAndBorder(
						HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/banner_texture"), bannerTexture),
						L10N.t("elementgui.bannerpattern.banner_texture")), ComponentUtils.squareAndBorder(
						HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/shield_texture"), shieldTexture),
						L10N.t("elementgui.bannerpattern.shield_texture"))));

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setOpaque(false);

		ComponentUtils.deriveFont(title, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel components = new JPanel(new GridLayout(3, 2, 10, 10));
		components.setOpaque(false);

		components.add(HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/title"),
				L10N.label("elementgui.bannerpattern.title")));
		components.add(title);

		components.add(HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/description"),
				L10N.label("elementgui.bannerpattern.description")));
		components.add(description);

		components.add(HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/creative_tab"),
				L10N.label("elementgui.bannerpattern.creative_tab")));
		components.add(creativeTab);

		mainPanel.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(textureComponent,
				PanelUtils.northAndCenterElement(PanelUtils.gridElements(1, 2,
						HelpUtils.wrapWithHelpButton(this.withEntry("bannerpattern/item_texture"),
								L10N.label("elementgui.bannerpattern.item_texture")),
						PanelUtils.centerInPanel(texture)), components), 25, 25)));

		bannerTexture.setValidator(new TileHolderValidator(bannerTexture));
		shieldTexture.setValidator(new TileHolderValidator(shieldTexture));
		texture.setValidator(new TileHolderValidator(texture));

		title.setValidator(new TextFieldValidator(title, L10N.t("elementgui.bannerpattern.needs_title")));
		title.enableRealtimeValidation();

		description.setValidator(
				new TextFieldValidator(description, L10N.t("elementgui.bannerpattern.needs_description")));
		description.enableRealtimeValidation();

		page1group.addValidationElement(bannerTexture);
		page1group.addValidationElement(shieldTexture);
		page1group.addValidationElement(texture);
		page1group.addValidationElement(title);
		page1group.addValidationElement(description);

		addPage(L10N.t("elementgui.common.page_properties"), mainPanel);

		if (!isEditingMode()) {
			String readableNameFromModElement = net.mcreator.util.StringUtils.machineToReadableName(
					modElement.getName());
			title.setText(L10N.t("elementgui.bannerpattern.default_name"));
			description.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("MISC"));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(BannerPattern bannerPattern) {
		bannerTexture.setTextureFromTextureName(bannerPattern.bannerTexture);
		shieldTexture.setTextureFromTextureName(bannerPattern.shieldTexture);
		texture.setTextureFromTextureName(bannerPattern.texture);
		title.setText(bannerPattern.title);
		description.setText(bannerPattern.description);
		creativeTab.setSelectedItem(bannerPattern.creativeTab.getUnmappedValue());
	}

	@Override public BannerPattern getElementFromGUI() {
		BannerPattern bannerPattern = new BannerPattern(modElement);
		bannerPattern.bannerTexture = bannerTexture.getID();
		bannerPattern.shieldTexture = shieldTexture.getID();
		bannerPattern.texture = texture.getID();
		bannerPattern.title = title.getText();
		bannerPattern.description = description.getText();
		bannerPattern.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		return bannerPattern;
	}
}
