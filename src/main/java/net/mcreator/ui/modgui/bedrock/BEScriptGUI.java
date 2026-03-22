/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.modgui.bedrock;

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.data.*;
import net.mcreator.blockly.javascript.BlocklyToJavaScript;
import net.mcreator.element.ModElementType;
import net.mcreator.element.types.bedrock.BEBlock;
import net.mcreator.element.types.bedrock.BEItem;
import net.mcreator.element.types.bedrock.BEScript;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.NewVariableDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.IBlocklyPanelHolder;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.search.ISearchable;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidator;
import net.mcreator.ui.validation.validators.JavaMemberNameValidator;
import net.mcreator.util.TestUtil;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import net.mcreator.workspace.elements.VariableTypeLoader;
import net.mcreator.workspace.references.ReferencesFinder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

public class BEScriptGUI extends ModElementGUI<BEScript> implements IBlocklyPanelHolder, ISearchable,
		IBlocklyPanelHolder.IBlocklyPanelVariablesHolder {

	private static final Logger LOG = LogManager.getLogger(BEScriptGUI.class);

	private final JPanel pane5 = new JPanel(new BorderLayout(0, 0));

	private BlocklyEditorToolbar blocklyEditorToolbar;

	private BlocklyPanel blocklyPanel;

	private final DefaultListModel<VariableElement> localVars = new DefaultListModel<>();
	private final JList<VariableElement> localVarsList = new JList<>(localVars);

	private boolean hasDependencyErrors = false;

	private List<Dependency> dependenciesArrayList = new ArrayList<>();

	private final JLabel depsWarningLabel = new JLabel();

	private final DefaultListModel<Dependency> dependencies = new DefaultListModel<>();
	private final DefaultListModel<Dependency> dependenciesExtTrigger = new DefaultListModel<>();

	private final JLabel extDepsLab = new JLabel();

	private Map<String, ToolboxBlock> externalBlocks;

	private ExternalTrigger trigger = null;

	private final JPanel triggerDepsPan = new JPanel(new BorderLayout());

	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();

	private final List<BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	private String triggerTypeBeforeEdit = null;
	private String triggerType = null;

	public BEScriptGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override public void addBlocklyChangedListener(BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override public synchronized List<BlocklyCompileNote> regenerateBlockAssemblies(boolean jsEventTriggeredChange) {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.SCRIPT));
		BlocklyToJavaScript blocklyToJavaScript;

		try {
			blocklyToJavaScript = new BlocklyToJavaScript(mcreator.getWorkspace(), this.modElement,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
					new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			TestUtil.failIfTestingEnvironment();
			return List.of(); // should not be possible to happen here
		}

		dependenciesArrayList = blocklyToJavaScript.getDependencies();

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJavaScript.getCompileNotes();

		// Check that no local variable has the same name as one of the dependencies
		for (var dependency : dependenciesArrayList) {
			for (int i = 0; i < localVars.getSize(); i++) {
				if (dependency.name().equals(localVars.get(i).getName())) {
					compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("elementgui.procedure.variable_name_clashes_with_dep", dependency.name())));
					break; // We found a match, there's no need to check the other variables
				}
			}
		}

		// Handle compile notes related to external trigger if present
		if (blocklyToJavaScript.getExternalTrigger() != null) {
			List<ExternalTrigger> externalTriggers = BlocklyLoader.INSTANCE.getExternalTriggerLoader(
					BlocklyEditorType.SCRIPT).getExternalTriggers();

			for (ExternalTrigger externalTrigger : externalTriggers) {
				if (externalTrigger.getID().equals(blocklyToJavaScript.getExternalTrigger())) {
					trigger = externalTrigger;
					break;
				}
			}

			if (trigger != null) {
				if (!mcreator.getGeneratorStats().getBlocklyTriggers(BlocklyEditorType.SCRIPT)
						.contains(trigger.getID())) {
					compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("elementgui.procedure.global_trigger_unsupported")));
				}
			} else {
				compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("elementgui.procedure.global_trigger_does_not_exist")));
			}
		} else {
			trigger = null;
		}

		if (trigger != null) {
			triggerType = trigger.getType();
			if (isEditingMode() && triggerTypeBeforeEdit == null) {
				triggerTypeBeforeEdit = trigger.getType();
			}

			if (trigger.getType().equals("block")) {
				compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.INFO,
						L10N.t("elementgui.bescript.global_trigger_block_warning")));
			} else if (trigger.getType().equals("item")) {
				compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.INFO,
						L10N.t("elementgui.bescript.global_trigger_item_warning")));
			}
		}

		// Handle UI-related stuff below, do not modify compileNotesArrayList here!
		SwingUtilities.invokeLater(() -> {
			dependencies.clear();
			dependenciesExtTrigger.clear();
			depsWarningLabel.setText("");

			hasDependencyErrors = false;

			List<Dependency> dependenciesProvided = Optional.ofNullable(trigger).map(t -> t.dependencies_provided)
					.orElse(new ArrayList<>());

			StringBuilder missingdeps = new StringBuilder();
			boolean warn = false;
			for (Dependency dependency : dependenciesArrayList) {
				if (!dependenciesProvided.contains(dependency)) {
					warn = true;
					missingdeps.append(" ").append(dependency.name());
				}
			}
			if (warn) {
				depsWarningLabel.setText(L10N.t("elementgui.procedure.dependencies_not_provided", missingdeps));
				hasDependencyErrors = true;
			}

			Collections.sort(dependenciesProvided);
			dependenciesProvided.forEach(dependenciesExtTrigger::addElement);
			if (trigger != null) {
				extDepsLab.setText("<html><font style='font-size: 10px;'>" + trigger.getName());
			} else {
				extDepsLab.setText("");
			}

			dependenciesArrayList.forEach(dependencies::addElement);

			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
		});

		blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel, jsEventTriggeredChange));

		return compileNotesArrayList;
	}

	@Override public DefaultListModel<VariableElement> getLocalVariablesListModel() {
		return localVars;
	}

	static class DependenciesListRenderer extends JLabel implements ListCellRenderer<Dependency> {
		@Override
		public Component getListCellRendererComponent(JList<? extends Dependency> list, Dependency value, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBorder(null);
			Color col = value.getColor();
			setBackground(isSelected ? col : Theme.current().getBackgroundColor());
			setForeground(isSelected ? Theme.current().getForegroundColor() : col.brighter());
			ComponentUtils.deriveFont(this, 14);
			setText(value.name());
			setToolTipText(value.toString());
			return this;
		}
	}

	@Override protected void initGUI() {
		pane5.setOpaque(false);

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

		localVarsList.setOpaque(false);
		localVarsList.setCellRenderer(new LocalVariableListRenderer());
		localVarsList.setBorder(BorderFactory.createEmptyBorder());
		localVarsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		JPanel localVarsPan = new JPanel(new BorderLayout());
		localVarsPan.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(localVarsList);
		scrollPane.setBackground(Theme.current().getBackgroundColor());
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(11);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		localVarsPan.add("Center", scrollPane);

		JToolBar bar = new JToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(2, 0, 5, 0));
		bar.setFloatable(false);
		bar.setOpaque(false);

		JLabel lab = L10N.label("elementgui.procedure.local_variables");
		lab.setToolTipText(L10N.t("elementgui.procedure.local_variables"));

		JButton addvar = new JButton(UIRES.get("16px.add"));
		addvar.setContentAreaFilled(false);
		addvar.setOpaque(false);
		ComponentUtils.deriveFont(addvar, 11);
		addvar.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 2));
		bar.add(addvar);

		JButton remvar = new JButton(UIRES.get("16px.delete"));
		remvar.setContentAreaFilled(false);
		remvar.setOpaque(false);
		ComponentUtils.deriveFont(remvar, 11);
		remvar.setBorder(BorderFactory.createEmptyBorder(1, 1, 0, 1));
		bar.add(remvar);

		addvar.addActionListener(e -> {
			VariableElement element = NewVariableDialog.showNewVariableDialog(mcreator, false,
					new OptionPaneValidator() {
						@Override public ValidationResult validate(JComponent component) {
							Validator validator = new JavaMemberNameValidator((VTextField) component, false, false);
							String variableName = ((VTextField) component).getText();
							for (int i = 0; i < localVars.getSize(); i++) {
								String nameinrow = localVars.get(i).getName();
								if (variableName.equals(nameinrow))
									return new ValidationResult(ValidationResult.Type.ERROR,
											L10N.t("common.name_already_exists"));
							}
							for (Dependency dependency : dependenciesArrayList) {
								String nameinrow = dependency.name();
								if (variableName.equals(nameinrow))
									return new ValidationResult(ValidationResult.Type.ERROR,
											L10N.t("elementgui.procedure.name_already_exists_dep"));
							}
							return validator.validate();
						}
					}, VariableTypeLoader.INSTANCE.getLocalVariableTypes(mcreator.getGeneratorConfiguration()));
			if (element != null) {
				blocklyPanel.addLocalVariable(element.getName(), element.getType().getBlocklyVariableType());
				localVars.addElement(element);
			}
		});

		remvar.addActionListener(e -> {
			List<VariableElement> elements = localVarsList.getSelectedValuesList();
			if (!elements.isEmpty()) {
				int n = JOptionPane.showConfirmDialog(mcreator, L10N.t("elementgui.procedure.confirm_delete_var_msg"),
						L10N.t("common.confirmation"), JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (n == JOptionPane.YES_OPTION) {
					for (var element : elements) {
						blocklyPanel.removeLocalVariable(element.getName());
						localVars.removeElement(element);
					}
				}
			}
		});

		localVarsList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (localVars.getSize() > 0 && e.getClickCount() == 2) {
					VariableElement selectedVar = localVarsList.getSelectedValue();
					if (selectedVar != null) {
						VariableType type = selectedVar.getType();
						String blockXml = "<xml xmlns=\"http://www.w3.org/1999/xhtml\"><block type=\"variables_"
								+ (e.isAltDown() ? "set_" : "get_") + type.getName() + "\"><field name=\"VAR\">local:"
								+ selectedVar.getName() + "</field></block></xml>";
						blocklyPanel.addBlocksFromXML(blockXml);
					}
				}
			}
		});

		dependenciesList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (dependencies.getSize() > 0 && e.getClickCount() == 2) {
					Dependency selectedDep = dependenciesList.getSelectedValue();
					if (selectedDep != null) {
						String blockXml = selectedDep.getDependencyBlockXml(BlocklyEditorType.SCRIPT);
						if (blockXml != null)
							blocklyPanel.addBlocksFromXML(blockXml);
					}
				}
			}
		});

		dependenciesExtTrigList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (dependenciesExtTrigger.getSize() > 0 && e.getClickCount() == 2) {
					Dependency selectedDep = dependenciesExtTrigList.getSelectedValue();
					if (selectedDep != null) {
						String blockXml = selectedDep.getDependencyBlockXml(BlocklyEditorType.SCRIPT);
						if (blockXml != null)
							blocklyPanel.addBlocksFromXML(blockXml);
					}
				}
			}
		});

		JPanel varHeader = new JPanel(new GridLayout());
		varHeader.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		varHeader.setBackground(Theme.current().getBackgroundColor());
		varHeader.add(PanelUtils.northAndCenterElement(ComponentUtils.deriveFont(lab, 13), bar));
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
		depsHeader.setBackground(Theme.current().getBackgroundColor());
		depsHeader.add("North", bar2);
		depsHeader.add("South", ComponentUtils.deriveFont(depsWarningLabel, 11));

		depsPan.add("North", depsHeader);
		depsPan.setOpaque(false);
		depsPan.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.current().getAltBackgroundColor()));

		JScrollPane scrollPaneDeps = new JScrollPane(dependenciesList);
		scrollPaneDeps.setBackground(Theme.current().getBackgroundColor());
		scrollPaneDeps.getViewport().setOpaque(false);
		scrollPaneDeps.getVerticalScrollBar().setUnitIncrement(11);
		scrollPaneDeps.getHorizontalScrollBar().setUnitIncrement(11);
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
		extdepsHeader.setBackground(Theme.current().getBackgroundColor());
		extdepsHeader.add("North", bar3);

		triggerDepsPan.add("North", extdepsHeader);
		triggerDepsPan.setBackground(Theme.current().getBackgroundColor());
		triggerDepsPan.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Theme.current().getAltBackgroundColor()));

		JScrollPane scrollPaneExtDeps = new JScrollPane(dependenciesExtTrigList);
		scrollPaneExtDeps.setBackground(Theme.current().getBackgroundColor());
		scrollPaneExtDeps.getViewport().setOpaque(false);
		scrollPaneExtDeps.getVerticalScrollBar().setUnitIncrement(11);
		scrollPaneExtDeps.getHorizontalScrollBar().setUnitIncrement(11);
		scrollPaneExtDeps.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		triggerDepsPan.add("Center", PanelUtils.northAndCenterElement(
				ComponentUtils.deriveFont(L10N.label("elementgui.procedure.provided_dependencies"), 13),
				scrollPaneExtDeps, 0, 1));
		triggerDepsPan.setPreferredSize(new Dimension(150, 0));

		JPanel eastPan = new JPanel();
		eastPan.setLayout(new BoxLayout(eastPan, BoxLayout.PAGE_AXIS));
		eastPan.setBackground(Theme.current().getBackgroundColor());
		eastPan.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.current().getBackgroundColor()));

		eastPan.add(localVarsPan);
		eastPan.add(depsPan);
		eastPan.add(triggerDepsPan);

		pane5.add("East", eastPan);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.SCRIPT).getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.SCRIPT);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.SCRIPT)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.SCRIPT);

			BlocklyLoader.INSTANCE.getExternalTriggerLoader(BlocklyEditorType.SCRIPT).getExternalTriggers()
					.forEach(blocklyPanel::addExternalTrigger);
			for (VariableElement variable : mcreator.getWorkspace().getVariableElements()) {
				blocklyPanel.addGlobalVariable(variable.getName(), variable.getType().getBlocklyVariableType());
			}
			blocklyPanel.addChangeListener(
					changeEvent -> new Thread(() -> regenerateBlockAssemblies(true), "ProcedureRegenerate").start());
		});
		if (!isEditingMode()) {
			blocklyPanel.setInitialXML(BEScript.XML_BASE);
		}

		pane5.add("Center", blocklyPanel);

		pane5.add("South", compileNotesPanel);

		compileNotesPanel.setPreferredSize(new Dimension(0, 70));

		blocklyEditorToolbar = new BlocklyEditorToolbar(mcreator, BlocklyEditorType.SCRIPT, blocklyPanel, this);
		blocklyEditorToolbar.setTemplateLibButtonWidth(168);
		pane5.add("North", blocklyEditorToolbar);

		addPage(pane5, false).lazyValidate(() -> {
			if (hasDependencyErrors)
				return new AggregatedValidationResult.FAIL(
						L10N.t("elementgui.procedure.external_trigger_does_not_provide_all_dependencies"));
			else
				return new AggregatedValidationResult.PASS();
		}).lazyValidate(BlocklyAggregatedValidationResult.blocklyValidator(this));
	}

	@Override protected void afterGeneratableElementGenerated() {
		super.afterGeneratableElementGenerated();

		boolean triggerTypeChanged = triggerTypeBeforeEdit != null && !triggerTypeBeforeEdit.equals(triggerType);

		// this procedure could be in use and new dependencies were added
		if (isEditingMode() && triggerTypeChanged)
			fixScriptReferences(modElement, triggerTypeBeforeEdit);

		triggerTypeBeforeEdit = triggerType;
	}

	private void fixScriptReferences(ModElement beScript, String triggerTypeBeforeEdit) {
		for (ModElement element : ReferencesFinder.searchModElementUsages(mcreator.getWorkspace(), beScript)) {
			if (triggerTypeBeforeEdit.equals("block") && element.getType() == ModElementType.BEBLOCK) {
				if (element.getGeneratableElement() instanceof BEBlock beBlock) {
					beBlock.localScripts.remove(beScript.getName());
					LOG.info("Regenerating BEBlock {} because it referenced script {}", element.getName(),
							beScript.getName());
					mcreator.getGenerator().generateElement(element.getGeneratableElement());
					mcreator.getWorkspace().getModElementManager().storeModElement(beBlock);
				}
			} else if (triggerTypeBeforeEdit.equals("item") && element.getType() == ModElementType.BEITEM) {
				if (element.getGeneratableElement() instanceof BEItem beItem) {
					beItem.localScripts.remove(beScript.getName());
					LOG.info("Regenerating BEItem {} because it referenced script {}", element.getName(),
							beScript.getName());
					mcreator.getGenerator().generateElement(element.getGeneratableElement());
					mcreator.getWorkspace().getModElementManager().storeModElement(beItem);
				}
			}
		}
	}

	@Override public void openInEditingMode(BEScript script) {
		blocklyPanel.setInitialXML(script.scriptxml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			localVars.removeAllElements();
			blocklyPanel.getLocalVariablesList().forEach(localVars::addElement);
		});
	}

	@Override public BEScript getElementFromGUI() {
		BEScript script = new BEScript(modElement);
		script.scriptxml = blocklyPanel.getXML();
		return script;
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/section/be-script-system");
	}

	@Override public void search(@Nullable String searchTerm) {
		blocklyEditorToolbar.getSearchField().requestFocusInWindow();

		if (searchTerm != null)
			blocklyEditorToolbar.getSearchField().setText(searchTerm);
	}

	static class LocalVariableListRenderer extends JLabel implements ListCellRenderer<VariableElement> {
		@Override
		public Component getListCellRendererComponent(JList<? extends VariableElement> list, VariableElement value,
				int index, boolean isSelected, boolean cellHasFocus) {
			setOpaque(isSelected);
			setBorder(null);
			Color col = value.getType().getBlocklyColor();
			setBackground(isSelected ? col : Theme.current().getBackgroundColor());
			setForeground(isSelected ? Theme.current().getForegroundColor() : col.brighter());
			ComponentUtils.deriveFont(this, 14);
			setText(value.getName());
			setToolTipText(value.getTooltipText());
			return this;
		}
	}

}