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

import net.mcreator.blockly.Dependency;
import net.mcreator.element.parts.Particle;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Dimension;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.minecraft.*;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.ConditionalTextFieldValidator;
import net.mcreator.ui.validation.validators.ItemListFieldValidator;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElementType;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class DimensionGUI extends ModElementGUI<Dimension> {

	private final VTextField igniterName = new VTextField(14);

	private TextureHolder portalTexture;
	private TextureHolder texture;

	private MCItemHolder portalFrame;
	private MCItemHolder mainFillerBlock;
	private MCItemHolder fluidBlock;

	private final JCheckBox canRespawnHere = new JCheckBox("Can player respawn here");
	private final JCheckBox hasFog = new JCheckBox("Has fog everywhere");
	private final JCheckBox isDark = new JCheckBox("Disable global light source");
	private final JCheckBox doesWaterVaporize = new JCheckBox("Does water vaporize");

	private final JCheckBox hasSkyLight = new JCheckBox("Has sky light (from the sun)");
	private final JCheckBox imitateOverworldBehaviour = new JCheckBox("Imitate overworld behaviour");

	private final JCheckBox enablePortal = new JCheckBox("Enable portal");

	private final JCheckBox hasWeather = new JCheckBox("Enable weather");

	private final SoundSelector portalSound = new SoundSelector(mcreator);
	private final JColor airColor = new JColor(mcreator);

	private final JComboBox<String> portalParticles = new JComboBox<>(ElementUtil.loadParticles());

	private final JComboBox<String> worldGenType = new JComboBox<>(
			new String[] { "Normal world gen", "Nether like gen", "End like gen" });

	private final JComboBox<String> sleepResult = new JComboBox<>(new String[] { "ALLOW", "DENY", "BED_EXPLODES" });

	private BiomeListField biomesInDimension;

	private final DataListComboBox igniterTab = new DataListComboBox(mcreator);

	private final JSpinner luminance = new JSpinner(new SpinnerNumberModel(0.00, 0, 1, 0.01));

	private ProcedureSelector portalMakeCondition;
	private ProcedureSelector portalUseCondition;

	private ProcedureSelector whenPortaTriggerlUsed;
	private ProcedureSelector onPortalTickUpdate;
	private ProcedureSelector onPlayerEntersDimension;
	private ProcedureSelector onPlayerLeavesDimension;

	private final ValidationGroup page1group = new ValidationGroup();
	private final ValidationGroup page2group = new ValidationGroup();

	public DimensionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		whenPortaTriggerlUsed = new ProcedureSelector(this.withEntry("dimension/when_portal_used"), mcreator,
				"When portal trigger used",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onPortalTickUpdate = new ProcedureSelector(this.withEntry("dimension/on_portal_tick_update"), mcreator,
				"On portal tick update", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onPlayerEntersDimension = new ProcedureSelector(this.withEntry("dimension/when_player_enters"), mcreator,
				"When player enters dimension",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));
		onPlayerLeavesDimension = new ProcedureSelector(this.withEntry("dimension/when_player_leaves"), mcreator,
				"When player leaves dimension",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		portalMakeCondition = new ProcedureSelector(this.withEntry("dimension/condition_portal_make"), mcreator,
				"Can make portal", VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		portalUseCondition = new ProcedureSelector(this.withEntry("dimension/condition_portal_use"), mcreator,
				"Can travel through portal", VariableElementType.LOGIC,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		worldGenType.setRenderer(new ItemTexturesComboBoxRenderer());
		biomesInDimension = new BiomeListField(mcreator);

		portalParticles.setSelectedItem("PORTAL");

		hasWeather.setSelected(true);

		portalFrame = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		mainFillerBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
		fluidBlock = new MCItemHolder(mcreator, ElementUtil::loadBlocks);

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		JPanel insid = new JPanel(new BorderLayout(20, 20));

		insid.add("East", PanelUtils.northAndCenterElement(
				PanelUtils.join(FlowLayout.LEFT, new JLabel("      World gen type:"), worldGenType),
				PanelUtils.join(new JLabel(UIRES.get("dimension_types")))));

		JPanel proper2 = new JPanel(new GridLayout(9, 2, 3, 3));
		proper2.setOpaque(false);

		airColor.setOpaque(false);
		airColor.setColor(new Color(0.753f, 0.847f, 1f));

		hasWeather.setOpaque(false);
		canRespawnHere.setOpaque(false);
		hasFog.setOpaque(false);
		doesWaterVaporize.setOpaque(false);

		proper2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/biomes"), new JLabel("Biomes in this dimension:")));
		proper2.add(biomesInDimension);

		biomesInDimension.setPreferredSize(new java.awt.Dimension(300, 42));

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/main_filler_block"),
				new JLabel("<html>Main filler block:<br><small>Normal, Nether, End"), new Color(0x2980b9)));
		proper2.add(PanelUtils.join(mainFillerBlock));

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/fluid_block"),
				new JLabel("<html>Fluid block:<br><small>Normal, Nether, End"), new Color(0xB8E700)));
		proper2.add(PanelUtils.join(fluidBlock));

		proper2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/sleep_result"), new JLabel("Sleep attempt result:")));
		proper2.add(PanelUtils.join(sleepResult));

		proper2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("dimension/imitate_overworld"), imitateOverworldBehaviour));
		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/can_respawn"), canRespawnHere));

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_fog"), hasFog));
		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_weather"), hasWeather));

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/has_skylight"), hasSkyLight));
		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/is_dark"), isDark));

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/does_water_vaporize"), doesWaterVaporize));
		proper2.add(new JLabel());

		proper2.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/fog_color"), new JLabel("Fog/air color:")));
		proper2.add(PanelUtils.join(airColor));

		isDark.setOpaque(false);
		hasSkyLight.setOpaque(false);
		imitateOverworldBehaviour.setOpaque(false);

		insid.setOpaque(false);

		insid.add("Center", PanelUtils.totalCenterInPanel(proper2));
		pane3.add("Center", PanelUtils.totalCenterInPanel(insid));

		pane3.setOpaque(false);

		portalTexture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Item"));

		portalTexture.setOpaque(false);
		texture.setOpaque(false);
		enablePortal.setOpaque(false);

		enablePortal.setSelected(true);

		JPanel proper = new JPanel(new GridLayout(7, 2, 5, 2));

		JPanel proper22 = new JPanel(new GridLayout(2, 2, 5, 2));
		proper.setOpaque(false);
		proper22.setOpaque(false);

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/enable_portal"), new JLabel("Enable dimension portal:")));
		proper.add(PanelUtils.join(enablePortal));

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/portal_frame_block"), new JLabel("Portal frame block:")));
		proper.add(PanelUtils.join(portalFrame));

		proper.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_particles"),
				new JLabel("Portal frame block particles:")));
		proper.add(portalParticles);

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/portal_sound"), new JLabel("Portal frame block sound:")));
		proper.add(portalSound);

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("block/luminance"), new JLabel("Portal frame block luminance:")));
		proper.add(luminance);

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Portal igniter item name:")));
		proper.add(igniterName);

		proper.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Portal igniter item tab:")));
		proper.add(igniterTab);

		proper22.add(HelpUtils.wrapWithHelpButton(this.withEntry("dimension/portal_igniter_texture"),
				new JLabel("Portal igniter item texture:")));
		proper22.add(PanelUtils.join(texture));

		proper22.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("dimension/portal_texture"), new JLabel("Portal block texture:")));
		proper22.add(PanelUtils.join(portalTexture));

		portalSound.setText("block.portal.ambient");

		portalParticles.setFont(portalParticles.getFont().deriveFont(16.0f));

		JPanel dsg = new JPanel(new BorderLayout(5, 5));

		dsg.setOpaque(false);

		dsg.add("North", proper);
		dsg.add("Center", proper22);
		dsg.add("South", PanelUtils.join(FlowLayout.LEFT, portalMakeCondition, portalUseCondition));

		pane2.setOpaque(false);

		pane2.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(dsg)));

		ComponentUtils.deriveFont(igniterName, 16);

		enablePortal.addActionListener(e -> updatePortalElements());

		JPanel events = new JPanel(new GridLayout(1, 4, 8, 8));
		events.add(whenPortaTriggerlUsed);
		events.add(onPortalTickUpdate);
		events.add(onPlayerEntersDimension);
		events.add(onPlayerLeavesDimension);
		events.setOpaque(false);
		pane5.add(PanelUtils.totalCenterInPanel(events));
		pane5.setOpaque(false);

		igniterName.setValidator(
				new ConditionalTextFieldValidator(igniterName, "Portal igniter needs a name", enablePortal, true));
		portalTexture.setValidator(new TileHolderValidator(portalTexture, enablePortal));
		texture.setValidator(new TileHolderValidator(texture, enablePortal));
		portalFrame.setValidator(new MCItemHolderValidator(portalFrame, enablePortal));
		igniterName.enableRealtimeValidation();

		page1group.addValidationElement(igniterName);
		page1group.addValidationElement(portalTexture);
		page1group.addValidationElement(texture);
		page1group.addValidationElement(portalFrame);

		biomesInDimension
				.setValidator(new ItemListFieldValidator(biomesInDimension, "Please select at least one biome"));
		mainFillerBlock.setValidator(new MCItemHolderValidator(mainFillerBlock));
		fluidBlock.setValidator(new MCItemHolderValidator(fluidBlock));

		page2group.addValidationElement(biomesInDimension);
		page2group.addValidationElement(mainFillerBlock);
		page2group.addValidationElement(fluidBlock);

		addPage("Properties", pane3);
		addPage("Portal", pane2);
		addPage("Triggers", pane5);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			igniterName.setText(readableNameFromModElement + " Portal Igniter");
		}
	}

	private void updatePortalElements() {
		portalFrame.setEnabled(enablePortal.isSelected());
		portalParticles.setEnabled(enablePortal.isSelected());
		portalSound.setEnabled(enablePortal.isSelected());
		luminance.setEnabled(enablePortal.isSelected());
		igniterName.setEnabled(enablePortal.isSelected());
		igniterTab.setEnabled(enablePortal.isSelected());
		texture.setEnabled(enablePortal.isSelected());
		portalTexture.setEnabled(enablePortal.isSelected());
		portalMakeCondition.setEnabled(enablePortal.isSelected());
		portalUseCondition.setEnabled(enablePortal.isSelected());
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		whenPortaTriggerlUsed.refreshListKeepSelected();
		onPortalTickUpdate.refreshListKeepSelected();
		onPlayerEntersDimension.refreshListKeepSelected();
		onPlayerLeavesDimension.refreshListKeepSelected();

		portalMakeCondition.refreshListKeepSelected();
		portalUseCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(igniterTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("TOOLS"));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page2group);
		else if (page == 1)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Dimension dimension) {
		portalFrame.setBlock(dimension.portalFrame);
		mainFillerBlock.setBlock(dimension.mainFillerBlock);
		fluidBlock.setBlock(dimension.fluidBlock);
		portalSound.setSound(dimension.portalSound);
		igniterName.setText(dimension.igniterName);
		portalTexture.setTextureFromTextureName(dimension.portalTexture);
		texture.setTextureFromTextureName(dimension.texture);
		worldGenType.setSelectedItem(dimension.worldGenType);
		sleepResult.setSelectedItem(dimension.sleepResult);
		igniterTab.setSelectedItem(dimension.igniterTab.getUnmappedValue());
		portalParticles.setSelectedItem(dimension.portalParticles.getUnmappedValue());
		biomesInDimension.setListElements(dimension.biomesInDimension);
		airColor.setColor(dimension.airColor);
		canRespawnHere.setSelected(dimension.canRespawnHere);
		hasFog.setSelected(dimension.hasFog);
		isDark.setSelected(dimension.isDark);
		doesWaterVaporize.setSelected(dimension.doesWaterVaporize);
		imitateOverworldBehaviour.setSelected(dimension.imitateOverworldBehaviour);
		hasSkyLight.setSelected(dimension.hasSkyLight);
		enablePortal.setSelected(dimension.enablePortal);
		hasWeather.setSelected(dimension.hasWeather);
		whenPortaTriggerlUsed.setSelectedProcedure(dimension.whenPortaTriggerlUsed);
		onPortalTickUpdate.setSelectedProcedure(dimension.onPortalTickUpdate);
		onPlayerEntersDimension.setSelectedProcedure(dimension.onPlayerEntersDimension);
		onPlayerLeavesDimension.setSelectedProcedure(dimension.onPlayerLeavesDimension);
		luminance.setValue(dimension.portalLuminance);
		portalMakeCondition.setSelectedProcedure(dimension.portalMakeCondition);
		portalUseCondition.setSelectedProcedure(dimension.portalUseCondition);

		updatePortalElements();
	}

	@Override public Dimension getElementFromGUI() {
		Dimension dimension = new Dimension(modElement);
		dimension.texture = texture.getID();
		dimension.portalTexture = portalTexture.getID();
		dimension.portalParticles = new Particle(mcreator.getWorkspace(), (String) portalParticles.getSelectedItem());
		dimension.igniterTab = new TabEntry(mcreator.getWorkspace(), igniterTab.getSelectedItem());
		dimension.portalSound = portalSound.getSound();
		dimension.biomesInDimension = biomesInDimension.getListElements();
		dimension.airColor = airColor.getColor();
		dimension.canRespawnHere = canRespawnHere.isSelected();
		dimension.hasFog = hasFog.isSelected();
		dimension.isDark = isDark.isSelected();
		dimension.imitateOverworldBehaviour = imitateOverworldBehaviour.isSelected();
		dimension.hasSkyLight = hasSkyLight.isSelected();
		dimension.enablePortal = enablePortal.isSelected();
		dimension.hasWeather = hasWeather.isSelected();
		dimension.portalFrame = portalFrame.getBlock();
		dimension.igniterName = igniterName.getText();
		dimension.worldGenType = (String) worldGenType.getSelectedItem();
		dimension.sleepResult = (String) sleepResult.getSelectedItem();
		dimension.mainFillerBlock = mainFillerBlock.getBlock();
		dimension.fluidBlock = fluidBlock.getBlock();
		dimension.whenPortaTriggerlUsed = whenPortaTriggerlUsed.getSelectedProcedure();
		dimension.onPortalTickUpdate = onPortalTickUpdate.getSelectedProcedure();
		dimension.onPlayerEntersDimension = onPlayerEntersDimension.getSelectedProcedure();
		dimension.onPlayerLeavesDimension = onPlayerLeavesDimension.getSelectedProcedure();
		dimension.portalLuminance = (double) luminance.getValue();
		dimension.doesWaterVaporize = doesWaterVaporize.isSelected();
		dimension.portalMakeCondition = portalMakeCondition.getSelectedProcedure();
		dimension.portalUseCondition = portalUseCondition.getSelectedProcedure();
		return dimension;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-dimension");
	}

}
