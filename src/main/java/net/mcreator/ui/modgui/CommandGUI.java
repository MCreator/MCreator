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
import net.mcreator.blockly.BlocklyToCmdArgs;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.ExternalBlockLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.element.types.Command;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.AITasksEditorToolbar;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CmdArgsEditorToolbar;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandGUI extends ModElementGUI<Command> {

	private final VTextField commandName = new VTextField(25);
	private final JComboBox<String> permissionLevel = new JComboBox<>(
			new String[] { "No requirement", "1", "2", "3", "4" });
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private final ValidationGroup page1group = new ValidationGroup();
	private ProcedureSelector onCommandExecuted;
	private BlocklyPanel blocklyPanel;
	private boolean hasErrors = false;
	private Map<String, ToolboxBlock> externalBlocks;

	public CommandGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	private void regenerateArgs() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getGeneratorCmdArgs());

		BlocklyToCmdArgs blocklyToJava;
		try {
			blocklyToJava = new BlocklyToCmdArgs(mcreator.getWorkspace(), blocklyPanel.getXML(), null,
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			hasErrors = false;
			for (BlocklyCompileNote note : compileNotesArrayList) {
				if (note.getType() == BlocklyCompileNote.Type.ERROR) {
					hasErrors = true;
					break;
				}
			}
		});
	}

	@Override protected void initGUI() {
		onCommandExecuted = new ProcedureSelector(this.withEntry("command/when_executed"), mcreator,
				L10N.t("elementgui.command.when_command_executed"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/cmdargs:ctx/cmdparams:map"));

		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		ComponentUtils.deriveFont(commandName, 16);

		JPanel enderpanel = new JPanel(new GridLayout(2, 2, 10, 2));

		enderpanel.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("command/name"), L10N.label("elementgui.command.name")));
		enderpanel.add(commandName);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("command/permission_level"),
				L10N.label("elementgui.command.permission_level")));
		enderpanel.add(permissionLevel);

		enderpanel.setOpaque(false);
		pane5.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getCmdArgsBlockLoader().getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getCmdArgsBlockLoader()
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ExternalBlockLoader.ToolboxType.EMPTY);
			blocklyPanel.getJSBridge()
					.setJavaScriptEventListener(() -> new Thread(CommandGUI.this::regenerateArgs).start());
			if (!isEditingMode()) {
				blocklyPanel
						.setXML("<xml><block type=\"args_start\" deletable=\"false\" x=\"40\" y=\"40\"></block></xml>");
			}
		});

		blocklyPanel.setPreferredSize(new Dimension(450, 440));

		JPanel args = (JPanel) PanelUtils.centerAndSouthElement(
				PanelUtils.northAndCenterElement(new CmdArgsEditorToolbar(mcreator, blocklyPanel), blocklyPanel),
				compileNotesPanel);
		args.setOpaque(false);

		pane5.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerAndSouthElement(PanelUtils.centerInPanel(enderpanel),
				PanelUtils.centerInPanel(onCommandExecuted))));

		commandName
				.setValidator(new TextFieldValidator(commandName, L10N.t("elementgui.command.warning.empty_string")));
		commandName.enableRealtimeValidation();

		page1group.addValidationElement(commandName);

		addPage(L10N.t("elementgui.common.page_properties"), pane5);
		addPage(L10N.t("elementgui.command.page_arguments"), args);

		if (!isEditingMode()) {
			commandName.setText(modElement.getName().toLowerCase(Locale.ENGLISH));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onCommandExecuted.refreshListKeepSelected();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group);
	}

	@Override public void openInEditingMode(Command command) {
		onCommandExecuted.setSelectedProcedure(command.onCommandExecuted);
		commandName.setText(command.commandName);
		permissionLevel.setSelectedItem(command.permissionLevel);

		blocklyPanel.setXMLDataOnly(command.argsxml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(command.argsxml);
			regenerateArgs();
		});
	}

	@Override public Command getElementFromGUI() {
		Command command = new Command(modElement);
		command.commandName = commandName.getText();
		command.onCommandExecuted = onCommandExecuted.getSelectedProcedure();
		command.permissionLevel = (String) permissionLevel.getSelectedItem();
		command.argsxml = blocklyPanel.getXML();
		return command;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/making-command");
	}

}
