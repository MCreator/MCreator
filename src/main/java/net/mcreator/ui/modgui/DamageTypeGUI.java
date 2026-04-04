/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

import net.mcreator.element.types.DamageType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.TranslatedComboBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public class DamageTypeGUI extends ModElementGUI<DamageType> {

	private final JSpinner exhaustion = new JSpinner(new SpinnerNumberModel(0.1, 0, 100, 0.01));
	private final TranslatedComboBox scaling = new TranslatedComboBox(
			//@formatter:off
			Map.entry("never","elementgui.damagetype.scaling.never"),
			Map.entry("always","elementgui.damagetype.scaling.always"),
			Map.entry("when_caused_by_living_non_player","elementgui.damagetype.scaling.when_caused_by_living_non_player")
			//@formatter:on
	);
	private final TranslatedComboBox effects = new TranslatedComboBox(
			//@formatter:off
			Map.entry("hurt","elementgui.damagetype.effects.hurt"),
			Map.entry("thorns","elementgui.damagetype.effects.thorns"),
			Map.entry("drowning","elementgui.damagetype.effects.drowning"),
			Map.entry("burning","elementgui.damagetype.effects.burning"),
			Map.entry("poking","elementgui.damagetype.effects.poking"),
			Map.entry("freezing","elementgui.damagetype.effects.freezing")
			//@formatter:on
	);
	private final VTextField normalDeathMessage = new VTextField(28).requireValue(
			"elementgui.damagetype.error_empty_death_message").enableRealtimeValidation();
	private final VTextField itemDeathMessage = new VTextField(28).requireValue(
			"elementgui.damagetype.error_empty_death_message").enableRealtimeValidation();
	private final VTextField playerDeathMessage = new VTextField(28).requireValue(
			"elementgui.damagetype.error_empty_death_message").enableRealtimeValidation();

	private final ValidationGroup page1group = new ValidationGroup();

	public DamageTypeGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel page = new JPanel(new BorderLayout(10, 10));
		page.setOpaque(false);

		JPanel damageProperties = new JPanel(new GridLayout(3, 2, 20, 2));
		ComponentUtils.makeSection(damageProperties, L10N.t("elementgui.damagetype.damage_properties"));
		damageProperties.setOpaque(false);

		exhaustion.setOpaque(false);

		damageProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/exhaustion"),
				L10N.label("elementgui.damagetype.exhaustion")));
		damageProperties.add(exhaustion);

		damageProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/scaling"),
				L10N.label("elementgui.damagetype.scaling")));
		damageProperties.add(scaling);

		damageProperties.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/effects"),
				L10N.label("elementgui.damagetype.effects")));
		damageProperties.add(effects);

		JPanel localizationPanel = new JPanel(new GridLayout(3, 2, 20, 2));
		ComponentUtils.makeSection(localizationPanel, L10N.t("elementgui.damagetype.death_messages"));
		localizationPanel.setOpaque(false);

		ComponentUtils.deriveFont(normalDeathMessage, 16);
		ComponentUtils.deriveFont(itemDeathMessage, 16);
		ComponentUtils.deriveFont(playerDeathMessage, 16);

		localizationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/normal_death_message"),
				L10N.label("elementgui.damagetype.normal_death_message")));
		localizationPanel.add(normalDeathMessage);

		localizationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/item_death_message"),
				L10N.label("elementgui.damagetype.item_death_message")));
		localizationPanel.add(itemDeathMessage);

		localizationPanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("damagetype/player_death_message"),
				L10N.label("elementgui.damagetype.player_death_message")));
		localizationPanel.add(playerDeathMessage);

		page1group.addValidationElement(normalDeathMessage);
		page1group.addValidationElement(itemDeathMessage);
		page1group.addValidationElement(playerDeathMessage);

		page.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(damageProperties, localizationPanel)));
		addPage(L10N.t("elementgui.common.page_properties"), page).validate(page1group);

		if (!isEditingMode()) {
			normalDeathMessage.setText("<player> died");
			itemDeathMessage.setText("<player> was killed by <attacker> using <item>");
			playerDeathMessage.setText("<player> died whilst trying to escape <attacker>");
		}
	}

	@Override protected void openInEditingMode(DamageType damageType) {
		exhaustion.setValue(damageType.exhaustion);
		scaling.setSelectedItem(damageType.scaling);
		effects.setSelectedItem(damageType.effects);
		normalDeathMessage.setText(
				damageType.normalDeathMessage.replace("%1$s", "<player>").replace("%2$s", "<attacker>"));
		itemDeathMessage.setText(damageType.itemDeathMessage.replace("%1$s", "<player>").replace("%2$s", "<attacker>")
				.replace("%3$s", "<item>"));
		playerDeathMessage.setText(
				damageType.playerDeathMessage.replace("%1$s", "<player>").replace("%2$s", "<attacker>"));
	}

	@Override public DamageType getElementFromGUI() {
		DamageType damageType = new DamageType(modElement);
		damageType.exhaustion = (double) exhaustion.getValue();
		damageType.scaling = scaling.getSelectedItem();
		damageType.effects = effects.getSelectedItem();
		damageType.normalDeathMessage = normalDeathMessage.getText().replace("<player>", "%1$s")
				.replace("<attacker>", "%2$s");
		damageType.itemDeathMessage = itemDeathMessage.getText().replace("<player>", "%1$s")
				.replace("<attacker>", "%2$s").replace("<item>", "%3$s");
		damageType.playerDeathMessage = playerDeathMessage.getText().replace("<player>", "%1$s")
				.replace("<attacker>", "%2$s");
		return damageType;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-damage-type");
	}

}
