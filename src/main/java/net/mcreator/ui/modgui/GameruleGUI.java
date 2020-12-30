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

import net.mcreator.element.types.Gamerule;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class GameruleGUI extends ModElementGUI<Gamerule> {

	private final VTextField name = new VTextField(20);
	private final VTextField ID = new VTextField(20);
	private final VTextField description = new VTextField(20);

	private final JComboBox<String> gameruleCategory = new JComboBox<>(
			new String[] { "Player", "Updates", "Chat", "Drops", "Misc", "Mobs", "Spawning" });
	private final JComboBox<String> gameruleType = new JComboBox<>(
			new String[] { "Number", "Logic" });

	private final ValidationGroup page1group = new ValidationGroup();

	public GameruleGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel pane3 = new JPanel(new BorderLayout());

		ComponentUtils.deriveFont(name, 16);
		ComponentUtils.deriveFont(ID, 16);
		ComponentUtils.deriveFont(description, 16);

		JPanel subpane2 = new JPanel(new GridLayout(5, 2, 45, 8));
		subpane2.setOpaque(false);

		ComponentUtils.deriveFont(name, 16);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/name"),
				L10N.label("elementgui.gamerule.name")));
		subpane2.add(name);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/id"),
				L10N.label("elementgui.gamerule.id")));
		subpane2.add(ID);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/description"),
				L10N.label("elementgui.gamerule.description")));
		subpane2.add(description);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/category"),
				L10N.label("elementgui.gamerule.category")));
		subpane2.add(gameruleCategory);

		subpane2.add(HelpUtils.wrapWithHelpButton(this.withEntry("gamerule/type"),
				L10N.label("elementgui.gamerule.type")));
		subpane2.add(gameruleType);

		page1group.addValidationElement(name);
		page1group.addValidationElement(ID);
		page1group.addValidationElement(description);

		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.gamerule.gamerule_needs_name")));
		name.enableRealtimeValidation();

		ID.setValidator(new TextFieldValidator(ID, L10N.t("elementgui.gamerule.gamerule_needs_id")));
		ID.enableRealtimeValidation();

		description.setValidator(new TextFieldValidator(description, L10N.t("elementgui.gamerule.gamerule_needs_description")));
		description.enableRealtimeValidation();

		pane3.add(PanelUtils.totalCenterInPanel(subpane2));
		pane3.setOpaque(false);

		addPage(L10N.t("elementgui.common.page_properties"), pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Gamerule gamerule) {
		name.setText(gamerule.name);
		ID.setText(gamerule.ID);
		description.setText(gamerule.description);
		gameruleCategory.setSelectedItem(gamerule.gameruleCategory);
		gameruleType.setSelectedItem(gamerule.gameruleType);
	}

	@Override public Gamerule getElementFromGUI() {
		Gamerule gamerule = new Gamerule(modElement);
		gamerule.name = name.getText();
		gamerule.ID = ID.getText();
		gamerule.description = description.getText();
		gamerule.gameruleCategory = (String) gameruleCategory.getSelectedItem();
		gamerule.gameruleType = (String) gameruleType.getSelectedItem();
		return gamerule;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-gamerule");
	}
}