/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.parts.StepSound;
import net.mcreator.element.types.bedrock.BEBlock;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.BlockTexturesSelector;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.MCItemListField;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BEBlockGUI extends ModElementGUI<BEBlock> {

	private BlockTexturesSelector textures;

	public static final Model normal = new Model.BuiltInModel("Normal");
	public static final Model[] builtinitemmodels = new Model[] { normal };
	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(builtinitemmodels);

	private final VTextField name = new VTextField(10).requireValue("elementgui.block.error_block_must_have_name")
			.enableRealtimeValidation();
	private final JCheckBox enableCreativeTab = new JCheckBox();
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator,
			ElementUtil.loadAllTabs(mcreator.getWorkspace()));

	private final JCheckBox isHiddenInCommands = L10N.checkbox("elementgui.common.enable");
	private final JSpinner hardness = new JSpinner(new SpinnerNumberModel(1, -1, 64000, 0.05));
	private final JSpinner resistance = new JSpinner(new SpinnerNumberModel(10, 0, Integer.MAX_VALUE, 0.5));
	private final JSpinner lightEmission = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));
	private final MCItemHolder customDrop = new MCItemHolder(mcreator, ElementUtil::loadBlocksAndItems);
	private final JSpinner dropAmount = new JSpinner(new SpinnerNumberModel(1, 0, 64, 1));

	private final DataListComboBox soundOnStep = new DataListComboBox(mcreator, ElementUtil.loadStepSounds());
	private final DataListComboBox colorOnMap = new DataListComboBox(mcreator, ElementUtil.loadMapColors());
	private final JSpinner friction = new JSpinner(new SpinnerNumberModel(0.6, 0, 0.9, 0.01));
	private final JSpinner flammability = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));
	private final JSpinner flammableDestroyChance = new JSpinner(new SpinnerNumberModel(0, 0, 1024, 1));

	private final JCheckBox generateFeature = L10N.checkbox("elementgui.common.enable");
	private final JMinMaxSpinner generateHeight = new JMinMaxSpinner(0, 64, -2032, 2016, 1).allowEqualValues();
	private final JSpinner frequencyPerChunks = new JSpinner(new SpinnerNumberModel(10, 1, 64, 1));
	private final JSpinner oreCount = new JSpinner(new SpinnerNumberModel(16, 1, 64, 1));
	private final MCItemListField blocksToReplace = new MCItemListField(mcreator, ElementUtil::loadBlocks, false, true);

	private final JComboBox<String> rotationMode = new JComboBox<>(
			new String[] { L10N.t("elementgui.block.rotation_mode.none"),
					L10N.t("elementgui.block.rotation_mode.player_y_axis"),
					L10N.t("elementgui.block.rotation_mode.player_all_axis"),
					L10N.t("elementgui.block.rotation_mode.block_all_axis"),
					L10N.t("elementgui.block.rotation_mode.log") });

	private final JComboBox<String> renderMethod = new JComboBox<>(
			new String[] { "opaque", "double_sided", "blend", "alpha_test_single_sided", "alpha_test",
					"alpha_test_to_opaque", "alpha_test_single_sided_to_opaque", "blend_to_opaque" });

	private final JComboBox<String> tintMethod = new JComboBox<>(
			new String[] { "(none)", "birch_foliage", "default_foliage", "dry_foliage", "evergreen_foliage", "grass",
					"water" });

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page2group = new ValidationGroup();

	public BEBlockGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel visualPanel = new JPanel(new BorderLayout(10, 10));
		visualPanel.setOpaque(false);
		JPanel propertiesPanel = new JPanel(new BorderLayout(10, 10));
		propertiesPanel.setOpaque(false);
		JPanel generationPanel = new JPanel(new BorderLayout(10, 10));
		generationPanel.setOpaque(false);

		textures = new BlockTexturesSelector(mcreator);
		page1group.addValidationElement(textures);

		ComponentUtils.deriveFont(renderType, 16);
		renderType.addActionListener(event -> updateTextureOptions());
		renderType.setPreferredSize(new Dimension(280, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		JPanel renderSettings = new JPanel(new GridLayout(3, 2, 0, 2));
		renderSettings.setOpaque(false);

		renderSettings.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("beblock/model"), L10N.label("elementgui.beblock.model")));
		renderSettings.add(renderType);

		renderSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("beblock/render_method"),
				L10N.label("elementgui.beblock.render_method")));
		renderSettings.add(renderMethod);

		renderSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("beblock/tint_method"),
				L10N.label("elementgui.beblock.tint_method")));
		renderSettings.add(tintMethod);

		renderMethod.setPreferredSize(new Dimension(260, 42));

		JPanel basicProperties = new JPanel(new GridLayout(14, 2, 2, 2));
		basicProperties.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		basicProperties.add(name);
		ComponentUtils.deriveFont(name, 16);
		page2group.addValidationElement(name);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/creative_tab"),
				L10N.label("elementgui.beitem.creative_tab")));
		basicProperties.add(PanelUtils.westAndCenterElement(enableCreativeTab, creativeTab));
		enableCreativeTab.addActionListener(e -> updateCreativeTab());
		enableCreativeTab.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("beitem/is_hidden_commands"),
				L10N.label("elementgui.beitem.is_hidden_commands")));
		isHiddenInCommands.setOpaque(false);
		basicProperties.add(isHiddenInCommands);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/rotation_mode"),
				L10N.label("elementgui.block.rotation_mode")));
		basicProperties.add(rotationMode);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/hardness"),
				L10N.label("elementgui.common.hardness")));
		basicProperties.add(hardness);
		hardness.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/resistance"),
				L10N.label("elementgui.common.resistance")));
		basicProperties.add(resistance);
		resistance.setOpaque(false);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"),
				L10N.label("elementgui.common.luminance")));
		basicProperties.add(lightEmission);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/custom_drop"),
				L10N.label("elementgui.common.custom_drop")));
		basicProperties.add(PanelUtils.centerInPanel(customDrop));

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/drop_amount"),
				L10N.label("elementgui.common.drop_amount")));
		basicProperties.add(dropAmount);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/block_sound"),
				L10N.label("elementgui.beblock.sound_on_step")));
		basicProperties.add(soundOnStep);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/color_on_map"),
				L10N.label("elementgui.block.color_on_map")));
		basicProperties.add(colorOnMap);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/slipperiness"),
				L10N.label("elementgui.block.slipperiness")));
		basicProperties.add(friction);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/flammability"),
				L10N.label("elementgui.block.flammability")));
		basicProperties.add(flammability);

		basicProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/fire_spread_speed"),
				L10N.label("elementgui.common.fire_spread_speed")));
		basicProperties.add(flammableDestroyChance);

		visualPanel.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndCenterElement(textures, PanelUtils.totalCenterInPanel(renderSettings), 55, 55)));

		propertiesPanel.add("Center", PanelUtils.totalCenterInPanel(basicProperties));

		JPanel genPanel = new JPanel(new GridLayout(5, 2, 65, 2));
		genPanel.setOpaque(false);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/generate_feature"),
				L10N.label("elementgui.block.generate_feature")));
		genPanel.add(generateFeature);

		generateFeature.addActionListener(e -> refreshSpawnProperties());
		refreshSpawnProperties();

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_replace_blocks"),
				L10N.label("elementgui.block.gen_replace_blocks")));
		blocksToReplace.setListElements(List.of(new MItemBlock(mcreator.getWorkspace(), "Blocks.STONE")));
		genPanel.add(blocksToReplace);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_chunk_count"),
				L10N.label("elementgui.block.gen_chunck_count")));
		genPanel.add(frequencyPerChunks);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_group_size"),
				L10N.label("elementgui.block.gen_group_size")));
		genPanel.add(oreCount);

		genPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/gen_height"),
				L10N.label("elementgui.block.gen_height")));
		genPanel.add(generateHeight);

		JComponent genPanelWithChunk = PanelUtils.westAndCenterElement(new JLabel(UIRES.get("chunk")),
				PanelUtils.totalCenterInPanel(genPanel), 25, 0);
		generationPanel.add("Center", PanelUtils.totalCenterInPanel(genPanelWithChunk));

		addPage(L10N.t("elementgui.common.page_visual"), visualPanel).validate(page1group);
		addPage(L10N.t("elementgui.common.page_properties"), propertiesPanel).validate(page2group);
		addPage(L10N.t("elementgui.common.page_generation"), generationPanel);

		if (!isEditingMode()) {
			name.setText(StringUtils.machineToReadableName(modElement.getName()));
			enableCreativeTab.setSelected(true);
		}
		updateTextureOptions();
		updateCreativeTab();
		refreshSpawnProperties();
	}

	private void updateTextureOptions() {
		if (normal.equals(renderType.getSelectedItem())) {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.ALL);
		} else {
			textures.setTextureFormat(BlockTexturesSelector.TextureFormat.SINGLE_TEXTURE);
		}
	}

	private void updateCreativeTab() {
		creativeTab.setEnabled(enableCreativeTab.isSelected());
	}

	private void refreshSpawnProperties() {
		boolean canSpawn = generateFeature.isSelected();

		generateHeight.setEnabled(canSpawn);
		oreCount.setEnabled(canSpawn);
		frequencyPerChunks.setEnabled(canSpawn);
		blocksToReplace.setEnabled(canSpawn);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Collections.singletonList(normal),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.BEDROCK).collect(Collectors.toList())));
	}

	@Override protected void openInEditingMode(BEBlock block) {
		textures.setTextures(block.texture, block.textureTop, block.textureLeft, block.textureFront, block.textureRight,
				block.textureBack);

		Model model = block.getModel();
		if (model != null)
			renderType.setSelectedItem(model);
		name.setText(block.name);
		enableCreativeTab.setSelected(block.enableCreativeTab);
		creativeTab.setSelectedItem(block.creativeTab);
		isHiddenInCommands.setSelected(block.isHiddenInCommands);
		hardness.setValue(block.hardness);
		resistance.setValue(block.resistance);
		soundOnStep.setSelectedItem(block.soundOnStep);
		lightEmission.setValue(block.lightEmission);
		customDrop.setBlock(block.customDrop);
		dropAmount.setValue(block.dropAmount);
		colorOnMap.setSelectedItem(block.colorOnMap);
		friction.setValue(block.friction);
		flammability.setValue(block.flammability);
		flammableDestroyChance.setValue(block.flammableDestroyChance);

		generateFeature.setSelected(block.generateFeature);
		blocksToReplace.setListElements(block.blocksToReplace);
		generateHeight.setMinValue(block.minGenerateHeight);
		generateHeight.setMaxValue(block.maxGenerateHeight);
		frequencyPerChunks.setValue(block.frequencyPerChunks);
		oreCount.setValue(block.oreCount);

		rotationMode.setSelectedIndex(block.rotationMode);
		renderMethod.setSelectedItem(block.renderMethod);
		tintMethod.setSelectedItem(block.tintMethod);

		updateTextureOptions();
		updateCreativeTab();
		refreshSpawnProperties();
	}

	@Override public BEBlock getElementFromGUI() {
		BEBlock block = new BEBlock(modElement);

		block.texture = textures.getTexture();
		block.textureTop = textures.getTextureTop();
		block.textureLeft = textures.getTextureLeft();
		block.textureFront = textures.getTextureFront();
		block.textureRight = textures.getTextureRight();

		block.textureBack = textures.getTextureBack();
		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		block.renderType = 10;
		if (model.getType() == Model.Type.BEDROCK)
			block.renderType = 2;
		block.customModelName = model.getReadableName();

		block.name = name.getText();
		block.enableCreativeTab = enableCreativeTab.isSelected();
		block.creativeTab = creativeTab.getSelectedItem().toString();
		block.isHiddenInCommands = isHiddenInCommands.isSelected();
		block.hardness = (double) hardness.getValue();
		block.resistance = (double) resistance.getValue();
		block.customDrop = customDrop.getBlock();
		block.dropAmount = (int) dropAmount.getValue();
		block.soundOnStep = new StepSound(mcreator.getWorkspace(), soundOnStep.getSelectedItem());
		block.lightEmission = (int) lightEmission.getValue();
		block.colorOnMap = colorOnMap.getSelectedItem().toString();
		block.flammability = (int) flammability.getValue();
		block.flammableDestroyChance = (int) flammableDestroyChance.getValue();
		block.friction = (double) friction.getValue();

		block.generateFeature = generateFeature.isSelected();
		block.frequencyPerChunks = (int) frequencyPerChunks.getValue();
		block.oreCount = (int) oreCount.getValue();
		block.minGenerateHeight = generateHeight.getIntMinValue();
		block.maxGenerateHeight = generateHeight.getIntMaxValue();
		block.blocksToReplace = blocksToReplace.getListElements();

		block.rotationMode = rotationMode.getSelectedIndex();
		block.renderMethod = (String) renderMethod.getSelectedItem();
		block.tintMethod = (String) tintMethod.getSelectedItem();

		return block;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-bedrock-block");
	}

}
