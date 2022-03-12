/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the tefms of the GNU General Public License as published by
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

import net.mcreator.element.types.ItemExtension;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.FuelRecipeMaker;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ItemExtensionGUI extends ModElementGUI<ItemExtension> {

	private final JComboBox<String> extension = new JComboBox<>(
			new String[] { "Fuel", "Dispenser behaviour", "Composter item" });
	// Fuel
	private final FuelRecipeMaker fm = new FuelRecipeMaker(mcreator, ElementUtil::loadBlocksAndItems);
	private final JSpinner fuelPower = new JSpinner(new SpinnerNumberModel(1600, 0, Integer.MAX_VALUE, 1));

	private final CardLayout cl = new CardLayout();
	private final JPanel panel = new JPanel(cl);

	public ItemExtensionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		//Fuel
		JPanel fuelPanel = new JPanel(new BorderLayout(10, 10));
		fuelPanel.setOpaque(false);
		JPanel fuelSubPanel = new JPanel(new BorderLayout(15, 15));
		fuelSubPanel.setOpaque(false);
		JPanel powerPanel = new JPanel();
		powerPanel.setOpaque(false);

		powerPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/burn_time"),
				L10N.label("elementgui.item_extension.burn_time")));
		powerPanel.add(fuelPower);

		fuelSubPanel.add("North", powerPanel);
		fuelSubPanel.add("Center", fm);
		fm.setOpaque(false);
		fuelPanel.add("Center", PanelUtils.totalCenterInPanel(fuelSubPanel));

		JPanel northPanel = new JPanel(new GridLayout(1, 2, 10, 15));
		northPanel.setOpaque(false);

		northPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/extension"),
				L10N.label("elementgui.item_extension.extension")));
		northPanel.add(extension);
		extension.addActionListener(e -> updateExtensionUI());

		panel.add(fuelPanel, "Fuel");
		panel.setOpaque(false);

		addPage(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(northPanel, panel)));

		updateExtensionUI();
	}

	private void updateExtensionUI() {
		cl.show(panel, (String) extension.getSelectedItem());
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if ("Fuel".equals(extension.getSelectedItem())) {
			if (!fm.getCb1().containsItem()) {
				return new AggregatedValidationResult(fm.getCb1());
			}
		}

		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(ItemExtension itemExtension) {
		extension.setSelectedItem(itemExtension.extension);
		fuelPower.setValue(itemExtension.fuelPower);
		fm.getCb1().setBlock(itemExtension.fuelItem);
	}

	@Override public ItemExtension getElementFromGUI() {
		ItemExtension itemExtension = new ItemExtension(modElement);
		itemExtension.fuelPower = (int) fuelPower.getValue();
		itemExtension.fuelItem = fm.getBlock();
		return itemExtension;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-game-item-extension");
	}
}
