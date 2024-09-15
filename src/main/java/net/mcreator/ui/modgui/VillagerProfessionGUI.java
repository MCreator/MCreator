/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.element.types.VillagerProfession;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.MCItemHolder;
import net.mcreator.ui.minecraft.SoundSelector;
import net.mcreator.ui.minecraft.TextureComboBox;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.MCItemHolderValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.UniqueNameValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class VillagerProfessionGUI extends ModElementGUI<VillagerProfession> {

	private final VTextField displayName = new VTextField(30);
	private final MCItemHolder pointOfInterest = new MCItemHolder(mcreator, ElementUtil::loadBlocks);
	private final SoundSelector actionSound = new SoundSelector(mcreator);
	private final JComboBox<String> hat = new JComboBox<>(new String[] { "None", "Partial", "Full" });

	private TextureComboBox professionTextureFile;
	private TextureComboBox zombifiedProfessionTextureFile;

	private final ValidationGroup page1group = new ValidationGroup();

	public VillagerProfessionGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		this.professionTextureFile = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
				"elementgui.villager_profession.profession_needs_texture");
		this.zombifiedProfessionTextureFile = new TextureComboBox(mcreator, TextureType.ENTITY).requireValue(
				"elementgui.villager_profession.profession_needs_zombified_texture");

		ComponentUtils.deriveFont(displayName, 16);
		ComponentUtils.deriveFont(hat, 16);

		JPanel subpanel = new JPanel(new GridLayout(6, 2, 0, 2));
		subpanel.setOpaque(false);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/display_name"),
				L10N.label("elementgui.villager_profession.display_name")));
		subpanel.add(displayName);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/profession_block"),
				L10N.label("elementgui.villager_profession.profession_block")));
		subpanel.add(PanelUtils.centerInPanel(pointOfInterest));

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/action_sound"),
				L10N.label("elementgui.villager_profession.action_sound")));
		subpanel.add(actionSound);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/hat"),
				L10N.label("elementgui.villager_profession.hat")));
		subpanel.add(hat);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/profession_texture"),
				L10N.label("elementgui.villager_profession.profession_texture")));
		subpanel.add(professionTextureFile);

		subpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("villagerprofession/zombified_profession_texture"),
				L10N.label("elementgui.villager_profession.zombified_profession_texture")));
		subpanel.add(zombifiedProfessionTextureFile);

		page1group.addValidationElement(displayName);
		page1group.addValidationElement(pointOfInterest);
		page1group.addValidationElement(actionSound.getVTextField());
		page1group.addValidationElement(professionTextureFile);
		page1group.addValidationElement(zombifiedProfessionTextureFile);

		displayName.setValidator(new TextFieldValidator(displayName,
				L10N.t("elementgui.villager_profession.profession_needs_display_name")));
		displayName.enableRealtimeValidation();
		pointOfInterest.setValidator(
				new UniqueNameValidator(L10N.t("elementgui.villager_profession.profession_block_validator"),
						() -> pointOfInterest.getBlock().getUnmappedValue(),
						() -> ElementUtil.loadAllPOIBlocks(mcreator.getWorkspace()).stream()
								.map(MItemBlock::getUnmappedValue),
						ElementUtil.loadBlocks(mcreator.getWorkspace()).stream().filter(MCItem::isPOI)
								.map(DataListEntry::getName).toList(),
						new MCItemHolderValidator(pointOfInterest).considerAirAsEmpty()).setIsPresentOnList(
						this::isEditingMode));
		actionSound.getVTextField().setValidator(new TextFieldValidator(actionSound.getVTextField(),
				L10N.t("elementgui.common.error_sound_empty_null")));

		addPage(L10N.t("elementgui.common.page_properties"), PanelUtils.totalCenterInPanel(subpanel));

		if (!isEditingMode()) {
			displayName.setText(StringUtils.machineToReadableName(modElement.getName()));
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();

		professionTextureFile.reload();
		zombifiedProfessionTextureFile.reload();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(VillagerProfession profession) {
		displayName.setText(profession.displayName);
		pointOfInterest.setBlock(profession.pointOfInterest);
		actionSound.setSound(profession.actionSound);
		hat.setSelectedItem(profession.hat);
		professionTextureFile.setTextureFromTextureName(profession.professionTextureFile);
		zombifiedProfessionTextureFile.setTextureFromTextureName(profession.zombifiedProfessionTextureFile);
	}

	@Override public VillagerProfession getElementFromGUI() {
		VillagerProfession profession = new VillagerProfession(modElement);
		profession.displayName = displayName.getText();
		profession.pointOfInterest = pointOfInterest.getBlock();
		profession.actionSound = actionSound.getSound();
		profession.hat = (String) hat.getSelectedItem();
		profession.professionTextureFile = professionTextureFile.getTextureName();
		profession.zombifiedProfessionTextureFile = zombifiedProfessionTextureFile.getTextureName();
		return profession;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-villager-profession");
	}

}
