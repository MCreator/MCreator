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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.types.ItemExtension;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class ItemExtensionGUI extends ModElementGUI<ItemExtension> {
	private final MCItemHolder item = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	// Fuel
	private final JCheckBox enableFuel = L10N.checkbox("elementgui.common.enable");
	private final JSpinner fuelPower = new JSpinner(new SpinnerNumberModel(1600, 0, Integer.MAX_VALUE, 1));

	// Composter
	private final JCheckBox isCompostable = L10N.checkbox("elementgui.common.enable");
	private final JSpinner layerChance = new JSpinner(new SpinnerNumberModel(0.65f, 0.00f, 1.00f, 0.05f));

	// Dispenser behaviour
	private final JCheckBox hasDispenseBehavior = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector dispenseSuccessCondition;
	private ProcedureSelector dispenseResultItemstack;

	public ItemExtensionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		//Fuel
		JPanel fuelPanel = new JPanel(new BorderLayout());
		fuelPanel.setOpaque(false);

		JComponent enableFuelComp = PanelUtils.gridElements(1, 2, 0, 5,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/enable_fuel"),
						L10N.label("elementgui.item_extension.enable_fuel")), enableFuel);
		enableFuel.setOpaque(false);
		enableFuelComp.setOpaque(false);
		fuelPanel.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(enableFuelComp,
				PanelUtils.westAndCenterElement(HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/burn_time"),
						L10N.label("elementgui.item_extension.burn_time")), PanelUtils.centerInPanel(fuelPower)))));

		fuelPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.item_extension.fuel_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		// Composter
		JPanel composterPanel = new JPanel(new BorderLayout());
		composterPanel.setOpaque(false);

		JComponent isCompostableComp = PanelUtils.gridElements(1, 2, 5, 5,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/is_compostable"),
						L10N.label("elementgui.item_extension.is_compostable")), isCompostable);
		isCompostable.setOpaque(false);
		isCompostableComp.setOpaque(false);
		composterPanel.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(isCompostableComp,
				PanelUtils.westAndCenterElement(HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/layer_chance"),
						L10N.label("elementgui.item_extension.layer_chance")), layerChance))));

		composterPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.item_extension.composter_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		// Dispenser behaviour
		dispenseSuccessCondition = new ProcedureSelector(this.withEntry("item_extension/dispense_success_condition"),
				mcreator, L10N.t("elementgui.item_extension.dispense_success_condition"),
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString(
				"x:number/y:number/z:number/world:world/itemstack:itemstack/direction:direction")).makeInline();
		dispenseResultItemstack = new ProcedureSelector(this.withEntry("item_extension/dispense_result_itemstack"),
				mcreator, L10N.t("elementgui.item_extension.dispense_result_itemstack"),
				VariableTypeLoader.BuiltInTypes.ITEMSTACK, Dependency.fromString(
				"x:number/y:number/z:number/world:world/itemstack:itemstack/direction:direction/success:boolean")).setDefaultName(
						L10N.t("elementgui.item_extension.dispense_result_itemstack.default")).makeInline()
				.makeReturnValueOptional();

		JComponent canDispense = PanelUtils.gridElements(1, 2, 0, 5,
				HelpUtils.wrapWithHelpButton(this.withEntry("item/has_dispense_behavior"),
						L10N.label("elementgui.item_extension.has_dispense_behavior")), hasDispenseBehavior);
		JComponent dispenseProcedures = PanelUtils.gridElements(2, 1, 0, 2, dispenseSuccessCondition,
				dispenseResultItemstack);

		hasDispenseBehavior.setOpaque(false);
		hasDispenseBehavior.setSelected(false);
		hasDispenseBehavior.addActionListener(e -> updateDispenseElements());

		JComponent dispenserBehaviourPanel = PanelUtils.northAndCenterElement(canDispense,
				PanelUtils.centerInPanel(dispenseProcedures));

		dispenserBehaviourPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.item_extension.dispense_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		dispenserBehaviourPanel.setOpaque(false);

		addPage(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(PanelUtils.join(
						HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/item"),
								L10N.label("elementgui.item_extension.item")), PanelUtils.centerInPanel(item)),
				PanelUtils.northAndCenterElement(PanelUtils.northAndCenterElement(fuelPanel, composterPanel), dispenserBehaviourPanel))));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult.PASS();
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		dispenseSuccessCondition.refreshListKeepSelected();
		dispenseResultItemstack.refreshListKeepSelected();
	}

	private void updateDispenseElements() {
		dispenseSuccessCondition.setEnabled(hasDispenseBehavior.isSelected());
		dispenseResultItemstack.setEnabled(hasDispenseBehavior.isSelected());
	}

	@Override protected void openInEditingMode(ItemExtension itemExtension) {
		item.setBlock(itemExtension.item);
		fuelPower.setValue(itemExtension.fuelPower);
		enableFuel.setSelected(itemExtension.enableFuel);
		hasDispenseBehavior.setSelected(itemExtension.hasDispenseBehavior);
		dispenseSuccessCondition.setSelectedProcedure(itemExtension.dispenseSuccessCondition);
		dispenseResultItemstack.setSelectedProcedure(itemExtension.dispenseResultItemstack);
		isCompostable.setSelected(itemExtension.isCompostable);
		layerChance.setValue(itemExtension.layerChance);

		updateDispenseElements();
	}

	@Override public ItemExtension getElementFromGUI() {
		ItemExtension itemExtension = new ItemExtension(modElement);
		itemExtension.item = item.getBlock();
		itemExtension.enableFuel = enableFuel.isSelected();
		itemExtension.fuelPower = (int) fuelPower.getValue();
		itemExtension.hasDispenseBehavior = hasDispenseBehavior.isSelected();
		itemExtension.dispenseSuccessCondition = dispenseSuccessCondition.getSelectedProcedure();
		itemExtension.dispenseResultItemstack = dispenseResultItemstack.getSelectedProcedure();
		itemExtension.isCompostable = isCompostable.isSelected();
		itemExtension.layerChance = (double) layerChance.getValue();
		return itemExtension;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-game-item-extension");
	}
}
