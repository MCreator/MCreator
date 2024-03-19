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
import net.mcreator.blockly.data.ToolboxBlock;
import net.mcreator.blockly.data.ToolboxType;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.element.types.Command;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.*;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
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
import java.util.*;

public class CommandGUI extends ModElementGUI<Command> implements IBlocklyPanelHolder {

	private final VTextField commandName = new VTextField(25);
	private final JComboBox<String> type = new JComboBox<>(
			new String[] { "STANDARD", "SINGLEPLAYER_ONLY", "MULTIPLAYER_ONLY", "CLIENTSIDE" });
	private final JComboBox<String> permissionLevel = new JComboBox<>(
			new String[] { "No requirement", "1", "2", "3", "4" });
	private final ValidationGroup page1group = new ValidationGroup();

	private BlocklyPanel blocklyPanel;
	private Map<String, ToolboxBlock> externalBlocks;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private final List<BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	public CommandGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override public void addBlocklyChangedListener(BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override protected void initGUI() {
		ComponentUtils.deriveFont(commandName, 16);

		JPanel enderpanel = new JPanel(new GridLayout(3, 2, 10, 2));

		enderpanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("command/name"), L10N.label("elementgui.command.name")));
		enderpanel.add(commandName);

		enderpanel.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("command/type"), L10N.label("elementgui.command.type")));
		enderpanel.add(type);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("command/permission_level"),
				L10N.label("elementgui.command.permission_level")));
		enderpanel.add(permissionLevel);

		enderpanel.setOpaque(false);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.COMMAND_ARG).getDefinedBlocks();

		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.COMMAND_ARG);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.COMMAND_ARG)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.COMMAND);
			blocklyPanel.getJSBridge().setJavaScriptEventListener(
					() -> new Thread(CommandGUI.this::regenerateArgs, "CommandRegenerate").start());
			if (!isEditingMode()) {
				blocklyPanel.setXML(Command.XML_BASE);
			}
		});

		blocklyPanel.setPreferredSize(new Dimension(450, 440));

		JPanel args = (JPanel) PanelUtils.centerAndSouthElement(PanelUtils.northAndCenterElement(
						new BlocklyEditorToolbar(mcreator, BlocklyEditorType.COMMAND_ARG, blocklyPanel), blocklyPanel),
				compileNotesPanel);
		args.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.command.arguments"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, getFont(),
				Theme.current().getForegroundColor()));
		args.setOpaque(false);

		commandName.setValidator(
				new TextFieldValidator(commandName, L10N.t("elementgui.command.warning.empty_string")));
		commandName.enableRealtimeValidation();

		page1group.addValidationElement(commandName);

		addPage(PanelUtils.northAndCenterElement(PanelUtils.join(FlowLayout.LEFT, enderpanel),
				ComponentUtils.applyPadding(args, 10, true, true, true, true)));

		if (!isEditingMode()) {
			commandName.setText(modElement.getName().toLowerCase(Locale.ENGLISH));
		}
	}

	private synchronized void regenerateArgs() {
		BlocklyToJava blocklyToJava;
		try {
			blocklyToJava = new BlocklyToJava(mcreator.getWorkspace(), this.modElement, BlocklyEditorType.COMMAND_ARG,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(
					new BlocklyBlockCodeGenerator(externalBlocks,
							mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.COMMAND_ARG))));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJava.getCompileNotes();

		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel));
		});
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group,
				new BlocklyAggregatedValidationResult(compileNotesPanel.getCompileNotes(),
						message -> message.replace("Command", "Command arguments")));
	}

	@Override public void openInEditingMode(Command command) {
		commandName.setText(command.commandName);
		type.setSelectedItem(command.type);
		permissionLevel.setSelectedItem(command.permissionLevel);

		blocklyPanel.setXMLDataOnly(command.argsxml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(command.argsxml);
			blocklyPanel.triggerEventFunction();
		});
	}

	@Override public Command getElementFromGUI() {
		Command command = new Command(modElement);
		command.commandName = commandName.getText();
		command.type = (String) type.getSelectedItem();

		command.permissionLevel = (String) permissionLevel.getSelectedItem();
		command.argsxml = blocklyPanel.getXML();
		return command;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/making-command");
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

}
