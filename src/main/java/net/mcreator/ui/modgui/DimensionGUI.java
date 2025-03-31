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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Dimension;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JMinMaxSpinner;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.*;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

public class DimensionGUI extends ModElementGUI<Dimension> {

	private final VTextField igniterName = new VTextField(19);
	private final TranslatedComboBox igniterRarity = new TranslatedComboBox(
			//@formatter:off
			Map.entry("COMMON", "elementgui.common.rarity_common"),
			Map.entry("UNCOMMON", "elementgui.common.rarity_uncommon"),
			Map.entry("RARE", "elementgui.common.rarity_rare"),
			Map.entry("EPIC", "elementgui.common.rarity_epic")
			//@formatter:on
	);

	private StringListProcedureSelector specialInformation;

	private TextureSelectionButton portalTexture;
	private TextureSelectionButton texture;

	private MCItemHolder portalFrame;
	private MCItemHolder mainFillerBlock;
	private MCItemHolder fluidBlock;
	private final JSpinner seaLevel = new JSpinner(new SpinnerNumberModel(63, -1024, 1024, 1));
	private final JCheckBox generateOreVeins = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox generateAquifers = L10N.checkbox("elementgui.common.enable");
	private final JSpinner horizontalNoiseSize = new JSpinner(
			new SpinnerListModel(List.of(1, 2, 4))); // Setting these values to 3 can cause crashes
	private final JSpinner verticalNoiseSize = new JSpinner(new SpinnerListModel(List.of(1, 2, 4)));

	private final JCheckBox canRespawnHere = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox bedWorks = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox hasFog = L10N.checkbox("elementgui.common.enable");
	private final JSpinner ambientLight = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));
	private final JCheckBox doesWaterVaporize = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox hasSkyLight = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox imitateOverworldBehaviour = L10N.checkbox("elementgui.common.enable");
	private final JSpinner coordinateScale = new JSpinner(new SpinnerNumberModel(1, 0.01, 1000, 0.01));
	private final VTextField infiniburnTag = new VTextField(24);
	private final JCheckBox hasFixedTime = L10N.checkbox("elementgui.common.enable");
	private final JSpinner fixedTimeValue = new JSpinner(new SpinnerNumberModel(0, 0, 24000, 1));
	private final JCheckBox piglinSafe = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox hasRaids = L10N.checkbox("elementgui.common.enable");
	private final JMinMaxSpinner monsterSpawnLightLimit = new JMinMaxSpinner(0, 7, 0, 15, 1).allowEqualValues();
	private final JSpinner monsterSpawnBlockLightLimit = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));

	private final TranslatedComboBox defaultEffects = new TranslatedComboBox(
			//@formatter:off
			Map.entry("overworld", "elementgui.dimension.effects_overworld"),
			Map.entry("the_nether", "elementgui.dimension.effects_the_nether"),
			Map.entry("the_end", "elementgui.dimension.effects_the_end")
			//@formatter:on
	);
	private final JCheckBox useCustomEffects = L10N.checkbox("elementgui.common.enable");
	private final JCheckBox hasClouds = L10N.checkbox("elementgui.common.enable");
	private final JSpinner cloudHeight = new JSpinner(new SpinnerNumberModel(192, -4096, 4096, 0.1));
	private final JComboBox<String> skyType = new JComboBox<>(new String[] { "NONE", "NORMAL", "END" });
	private final JCheckBox sunHeightAffectsFog = L10N.checkbox("elementgui.common.enable");

	private final JCheckBox enablePortal = L10N.checkbox("elementgui.dimension.enable_portal");
	private final JCheckBox enableIgniter = L10N.checkbox("elementgui.common.enable");

	private final SoundSelector portalSound = new SoundSelector(mcreator);
	private final JColor airColor = new JColor(mcreator, true, false);

	private final DataListComboBox portalParticles = new DataListComboBox(mcreator);

	private final JComboBox<String> worldGenType = new JComboBox<>(
			new String[] { "Normal world gen", "Nether like gen", "End like gen" });

	private BiomeListField biomesInDimension;

	private final TabListField creativeTabs = new TabListField(mcreator);

	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));

	private ProcedureSelector portalMakeCondition;
	private ProcedureSelector portalUseCondition;

	private ProcedureSelector whenPortaTriggerlUsed;
	private ProcedureSelector onPortalTickUpdate;
	private ProcedureSelector onPlayerEntersDimension;
	private ProcedureSelector onPlayerLeavesDimension;

	private final ValidationGroup portalPageGroup = new ValidationGroup();
	private final ValidationGroup generationPageGroup = new ValidationGroup();

	public DimensionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		whenPortaTriggerlUsed = new ProcedureSelector(this.withEntry("dimension/when_portal_used"), mcreator,
				L10N.t("elementgui.dimension.event_portal_trigger_used"),
				VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE, Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeReturnValueOptional();
		onPortalTickUpdate = new ProcedureSelector(this.withEntry("dimension/on_portal_tick_update"), mcreator,
				L10N.t("elementgui.dimension.event_portal_tick_update"),
				Dependency.fromString("x:number/y:number/z:number/world:world/blockstate:blockstate"));
		onPlayerEntersDimension = new ProcedureSelector(this.withEntry("dimension/when_player_enters"), mcreator,
				L10N.t("elementgui.dimension.event_player_enters"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onPlayerLeavesDimension = new ProcedureSelector(this.withEntry("dimension/when_player_leaves"), mcreator,
				L10N.t("elementgui.dimension.event_player_leaves"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		portalMakeCondition = new ProcedureSelector(this.withEntry("dimension/condition_portal_make"), mcreator,
				L10N.t("elementgui.dimension.event_can_make_portal"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack")).makeInline();
		portalUseCondition = new ProcedureSelector(this.withEntry("dimension/condition_portal_use"), mcreator,
				L10N.t("elementgui.dimension.event_can_travel_through_portal"), VariableTypeLoader.BuiltInTypes.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity")).makeInline();
		specialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));

		worldGenType.setRenderer(new ItemTexturesComboBoxRenderer());
		worldGenType.addActionListener(e -> updateWorldgenSettings());
		biomesInDimension = new BiomeListField(mcreator);

		portalParticles.setPrototypeDisplayValue(new DataListEntry.Dummy("XXXXXXXXXXXXXXXXXXX"));

		portalFrame = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		mainFillerBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		fluidBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		JPanel propertiesPage = new JPanel(new BorderLayout(10, 10));
		JPanel generationPage = new JPanel(new BorderLayout(10, 10));
		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		// Dimension type settings
		JPanel dimensionTypeSettings = new JPanel(new GridLayout(10, 2, 15, 2));
		dimensionTypeSettings.setOpaque(false);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/can_use_beds"),
				L10N.label("elementgui.dimension.can_use_beds")));
		dimensionTypeSettings.add(bedWorks);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/imitate_overworld"),
				L10N.label("elementgui.dimension.imitate_overworld_behaviour")));
		dimensionTypeSettings.add(imitateOverworldBehaviour);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/can_use_respawn_anchor"),
				L10N.label("elementgui.dimension.can_use_respawn_anchor")));
		dimensionTypeSettings.add(canRespawnHere);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_skylight"),
				L10N.label("elementgui.dimension.has_sky_light")));
		dimensionTypeSettings.add(hasSkyLight);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/ambient_light"),
				L10N.label("elementgui.dimension.ambient_light")));
		dimensionTypeSettings.add(ambientLight);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/does_water_vaporize"),
				L10N.label("elementgui.dimension.does_water_vaporize")));
		dimensionTypeSettings.add(doesWaterVaporize);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_fixed_time"),
				L10N.label("elementgui.dimension.has_fixed_time")));
		dimensionTypeSettings.add(hasFixedTime);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/fixed_time_value"),
				L10N.label("elementgui.dimension.fixed_time_value")));
		dimensionTypeSettings.add(fixedTimeValue);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/coordinate_scale"),
				L10N.label("elementgui.dimension.coordinate_scale")));
		dimensionTypeSettings.add(coordinateScale);

		dimensionTypeSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/infiniburn_tag"),
				L10N.label("elementgui.dimension.infiniburn_tag")));
		dimensionTypeSettings.add(infiniburnTag);

		dimensionTypeSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.dimension.dimension_type_settings"), TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JPanel dimensionEffects = new JPanel(new GridLayout(8, 2, 15, 2));
		dimensionEffects.setOpaque(false);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/default_effects"),
				L10N.label("elementgui.dimension.default_effects")));
		dimensionEffects.add(defaultEffects);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/use_custom_effects"),
				L10N.label("elementgui.dimension.use_custom_effects")));
		dimensionEffects.add(useCustomEffects);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_clouds"),
				L10N.label("elementgui.dimension.has_clouds")));
		dimensionEffects.add(hasClouds);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/cloud_height"),
				L10N.label("elementgui.dimension.cloud_height")));
		dimensionEffects.add(cloudHeight);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/sky_type"),
				L10N.label("elementgui.dimension.sky_type")));
		dimensionEffects.add(skyType);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/fog_color"),
				L10N.label("elementgui.dimension.fog_air_color")));
		dimensionEffects.add(airColor);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/sun_height_affects_fog"),
				L10N.label("elementgui.dimension.sun_height_affects_fog")));
		dimensionEffects.add(sunHeightAffectsFog);

		dimensionEffects.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_fog"),
				L10N.label("elementgui.dimension.has_fog")));
		dimensionEffects.add(hasFog);

		dimensionEffects.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.dimension.dimension_effects"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		JPanel mobSettings = new JPanel(new GridLayout(4, 2, 15, 2));
		mobSettings.setOpaque(false);

		mobSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/piglin_safe"),
				L10N.label("elementgui.dimension.piglin_safe")));
		mobSettings.add(piglinSafe);

		mobSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_raids"),
				L10N.label("elementgui.dimension.has_raids")));
		mobSettings.add(hasRaids);

		mobSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/spawning_light_limit"),
				L10N.label("elementgui.dimension.monster_spawn_light_limit")));
		mobSettings.add(monsterSpawnLightLimit);

		mobSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/spawning_block_light_limit"),
				L10N.label("elementgui.dimension.monster_spawn_block_light_limit")));
		mobSettings.add(monsterSpawnBlockLightLimit);

		monsterSpawnBlockLightLimit.setPreferredSize(new java.awt.Dimension(0, 36));

		mobSettings.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.dimension.mob_settings"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont().deriveFont(12.0f), Theme.current().getForegroundColor()));

		bedWorks.setOpaque(false);
		hasSkyLight.setOpaque(false);
		imitateOverworldBehaviour.setOpaque(false);
		piglinSafe.setOpaque(false);
		hasRaids.setOpaque(false);
		canRespawnHere.setOpaque(false);
		doesWaterVaporize.setOpaque(false);
		hasFixedTime.setOpaque(false);
		hasFixedTime.addActionListener(e -> fixedTimeValue.setEnabled(hasFixedTime.isSelected()));
		fixedTimeValue.setEnabled(false);
		if (!isEditingMode()) {
			bedWorks.setSelected(true);
			imitateOverworldBehaviour.setSelected(true);
			hasSkyLight.setSelected(true);
			hasRaids.setSelected(true);
			infiniburnTag.setText("minecraft:infiniburn_overworld");
		}
		ambientLight.setPreferredSize(new java.awt.Dimension(-1, 36));

		useCustomEffects.setOpaque(false);
		useCustomEffects.addActionListener(e -> updateDimensionEffectSettings(useCustomEffects.isSelected()));
		hasClouds.setOpaque(false);
		hasClouds.addActionListener(
				e -> cloudHeight.setEnabled(useCustomEffects.isSelected() && hasClouds.isSelected()));
		airColor.setOpaque(false);
		airColor.setPreferredSize(new java.awt.Dimension(240, 36));
		sunHeightAffectsFog.setOpaque(false);
		hasFog.setOpaque(false);

		if (!isEditingMode()) {
			// Currently only Java based mods support custom dimension effects
			useCustomEffects.setSelected(modElement.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
					== GeneratorFlavor.BaseLanguage.JAVA);
			hasClouds.setSelected(true);
			updateDimensionEffectSettings(useCustomEffects.isSelected());
		}

		propertiesPage.add("Center", PanelUtils.totalCenterInPanel(
				PanelUtils.westAndEastElement(PanelUtils.pullElementUp(dimensionTypeSettings),
						PanelUtils.pullElementUp(PanelUtils.northAndCenterElement(dimensionEffects, mobSettings)))));
		propertiesPage.setOpaque(false);

		// Dimension generation settings
		JPanel insid = new JPanel(new BorderLayout(20, 20));

		insid.add("East", PanelUtils.northAndCenterElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.dimension.world_gen_type"), worldGenType),
				PanelUtils.join(new JLabel(UIRES.get("dimension_types")))));

		JPanel worldgenSettings = new JPanel(new GridLayout(8, 2, 2, 2));
		worldgenSettings.setOpaque(false);

		biomesInDimension.setPreferredSize(new java.awt.Dimension(300, 42));

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/main_filler_block"),
				L10N.label("elementgui.dimension.main_filler_block"), new Color(0x2980b9)));
		worldgenSettings.add(PanelUtils.join(mainFillerBlock));

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/fluid_block"),
				L10N.label("elementgui.dimension.fluid_block"), new Color(0xB8E700)));
		worldgenSettings.add(PanelUtils.join(fluidBlock));

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/biomes"),
				L10N.label("elementgui.dimension.biomes_in")));
		worldgenSettings.add(biomesInDimension);

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/sea_level"),
				L10N.label("elementgui.dimension.sea_level")));
		worldgenSettings.add(seaLevel);

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/generate_ore_veins"),
				L10N.label("elementgui.dimension.generate_ore_veins")));
		worldgenSettings.add(generateOreVeins);

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/generate_aquifers"),
				L10N.label("elementgui.dimension.generate_aquifers")));
		worldgenSettings.add(generateAquifers);

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/horizontal_noise_size"),
				L10N.label("elementgui.dimension.horizontal_noise_size")));
		worldgenSettings.add(horizontalNoiseSize);

		worldgenSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/vertical_noise_size"),
				L10N.label("elementgui.dimension.vertical_noise_size")));
		worldgenSettings.add(verticalNoiseSize);

		insid.setOpaque(false);
		generateOreVeins.setOpaque(false);
		generateOreVeins.setSelected(true);
		generateAquifers.setOpaque(false);
		generateAquifers.setSelected(true);
		if (!this.isEditingMode())
			verticalNoiseSize.setValue(2);

		insid.add("Center", PanelUtils.totalCenterInPanel(worldgenSettings));
		generationPage.add("Center", PanelUtils.totalCenterInPanel(insid));

		generationPage.setOpaque(false);

		portalTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));

		portalTexture.setOpaque(false);
		texture.setOpaque(false);
		enablePortal.setOpaque(false);
		enableIgniter.setOpaque(false);

		// Currently only Java based mods support dimension portals
		enablePortal.setSelected(modElement.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA);
		enableIgniter.setSelected(modElement.getGeneratorConfiguration().getGeneratorFlavor().getBaseLanguage()
				== GeneratorFlavor.BaseLanguage.JAVA);

		JPanel proper = new JPanel(new GridLayout(4, 2, 5, 2));
		proper.setOpaque(false);

		JPanel proper22 = new JPanel(new GridLayout(3, 2, 5, 2));
		proper22.setOpaque(false);

		proper.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_particles"),
				L10N.label("elementgui.dimension.portal_particles")));
		proper.add(portalParticles);

		proper.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_sound"),
				L10N.label("elementgui.dimension.portal_sound")));
		proper.add(portalSound);

		proper.add(HelpUtils.wrapWithHelpButton(this.withEntry("block/luminance"),
				L10N.label("elementgui.dimension.portal_luminance")));
		proper.add(luminance);

		proper.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_frame_block"),
				L10N.label("elementgui.dimension.portal_frame_block")));
		proper.add(PanelUtils.join(portalFrame));

		proper22.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.dimension.portal_igniter_name")));
		proper22.add(igniterName);

		proper22.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		proper22.add(igniterRarity);

		proper22.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tabs"),
				L10N.label("elementgui.dimension.portal_igniter_tabs")));
		proper22.add(creativeTabs);

		creativeTabs.setPreferredSize(new java.awt.Dimension(0, 42));

		portalSound.setText("block.portal.ambient");

		portalParticles.setFont(portalParticles.getFont().deriveFont(16.0f));

		JPanel igniterPanel = new JPanel(new BorderLayout(5, 5));
		igniterPanel.setOpaque(false);

		igniterPanel.add("North", PanelUtils.gridElements(1, 2, 5, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("dimension/enable_igniter"),
						L10N.label("elementgui.dimension.enable_new_igniter")), enableIgniter));

		igniterPanel.add("Center", PanelUtils.gridElements(1, 2,
				HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_igniter_texture"),
						L10N.label("elementgui.dimension.portal_igniter_texture")), PanelUtils.join(texture)));

		JPanel conditions = new JPanel(new GridLayout(2, 1, 5, 2));
		conditions.setOpaque(false);
		conditions.add(specialInformation);
		conditions.add(portalMakeCondition);

		igniterPanel.add("South", PanelUtils.centerAndSouthElement(proper22, conditions, 2, 2));

		igniterPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.dimension.portal_igniter_properties"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JPanel propertiesPanel = new JPanel(new BorderLayout(5, 2));
		propertiesPanel.setOpaque(false);
		propertiesPanel.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(
				PanelUtils.gridElements(1, 2, HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_texture"),
						L10N.label("elementgui.dimension.portal_block_texture")), PanelUtils.join(portalTexture)),
				proper)));
		propertiesPanel.add("South", portalUseCondition);

		propertiesPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.dimension.portal_properties"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));

		JPanel portalPanelMain = new JPanel(new BorderLayout(0, 0));
		portalPanelMain.setOpaque(false);
		portalPanelMain.add("West", propertiesPanel);
		portalPanelMain.add("East", PanelUtils.pullElementUp(igniterPanel));

		JPanel portalPanel = new JPanel(new BorderLayout(5, 2));
		portalPanel.setOpaque(false);
		portalPanel.add("North", PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(this.withEntry("dimension/enable_portal"),
						L10N.label("elementgui.dimension.enable_dimension_portal")), enablePortal));
		portalPanel.add("Center", portalPanelMain);

		pane2.setOpaque(false);

		pane2.add(PanelUtils.totalCenterInPanel(portalPanel));

		ComponentUtils.deriveFont(igniterName, 16);

		enablePortal.addActionListener(e -> updatePortalElements());
		enableIgniter.addActionListener(e -> updateIgniterElements(enableIgniter.isSelected()));

		JPanel events = new JPanel(new GridLayout(1, 4, 5, 5));
		events.add(whenPortaTriggerlUsed);
		events.add(onPortalTickUpdate);
		events.add(onPlayerEntersDimension);
		events.add(onPlayerLeavesDimension);
		events.setOpaque(false);
		pane5.add(PanelUtils.totalCenterInPanel(events));
		pane5.setOpaque(false);

		infiniburnTag.setValidator(
				new ResourceLocationValidator<>(L10N.t("elementgui.dimension.infiniburn_validator"), infiniburnTag,
						true));
		infiniburnTag.enableRealtimeValidation();

		igniterName.setValidator(new ConditionalTextFieldValidator(igniterName,
				L10N.t("elementgui.dimension.error_portal_igniter_needs_name"), enableIgniter, true));
		portalTexture.setValidator(new TileHolderValidator(portalTexture, enablePortal));
		texture.setValidator(new TileHolderValidator(texture, enableIgniter));
		portalFrame.setValidator(new MCItemHolderValidator(portalFrame, enablePortal));
		igniterName.enableRealtimeValidation();

		portalPageGroup.addValidationElement(igniterName);
		portalPageGroup.addValidationElement(portalTexture);
		portalPageGroup.addValidationElement(texture);
		portalPageGroup.addValidationElement(portalFrame);

		biomesInDimension.setValidator(
				new ItemListFieldValidator(biomesInDimension, L10N.t("elementgui.dimension.error_select_biome")));
		mainFillerBlock.setValidator(new MCItemHolderValidator(mainFillerBlock).considerAirAsEmpty());
		fluidBlock.setValidator(new MCItemHolderValidator(fluidBlock));

		generationPageGroup.addValidationElement(biomesInDimension);
		generationPageGroup.addValidationElement(mainFillerBlock);
		generationPageGroup.addValidationElement(fluidBlock);

		addPage(L10N.t("elementgui.dimension.page_generation"), generationPage).validate(generationPageGroup);
		addPage(L10N.t("elementgui.common.page_properties"), propertiesPage).validate(infiniburnTag);
		addPage(L10N.t("elementgui.dimension.page_portal"), pane2).validate(portalPageGroup);
		addPage(L10N.t("elementgui.common.page_triggers"), pane5);

		if (!isEditingMode()) {
			creativeTabs.setListElements(List.of(new TabEntry(mcreator.getWorkspace(), "TOOLS")));

			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			igniterName.setText(readableNameFromModElement + " Portal Igniter");
		}
	}

	private void updatePortalElements() {
		portalFrame.setEnabled(enablePortal.isSelected());
		portalParticles.setEnabled(enablePortal.isSelected());
		portalSound.setEnabled(enablePortal.isSelected());
		luminance.setEnabled(enablePortal.isSelected());
		portalTexture.setEnabled(enablePortal.isSelected());
		portalUseCondition.setEnabled(enablePortal.isSelected());
		enableIgniter.setEnabled(enablePortal.isSelected());
		updateIgniterElements(enablePortal.isSelected() && enableIgniter.isSelected());
	}

	private void updateIgniterElements(boolean enabled) {
		igniterName.setEnabled(enabled);
		creativeTabs.setEnabled(enabled);
		specialInformation.setEnabled(enabled);
		texture.setEnabled(enabled);
		portalMakeCondition.setEnabled(enabled);
		igniterRarity.setEnabled(enabled);
	}

	private void updateDimensionEffectSettings(boolean hasCustomEffects) {
		defaultEffects.setEnabled(!hasCustomEffects);
		hasClouds.setEnabled(hasCustomEffects);
		cloudHeight.setEnabled(hasCustomEffects && hasClouds.isSelected());
		skyType.setEnabled(hasCustomEffects);
		airColor.setEnabled(hasCustomEffects);
		sunHeightAffectsFog.setEnabled(hasCustomEffects);
		hasFog.setEnabled(hasCustomEffects);
	}

	private void updateWorldgenSettings() {
		String genType = (String) worldGenType.getSelectedItem();
		if ("Normal world gen".equals(genType)) {
			generateAquifers.setEnabled(true);
			generateOreVeins.setEnabled(true);
			if (!isEditingMode()) {
				seaLevel.setValue(63);
				horizontalNoiseSize.setValue(1);
				verticalNoiseSize.setValue(2);
			}
		} else {
			generateAquifers.setEnabled(false);
			generateOreVeins.setEnabled(false);
			if (!isEditingMode()) {
				if ("Nether like gen".equals(genType)) {
					seaLevel.setValue(32);
					horizontalNoiseSize.setValue(1);
					verticalNoiseSize.setValue(2);
				} else {
					seaLevel.setValue(0);
					horizontalNoiseSize.setValue(2);
					verticalNoiseSize.setValue(1);
				}
			}
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		whenPortaTriggerlUsed.refreshListKeepSelected();
		onPortalTickUpdate.refreshListKeepSelected();
		onPlayerEntersDimension.refreshListKeepSelected();
		onPlayerLeavesDimension.refreshListKeepSelected();

		portalMakeCondition.refreshListKeepSelected();
		portalUseCondition.refreshListKeepSelected();
		specialInformation.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(portalParticles, ElementUtil.loadAllParticles(mcreator.getWorkspace()),
				new DataListEntry.Dummy("PORTAL"));
	}

	@Override public void openInEditingMode(Dimension dimension) {
		portalFrame.setBlock(dimension.portalFrame);
		mainFillerBlock.setBlock(dimension.mainFillerBlock);
		fluidBlock.setBlock(dimension.fluidBlock);
		seaLevel.setValue(dimension.seaLevel);
		generateOreVeins.setSelected(dimension.generateOreVeins);
		generateAquifers.setSelected(dimension.generateAquifers);
		horizontalNoiseSize.setValue(dimension.horizontalNoiseSize);
		verticalNoiseSize.setValue(dimension.verticalNoiseSize);
		portalSound.setSound(dimension.portalSound);
		enableIgniter.setSelected(dimension.enableIgniter);
		igniterName.setText(dimension.igniterName);
		igniterRarity.setSelectedItem(dimension.igniterRarity);
		specialInformation.setSelectedProcedure(dimension.specialInformation);
		portalTexture.setTexture(dimension.portalTexture);
		texture.setTexture(dimension.texture);
		worldGenType.setSelectedItem(dimension.worldGenType);
		bedWorks.setSelected(dimension.bedWorks);
		creativeTabs.setListElements(dimension.creativeTabs);
		portalParticles.setSelectedItem(dimension.portalParticles);
		biomesInDimension.setListElements(dimension.biomesInDimension);
		airColor.setColor(dimension.airColor);
		defaultEffects.setSelectedItem(dimension.defaultEffects);
		useCustomEffects.setSelected(dimension.useCustomEffects);
		hasClouds.setSelected(dimension.hasClouds);
		cloudHeight.setValue(dimension.cloudHeight);
		skyType.setSelectedItem(dimension.skyType);
		sunHeightAffectsFog.setSelected(dimension.sunHeightAffectsFog);
		canRespawnHere.setSelected(dimension.canRespawnHere);
		hasFog.setSelected(dimension.hasFog);
		ambientLight.setValue(dimension.ambientLight);
		doesWaterVaporize.setSelected(dimension.doesWaterVaporize);
		imitateOverworldBehaviour.setSelected(dimension.imitateOverworldBehaviour);
		hasSkyLight.setSelected(dimension.hasSkyLight);
		hasFixedTime.setSelected(dimension.hasFixedTime);
		fixedTimeValue.setValue(dimension.fixedTimeValue);
		coordinateScale.setValue(dimension.coordinateScale);
		infiniburnTag.setText(dimension.infiniburnTag);
		piglinSafe.setSelected(dimension.piglinSafe);
		hasRaids.setSelected(dimension.hasRaids);
		monsterSpawnLightLimit.setMinValue(dimension.minMonsterSpawnLightLimit);
		monsterSpawnLightLimit.setMaxValue(dimension.maxMonsterSpawnLightLimit);
		monsterSpawnBlockLightLimit.setValue(dimension.monsterSpawnBlockLightLimit);
		enablePortal.setSelected(dimension.enablePortal);
		whenPortaTriggerlUsed.setSelectedProcedure(dimension.whenPortaTriggerlUsed);
		onPortalTickUpdate.setSelectedProcedure(dimension.onPortalTickUpdate);
		onPlayerEntersDimension.setSelectedProcedure(dimension.onPlayerEntersDimension);
		onPlayerLeavesDimension.setSelectedProcedure(dimension.onPlayerLeavesDimension);
		luminance.setValue(dimension.portalLuminance);
		portalMakeCondition.setSelectedProcedure(dimension.portalMakeCondition);
		portalUseCondition.setSelectedProcedure(dimension.portalUseCondition);

		fixedTimeValue.setEnabled(dimension.hasFixedTime);
		updateWorldgenSettings();
		updateDimensionEffectSettings(dimension.useCustomEffects);
		updatePortalElements();
	}

	@Override public Dimension getElementFromGUI() {
		Dimension dimension = new Dimension(modElement);
		dimension.texture = texture.getTextureHolder();
		dimension.portalTexture = portalTexture.getTextureHolder();
		dimension.portalParticles = new Particle(mcreator.getWorkspace(), portalParticles.getSelectedItem());
		dimension.creativeTabs = creativeTabs.getListElements();
		dimension.portalSound = portalSound.getSound();
		dimension.biomesInDimension = biomesInDimension.getListElements();
		dimension.airColor = airColor.getColor();
		dimension.defaultEffects = defaultEffects.getSelectedItem();
		dimension.useCustomEffects = useCustomEffects.isSelected();
		dimension.hasClouds = hasClouds.isSelected();
		dimension.cloudHeight = (double) cloudHeight.getValue();
		dimension.skyType = (String) skyType.getSelectedItem();
		dimension.sunHeightAffectsFog = sunHeightAffectsFog.isSelected();
		dimension.canRespawnHere = canRespawnHere.isSelected();
		dimension.hasFog = hasFog.isSelected();
		dimension.ambientLight = (double) ambientLight.getValue();
		dimension.imitateOverworldBehaviour = imitateOverworldBehaviour.isSelected();
		dimension.hasSkyLight = hasSkyLight.isSelected();
		dimension.hasFixedTime = hasFixedTime.isSelected();
		dimension.fixedTimeValue = (int) fixedTimeValue.getValue();
		dimension.coordinateScale = (double) coordinateScale.getValue();
		dimension.infiniburnTag = infiniburnTag.getText();
		dimension.piglinSafe = piglinSafe.isSelected();
		dimension.hasRaids = hasRaids.isSelected();
		dimension.minMonsterSpawnLightLimit = monsterSpawnLightLimit.getIntMinValue();
		dimension.maxMonsterSpawnLightLimit = monsterSpawnLightLimit.getIntMaxValue();
		dimension.monsterSpawnBlockLightLimit = (int) monsterSpawnBlockLightLimit.getValue();
		dimension.enablePortal = enablePortal.isSelected();
		dimension.portalFrame = portalFrame.getBlock();
		dimension.enableIgniter = enableIgniter.isSelected();
		dimension.igniterName = igniterName.getText();
		dimension.igniterRarity = igniterRarity.getSelectedItem();
		dimension.specialInformation = specialInformation.getSelectedProcedure();
		dimension.worldGenType = (String) worldGenType.getSelectedItem();
		dimension.bedWorks = bedWorks.isSelected();
		dimension.mainFillerBlock = mainFillerBlock.getBlock();
		dimension.fluidBlock = fluidBlock.getBlock();
		dimension.seaLevel = (int) seaLevel.getValue();
		dimension.generateOreVeins = generateOreVeins.isSelected();
		dimension.generateAquifers = generateAquifers.isSelected();
		dimension.horizontalNoiseSize = (int) horizontalNoiseSize.getValue();
		dimension.verticalNoiseSize = (int) verticalNoiseSize.getValue();
		dimension.whenPortaTriggerlUsed = whenPortaTriggerlUsed.getSelectedProcedure();
		dimension.onPortalTickUpdate = onPortalTickUpdate.getSelectedProcedure();
		dimension.onPlayerEntersDimension = onPlayerEntersDimension.getSelectedProcedure();
		dimension.onPlayerLeavesDimension = onPlayerLeavesDimension.getSelectedProcedure();
		dimension.portalLuminance = (int) luminance.getValue();
		dimension.doesWaterVaporize = doesWaterVaporize.isSelected();
		dimension.portalMakeCondition = portalMakeCondition.getSelectedProcedure();
		dimension.portalUseCondition = portalUseCondition.getSelectedProcedure();
		return dimension;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-dimension");
	}

}
