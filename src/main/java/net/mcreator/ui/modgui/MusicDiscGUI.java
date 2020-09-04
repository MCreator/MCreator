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
import net.mcreator.element.types.MusicDisc;
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
import net.mcreator.ui.minecraft.ProcedureSelector;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;

public class MusicDiscGUI extends ModElementGUI<MusicDisc> {

	private TextureHolder texture;

	private final JTextField specialInfo = new JTextField(20);
	private final VTextField name = new VTextField(20);
	private final VTextField description = new VTextField(20);

	private final JCheckBox hasGlow = new JCheckBox("Check to enable");

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
	private ProcedureSelector onStoppedUsing;
	private ProcedureSelector onEntitySwing;

	private final SoundSelector music = new SoundSelector(mcreator);

	private final ValidationGroup page1group = new ValidationGroup();

	public MusicDiscGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onRightClickedInAir = new ProcedureSelector(this.withEntry("item/when_right_clicked"), mcreator,
				"When right clicked in air (player loc.)",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onCrafted = new ProcedureSelector(this.withEntry("item/on_crafted"), mcreator, "When item is crafted/smelted",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClickedOnBlock = new ProcedureSelector(this.withEntry("item/when_right_clicked_block"), mcreator,
				"When right clicked on block (hand loc.)", Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/direction:direction"));
		onEntityHitWith = new ProcedureSelector(this.withEntry("item/when_entity_hit"), mcreator,
				"When living entity is hit with item", Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack"));
		onItemInInventoryTick = new ProcedureSelector(this.withEntry("item/inventory_tick"), mcreator,
				"When item in inventory tick", Dependency
				.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onItemInUseTick = new ProcedureSelector(this.withEntry("item/hand_tick"), mcreator, "When item in hand tick",
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onStoppedUsing = new ProcedureSelector(this.withEntry("item/when_stopped_using"), mcreator,
				"On player stopped using", Dependency
				.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/time:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				"When entity swings item",
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, "Item"));

		texture.setOpaque(false);
		hasGlow.setOpaque(false);

		JPanel pane3 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(specialInfo, 16);
		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel subpane2 = new JPanel(new GridLayout(6, 2, 45, 8));
		subpane2.setOpaque(false);

		name.setOpaque(false);
		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("musicdisc/sound"),
				new JLabel("<html>Sound to play:<br><small>Music needs to be mono to properly work")));
		subpane2.add(music);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"), new JLabel("Name in GUI:")));
		subpane2.add(name);

		subpane2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("musicdisc/description"), new JLabel("Music disc description:")));
		subpane2.add(description);

		subpane2.add(HelpUtils
				.wrapWithHelpButton(this.withEntry("common/creative_tab"), new JLabel("Creative inventory tab:")));
		subpane2.add(creativeTab);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/glowing_effect"), new JLabel("Has glowing effect?")));
		subpane2.add(hasGlow);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("item/special_information"), new JLabel(
				"<html>Special information about the music disc:<br><small>Separate entries with comma, to use comma in description use \\,")));
		subpane2.add(specialInfo);

		JPanel destal3 = new JPanel(new BorderLayout(15, 15));
		destal3.setOpaque(false);
		destal3.add("West", PanelUtils.totalCenterInPanel(ComponentUtils.squareAndBorder(texture, "Disc texture")));

		pane3.add(PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(PanelUtils.centerInPanel(destal3), subpane2, 40, 40)));
		pane3.setOpaque(false);

		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		JPanel events = new JPanel(new GridLayout(3, 3, 10, 10));
		events.setOpaque(false);
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onStoppedUsing);
		events.add(onEntitySwing);
		pane4.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.maxMargin(events, 20, true, true, true, true)));
		pane4.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);
		page1group.addValidationElement(name);
		page1group.addValidationElement(description);
		page1group.addValidationElement(music.getVTextField());

		name.setValidator(new TextFieldValidator(name, "Music disc needs a name"));
		name.enableRealtimeValidation();

		description.setValidator(new TextFieldValidator(description, "Music disc needs a description"));
		description.enableRealtimeValidation();

		music.getVTextField().setValidator(new TextFieldValidator(music.getVTextField(), "Select a sound"));
		music.getVTextField().enableRealtimeValidation();

		addPage("Properties", pane3);
		addPage("Triggers", pane4);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onRightClickedInAir.refreshListKeepSelected();
		onCrafted.refreshListKeepSelected();
		onRightClickedOnBlock.refreshListKeepSelected();
		onEntityHitWith.refreshListKeepSelected();
		onItemInInventoryTick.refreshListKeepSelected();
		onItemInUseTick.refreshListKeepSelected();
		onStoppedUsing.refreshListKeepSelected();
		onEntitySwing.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()),
				new DataListEntry.Dummy("MISC"));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(MusicDisc musicDisc) {
		name.setText(musicDisc.name);
		description.setText(musicDisc.description);
		texture.setTextureFromTextureName(musicDisc.texture);
		specialInfo.setText(
				musicDisc.specialInfo.stream().map(info -> info.replace(",", "\\,")).collect(Collectors.joining(",")));
		onRightClickedInAir.setSelectedProcedure(musicDisc.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(musicDisc.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(musicDisc.onCrafted);
		onEntityHitWith.setSelectedProcedure(musicDisc.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(musicDisc.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(musicDisc.onItemInUseTick);
		onStoppedUsing.setSelectedProcedure(musicDisc.onStoppedUsing);
		onEntitySwing.setSelectedProcedure(musicDisc.onEntitySwing);
		creativeTab.setSelectedItem(musicDisc.creativeTab);
		hasGlow.setSelected(musicDisc.hasGlow);
		music.setSound(musicDisc.music);
	}

	@Override public MusicDisc getElementFromGUI() {
		MusicDisc musicDisc = new MusicDisc(modElement);
		musicDisc.name = name.getText();
		musicDisc.description = description.getText();
		musicDisc.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		musicDisc.hasGlow = hasGlow.isSelected();
		musicDisc.onRightClickedInAir = onRightClickedInAir.getSelectedProcedure();
		musicDisc.onRightClickedOnBlock = onRightClickedOnBlock.getSelectedProcedure();
		musicDisc.onCrafted = onCrafted.getSelectedProcedure();
		musicDisc.onEntityHitWith = onEntityHitWith.getSelectedProcedure();
		musicDisc.onItemInInventoryTick = onItemInInventoryTick.getSelectedProcedure();
		musicDisc.onItemInUseTick = onItemInUseTick.getSelectedProcedure();
		musicDisc.onStoppedUsing = onStoppedUsing.getSelectedProcedure();
		musicDisc.onEntitySwing = onEntitySwing.getSelectedProcedure();
		musicDisc.specialInfo = StringUtils.splitCommaSeparatedStringListWithEscapes(specialInfo.getText());
		musicDisc.texture = texture.getID();
		musicDisc.music = music.getSound();
		return musicDisc;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-music-disc");
	}

}
