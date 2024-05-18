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

package net.mcreator.ui.procedure;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatLineBorder;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.element.parts.procedure.RetvalProcedure;
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
import net.mcreator.ui.workspace.breadcrumb.WorkspaceFolderBreadcrumb;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public abstract class RetvalProcedureSelector<E, T extends RetvalProcedure<E>> extends AbstractProcedureSelector {

	@Nullable private final JComponent fixedValue;

	public RetvalProcedureSelector(VariableType returnType, @Nullable IHelpContext helpContext, MCreator mcreator,
			String eventName, Side side, boolean allowInlineEditor, @Nullable JComponent fixedValue, int width,
			Dependency... providedDependencies) {
		super(mcreator, returnType, providedDependencies);

		this.fixedValue = fixedValue;
		this.defaultName = L10N.t("procedure.common.fixed");

		setLayout(new BorderLayout(4, 0));

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				e.consume();
			}
		});

		setOpaque(true);
		setBackground(UIManager.getColor("TextField.background"));

		procedures.setRenderer(new ConditionalComboBoxRenderer());
		procedures.addPopupMenuListener(new ComboBoxFullWidthPopup());
		procedures.addActionListener(e -> {
			ProcedureEntry selectedItem = procedures.getSelectedItem();
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

		JComponent procwrap = PanelUtils.westAndCenterElement(PanelUtils.totalCenterInPanel(procedures),
				Objects.requireNonNullElse(fixedValue, new JEmptyBox(1, 1)), 3, 3);

		Border outerBorder = new FlatLineBorder(new Insets(0, 0, 0, 0), UIManager.getColor("Component.borderColor"));

		if (allowInlineEditor) {
			setBorder(BorderFactory.createCompoundBorder(outerBorder, BorderFactory.createEmptyBorder(0, 2, 0, 4)));

			add.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
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

				WorkspaceFolderBreadcrumb.Small breadcrumb = new WorkspaceFolderBreadcrumb.Small(mcreator);

				procedureNameString = VOptionPane.showInputDialog(mcreator,
						L10N.t("action.procedure.enter_procedure_name"),
						L10N.t("action.procedure.new_procedure_dialog_title"), null, new OptionPaneValidatior() {
							@Override public ValidationResult validate(JComponent component) {
								return new ModElementNameValidator(mcreator.getWorkspace(), (VTextField) component,
										L10N.t("common.mod_element_name")).validate();
							}
						}, L10N.t("action.procedure.create_procedure"),
						UIManager.getString("OptionPane.cancelButtonText"), procedureNameString,
						breadcrumb.getInScrollPane(), null);

				if (procedureNameString != null) {
					ModElement element = new ModElement(mcreator.getWorkspace(), procedureNameString,
							ModElementType.PROCEDURE);
					ModElementGUI<?> newGUI = ModElementType.PROCEDURE.getModElementGUI(mcreator, element, false);
					if (newGUI != null) {
						newGUI.setTargetFolder(breadcrumb.getCurrentFolder());
						newGUI.showView();
						newGUI.setModElementCreatedListener(generatableElement -> {
							String modName = JavaConventions.convertToValidClassName(
									generatableElement.getModElement().getName());
							refreshList();
							setSelectedProcedure(modName);
						});
					}
				}
			});

			edit.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
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

			add("Center", PanelUtils.centerAndEastElement(procwrap,
					PanelUtils.totalCenterInPanel(PanelUtils.gridElements(1, 2, add, edit)), 2, 0));
		} else {
			setBorder(BorderFactory.createCompoundBorder(outerBorder, BorderFactory.createEmptyBorder(0, 2, 0, 1)));

			add("Center", procwrap);
		}

		add("West", PanelUtils.join(FlowLayout.LEFT, 4, 4, top));

		procedures.setToolTipText(L10N.t("action.procedure.match_dependencies"));

		procedures.setPrototypeDisplayValue(new ProcedureEntry("XXXXXXXXXX", null));

		if (fixedValue != null && width != 0)
			fixedValue.setPreferredSize(new Dimension(width, 0));
	}

	@Override ProcedureEntry updateDepsList(boolean smallIcons) {
		ProcedureEntry selected = super.updateDepsList(true);

		edit.setEnabled(selected != null && !selected.string.equals(defaultName));

		if (fixedValue != null)
			fixedValue.setEnabled(isEnabled() && !edit.isEnabled());

		return selected;
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		if (fixedValue != null)
			fixedValue.setEnabled(enabled);

		if (enabled) {
			setBackground(UIManager.getColor("TextField.background"));
		} else {
			setBackground(UIManager.getColor("TextField.disabledBackground"));
		}

		updateDepsList(true);
	}

	@Override public abstract T getSelectedProcedure();

	@Override public abstract void setSelectedProcedure(Procedure procedure);

	public abstract E getFixedValue();

	public abstract void setFixedValue(E value);

}
