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
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.MusicDisc;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JStringListField;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.DataListComboBox;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.procedure.AbstractProcedureSelector;
import net.mcreator.ui.procedure.LogicProcedureSelector;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.procedure.StringListProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class MusicDiscGUI extends ModElementGUI<MusicDisc> {

	private TextureHolder texture;

	private StringListProcedureSelector specialInformation;
	private final VTextField name = new VTextField(20);
	private final JComboBox<String> rarity = new JComboBox<>(new String[] { "COMMON", "UNCOMMON", "RARE", "EPIC" });
	private final VTextField description = new VTextField(20);

	private final JSpinner lengthInTicks = new JSpinner(new SpinnerNumberModel(100, 0, 20 * 3600, 1));
	private final JSpinner analogOutput = new JSpinner(new SpinnerNumberModel(0, 0, 15, 1));

	private LogicProcedureSelector glowCondition;

	private final DataListComboBox creativeTab = new DataListComboBox(mcreator);

	private ProcedureSelector onRightClickedInAir;
	private ProcedureSelector onCrafted;
	private ProcedureSelector onRightClickedOnBlock;
	private ProcedureSelector onEntityHitWith;
	private ProcedureSelector onItemInInventoryTick;
	private ProcedureSelector onItemInUseTick;
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
				L10N.t("elementgui.common.event_right_clicked_air"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onCrafted = new ProcedureSelector(this.withEntry("item/on_crafted"), mcreator,
				L10N.t("elementgui.common.event_on_crafted"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		onRightClickedOnBlock = new ProcedureSelector(this.withEntry("item/when_right_clicked_block"), mcreator,
				L10N.t("elementgui.common.event_right_clicked_block"), VariableTypeLoader.BuiltInTypes.ACTIONRESULTTYPE,
				Dependency.fromString(
						"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/direction:direction/blockstate:blockstate")).makeReturnValueOptional();
		onEntityHitWith = new ProcedureSelector(this.withEntry("item/when_entity_hit"), mcreator,
				L10N.t("elementgui.music_disc.event_entity_hitwith"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/sourceentity:entity/itemstack:itemstack"));
		onItemInInventoryTick = new ProcedureSelector(this.withEntry("item/inventory_tick"), mcreator,
				L10N.t("elementgui.music_disc.event_inventory"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onItemInUseTick = new ProcedureSelector(this.withEntry("item/hand_tick"), mcreator,
				L10N.t("elementgui.music_disc.event_inhand"), Dependency.fromString(
				"x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack/slot:number"));
		onEntitySwing = new ProcedureSelector(this.withEntry("item/when_entity_swings"), mcreator,
				L10N.t("elementgui.music_disc.event_swing"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		specialInformation = new StringListProcedureSelector(this.withEntry("item/special_information"), mcreator,
				L10N.t("elementgui.common.special_information"), AbstractProcedureSelector.Side.CLIENT,
				new JStringListField(mcreator, null), 0,
				Dependency.fromString("x:number/y:number/z:number/entity:entity/world:world/itemstack:itemstack"));

		glowCondition = new LogicProcedureSelector(this.withEntry("item/glowing_effect"), mcreator,
				L10N.t("elementgui.item.glowing_effect"), ProcedureSelector.Side.CLIENT,
				L10N.checkbox("elementgui.common.enable"), 160,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM));

		texture.setOpaque(false);

		JPanel pane3 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel subpane2 = new JPanel(new GridLayout(7, 2, 45, 2));
		subpane2.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("musicdisc/sound"),
				L10N.label("elementgui.music_disc.music_mono_tip")));
		subpane2.add(music);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/gui_name"),
				L10N.label("elementgui.common.name_in_gui")));
		subpane2.add(name);

		subpane2.add(
				HelpUtils.wrapWithHelpButton(this.withEntry("item/rarity"), L10N.label("elementgui.common.rarity")));
		subpane2.add(rarity);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("musicdisc/description"),
				L10N.label("elementgui.music_disc.disc_description")));
		subpane2.add(description);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("musicdisc/length"),
				L10N.label("elementgui.music_disc.disc_length")));
		subpane2.add(lengthInTicks);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("musicdisc/analog_output"),
				L10N.label("elementgui.music_disc.disc_analog_output")));
		subpane2.add(analogOutput);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("common/creative_tab"),
				L10N.label("elementgui.common.creative_tab")));
		subpane2.add(creativeTab);

		JPanel subpane3 = new JPanel(new BorderLayout(0, 2));
		subpane3.setOpaque(false);
		subpane3.add("North", PanelUtils.westAndCenterElement(new JEmptyBox(4, 4), glowCondition, 0, 0));
		subpane3.add("Center", PanelUtils.westAndCenterElement(new JEmptyBox(4, 4), specialInformation, 0, 0));

		JPanel destal3 = new JPanel(new BorderLayout(15, 15));
		destal3.setOpaque(false);
		destal3.add("West", PanelUtils.totalCenterInPanel(
				ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.music_disc.disc_texture"))));

		pane3.add(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(PanelUtils.centerInPanel(destal3),
				PanelUtils.centerAndSouthElement(subpane2, subpane3, 2, 2), 40, 40)));
		pane3.setOpaque(false);

		JPanel pane4 = new JPanel(new BorderLayout(10, 10));

		JPanel events = new JPanel(new GridLayout(3, 3, 5, 5));
		events.setOpaque(false);
		events.add(onRightClickedInAir);
		events.add(onRightClickedOnBlock);
		events.add(onCrafted);
		events.add(onEntityHitWith);
		events.add(onItemInInventoryTick);
		events.add(onItemInUseTick);
		events.add(onEntitySwing);
		pane4.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.maxMargin(events, 20, true, true, true, true)));
		pane4.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));

		page1group.addValidationElement(texture);
		page1group.addValidationElement(name);
		page1group.addValidationElement(description);
		page1group.addValidationElement(music.getVTextField());

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.music_disc.error_disc_needs_name")));
		name.enableRealtimeValidation();

		description.setValidator(
				new TextFieldValidator(description, L10N.t("elementgui.music_disc.error_disc_needs_description")));
		description.enableRealtimeValidation();

		music.getVTextField().setValidator(
				new TextFieldValidator(music.getVTextField(), L10N.t("elementgui.music_disc.error_needs_sound")));
		music.getVTextField().enableRealtimeValidation();

		addPage(L10N.t("elementgui.common.page_properties"), pane3);
		addPage(L10N.t("elementgui.common.page_triggers"), pane4);

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
		onEntitySwing.refreshListKeepSelected();
		specialInformation.refreshListKeepSelected();

		glowCondition.refreshListKeepSelected();

		ComboBoxUtil.updateComboBoxContents(creativeTab, ElementUtil.loadAllTabs(mcreator.getWorkspace()));
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(MusicDisc musicDisc) {
		name.setText(musicDisc.name);
		rarity.setSelectedItem(musicDisc.rarity);
		description.setText(musicDisc.description);
		texture.setTextureFromTextureName(musicDisc.texture);
		onRightClickedInAir.setSelectedProcedure(musicDisc.onRightClickedInAir);
		onRightClickedOnBlock.setSelectedProcedure(musicDisc.onRightClickedOnBlock);
		onCrafted.setSelectedProcedure(musicDisc.onCrafted);
		onEntityHitWith.setSelectedProcedure(musicDisc.onEntityHitWith);
		onItemInInventoryTick.setSelectedProcedure(musicDisc.onItemInInventoryTick);
		onItemInUseTick.setSelectedProcedure(musicDisc.onItemInUseTick);
		onEntitySwing.setSelectedProcedure(musicDisc.onEntitySwing);
		specialInformation.setSelectedProcedure(musicDisc.specialInformation);
		creativeTab.setSelectedItem(musicDisc.creativeTab);
		glowCondition.setSelectedProcedure(musicDisc.glowCondition);
		music.setSound(musicDisc.music);
		lengthInTicks.setValue(musicDisc.lengthInTicks);
		analogOutput.setValue(musicDisc.analogOutput);
	}

	@Override public MusicDisc getElementFromGUI() {
		MusicDisc musicDisc = new MusicDisc(modElement);
		musicDisc.name = name.getText();
		musicDisc.rarity = (String) rarity.getSelectedItem();
		musicDisc.description = description.getText();
		musicDisc.creativeTab = new TabEntry(mcreator.getWorkspace(), creativeTab.getSelectedItem());
		musicDisc.glowCondition = glowCondition.getSelectedProcedure();
		musicDisc.onRightClickedInAir = onRightClickedInAir.getSelectedProcedure();
		musicDisc.onRightClickedOnBlock = onRightClickedOnBlock.getSelectedProcedure();
		musicDisc.onCrafted = onCrafted.getSelectedProcedure();
		musicDisc.onEntityHitWith = onEntityHitWith.getSelectedProcedure();
		musicDisc.onItemInInventoryTick = onItemInInventoryTick.getSelectedProcedure();
		musicDisc.onItemInUseTick = onItemInUseTick.getSelectedProcedure();
		musicDisc.onEntitySwing = onEntitySwing.getSelectedProcedure();
		musicDisc.specialInformation = specialInformation.getSelectedProcedure();
		musicDisc.texture = texture.getID();
		musicDisc.music = music.getSound();
		musicDisc.lengthInTicks = (int) lengthInTicks.getValue();
		musicDisc.analogOutput = (int) analogOutput.getValue();
		return musicDisc;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-music-disc");
	}

}
