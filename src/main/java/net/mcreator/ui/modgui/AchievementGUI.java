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
import net.mcreator.blockly.datapack.BlocklyToJSONTrigger;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.AchievementEntry;
import net.mcreator.element.types.Achievement;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.mapping.NonMappableElement;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.BlocklyAggregatedValidationResult;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.ModElementListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AchievementGUI extends ModElementGUI<Achievement> implements IBlocklyPanelHolder {

	private final VTextField achievementName = new VTextField(20);
	private final VTextField achievementDescription = new VTextField(20);

	private final DataListComboBox parentAchievement = new DataListComboBox(mcreator);

	private MCItemHolder achievementIcon;

	private final JComboBox<String> achievementType = new JComboBox<>(new String[] { "task", "goal", "challenge" });

	private final JComboBox<String> rewardFunction = new JComboBox<>();

	private final JComboBox<String> background = new JComboBox<>();

	JCheckBox showPopup = L10N.checkbox("elementgui.common.enable");
	JCheckBox announceToChat = L10N.checkbox("elementgui.common.enable");
	JCheckBox hideIfNotCompleted = L10N.checkbox("elementgui.common.enable");
	JCheckBox disableDisplay = L10N.checkbox("elementgui.common.enable");

	private final ValidationGroup page1group = new ValidationGroup();

	private ModElementListField rewardLoot;
	private ModElementListField rewardRecipes;

	private final JSpinner rewardXP = new JSpinner(new SpinnerNumberModel(0, 0, 64000, 1));

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private Map<String, ToolboxBlock> externalBlocks;
	private final List<BlocklyChangedListener> blocklyChangedListeners = new ArrayList<>();

	public AchievementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override public void addBlocklyChangedListener(BlocklyChangedListener listener) {
		blocklyChangedListeners.add(listener);
	}

	@Override protected void initGUI() {
		achievementIcon = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		JPanel propertiesPanel = new JPanel(new GridLayout(7, 2, 10, 2));
		JPanel logicPanel = new JPanel(new GridLayout(7, 2, 10, 2));

		rewardLoot = new ModElementListField(mcreator, ModElementType.LOOTTABLE);
		rewardRecipes = new ModElementListField(mcreator, ModElementType.RECIPE);

		ComponentUtils.deriveFont(achievementName, 16);
		ComponentUtils.deriveFont(achievementDescription, 16);

		background.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");
		background.setRenderer(new WTextureComboBoxRenderer.TypeTextures(mcreator.getWorkspace(), TextureType.SCREEN));

		parentAchievement.setPrototypeDisplayValue(new DataListEntry.Dummy("XXXXXXXXXXXXXXXXXXXXXXX"));

		showPopup.setOpaque(false);
		announceToChat.setOpaque(false);
		hideIfNotCompleted.setOpaque(false);
		disableDisplay.setOpaque(false);

		showPopup.setSelected(true);
		announceToChat.setSelected(true);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/name"),
				L10N.label("elementgui.advancement.name")));
		propertiesPanel.add(achievementName);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/description"),
				L10N.label("elementgui.advancement.description")));
		propertiesPanel.add(achievementDescription);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/icon"),
				L10N.label("elementgui.advancement.icon")));
		propertiesPanel.add(PanelUtils.join(FlowLayout.LEFT, achievementIcon));

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/background"),
				L10N.label("elementgui.advancement.background")));
		propertiesPanel.add(background);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/type"),
				L10N.label("elementgui.advancement.type")));
		propertiesPanel.add(achievementType);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/parent"),
				L10N.label("elementgui.advancement.parent")));
		propertiesPanel.add(parentAchievement);

		propertiesPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/show_toast"),
				L10N.label("elementgui.advancement.show_toast")));
		propertiesPanel.add(showPopup);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_xp"),
				L10N.label("elementgui.advancement.reward_xp")));
		logicPanel.add(rewardXP);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_function"),
				L10N.label("elementgui.advancement.reward_functions")));
		logicPanel.add(rewardFunction);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_loot_tables"),
				L10N.label("elementgui.advancement.reward_loot_tables")));
		logicPanel.add(rewardLoot);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_recipes"),
				L10N.label("elementgui.advancement.reward_recipes")));
		logicPanel.add(rewardRecipes);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/announce_to_chat"),
				L10N.label("elementgui.advancement.announce_to_chat")));
		logicPanel.add(announceToChat);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/hide_if_not_completed"),
				L10N.label("elementgui.advancement.hide_if_not_completed")));
		logicPanel.add(hideIfNotCompleted);

		logicPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/hide_display"),
				L10N.label("elementgui.advancement.hide_display")));
		logicPanel.add(disableDisplay);

		logicPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.advancement.logic"), 0, 0, logicPanel.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		propertiesPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.advancement.display_paramters"), 0, 0, propertiesPanel.getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		propertiesPanel.setOpaque(false);
		logicPanel.setOpaque(false);

		achievementName.setValidator(
				new TextFieldValidator(achievementName, L10N.t("elementgui.advancement.cant_be_empty")));
		achievementDescription.setValidator(
				new TextFieldValidator(achievementDescription, L10N.t("elementgui.advancement.must_have_description")));
		achievementIcon.setValidator(new MCItemHolderValidator(achievementIcon));
		achievementName.enableRealtimeValidation();
		achievementDescription.enableRealtimeValidation();

		page1group.addValidationElement(achievementIcon);
		page1group.addValidationElement(achievementName);
		page1group.addValidationElement(achievementDescription);

		externalBlocks = BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.JSON_TRIGGER).getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator, BlocklyEditorType.JSON_TRIGGER);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.JSON_TRIGGER)
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ToolboxType.EMPTY);
			blocklyPanel.getJSBridge().setJavaScriptEventListener(
					() -> new Thread(AchievementGUI.this::regenerateTrigger, "TriggerRegenerate").start());
			if (!isEditingMode()) {
				blocklyPanel.setXML(
						"<xml><block type=\"advancement_trigger\" deletable=\"false\" x=\"40\" y=\"80\"/></xml>");
			}
		});

		JPanel advancementTrigger = (JPanel) PanelUtils.centerAndSouthElement(blocklyPanel, compileNotesPanel);
		advancementTrigger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.advancement.trigger_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));

		JComponent wrap = PanelUtils.northAndCenterElement(PanelUtils.westAndCenterElement(propertiesPanel, logicPanel),
				advancementTrigger);
		wrap.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		addPage(wrap, false);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			achievementName.setText(readableNameFromModElement);
		}
	}

	private synchronized void regenerateTrigger() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.JSON_TRIGGER));

		BlocklyToJSONTrigger blocklyToJSONTrigger;
		try {
			blocklyToJSONTrigger = new BlocklyToJSONTrigger(mcreator.getWorkspace(), this.modElement,
					blocklyPanel.getXML(), null, new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJSONTrigger.getCompileNotes();

		if (!blocklyToJSONTrigger.hasTrigger()) {
			compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("elementgui.advancement.need_trigger")));
		}

		SwingUtilities.invokeLater(() -> {
			compileNotesPanel.updateCompileNotes(compileNotesArrayList);
			blocklyChangedListeners.forEach(l -> l.blocklyChanged(blocklyPanel));
		});
	}

	@Override public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(parentAchievement,
				ElementUtil.loadAllAchievements(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(rewardFunction, ListUtils.merge(Collections.singleton("No function"),
				mcreator.getWorkspace().getModElements().stream().filter(e -> e.getType() == ModElementType.FUNCTION)
						.map(ModElement::getName).collect(Collectors.toList())), "No function");

		ComboBoxUtil.updateComboBoxContents(background, ListUtils.merge(Collections.singleton("Default"),
				mcreator.getFolderManager().getTexturesList(TextureType.SCREEN).stream().map(File::getName)
						.collect(Collectors.toList())), "Default");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(page1group,
				new BlocklyAggregatedValidationResult(compileNotesPanel.getCompileNotes(),
						compileNote -> L10N.t("elementgui.advancement.trigger", compileNote)));
	}

	@Override public void openInEditingMode(Achievement achievement) {
		achievementName.setText(achievement.achievementName);
		achievementDescription.setText(achievement.achievementDescription);
		achievementIcon.setBlock(achievement.achievementIcon);
		achievementType.setSelectedItem(achievement.achievementType);
		parentAchievement.setSelectedItem(achievement.parent.getUnmappedValue());
		disableDisplay.setSelected(achievement.disableDisplay);
		showPopup.setSelected(achievement.showPopup);
		announceToChat.setSelected(achievement.announceToChat);
		hideIfNotCompleted.setSelected(achievement.hideIfNotCompleted);
		rewardFunction.setSelectedItem(achievement.rewardFunction);
		background.setSelectedItem(achievement.background);
		rewardLoot.setListElements(achievement.rewardLoot.stream().map(NonMappableElement::new).toList());
		rewardRecipes.setListElements(achievement.rewardRecipes.stream().map(NonMappableElement::new).toList());
		rewardXP.setValue(achievement.rewardXP);

		blocklyPanel.setXMLDataOnly(achievement.triggerxml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(achievement.triggerxml);
			blocklyPanel.triggerEventFunction();
		});
	}

	@Override public Achievement getElementFromGUI() {
		Achievement achievement = new Achievement(modElement);
		achievement.achievementName = achievementName.getText();
		achievement.achievementDescription = achievementDescription.getText();
		achievement.achievementIcon = achievementIcon.getBlock();
		achievement.achievementType = (String) achievementType.getSelectedItem();
		achievement.parent = new AchievementEntry(mcreator.getWorkspace(), parentAchievement.getSelectedItem());
		achievement.showPopup = showPopup.isSelected();
		achievement.disableDisplay = disableDisplay.isSelected();
		achievement.announceToChat = announceToChat.isSelected();
		achievement.hideIfNotCompleted = hideIfNotCompleted.isSelected();
		achievement.rewardFunction = (String) rewardFunction.getSelectedItem();
		achievement.background = (String) background.getSelectedItem();
		achievement.rewardLoot = rewardLoot.getListElements().stream().map(NonMappableElement::getUnmappedValue)
				.collect(Collectors.toList());
		achievement.rewardRecipes = rewardRecipes.getListElements().stream().map(NonMappableElement::getUnmappedValue)
				.collect(Collectors.toList());
		achievement.rewardXP = (int) rewardXP.getValue();

		achievement.triggerxml = blocklyPanel.getXML();

		return achievement;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-achievement");
	}

	@Override public Set<BlocklyPanel> getBlocklyPanels() {
		return Set.of(blocklyPanel);
	}

	@Override public boolean isInitialXMLValid() {
		return false;
	}

}
