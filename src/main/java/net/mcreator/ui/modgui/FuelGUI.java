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

import net.mcreator.element.types.Fuel;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.FuelRecipeMaker;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class FuelGUI extends ModElementGUI<Fuel> {

	private FuelRecipeMaker fm;
	private final JSpinner power = new JSpinner(new SpinnerNumberModel(1600, 0, 1000000000, 1));

	public FuelGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		fm = new FuelRecipeMaker(mcreator, ElementUtil::loadBlocksAndItems);

		JPanel pane5 = new JPanel(new BorderLayout(10, 10));
		JPanel enderpanel = new JPanel(new BorderLayout(15, 15));

		JPanel ps = new JPanel();
		ps.setOpaque(false);
		pane5.setOpaque(false);

		ps.add(HelpUtils.wrapWithHelpButton(this.withEntry("fuel/burn_time"), new JLabel(L10N.t("elementgui.fuel.burn_time"))));
		ps.add(power);

		enderpanel.add("North", ps);
		enderpanel.add("Center", fm);

		fm.setOpaque(false);

		enderpanel.setOpaque(false);
		enderpanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2), L10N.t("elementgui.fuel.properties"),
				0, 0, getFont().deriveFont(12.0f), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		pane5.add("Center", PanelUtils.totalCenterInPanel(enderpanel));

		fm.getCb1().setValidator(new MCItemHolderValidator(fm.getCb1()));

		addPage(pane5);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(fm.getCb1());
	}

	@Override public void openInEditingMode(Fuel fuel) {
		power.setValue(fuel.power);
		fm.getCb1().setBlock(fuel.block);
	}

	@Override public Fuel getElementFromGUI() {
		Fuel fuel = new Fuel(modElement);
		fuel.power = (int) power.getValue();
		fuel.block = fm.getBlock();
		return fuel;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-fuel");
	}

}
