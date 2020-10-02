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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class TabGUI extends ModElementGUI<Tab> {

	private final VTextField name = new VTextField(20);
	private MCItemHolder icon;
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
		JPanel selp = new JPanel(new GridLayout(3, 2, 100, 1));

		ComponentUtils.deriveFont(name, 16);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/name"),
				L10N.label("elementgui.tab.name")));
		selp.add(name);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/icon"),
				L10N.label("elementgui.tab.icon")));
		selp.add(PanelUtils.join(FlowLayout.LEFT, icon));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("tab/search_bar"),
				L10N.label("elementgui.tab.search_bar")));
		selp.add(showSearch);

		showSearch.setOpaque(false);

		selp.setOpaque(false);

		JPanel slpa = new JPanel(new BorderLayout(25, 0));

		slpa.add("Center", PanelUtils.centerInPanel(selp));

		slpa.setOpaque(false);

		slpa.setMaximumSize(new Dimension(700, 110));

		slpa.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.tab.add_stuff_tip"),
				TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
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
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Tab tab) {
		name.setText(tab.name);
		icon.setBlock(tab.icon);
		showSearch.setSelected(tab.showSearch);
	}

	@Override public Tab getElementFromGUI() {
		Tab tab = new Tab(modElement);
		tab.name = name.getText();
		tab.icon = icon.getBlock();
		tab.showSearch = showSearch.isSelected();
		return tab;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-creative-inventory-tab");
	}

}
