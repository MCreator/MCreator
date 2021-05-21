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

package net.mcreator.ui.minecraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.Procedure;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorStats;
import net.mcreator.io.net.analytics.AnalyticsConstants;
import net.mcreator.java.JavaConventions;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxFullWidthPopup;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.ModElementNameValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;

public class ProcedureSelector extends JPanel {

	private static final Gson gson = new GsonBuilder().setLenient().create();

	private final SearchableComboBox<CBoxEntry> procedures = new SearchableComboBox<>();

	private final Dependency[] providedDependencies;
	private final Map<String, List<Dependency>> depsMap = new HashMap<>();
	private final JLabel depslab = new JLabel();

	private final JButton edit = new JButton(UIRES.get("18px.edit"));
	private final JButton add = new JButton(UIRES.get("18px.add"));

	private CBoxEntry oldItem;

	private final MCreator mcreator;

	private final VariableElementType returnType;

	private String defaultName = "(no procedure)";

	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, Side.BOTH, providedDependencies);
	}

	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, true, null, providedDependencies);
	}

	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, side, allowInlineEditor, null, providedDependencies);
	}

	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName,
			@Nullable VariableElementType returnType, Dependency... providedDependencies) {
		this(helpContext, mcreator, eventName, Side.BOTH, true, returnType, providedDependencies);
	}

	public ProcedureSelector(@Nullable IHelpContext helpContext, MCreator mcreator, String eventName, Side side,
			boolean allowInlineEditor, @Nullable VariableElementType returnType, Dependency... providedDependencies) {
		super(new BorderLayout(0, 0));

		this.mcreator = mcreator;
		this.returnType = returnType;

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				e.consume();
			}
		});

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		if (returnType == VariableElementType.LOGIC) {
			defaultName = "(always)";
			setBorder(BorderFactory
					.createLineBorder(new Dependency("", VariableElementType.LOGIC.toDependencyType()).getColor()));
		} else if (returnType == VariableElementType.ITEMSTACK) {
			defaultName = "(empty itemstack)";
			setBorder(BorderFactory
					.createLineBorder(new Dependency("", VariableElementType.ITEMSTACK.toDependencyType()).getColor()));
		}

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
			updateDepsList();
		});

		this.providedDependencies = providedDependencies;

		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);

		JLabel eventNameLabel = new JLabel();
		if (side == Side.CLIENT) {
			eventNameLabel.setIcon(UIRES.get("16px.client"));
			eventNameLabel.setToolTipText(L10N.t("trigger.triggers_on_client_side_only"));
			if (helpContext == null)
				top.add("North", PanelUtils
						.westAndCenterElement(eventNameLabel, ComponentUtils.deriveFont(new JLabel(eventName), 14)));
			else
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel, HelpUtils
						.wrapWithHelpButton(helpContext, ComponentUtils.deriveFont(new JLabel(eventName), 14),
								SwingConstants.LEFT)));
		} else if (side == Side.SERVER) {
			eventNameLabel.setToolTipText(L10N.t("trigger.triggers_on_server_side_only"));
			eventNameLabel.setIcon(UIRES.get("16px.server"));
			if (helpContext == null)
				top.add("North", PanelUtils
						.westAndCenterElement(eventNameLabel, ComponentUtils.deriveFont(new JLabel(eventName), 14)));
			else
				top.add("North", PanelUtils.westAndCenterElement(eventNameLabel, HelpUtils
						.wrapWithHelpButton(helpContext, ComponentUtils.deriveFont(new JLabel(eventName), 14),
								SwingConstants.LEFT)));
		} else {
			if (helpContext == null)
				top.add("North", ComponentUtils.deriveFont(new JLabel(eventName), 14));
			else
				top.add("North", HelpUtils
						.wrapWithHelpButton(helpContext, ComponentUtils.deriveFont(new JLabel(eventName), 14),
								SwingConstants.LEFT));
		}

		top.add("South", depslab);

		JComponent procwrap;
		if (returnType == VariableElementType.LOGIC) {
			procwrap = PanelUtils.westAndCenterElement(ComponentUtils.deriveFont(new JLabel(" if:  "), 15), procedures);
		} else if (returnType == VariableElementType.ITEMSTACK) {
			procwrap = PanelUtils.westAndCenterElement(ComponentUtils.deriveFont(new JLabel(" item:  "), 15), procedures);
		} else if (returnType == null) {
			procwrap = PanelUtils.westAndCenterElement(ComponentUtils.deriveFont(new JLabel(" do:  "), 15), procedures);
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
					StringBuilder procedureName = new StringBuilder(
							((ModElementGUI<?>) mcreator.mcreatorTabs.getCurrentTab().getContent()).getModElement()
									.getName());
					String[] parts = eventName.replaceAll("\\(.*\\)", "").split(" ");
					for (String part : parts) {
						procedureName.append(StringUtils.uppercaseFirstLetter(part));
					}
					procedureNameString = JavaConventions
							.convertToValidClassName(procedureName.toString().replace("When", ""));
				}

				procedureNameString = VOptionPane
						.showInputDialog(mcreator, L10N.t("action.procedure.enter_procedure_name"),
								L10N.t("action.procedure.new_procedure_dialog_title"), null,
								new OptionPaneValidatior() {
									@Override public Validator.ValidationResult validate(JComponent component) {
										return new ModElementNameValidator(mcreator.getWorkspace(),
												(VTextField) component).validate();
									}
								}, L10N.t("action.procedure.create_procedure"),
								L10N.t("action.procedure.cancel_creation"), procedureNameString);

				if (procedureNameString != null) {
					ModElement element = new ModElement(mcreator.getWorkspace(), procedureNameString,
							ModElementType.PROCEDURE);
					ModElementGUI<?> newGUI = ModElementTypeRegistry.REGISTRY.get(ModElementType.PROCEDURE)
							.getModElement(mcreator, element, false);
					if (newGUI != null) {
						newGUI.showView();
						newGUI.setModElementCreatedListener(generatableElement -> {
							String modName = JavaConventions
									.convertToValidClassName(generatableElement.getModElement().getName());
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
					ModElementGUI<?> modeditor = ModElementTypeRegistry.REGISTRY
							.get(selectedProcedureAsModElement.getType())
							.getModElement(mcreator, selectedProcedureAsModElement, true);
					if (modeditor != null)
						modeditor.showView();
				}
			});

			JComponent component = PanelUtils.centerAndEastElement(procwrap, PanelUtils.westAndEastElement(add, edit));
			component.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
			add("South", component);
		} else {
			procwrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 4));
			add("South", procwrap);
		}

		add("North", PanelUtils.join(FlowLayout.LEFT, 4, 4, top));

		procedures.setToolTipText(L10N.t("action.procedure.match_dependencies"));

		procedures.setPrototypeDisplayValue(new CBoxEntry("XXXXXXXXX"));

		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE)
			setEnabled(false);
	}

	public ProcedureSelector setDefaultName(String defaultName) {
		this.defaultName = defaultName;
		return this;
	}

	@Override public void setEnabled(boolean enabled) {
		GeneratorConfiguration gc = mcreator.getGeneratorConfiguration();
		if (gc.getGeneratorStats().getModElementTypeCoverageInfo().get(ModElementType.PROCEDURE)
				== GeneratorStats.CoverageStatus.NONE)
			enabled = false;

		super.setEnabled(enabled);

		procedures.setEnabled(enabled);
		edit.setEnabled(enabled);
		add.setEnabled(enabled);
	}

	public void refreshList() {
		depsMap.clear();
		procedures.removeAllItems();

		procedures.addItem(new CBoxEntry(defaultName));

		for (ModElement mod : mcreator.getWorkspace().getModElements()) {
			if (mod.getType() == ModElementType.PROCEDURE) {
				List<?> dependenciesList = (List<?>) mod.getMetadata("dependencies");
				VariableElementType returnTypeCurrent = mod.getMetadata("return_type") != null ?
						VariableElementType.valueOf((String) mod.getMetadata("return_type")) :
						null;
				List<Dependency> realdepsList = new ArrayList<>();
				if (dependenciesList == null)
					continue;

				boolean missing = false;

				for (Object depobj : dependenciesList) {
					Dependency dependency = gson.fromJson(gson.toJsonTree(depobj).getAsJsonObject(), Dependency.class);
					realdepsList.add(dependency);
					if (!Arrays.asList(providedDependencies).contains(dependency))
						missing = true;
				}

				boolean correctReturnType = true;

				if (returnType != null)
					if (returnTypeCurrent != returnType)
						correctReturnType = false;

				if (!missing && correctReturnType) {
					depsMap.put(mod.getName(), realdepsList);
				}

				if (correctReturnType)
					procedures.addItem(new CBoxEntry(mod.getName(), !missing));
			}
		}
	}

	public void refreshListKeepSelected() {
		Procedure selected = getSelectedProcedure();
		refreshList();
		setSelectedProcedure(selected);
		updateDepsList();
	}

	private void updateDepsList() {
		CBoxEntry selected = procedures.getSelectedItem();

		List<Dependency> dependencies = null;
		if (selected != null) {
			dependencies = depsMap.get(selected.string);
		}

		StringBuilder deps = new StringBuilder(
				"<html><div style='font-size: 9px; margin-top: 2px; margin-bottom: 1px; color: white;'>");
		for (Dependency dependency : providedDependencies) {
			String bg = "999999";
			String optcss = "color: #444444;";
			if (dependencies != null && dependencies.contains(dependency)) {
				optcss = "color: #ffffff;";
				bg = Integer.toHexString(dependency.getColor().getRGB()).substring(2);
			}
			deps.append("<span style='background: #").append(bg).append("; ").append(optcss).append("'>&nbsp;")
					.append(dependency.getName()).append("&nbsp;</span>&#32;");
		}

		depslab.setText(deps.toString());
		edit.setEnabled(getSelectedProcedure() != null);

		if (selected == null || !selected.correctDependencies) {
			edit.setEnabled(false);
		}
	}

	public Procedure getSelectedProcedure() {
		CBoxEntry selected = procedures.getSelectedItem();
		if (selected == null || selected.string.equals(defaultName))
			return null;
		return new Procedure(selected.string);
	}

	public void setSelectedProcedure(String procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new CBoxEntry(procedure));
	}

	public void setSelectedProcedure(Procedure procedure) {
		if (procedure != null)
			procedures.setSelectedItem(new CBoxEntry(procedure.getName()));
	}

	static class ConditionalComboBoxRenderer implements ListCellRenderer<CBoxEntry> {

		private final BasicComboBoxRenderer renderer = new BasicComboBoxRenderer();

		@Override
		public Component getListCellRendererComponent(JList list, CBoxEntry value, int index, boolean isSelected,
				boolean cellHasFocus) {
			JLabel component = (JLabel) renderer
					.getListCellRendererComponent(list, value.string, index, isSelected, cellHasFocus);

			if (!value.correctDependencies) {
				component.setBackground(list.getBackground());
				component.setForeground(Color.gray.brighter());
				component.setText("<html>" + component.getText() + L10N.t("action.procedure.missing_dependencies"));
			}

			return component;
		}
	}

	private static class CBoxEntry {
		String string;
		boolean correctDependencies;

		CBoxEntry(String string) {
			this(string, true);
		}

		CBoxEntry(String string, boolean correctDependencies) {
			this.string = string;
			this.correctDependencies = correctDependencies;
		}

		@Override public boolean equals(Object o) {
			return o instanceof CBoxEntry && ((CBoxEntry) o).string.equals(this.string);
		}

		@Override public String toString() {
			return string;
		}

	}

	public enum Side {
		BOTH, CLIENT, SERVER
	}

}
