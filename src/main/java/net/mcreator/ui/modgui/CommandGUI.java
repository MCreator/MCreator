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
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.data.ExternalBlockLoader;
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.types.Command;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.BlocklyEditorToolbar;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.traslatable.AdvancedTranslatableComboBox;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandGUI extends ModElementGUI<Command> {

	private final VTextField commandName = new VTextField(25);
	private final AdvancedTranslatableComboBox<String> permissionLevel = new AdvancedTranslatableComboBox<>(
			new String[] { "No requirement", "1", "2", "3", "4" },new String[]{"无权限","1","2","3","4"});
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private final ValidationGroup page1group = new ValidationGroup();
	private BlocklyPanel blocklyPanel;
	private boolean hasErrors = false;
	private Map<String, ToolboxBlock> externalBlocks;

	public CommandGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		ComponentUtils.deriveFont(commandName, 16);

		JPanel enderpanel = new JPanel(new GridLayout(2, 2, 10, 2));

		enderpanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("command/name"), L10N.label("elementgui.command.name")));
		enderpanel.add(commandName);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("command/permission_level"),
				L10N.label("elementgui.command.permission_level")));
		enderpanel.add(permissionLevel);

		enderpanel.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getCmdArgsBlockLoader().getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getCmdArgsBlockLoader()
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ExternalBlockLoader.ToolboxType.COMMAND);
			blocklyPanel.getJSBridge()
					.setJavaScriptEventListener(() -> new Thread(CommandGUI.this::regenerateArgs).start());
			if (!isEditingMode()) {
				blocklyPanel.setXML(
						"<xml><block type=\"args_start\" deletable=\"false\" x=\"40\" y=\"40\"><next><block type=\"call_procedure\"></block></next></block></xml>");
			}
		});

		blocklyPanel.setPreferredSize(new Dimension(450, 440));

		JPanel args = (JPanel) PanelUtils.centerAndSouthElement(PanelUtils.northAndCenterElement(
						new BlocklyEditorToolbar(mcreator, BlocklyEditorType.COMMAND_ARG, blocklyPanel), blocklyPanel),
				compileNotesPanel);
		args.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.command.arguments"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));
		args.setOpaque(false);

		commandName.setValidator(
				new TextFieldValidator(commandName, L10N.t("elementgui.command.warning.empty_string")));
		commandName.enableRealtimeValidation();

		page1group.addValidationElement(commandName);

		addPage(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, enderpanel),
				PanelUtils.maxMargin(args, 10, true, true, true, true)));

		if (!isEditingMode()) {
			commandName.setText(modElement.getName().toLowerCase(Locale.ENGLISH));
		}
	}

	private void regenerateArgs() {
		BlocklyToJava blocklyToJava;
		try {
			blocklyToJava = new BlocklyToJava(mcreator.getWorkspace(), BlocklyEditorType.COMMAND_ARG,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(
					new BlocklyBlockCodeGenerator(externalBlocks, mcreator.getGeneratorStats().getGeneratorCmdArgs())));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			hasErrors = compileNotesArrayList.stream().anyMatch(note -> note.type() == BlocklyCompileNote.Type.ERROR);
		});
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (!hasErrors)
			return new AggregatedValidationResult(page1group);
		else
			return new AggregatedValidationResult.MULTIFAIL(
					compileNotesPanel.getCompileNotes().stream().map(BlocklyCompileNote::message)
							.collect(Collectors.toList()));
	}

	@Override public void openInEditingMode(Command command) {
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
		command.permissionLevel = (String) permissionLevel.getSelectedItem();
		command.argsxml = blocklyPanel.getXML();
		return command;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/making-command");
	}

}
