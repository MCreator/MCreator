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
import net.mcreator.element.ModElementType;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.Fluid;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.laf.renderer.ItemTexturesComboBoxRenderer;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.DimensionListField;
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

public class FluidGUI extends ModElementGUI<Fluid> {

	private TextureHolder textureStill;
	private TextureHolder textureFlowing;

	private final JSpinner luminosity = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
	private final JSpinner density = new JSpinner(new SpinnerNumberModel(1000, -100000, 100000, 1));
	private final JSpinner viscosity = new JSpinner(new SpinnerNumberModel(1000, 0, 100000, 1));

	private final JCheckBox generateBucket = new JCheckBox("Check to enable the fluid bucket");
	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private final JCheckBox isGas = new JCheckBox("Check if this fluid is gas");
	private final JComboBox<String> fluidtype = new JComboBox<>(new String[] { "WATER", "LAVA" });

	private ProcedureSelector onBlockAdded;
	private ProcedureSelector onNeighbourChanges;
	private ProcedureSelector onTickUpdate;
	private ProcedureSelector onEntityCollides;

	private DimensionListField spawnWorldTypes;

	private final ValidationGroup page1group = new ValidationGroup();

	public FluidGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onBlockAdded = new ProcedureSelector(this.withEntry("block/when_added"), mcreator, "When block added",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onNeighbourChanges = new ProcedureSelector(this.withEntry("block/when_neighbour_changes"), mcreator,
				"When neighbour block changes", Dependency.fromString("x:number/y:number/z:number/world:world"));
		onTickUpdate = new ProcedureSelector(this.withEntry("block/update_tick"), mcreator, "Update tick",
				Dependency.fromString("x:number/y:number/z:number/world:world"));
		onEntityCollides = new ProcedureSelector(this.withEntry("block/when_entity_collides"), mcreator,
				"When mob/player collides block",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		spawnWorldTypes = new DimensionListField(mcreator);
		spawnWorldTypes.setListElements(Collections.singletonList("Surface"));

		fluidtype.setRenderer(new ItemTexturesComboBoxRenderer());

		JPanel pane3 = new JPanel(new BorderLayout(10, 10));
		pane3.setOpaque(false);

		JPanel destalx = new JPanel(new FlowLayout(FlowLayout.CENTER));
		destalx.setOpaque(false);

		textureStill = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureStill.setOpaque(false);
		textureFlowing = new TextureHolder(new BlockItemTextureSelector(mcreator, "Block"));
		textureFlowing.setOpaque(false);

		destalx.add(ComponentUtils.squareAndBorder(textureStill, "Still texture"));
		destalx.add(ComponentUtils.squareAndBorder(textureFlowing, "Flowing"));

		pane3.add(PanelUtils.totalCenterInPanel(destalx));

		JPanel pane2 = new JPanel(new BorderLayout(10, 10));
		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		JPanel destal = new JPanel(new GridLayout(8, 2, 20, 5));
		destal.setOpaque(false);

		luminosity.setOpaque(false);
		density.setOpaque(false);
		viscosity.setOpaque(false);
		isGas.setOpaque(false);
		generateBucket.setOpaque(false);

		ComponentUtils.deriveFont(luminosity, 16);
		ComponentUtils.deriveFont(density, 16);
		ComponentUtils.deriveFont(viscosity, 16);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/luminosity"),
				new JLabel("<html>Luminosity:<br><small>The light level emitted")));
		destal.add(luminosity);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/density"), new JLabel(
				"<html>Density:<br><small>Negative value makes it lighter than air. It uses real life units kg/m^2")));
		destal.add(density);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/viscosity"), new JLabel(
				"<html>Viscosity:<br><small>Higher value makes it flow more slowly. It uses real-life units m/s^2(*10^-3)")));
		destal.add(viscosity);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/is_gas"),
				new JLabel("<html>Is this fluid gas?<br><small>Makes it flow upwards")));
		destal.add(PanelUtils.centerInPanel(isGas));

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/type"), new JLabel(
				"<html>Fluid type<br><small>Defines some properties of the fluid (sound, does ignite fire, does hurt entities)")));
		destal.add(fluidtype);

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("fluid/generate_lakes"),
				new JLabel("Generate lakes of this fluids in:")));
		destal.add(spawnWorldTypes);

		destal.add(new JLabel("Select if this fluid should have own bucket generated automatically"));
		destal.add(PanelUtils.centerInPanel(generateBucket));

		destal.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				new JLabel("Bucket creative inventory tab:")));
		destal.add(creativeTab);

		generateBucket.setSelected(true);

		JPanel render = new JPanel();
		render.setLayout(new BoxLayout(render, BoxLayout.PAGE_AXIS));

		render.setOpaque(false);

		pane2.setOpaque(false);
		pane2.add("Center", PanelUtils.totalCenterInPanel(destal));

		JPanel events = new JPanel();
		events.setLayout(new BoxLayout(events, BoxLayout.PAGE_AXIS));
		JPanel events2 = new JPanel(new GridLayout(1, 4, 8, 8));
		events2.setOpaque(false);
		events2.add(onBlockAdded);
		events2.add(onNeighbourChanges);
		events2.add(onTickUpdate);
		events2.add(onEntityCollides);
		events.add(PanelUtils.join(events2));
		events.setOpaque(false);
		pane4.add("Center", PanelUtils.totalCenterInPanel(events));
		pane4.setOpaque(false);

		textureStill.setValidator(new TileHolderValidator(textureStill));
		textureFlowing.setValidator(new TileHolderValidator(textureFlowing));

		page1group.addValidationElement(textureStill);
		page1group.addValidationElement(textureFlowing);

		addPage("Visual", pane3);
		addPage("Properties", pane2);
		addPage("Triggers", pane4);
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onBlockAdded.refreshListKeepSelected();
		onNeighbourChanges.refreshListKeepSelected();
		onTickUpdate.refreshListKeepSelected();
		onEntityCollides.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("MISC"));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Fluid fluid) {
		textureStill.setTextureFromTextureName(fluid.textureStill);
		textureFlowing.setTextureFromTextureName(fluid.textureFlowing);
		luminosity.setValue(fluid.luminosity);
		density.setValue(fluid.density);
		viscosity.setValue(fluid.viscosity);
		isGas.setSelected(fluid.isGas);
		generateBucket.setSelected(fluid.generateBucket);
		spawnWorldTypes.setListElements(fluid.spawnWorldTypes);
		onBlockAdded.setSelectedProcedure(fluid.onBlockAdded);
		onNeighbourChanges.setSelectedProcedure(fluid.onNeighbourChanges);
		onTickUpdate.setSelectedProcedure(fluid.onTickUpdate);
		onEntityCollides.setSelectedProcedure(fluid.onEntityCollides);
		fluidtype.setSelectedItem(fluid.type);
		if (fluid.creativeTab != null)
			creativeTab.setSelectedItem(fluid.creativeTab);
	}

	@Override public Fluid getElementFromGUI() {
		Fluid fluid = new Fluid(modElement);
		fluid.name = modElement.getName();
		fluid.textureFlowing = textureFlowing.getID();
		fluid.textureStill = textureStill.getID();
		fluid.luminosity = (int) luminosity.getValue();
		fluid.density = (int) density.getValue();
		fluid.viscosity = (int) viscosity.getValue();
		fluid.isGas = isGas.isSelected();
		fluid.generateBucket = generateBucket.isSelected();
		fluid.onBlockAdded = onBlockAdded.getSelectedProcedure();
		fluid.onNeighbourChanges = onNeighbourChanges.getSelectedProcedure();
		fluid.onTickUpdate = onTickUpdate.getSelectedProcedure();
		fluid.onEntityCollides = onEntityCollides.getSelectedProcedure();
		fluid.type = (String) fluidtype.getSelectedItem();
		fluid.spawnWorldTypes = spawnWorldTypes.getListElements();
		fluid.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		return fluid;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-fluid");
	}

}
