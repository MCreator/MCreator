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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class DamageTypeGUI extends ModElementGUI<DamageType> {

	private final JSpinner exhaustion = new JSpinner(new SpinnerNumberModel(0.1, 0, 100, 0.01));
	private final JComboBox<String> scaling = new JComboBox<>(
			new String[] { "never", "always", "when_caused_by_living_non_player" });
	private final JComboBox<String> effects = new JComboBox<>(
			new String[] { "hurt", "thorns", "drowning", "burning", "poking", "freezing" });
	private final VTextField normalDeathMessage = new VTextField(28);
	private final VTextField itemDeathMessage = new VTextField(28);
	private final VTextField playerDeathMessage = new VTextField(28);

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
		damageProperties.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.damagetype.damage_properties"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));
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
		localizationPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.damagetype.death_messages"), TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION,
				getFont(), Theme.current().getForegroundColor()));
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

		normalDeathMessage.setValidator(
				new TextFieldValidator(normalDeathMessage, L10N.t("elementgui.damagetype.error_empty_death_message")));
		normalDeathMessage.enableRealtimeValidation();
		page1group.addValidationElement(normalDeathMessage);

		itemDeathMessage.setValidator(
				new TextFieldValidator(itemDeathMessage, L10N.t("elementgui.damagetype.error_empty_death_message")));
		itemDeathMessage.enableRealtimeValidation();
		page1group.addValidationElement(itemDeathMessage);

		playerDeathMessage.setValidator(
				new TextFieldValidator(playerDeathMessage, L10N.t("elementgui.damagetype.error_empty_death_message")));
		playerDeathMessage.enableRealtimeValidation();
		page1group.addValidationElement(playerDeathMessage);

		page.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(damageProperties, localizationPanel)));
		addPage(L10N.t("elementgui.common.page_properties"), page);

		if (!isEditingMode()) {
			normalDeathMessage.setText("<player> died");
			itemDeathMessage.setText("<player> was killed by <attacker> using <item>");
			playerDeathMessage.setText("<player> died whilst trying to escape <attacker>");
		}

	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
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
		damageType.scaling = (String) scaling.getSelectedItem();
		damageType.effects = (String) effects.getSelectedItem();
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
