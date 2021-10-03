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

package net.mcreator.ui.procedure;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.NumberProcedure;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class NumberProcedureSelector extends AbstractProcedureSelector {

	@Nullable private final JSpinner fixedValue;

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, @Nullable JSpinner fixedValue,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, L10N.t("elementgui.common.value"), Side.BOTH, fixedValue, providedDependencies);
	}

	public NumberProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			@Nullable JSpinner fixedValue, Dependency... providedDependencies) {
		super(mcreator, VariableTypeLoader.BuiltInTypes.NUMBER, providedDependencies);

		this.fixedValue = fixedValue;
		this.defaultName = L10N.t("procedure.common.fixed");

		setLayout(new BorderLayout(0, 0));

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				e.consume();
			}
		});

		setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));

		setOpaque(true);
		procedures.setBorder(BorderFactory.createLineBorder(returnType.getBlocklyColor()));
		setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		procedures.setRenderer(new ConditionalComboBoxRenderer());
		procedures.addPopupMenuListener(new ComboBoxFullWidthPopup());
		procedures.addActionListener(e -> {
			CBoxEntry selectedItem = procedures.getSelectedItem();
			if (selectedItem != null) {
				if (!selectedItem.correctDependencies) {
					procedures.setSelectedItem(oldItem);
				} else {
					oldItem = selectedItem;
				}
			}
			updateDepsList(true);
		});

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		JLabel nameLabel = new JLabel(eventName);
		ComponentUtils.deriveFont(nameLabel, 12);

		JLabel eventNameLabel = new JLabel();
		eventNameLabel.setFont(eventNameLabel.getFont().deriveFont(9f));
		if (side == Side.CLIENT) {
			eventNameLabel.setIcon(UIRES.get("16px.client"));
			eventNameLabel.setToolTipText(L10N.t("trigger.triggers_on_client_side_only"));
			if (helpContext == null)
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel, nameLabel));
			else
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel,
						HelpUtils.wrapWithHelpButton(helpContext, nameLabel, SwingConstants.LEFT)));
		} else if (side == Side.SERVER) {
			eventNameLabel.setToolTipText(L10N.t("trigger.triggers_on_server_side_only"));
			eventNameLabel.setIcon(UIRES.get("16px.server"));
			if (helpContext == null)
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel, nameLabel));
			else
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel,
						HelpUtils.wrapWithHelpButton(helpContext, nameLabel, SwingConstants.LEFT)));
		} else {
			if (helpContext == null)
				top.add("North", nameLabel);
			else
				top.add("North", HelpUtils.wrapWithHelpButton(helpContext, nameLabel, SwingConstants.LEFT));
		}

		top.add("South", depslab);

		add.setContentAreaFilled(false);
		add.setOpaque(false);
		add.setMargin(new Insets(0, 0, 0, 0));
		add.addActionListener(e -> {
			String procedureNameString = "";
			if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI) {
				StringBuilder procedureName = new StringBuilder(
						((ModElementGUI<?>) mcreator.mcreatorTabs.getCurrentTab().getContent()).getModElement()
								.getName());
				String[] parts = eventName.replaceAll("\\(.*\\)", "").split(" ");
				for (String part : parts) {
					procedureName.append(StringUtils.uppercaseFirstLetter(part));
				}
				procedureNameString = JavaConventions.convertToValidClassName(
						procedureName.toString().replace("When", ""));
			}

			procedureNameString = VOptionPane.showInputDialog(mcreator, L10N.t("action.procedure.enter_procedure_name"),
					L10N.t("action.procedure.new_procedure_dialog_title"), null, new OptionPaneValidatior() {
						@Override public ValidationResult validate(JComponent component) {
							return new ModElementNameValidator(mcreator.getWorkspace(),
									(VTextField) component).validate();
						}
					}, L10N.t("action.procedure.create_procedure"), UIManager.getString("OptionPane.cancelButtonText"),
					procedureNameString);

			if (procedureNameString != null) {
				ModElement element = new ModElement(mcreator.getWorkspace(), procedureNameString,
						ModElementType.PROCEDURE);
				ModElementGUI<?> newGUI = ModElementType.PROCEDURE.getModElementGUI(mcreator, element, false);
				if (newGUI != null) {
					newGUI.showView();
					newGUI.setModElementCreatedListener(generatableElement -> {
						String modName = JavaConventions.convertToValidClassName(
								generatableElement.getModElement().getName());
						refreshList();
						setSelectedProcedure(modName);
					});
					mcreator.getApplication().getAnalytics().async(() -> mcreator.getApplication().getAnalytics()
							.trackEvent(AnalyticsConstants.EVENT_NEW_MOD_ELEMENT,
									ModElementType.PROCEDURE.getReadableName(), null, null));
				}
			}
		});

		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setOpaque(false);
		edit.setContentAreaFilled(false);
		edit.addActionListener(e -> {
			if (getSelectedProcedure() != null) {
				ModElement selectedProcedureAsModElement = mcreator.getWorkspace()
						.getModElementByName(getSelectedProcedure().getName());
				ModElementGUI<?> modeditor = selectedProcedureAsModElement.getType()
						.getModElementGUI(mcreator, selectedProcedureAsModElement, true);
				if (modeditor != null)
					modeditor.showView();
			}
		});

		if (fixedValue != null)
			add("East", PanelUtils.totalCenterInPanel(
					PanelUtils.join(FlowLayout.LEFT, 0, 1, PanelUtils.westAndEastElement(procedures, fixedValue),
							new JEmptyBox(1, 1), PanelUtils.gridElements(1, 2, add, edit))));
		else
			add("East", PanelUtils.totalCenterInPanel(
					PanelUtils.join(FlowLayout.LEFT, 0, 1, procedures, new JEmptyBox(1, 1),
							PanelUtils.gridElements(1, 2, add, edit))));

		add("West", PanelUtils.join(FlowLayout.LEFT, 4, 4, top));

		procedures.setToolTipText(L10N.t("action.procedure.match_dependencies"));

		procedures.setPrototypeDisplayValue(new CBoxEntry("XXXXXXXXXX", null));

		if (fixedValue != null)
			fixedValue.setPreferredSize(new Dimension(50, 0));

		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE)
			setEnabled(false);
	}

	@Override protected CBoxEntry updateDepsList(boolean smallIcons) {
		CBoxEntry selected = super.updateDepsList(true);

		edit.setEnabled(selected != null && !selected.string.equals(defaultName));

		if (fixedValue != null)
			fixedValue.setEnabled(!edit.isEnabled());

		return selected;
	}

	@Override public void setEnabled(boolean enabled) {
		if (fixedValue != null)
			fixedValue.setEnabled(enabled);

		if (enabled) {
			setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		} else {
			setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		}

		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE)
			enabled = false;

		procedures.setEnabled(enabled);
		edit.setEnabled(enabled);
		add.setEnabled(enabled);

		updateDepsList(true);
	}

	@Override public NumberProcedure getSelectedProcedure() {
		Double value = (double) 0;

		if (fixedValue != null) {
			Object rawValue = fixedValue.getValue();
			if (rawValue instanceof Double)
				value = (Double) rawValue;
			else if (rawValue instanceof Float)
				value = Double.valueOf((Float) rawValue);
			else if (rawValue instanceof Integer)
				value = Double.valueOf((Integer) rawValue);
			else if (rawValue instanceof Short)
				value = Double.valueOf((Short) rawValue);
			else if (rawValue instanceof Byte)
				value = Double.valueOf((Byte) rawValue);
		}

		CBoxEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return new NumberProcedure(null, value);
		return new NumberProcedure(selected.string, value);
	}

	public void setSelectedProcedure(NumberProcedure procedure) {
		if (procedure != null) {
			if (procedure.getName() != null)
				procedures.setSelectedItem(new CBoxEntry(procedure.getName(), null));

			if (fixedValue != null)
				fixedValue.setValue(procedure.getFixedValue());
		}
	}

}
