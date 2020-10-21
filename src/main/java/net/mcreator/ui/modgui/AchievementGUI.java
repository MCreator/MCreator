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
import net.mcreator.blockly.datapack.BlocklyToJSONTrigger;
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.AchievementEntry;
import net.mcreator.element.types.Achievement;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.blockly.BlocklyPanel;
import net.mcreator.ui.blockly.CompileNotesPanel;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.WTextureComboBoxRenderer;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.ModElementListField;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AchievementGUI extends ModElementGUI<Achievement> {

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

	private final JSpinner rewardXP = new JSpinner(new SpinnerNumberModel(1, 0, 64000, 1));

	private BlocklyPanel blocklyPanel;
	private final CompileNotesPanel compileNotesPanel = new CompileNotesPanel();
	private boolean hasErrors = false;
	private Map<String, ToolboxBlock> externalBlocks;

	public AchievementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		achievementIcon = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);

		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel selp = new JPanel(new GridLayout(10, 2, 15, 10));
		JPanel selp2 = new JPanel(new GridLayout(4, 2, 10, 10));

		rewardLoot = new ModElementListField(mcreator, ModElementType.LOOTTABLE);
		rewardRecipes = new ModElementListField(mcreator, ModElementType.RECIPE);

		ComponentUtils.deriveFont(achievementName, 16);
		ComponentUtils.deriveFont(achievementDescription, 16);

		background.setPrototypeDisplayValue("XXXXXXXXXXXXXXXXXXXXXXXXXX");
		background.setRenderer(new WTextureComboBoxRenderer.OtherTextures(mcreator.getWorkspace()));

		showPopup.setOpaque(false);
		announceToChat.setOpaque(false);
		hideIfNotCompleted.setOpaque(false);
		disableDisplay.setOpaque(false);

		showPopup.setSelected(true);
		announceToChat.setSelected(true);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("advancement/name"), L10N.label("elementgui.advancement.name")));
		selp.add(achievementName);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/description"),
				L10N.label("elementgui.advancement.description")));
		selp.add(achievementDescription);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("advancement/icon"), new JLabel("elementgui.advancement.icon")));
		selp.add(PanelUtils.join(FlowLayout.LEFT, achievementIcon));

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/background"),
				L10N.label("elementgui.advancement.background")));
		selp.add(background);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("advancement/type"), new JLabel("elementgui.advancement.type")));
		selp.add(achievementType);

		selp.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("advancement/parent"), L10N.label("elementgui.advancement.parent")));
		selp.add(parentAchievement);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/show_toast"),
				L10N.label("elementgui.advancement.show_toast")));
		selp.add(showPopup);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/announce_to_chat"),
				L10N.label("elementgui.advancement.announce_to_chat")));
		selp.add(announceToChat);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/hide_if_not_completed"),
				L10N.label("elementgui.advancement.hide_if_not_completed")));
		selp.add(hideIfNotCompleted);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/hide_display"),
				new JLabel(L10N.t("elementgui.advancement.hide_display"))));
		selp.add(disableDisplay);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_xp"),
				L10N.label("elementgui.advancement.reward_xp")));
		selp2.add(rewardXP);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_function"),
				L10N.label("elementgui.advancement.reward_functions")));
		selp2.add(rewardFunction);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_loot_tables"),
				L10N.label("elementgui.advancement.reward_loot_tables")));
		selp2.add(rewardLoot);

		selp2.add(HelpUtils.wrapWithHelpButton(this.withEntry("advancement/reward_recipes"),
				L10N.label("elementgui.advancement.reward_recipes")));
		selp2.add(rewardRecipes);

		selp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.advancement.display_paramters"), 0, 0, selp.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		selp2.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.advancement.logic"), 0, 0, selp2.getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		selp.setOpaque(false);
		selp2.setOpaque(false);

		achievementName
				.setValidator(new TextFieldValidator(achievementName, L10N.t("elementgui.advancement.cant_be_empty")));
		achievementDescription.setValidator(
				new TextFieldValidator(achievementDescription, L10N.t("elementgui.advancement.must_have_description")));
		achievementIcon.setValidator(new MCItemHolderValidator(achievementIcon));
		achievementName.enableRealtimeValidation();
		achievementDescription.enableRealtimeValidation();

		page1group.addValidationElement(achievementIcon);
		page1group.addValidationElement(achievementName);
		page1group.addValidationElement(achievementDescription);

		externalBlocks = BlocklyLoader.INSTANCE.getJSONTriggerLoader().getDefinedBlocks();
		blocklyPanel = new BlocklyPanel(mcreator);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			BlocklyLoader.INSTANCE.getJSONTriggerLoader()
					.loadBlocksAndCategoriesInPanel(blocklyPanel, ExternalBlockLoader.ToolboxType.EMPTY);
			blocklyPanel.getJSBridge()
					.setJavaScriptEventListener(() -> new Thread(AchievementGUI.this::regenerateTrigger).start());
			if (!isEditingMode()) {
				blocklyPanel
						.setXML("<xml><block type=\"advancement_trigger\" deletable=\"false\" x=\"40\" y=\"80\"/></xml>");
			}
		});

		JPanel advancementTrigger = (JPanel) PanelUtils.centerAndSouthElement(blocklyPanel, compileNotesPanel);
		advancementTrigger.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1),
				L10N.t("elementgui.advancement.trigger_builder"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Color.white));

		advancementTrigger.setPreferredSize(new Dimension(0, 330));

		pane3.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(PanelUtils
				.westAndEastElement(PanelUtils.centerInPanel(selp),
						PanelUtils.northAndCenterElement(selp2, advancementTrigger)))));

		pane3.setOpaque(false);

		addPage(pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			achievementName.setText(readableNameFromModElement);
		}
	}

	private void regenerateTrigger() {
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(externalBlocks,
				mcreator.getWorkspace().getGenerator().getGeneratorStats().getJsonTriggers());

		BlocklyToJSONTrigger blocklyToJSONTrigger;
		try {
			blocklyToJSONTrigger = new BlocklyToJSONTrigger(mcreator.getWorkspace(), blocklyPanel.getXML(), null,
					new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator));
		} catch (TemplateGeneratorException e) {
			return;
		}

		List<BlocklyCompileNote> compileNotesArrayList = blocklyToJSONTrigger.getCompileNotes();

		if (!blocklyToJSONTrigger.hasTrigger()) {
			compileNotesArrayList.add(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("elementgui.advancement.need_trigger")));
		}

		SwingUtilities.invokeLater(() -> {
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

	@Override public void reloadDataLists() {
		ComboBoxUtil
				.updateComboBoxContents(parentAchievement, ElementUtil.loadAllAchievements(mcreator.getWorkspace()));

		ComboBoxUtil.updateComboBoxContents(rewardFunction, ListUtils
				.merge(Collections.singleton("No function"),
						mcreator.getWorkspace().getModElements().stream()
								.filter(e -> e.getType() == ModElementType.FUNCTION).map(ModElement::getName)
								.collect(Collectors.toList())), "No function");

		ComboBoxUtil.updateComboBoxContents(background, ListUtils
				.merge(Collections.singleton("Default"),
						mcreator.getWorkspace().getFolderManager().getOtherTexturesList().stream().map(File::getName)
								.collect(Collectors.toList())), "Default");
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (hasErrors)
			return new AggregatedValidationResult.MULTIFAIL(compileNotesPanel.getCompileNotes().stream()
					.map(compileNote -> L10N.t("elementgui.advancement.trigger") + compileNote.getMessage())
					.collect(Collectors.toList()));

		return new AggregatedValidationResult(page1group);
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
		rewardLoot.setListElements(achievement.rewardLoot);
		rewardRecipes.setListElements(achievement.rewardRecipes);
		rewardXP.setValue(achievement.rewardXP);

		blocklyPanel.setXMLDataOnly(achievement.triggerxml);
		blocklyPanel.addTaskToRunAfterLoaded(() -> {
			blocklyPanel.clearWorkspace();
			blocklyPanel.setXML(achievement.triggerxml);

			regenerateTrigger();
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
		achievement.rewardLoot = rewardLoot.getListElements();
		achievement.rewardRecipes = rewardRecipes.getListElements();
		achievement.rewardXP = (int) rewardXP.getValue();

		achievement.triggerxml = blocklyPanel.getXML();

		return achievement;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-achievement");
	}

}
