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

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.*;
import net.mcreator.blockly.java.BlocklyToProcedure;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementTypeRegistry;
import net.mcreator.element.parts.Procedure;
import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.types.GUI;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.Transliteration;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.blockly.ProcedureEditorToolbar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.NewVariableDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ProcedureGUI extends ModElementGUI<net.mcreator.element.types.Procedure> {

	private final JPanel pane5 = new JPanel(new BorderLayout(0, 0));

	private BlocklyPanel blocklyPanel;

	public final DefaultListModel<VariableElement> localVars = new DefaultListModel<>();
	private final JList<VariableElement> localVarsList = new JList<>(localVars);

	private boolean hasErrors = false;
	private boolean hasDependencyErrors = false;

	private List<Dependency> dependenciesArrayList = new ArrayList<>();
	private List<Dependency> dependenciesBeforeEdit = null;

	private final JLabel depsWarningLabel = new JLabel();

	private final DefaultListModel<Dependency> dependencies = new DefaultListModel<>();
	private final DefaultListModel<Dependency> dependenciesExtTrigger = new DefaultListModel<>();

	private final JLabel extDepsLab = new JLabel();

	private Map<String, ToolboxBlock> externalBlocks;

	private ExternalTrigger trigger = null;

	private final JPanel triggerDepsPan = new JPanel(new BorderLayout());
	private final JPanel returnType = new JPanel(new BorderLayout());
	private final JLabel returnTypeLabel = new JLabel();

	private final JPanel triggerInfoPanel = new JPanel(new BorderLayout());
	private final JLabel cancelableTriggerLabel = new JLabel();
	private final JLabel hasResultTriggerLabel = new JLabel();

	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();

	public ProcedureGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI(false);
	}

	private void regenerateProcedure() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getGeneratorProcedures());
		BlocklyToProcedure blocklyToJava;

		try {
			blocklyToJava = new BlocklyToProcedure(mcreator.getWorkspace(), blocklyPanel.getXML(), null,
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		dependenciesArrayList = blocklyToJava.getDependencies();
		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			dependencies.clear();
			dependenciesExtTrigger.clear();
			depsWarningLabel.setText("");

			cancelableTriggerLabel.setText("");
			hasResultTriggerLabel.setText("");

			cancelableTriggerLabel.setIcon(null);
			hasResultTriggerLabel.setIcon(null);

			if (isEditingMode() && dependenciesBeforeEdit == null) {
				dependenciesBeforeEdit = new ArrayList<>(dependenciesArrayList);
			} else if (dependenciesBeforeEdit != null) {
				boolean hasNewDependenciesAdded = false;
				// we go through new dependency list and check if old one contains all of them
				for (Dependency dependency : dependenciesArrayList) {
					if (!dependenciesBeforeEdit.contains(dependency)) {
						hasNewDependenciesAdded = true;
						break;
					}
				}
				if (hasNewDependenciesAdded) {
					depsWarningLabel.setText(L10N.t("elementgui.procedure.dependencies_added"));
				}
			}

			if (blocklyToJava.getReturnType() != null) {
				returnType.setVisible(true);
				returnTypeLabel.setText(blocklyToJava.getReturnType().getName().toUpperCase());
				returnTypeLabel.setForeground(blocklyToJava.getReturnType().getColor().brighter());
			} else {
				returnType.setVisible(false);
			}

			hasDependencyErrors = false;
			if (blocklyToJava.getExternalTrigger() != null) {
				List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader()
						.getExternalTrigers();

				for (ExternalTrigger externalTrigger : externalTriggers) {
					if (externalTrigger.getID().equals(blocklyToJava.getExternalTrigger())) {
						trigger = externalTrigger;
						break;
					}
				}

				if (trigger != null) {
					triggerDepsPan.setVisible(true);

					// if we find a trigger, we go through list of procedure dependencies and
					// make sure that all of them are contained in trigger's dependency list
					StringBuilder missingdeps = new StringBuilder();
					boolean warn = false;
					for (Dependency dependency : dependenciesArrayList) {
						if (trigger.dependencies_provided != null && !trigger.dependencies_provided
								.contains(dependency)) {
							warn = true;
							missingdeps.append(" ").append(dependency.getName());
						}
					}
					if (warn) {
						depsWarningLabel.setText(L10N.t("elementgui.procedure.dependencies_not_provided", missingdeps));
						hasDependencyErrors = true;
					}
					extDepsLab.setText("<html><font style='font-size: 10px;'>" + trigger.getName());
					List<Dependency> tdeps = trigger.dependencies_provided;
					if (tdeps != null) {
						Collections.sort(tdeps);
						tdeps.forEach(dependenciesExtTrigger::addElement);
					}

					if (trigger.cancelable) {
						cancelableTriggerLabel.setText(L10N.t("elementgui.procedure.cancelable_trigger"));
						cancelableTriggerLabel.setIcon(UIRES.get("info"));
					}
					if (trigger.has_result) {
						hasResultTriggerLabel.setText(L10N.t("elementgui.procedure.can_specify_result_trigger"));
						hasResultTriggerLabel.setIcon(UIRES.get("info"));
					}

					if (!mcreator.getGeneratorStats().getGeneratorTriggers().contains(trigger.getID())) {
						compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
								L10N.t("elementgui.procedure.global_trigger_unsupported")));
					}

					if (trigger.required_apis != null) {
						for (String required_api : trigger.required_apis) {
							if (!mcreator.getWorkspaceSettings().getMCreatorDependencies().contains(required_api)) {
								compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
										L10N.t("elementgui.procedure.global_trigger_not_activated", required_api)));
							}
						}
					}

					// Check if trigger is tick based
					if (trigger.getID().endsWith("_ticks")) {
						compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.INFO,
								L10N.t("elementgui.procedure.global_trigger_tick_based", trigger.getName())));
					}
				} else {
					compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("elementgui.procedure.global_trigger_does_not_exist")));
					triggerDepsPan.setVisible(false);
				}
			} else {
				triggerDepsPan.setVisible(false);
			}

			dependenciesArrayList.forEach(dependencies::addElement);

			hasErrors = false;
			for (BlocklyCompileNote note : compileNotesArrayList) {
				if (note.getType() == BlocklyCompileNote.Type.ERROR) {
					hasErrors = true;
					break;
				}
			}

			compileNotesPanel.updateCompileNotes(compileNotesArrayList);

		});
	}

	static class DependenciesListRenderer extends JLabel implements ListCellRenderer<Dependency> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Dependency> list, Dependency value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBorder(null);
			Color col = value.getColor();
			setBackground(isSelected ? col : (Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setForeground(isSelected ? (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") : col.brighter());
			ComponentUtils.deriveFont(this, 14);
			setText(value.getName());
			setToolTipText(value.getName() + ", type: " + value.getRawType());
			return this;
		}
	}

	static class LocalVariableListRenderer extends JLabel implements ListCellRenderer<VariableElement> {
		@Override
		public Component getListCellRendererComponent(JList<? extends VariableElement> list, VariableElement value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBorder(null);
			setBackground(isSelected ? value.getType().getColor() : (Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setForeground(isSelected ? (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR") : value.getType().getColor());
			ComponentUtils.deriveFont(this, 14);
			setText(value.getName());
			return this;
		}
	}

	@Override protected void initGUI() {
		pane5.setOpaque(false);

		localVarsList.setOpaque(false);
		localVarsList.setCellRenderer(new LocalVariableListRenderer());
		localVarsList.setBorder(BorderFactory.createEmptyBorder());
		localVarsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JList<Dependency> dependenciesList = new JList<>(dependencies);
		dependenciesList.setOpaque(false);
		dependenciesList.setCellRenderer(new DependenciesListRenderer());
		dependenciesList.setBorder(BorderFactory.createEmptyBorder());
		dependenciesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JList<Dependency> dependenciesExtTrigList = new JList<>(dependenciesExtTrigger);
		dependenciesExtTrigList.setOpaque(false);
		dependenciesExtTrigList.setCellRenderer(new DependenciesListRenderer());
		dependenciesExtTrigList.setBorder(BorderFactory.createEmptyBorder());
		dependenciesExtTrigList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		returnType.setVisible(false);
		returnType.setOpaque(false);

		returnType.add("Center", returnTypeLabel);

		returnTypeLabel.setOpaque(true);
		returnTypeLabel.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		returnTypeLabel.setBorder(BorderFactory.createEmptyBorder(0, 7, 9, 0));
		ComponentUtils.deriveFont(returnType, 13);

		JToolBar bar4 = new JToolBar();
		bar4.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 0));
		bar4.setFloatable(false);
		bar4.setOpaque(false);
		bar4.add(ComponentUtils.deriveFont(L10N.label("elementgui.procedure.return_type"), 13));

		JPanel rettypeHeader = new JPanel(new GridLayout());
		rettypeHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		rettypeHeader.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		rettypeHeader.add(bar4);
		returnType.add("North", rettypeHeader);
		returnType.setOpaque(false);
		returnType.setPreferredSize(new Dimension(150, 46));

		returnType.setBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		triggerInfoPanel.setOpaque(false);
		triggerInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
		triggerInfoPanel.add("North", cancelableTriggerLabel);
		triggerInfoPanel.add("Center", hasResultTriggerLabel);

		JPanel localVarsPan = new JPanel(new BorderLayout());
		localVarsPan.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(localVarsList);
		scrollPane.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(11);
		scrollPane.getVerticalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPane.getVerticalScrollBar()));
		scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPane.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPane.getHorizontalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPane.getHorizontalScrollBar()));
		scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		localVarsPan.add("Center", scrollPane);

		JToolBar bar = new JToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 0));
		bar.setFloatable(false);
		bar.setOpaque(false);

		bar.add(ComponentUtils.deriveFont(L10N.label("elementgui.procedure.local_variables"), 13));

		JButton addvar = new JButton(UIRES.get("16px.add.gif"));
		addvar.setContentAreaFilled(false);
		addvar.setOpaque(false);
		ComponentUtils.deriveFont(addvar, 11);
		addvar.setBorder(BorderFactory.createEmptyBorder(1, 6, 0, 2));
		bar.add(addvar);

		JButton remvar = new JButton(UIRES.get("16px.delete.gif"));
		remvar.setContentAreaFilled(false);
		remvar.setOpaque(false);
		ComponentUtils.deriveFont(remvar, 11);
		remvar.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
		bar.add(remvar);

		addvar.addActionListener(e -> {
			VariableElement element = NewVariableDialog
					.showNewVariableDialog(mcreator, false, new OptionPaneValidatior() {
								@Override public Validator.ValidationResult validate(JComponent component) {
									Validator validator = new JavaMemeberNameValidator((VTextField) component, false);
									String textname = Transliteration.transliterateString(((VTextField) component).getText());
									for (int i = 0; i < localVars.getSize(); i++) {
										String nameinrow = localVars.get(i).getName();
										if (textname.equals(nameinrow))
											return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
													L10N.t("elementgui.procedure.name_already_exists"));
									}
									for (Dependency dependency : dependenciesArrayList) {
										String nameinrow = dependency.getName();
										if (textname.equals(nameinrow))
											return new ValidationResult(ValidationResultType.ERROR,
													L10N.t("elementgui.procedure.name_already_exists_dep"));
									}
									return validator.validate();
								}
							}, VariableElement.getAllTypes().toArray(new String[0]));
			if (element != null) {
				blocklyPanel.addLocalVariable(element.getName(),
						element.getType().getBlocklyVariableType());
				localVars.addElement(element);
			}
		});

		remvar.addActionListener(e -> {
			VariableElement element = localVarsList.getSelectedValue();
			if (element != null) {
				int n = JOptionPane.showConfirmDialog(mcreator, L10N.t("elementgui.procedure.confirm_delete_var_msg"),
						L10N.t("elementgui.procedure.confirm_delete_var_title"), JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				if (n == JOptionPane.YES_OPTION) {
					blocklyPanel.removeLocalVariable(element.getName());
					localVars.removeElement(element);
				}
			}
		});

		JPanel varHeader = new JPanel(new GridLayout());
		varHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		varHeader.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		varHeader.add(bar);
		localVarsPan.add("North", varHeader);
		localVarsPan.setOpaque(false);
		localVarsPan.setPreferredSize(new Dimension(150, 0));

		JPanel depsPan = new JPanel(new BorderLayout());
		depsPan.setOpaque(false);

		JToolBar bar2 = new JToolBar();
		bar2.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 0));
		bar2.setFloatable(false);
		bar2.setOpaque(false);
		bar2.add(ComponentUtils.deriveFont(L10N.label("elementgui.procedure.required_dependencies"), 13));

		JPanel depsHeader = new JPanel(new BorderLayout());
		depsHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		depsHeader.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		depsHeader.add("North", bar2);
		depsHeader.add("South", ComponentUtils.deriveFont(depsWarningLabel, 11));

		depsPan.add("North", depsHeader);
		depsPan.setOpaque(false);
		depsPan.setBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		JScrollPane scrollPaneDeps = new JScrollPane(dependenciesList);
		scrollPaneDeps.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		scrollPaneDeps.getViewport().setOpaque(false);
		scrollPaneDeps.getVerticalScrollBar().setUnitIncrement(11);
		scrollPaneDeps.getVerticalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPaneDeps.getVerticalScrollBar()));
		scrollPaneDeps.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPaneDeps.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPaneDeps.getHorizontalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPaneDeps.getHorizontalScrollBar()));
		scrollPaneDeps.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
		scrollPaneDeps.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		depsPan.add("Center", scrollPaneDeps);
		depsPan.setPreferredSize(new Dimension(150, 0));

		JToolBar bar3 = new JToolBar();
		bar3.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 0));
		bar3.setFloatable(false);
		bar3.setOpaque(false);
		bar3.add(ComponentUtils.deriveFont(extDepsLab, 13));

		JPanel extdepsHeader = new JPanel(new BorderLayout());
		extdepsHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		extdepsHeader.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		extdepsHeader.add("North", bar3);

		triggerDepsPan.add("North", extdepsHeader);
		triggerDepsPan.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		triggerDepsPan.setBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));

		JScrollPane scrollPaneExtDeps = new JScrollPane(dependenciesExtTrigList);
		scrollPaneExtDeps.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		scrollPaneExtDeps.getViewport().setOpaque(false);
		scrollPaneExtDeps.getVerticalScrollBar().setUnitIncrement(11);
		scrollPaneExtDeps.getVerticalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPaneExtDeps.getVerticalScrollBar()));
		scrollPaneExtDeps.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		scrollPaneExtDeps.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPaneExtDeps.getHorizontalScrollBar()
				.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), scrollPaneExtDeps.getHorizontalScrollBar()));
		scrollPaneExtDeps.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
		scrollPaneExtDeps.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		triggerDepsPan.add("Center", PanelUtils.northAndCenterElement(triggerInfoPanel, PanelUtils
				.northAndCenterElement(
						ComponentUtils.deriveFont(L10N.label("elementgui.procedure.provided_dependencies"), 13),
						scrollPaneExtDeps, 0, 1), 0, 4));
		triggerDepsPan.setPreferredSize(new Dimension(150, 0));
		triggerDepsPan.setVisible(false);

		JPanel eastPan = new JPanel();
		eastPan.setLayout(new BoxLayout(eastPan, BoxLayout.PAGE_AXIS));
		eastPan.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		eastPan.setBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		eastPan.add(localVarsPan);
		eastPan.add(depsPan);
		eastPan.add(triggerDepsPan);

		pane5.add("East", PanelUtils.centerAndSouthElement(eastPan, returnType));
		pane5.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		externalBlocks = BlocklyLoader.INSTANCE.getProcedureBlockLoader().getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getProcedureBlockLoader()
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ExternalBlockLoader.ToolboxType.PROCEDURE);

			BlocklyLoader.INSTANCE.getExternalTriggerLoader().getExternalTrigers()
					.forEach(blocklyPanel.getJSBridge()::addExternalTrigger);
			for (VariableElement variable : mcreator.getWorkspace().getVariableElements()) {
				blocklyPanel.addGlobalVariable(variable.getName(),
						variable.getType().getBlocklyVariableType());
			}
			blocklyPanel.getJSBridge().setJavaScriptEventListener(() -> new Thread(this::regenerateProcedure).start());
			if (!isEditingMode()) {
				blocklyPanel
						.setXML("<xml><block type=\"event_trigger\" deletable=\"false\" x=\"40\" y=\"40\"></block></xml>");
			}
		});

		pane5.add("Center", blocklyPanel);

		pane5.add("South", compileNotesPanel);

		compileNotesPanel.setPreferredSize(new Dimension(0, 70));

		pane5.add("North", new ProcedureEditorToolbar(mcreator, blocklyPanel, this));

		addPage(PanelUtils.gridElements(1, 1, pane5));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (!hasErrors && !hasDependencyErrors)
			return new AggregatedValidationResult.PASS();
		else if (hasErrors)
			return new AggregatedValidationResult.MULTIFAIL(
					compileNotesPanel.getCompileNotes().stream().map(BlocklyCompileNote::getMessage)
							.collect(Collectors.toList()));
		else
			return new AggregatedValidationResult.FAIL(
					L10N.t("elementgui.procedure.external_trigger_does_not_provide_all_dependencies"));

	}

	@Override protected void afterGeneratableElementStored() {
		super.afterGeneratableElementStored();

		// check if dependency list has changed
		boolean dependenciesChanged = dependenciesBeforeEdit != null && !new HashSet<>(dependenciesBeforeEdit)
				.equals(new HashSet<>(dependenciesArrayList));

		// this procedure could be in use and new dependencies were added
		if (isEditingMode() && dependenciesChanged) {
			for (ModElement element : mcreator.getWorkspace().getModElements()) {
				// if this mod element is not locked and has procedures, we try to update dependencies
				// in this case, we (re)generate mod element code so dependencies get updated in the trigger code
				if (!element.isCodeLocked()) {
					GeneratableElement generatableElement = element.getGeneratableElement();
					if (generatableElement instanceof GUI) {
						boolean procedureUsedByGUI = false;
						for (GUIComponent component : ((GUI) generatableElement).components) {
							if (Procedure.isElementUsingProcedure(component, modElement.getName())) {
								procedureUsedByGUI = true;
								break;
							}
						}
						if (procedureUsedByGUI)
							mcreator.getGenerator().generateElement(generatableElement);
					} else if (generatableElement != null && ModElementTypeRegistry.REGISTRY.get(element.getType())
							.hasProcedureTriggers()) {
						if (Procedure.isElementUsingProcedure(generatableElement, modElement.getName())) {
							mcreator.getGenerator().generateElement(generatableElement);
						}
					}
				}
			}
		}
	}

	@Override public void openInEditingMode(net.mcreator.element.types.Procedure procedure) {
		blocklyPanel.setXMLDataOnly(procedure.procedurexml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(procedure.procedurexml);
			localVars.removeAllElements();
			blocklyPanel.getLocalVariablesList().forEach(localVars::addElement);
			regenerateProcedure();
		});
	}

	@Override public net.mcreator.element.types.Procedure getElementFromGUI() {
		net.mcreator.element.types.Procedure procedure = new net.mcreator.element.types.Procedure(modElement);
		procedure.procedurexml = blocklyPanel.getXML();
		return procedure;
	}

}