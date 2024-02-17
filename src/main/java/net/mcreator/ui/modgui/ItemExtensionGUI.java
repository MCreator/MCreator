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
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.procedure.NumberProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.net.URI;
import java.net.URISyntaxException;

public class ItemExtensionGUI extends ModElementGUI<ItemExtension> {
	private final MCItemHolder item = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

	private final JCheckBox enableFuel = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector fuelSuccessCondition;
	private NumberProcedureSelector fuelPower;

	private final JSpinner compostLayerChance = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));

	private final JCheckBox hasDispenseBehavior = L10N.checkbox("elementgui.common.enable");
	private ProcedureSelector dispenseSuccessCondition;
	private ProcedureSelector dispenseResultItemstack;

	private final ValidationGroup pageGroup = new ValidationGroup();

	public ItemExtensionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		//Fuel
		enableFuel.setOpaque(false);
		enableFuel.addActionListener(e -> updateFuelElements());

		fuelPower = new NumberProcedureSelector(null, mcreator,
				new JSpinner(new SpinnerNumberModel(1600, 0, Integer.MAX_VALUE, 1)), 75,
				Dependency.fromString("itemstack:itemstack"));

		fuelSuccessCondition = new ProcedureSelector(this.withEntry("item_extension/fuel_success_condition"), mcreator,
				L10N.t("elementgui.item_extension.fuel_success_condition"), ProcedureSelector.Side.BOTH, true,
				VariableTypeLoader.BuiltInTypes.LOGIC, Dependency.fromString("itemstack:itemstack")).makeInline();

		JComponent fuelTopPanel = PanelUtils.gridElements(2, 2, 0, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/enable_fuel"),
						L10N.label("elementgui.item_extension.enable_fuel")), enableFuel,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/burn_time"),
						L10N.label("elementgui.item_extension.burn_time")), fuelPower);

		JComponent fuelPanel = PanelUtils.northAndCenterElement(fuelTopPanel, fuelSuccessCondition, 0, 2);
		fuelPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item_extension.fuel_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), Theme.current().getForegroundColor()));

		// Compostable
		JComponent compostPanel = PanelUtils.gridElements(1, 2, 0, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/layer_chance"),
						L10N.label("elementgui.item_extension.layer_chance")), compostLayerChance);
		compostPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item_extension.compost_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), Theme.current().getForegroundColor()));

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

		JComponent canDispense = PanelUtils.gridElements(1, 2, 0, 0,
				HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/has_dispense_behavior"),
						L10N.label("elementgui.item_extension.has_dispense_behavior")), hasDispenseBehavior);

		hasDispenseBehavior.setOpaque(false);
		hasDispenseBehavior.setSelected(false);
		hasDispenseBehavior.addActionListener(e -> updateDispenseElements());

		JComponent dispenserBehaviourPanel = PanelUtils.gridElements(3, 1, 0, 2, canDispense, dispenseSuccessCondition,
				dispenseResultItemstack);
		dispenserBehaviourPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.item_extension.dispense_properties"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont(), Theme.current().getForegroundColor()));

		JPanel itemPanel = PanelUtils.join(HelpUtils.wrapWithHelpButton(this.withEntry("item_extension/item"),
				L10N.label("elementgui.item_extension.item")), PanelUtils.centerInPanel(item));
		item.setValidator(new MCItemHolderValidator(item));
		pageGroup.addValidationElement(item);

		JPanel parameters = new JPanel();
		parameters.setOpaque(false);
		parameters.setLayout(new BoxLayout(parameters, BoxLayout.PAGE_AXIS));

		parameters.add(itemPanel);
		parameters.add(new JEmptyBox(20, 20));
		parameters.add(fuelPanel);
		parameters.add(new JEmptyBox(10, 10));
		parameters.add(compostPanel);
		parameters.add(new JEmptyBox(10, 10));
		parameters.add(dispenserBehaviourPanel);

		addPage(PanelUtils.totalCenterInPanel(parameters));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(pageGroup);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		fuelPower.refreshListKeepSelected();
		fuelSuccessCondition.refreshListKeepSelected();
		dispenseSuccessCondition.refreshListKeepSelected();
		dispenseResultItemstack.refreshListKeepSelected();
	}

	private void updateDispenseElements() {
		dispenseSuccessCondition.setEnabled(hasDispenseBehavior.isSelected());
		dispenseResultItemstack.setEnabled(hasDispenseBehavior.isSelected());
	}

	private void updateFuelElements() {
		fuelPower.setEnabled(enableFuel.isSelected());
		fuelSuccessCondition.setEnabled(enableFuel.isSelected());
	}

	@Override protected void openInEditingMode(ItemExtension itemExtension) {
		item.setBlock(itemExtension.item);
		enableFuel.setSelected(itemExtension.enableFuel);
		fuelPower.setSelectedProcedure(itemExtension.fuelPower);
		fuelSuccessCondition.setSelectedProcedure(itemExtension.fuelSuccessCondition);
		hasDispenseBehavior.setSelected(itemExtension.hasDispenseBehavior);
		dispenseSuccessCondition.setSelectedProcedure(itemExtension.dispenseSuccessCondition);
		dispenseResultItemstack.setSelectedProcedure(itemExtension.dispenseResultItemstack);
		compostLayerChance.setValue(itemExtension.compostLayerChance);

		updateFuelElements();
		updateDispenseElements();
	}

	@Override public ItemExtension getElementFromGUI() {
		ItemExtension itemExtension = new ItemExtension(modElement);
		itemExtension.item = item.getBlock();
		itemExtension.enableFuel = enableFuel.isSelected();
		itemExtension.fuelPower = fuelPower.getSelectedProcedure();
		itemExtension.fuelSuccessCondition = fuelSuccessCondition.getSelectedProcedure();
		itemExtension.hasDispenseBehavior = hasDispenseBehavior.isSelected();
		itemExtension.dispenseSuccessCondition = dispenseSuccessCondition.getSelectedProcedure();
		itemExtension.dispenseResultItemstack = dispenseResultItemstack.getSelectedProcedure();
		itemExtension.compostLayerChance = (double) compostLayerChance.getValue();
		return itemExtension;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-item-extension");
	}
}
