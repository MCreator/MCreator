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
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ProcedureSelector extends AbstractProcedureSelector {

	private boolean inline = false;

	private final JLabel nameLabel;
	private final JLabel actionLabel;
	private final JComponent componentA;
	private final JComponent componentB;

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, Side.BOTH, providedDependencies);
	}

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param side                 Side of the game on which the event may occur (CLIENT, SERVER or BOTH).
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, null, providedDependencies);
	}

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param side                 Side of the game on which the event may occur (CLIENT, SERVER or BOTH).
	 * @param allowInlineEditor    Whether layout of this selector can adapt to low height values.
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, allowInlineEditor, null, providedDependencies);
	}

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param returnType           The type of value the selected procedure should return.
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			@Nullable VariableType returnType, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, Side.BOTH, true, returnType, providedDependencies);
	}

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param side                 Side of the game on which the event may occur (CLIENT, SERVER or BOTH).
	 * @param allowInlineEditor    Whether layout of this selector can adapt to low height values.
	 * @param returnType           The type of value the selected procedure should return.
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable VariableType returnType, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, eventName, side, allowInlineEditor, returnType, providedDependencies);
	}

	/**
	 * @param helpContext          Help tip explaining how is the selected procedure used.
	 * @param mcreator             Workspace window inside which this selector is to be created.
	 * @param eventName            Name of the event calling the selected procedure.
	 * @param procedureName        Suggested name of procedure mod element this selector can create
	 * @param side                 Side of the game on which the event may occur (CLIENT, SERVER or BOTH).
	 * @param allowInlineEditor    Whether layout of this selector can adapt to low height values.
	 * @param returnType           The type of value the selected procedure should return.
	 * @param providedDependencies List of dependencies the selected procedure is provided upon its call.
	 */
	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			String procedureName, Side side, boolean allowInlineEditor, @Nullable VariableType returnType,
			Dependency... providedDependencies) {
		super(mcreator, returnType, providedDependencies);

		setLayout(new BorderLayout(0, 0));

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				e.consume();
			}
		});

		setBackground(Theme.current().getBackgroundColor());
		setBorder(BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor()));

		if (returnType != null) {
			setBorder(BorderFactory.createLineBorder(returnType.getBlocklyColor()));

			if (returnType == VariableTypeLoader.BuiltInTypes.LOGIC)
				defaultName = L10N.t("condition.common.true");
		}

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
			updateDepsList(false);
		});

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		nameLabel = new JLabel(eventName);
		ComponentUtils.deriveFont(nameLabel, 14);

		JLabel eventNameLabel = new JLabel();
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

		actionLabel = L10N.label("procedure.common.if");
		ComponentUtils.deriveFont(actionLabel, 14);

		JComponent procwrap;
		if (returnType == VariableTypeLoader.BuiltInTypes.LOGIC) {
			procwrap = PanelUtils.westAndCenterElement(actionLabel, procedures);
		} else if (returnType == null || returnType == VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE) {
			actionLabel.setText(L10N.t("procedure.common.do"));
			procwrap = PanelUtils.westAndCenterElement(actionLabel, procedures);
		} else {
			procwrap = procedures;
		}

		if (allowInlineEditor) {
			add.setContentAreaFilled(false);
			add.setOpaque(false);
			add.setMargin(new Insets(0, 0, 0, 0));
			add.addActionListener(e -> {
				String procedureNameString = "";
				if (mcreator.mcreatorTabs.getCurrentTab().getContent() instanceof ModElementGUI) {
					StringBuilder procedureNameBuilder = new StringBuilder(
							((ModElementGUI<?>) mcreator.mcreatorTabs.getCurrentTab().getContent()).getModElement()
									.getName());
					String[] parts = procedureName.replaceAll("\\(.*\\)", "").split(" ");
					for (String part : parts) {
						procedureNameBuilder.append(StringUtils.uppercaseFirstLetter(part));
					}
					procedureNameString = JavaConventions.convertToValidClassName(
							procedureNameBuilder.toString().replace("When", ""));
				}

				procedureNameString = VOptionPane.showInputDialog(mcreator,
						L10N.t("action.procedure.enter_procedure_name"),
						L10N.t("action.procedure.new_procedure_dialog_title"), null, new OptionPaneValidatior() {
							@Override public ValidationResult validate(JComponent component) {
								return new ModElementNameValidator(mcreator.getWorkspace(), (VTextField) component,
										L10N.t("common.mod_element_name")).validate();
							}
						}, L10N.t("action.procedure.create_procedure"),
						UIManager.getString("OptionPane.cancelButtonText"), procedureNameString);

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

			componentB = PanelUtils.centerAndEastElement(procwrap, PanelUtils.westAndEastElement(add, edit), 3, 0);
			componentB.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
		} else {
			procwrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
			componentB = procwrap;
		}

		componentA = PanelUtils.join(FlowLayout.LEFT, 4, 4, top);

		add("North", componentA);
		add("South", componentB);

		procedures.setToolTipText(L10N.t("action.procedure.match_dependencies"));

		procedures.setPrototypeDisplayValue(new ProcedureEntry("XXXXXXXXX", null));
	}

	public ProcedureSelector setDefaultName(String defaultName) {
		this.defaultName = defaultName;
		return this;
	}

	public ProcedureSelector makeInline() {
		inline = true;

		removeAll();
		setLayout(new GridLayout(1, 2));
		add(componentA);
		add(componentB);

		componentB.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 2));

		updateDepsList(true);
		ComponentUtils.deriveFont(nameLabel, 12);
		actionLabel.setVisible(false);

		return this;
	}

	@Override public ProcedureSelector makeReturnValueOptional() {
		AbstractProcedureSelector retval = super.makeReturnValueOptional();

		if (returnType != null)
			setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(1, 0, 1, 1, Theme.current().getAltBackgroundColor()),
					BorderFactory.createMatteBorder(0, 5, 0, 0, returnType.getBlocklyColor())));

		return (ProcedureSelector) retval;
	}

	@Override protected ProcedureEntry updateDepsList(boolean smallIcons) {
		return super.updateDepsList(inline);
	}
}
