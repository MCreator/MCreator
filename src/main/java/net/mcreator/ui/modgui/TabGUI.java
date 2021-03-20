/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

import net.mcreator.element.types.Tab;
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
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

public class TabGUI extends ModElementGUI<Tab> {

	private final VTextField name = new VTextField(20);
	private MCItemHolder icon;
	private final VComboBox<String> bgTexture = new SearchableComboBox<>();
	private final JCheckBox showSearch = L10N.checkbox("elementgui.common.enable");

	private final ValidationGroup page1group = new ValidationGroup();

	public TabGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		icon = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		JPanel pane3 = new JPanel(new BorderLayout());
		JPanel selp = new JPanel(new GridLayout(4, 2, 100, 1));

		ComponentUtils.deriveFont(name, 16);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/name"), L10N.label("elementgui.tab.name")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/icon"), L10N.label("elementgui.tab.icon")));
		selp.add(PanelUtils.join(FlowLayout.LEFT, icon));

		bgTexture.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));
		bgTexture.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");JButton importicontexture = new JButton(UIRES.get("18px.add"));
		importicontexture.setToolTipText(L10N.t("elementgui.tab.import_background"));
		importicontexture.setOpaque(false);
		importicontexture.addActionListener(e -> {
			TextureImportDialogs.importOtherTextures(mcreator);
			bgTexture.removeAllItems();
			bgTexture.addItem("");
			mcreator.getFolderManager().getOtherTexturesList().forEach(el -> bgTexture.addItem(el.getName()));
		});

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/background_texture"),
				L10N.label("elementgui.tab.background_texture")));
		selp.add(PanelUtils.centerAndEastElement(bgTexture, importicontexture));

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("tab/search_bar"), L10N.label("elementgui.tab.search_bar")));
		selp.add(showSearch);
		showSearch.addActionListener(e -> enableBackgroundTexture());

		showSearch.setOpaque(false);

		selp.setOpaque(false);

		JPanel slpa = new JPanel(new BorderLayout(25, 0));

		slpa.add("Center", PanelUtils.centerInPanel(selp));

		slpa.setOpaque(false);

		slpa.setMaximumSize(new Dimension(700, 110));

		slpa.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.tab.add_stuff_tip"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane3.add(PanelUtils.totalCenterInPanel(slpa));
		pane3.setOpaque(false);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.tab.error_needs_name")));
		name.enableRealtimeValidation();
		icon.setValidator(new MCItemHolderValidator(icon));

		page1group.addValidationElement(name);
		page1group.addValidationElement(icon);

		addPage(pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
		enableBackgroundTexture();
	}

	private void enableBackgroundTexture() {
		if(showSearch.isSelected())
			bgTexture.setEnabled(false);
		else
			bgTexture.setEnabled(true);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(bgTexture, ListUtils.merge(Collections.singleton(""),
				mcreator.getFolderManager().getOtherTexturesList().stream().map(File::getName)
						.collect(Collectors.toList())), "");
	}


	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Tab tab) {
		//Executed for Minecraft 1.16.4 and before
		//Delete the old texture file if a new one is selected
		if (Float.parseFloat(org.apache.commons.lang3.StringUtils
				.removeStart(getModElement().getWorkspace().getGeneratorConfiguration().getGeneratorMinecraftVersion(),
						"1.")) < 16.5) {
			if (!Objects.equals(FilenameUtils.removeExtension(bgTexture.getSelectedItem()), tab.bgTexture)) {
				File texture = new File(getModElement().getFolderManager().getWorkspaceFolder(),
						"src/main/resources/assets/minecraft/textures/gui/container/creative_inventory/tab_"
								+ FilenameUtils.removeExtension(tab.bgTexture) + ".png");
				texture.delete();
			}
		}

		name.setText(tab.name);
		icon.setBlock(tab.icon);
		bgTexture.setSelectedItem(tab.bgTexture);
		showSearch.setSelected(tab.showSearch);
	}

	@Override public Tab getElementFromGUI() {
		Tab tab = new Tab(modElement);
		tab.name = name.getText();
		tab.icon = icon.getBlock();
		tab.bgTexture = bgTexture.getSelectedItem();
		tab.showSearch = showSearch.isSelected();
		return tab;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-creative-inventory-tab");
	}

}
