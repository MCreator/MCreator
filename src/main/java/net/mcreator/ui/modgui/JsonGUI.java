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

import net.mcreator.element.types.Json;
import net.mcreator.minecraft.ElementUtil;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.minecraft.jsonvalues.JJsonValuesList;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.workspace.elements.ModElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.stream.Collectors;

public class JsonGUI extends ModElementGUI<Json>{

	private final JJsonValuesList jsonValuesList = new JJsonValuesList(mcreator);

	private final JPanel values = new JPanel(new GridLayout(0, 1, 5, 5));

	public JsonGUI(MCreator mcreator, @NotNull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		this.finalizeGUI(false);
	}

	@Override protected void initGUI() {
		JPanel pane1 = new JPanel(new GridLayout());
		pane1.setOpaque(false);
		JComponent component = PanelUtils.northAndCenterElement(HelpUtils
						.wrapWithHelpButton(this.withEntry("json/values"), new JLabel(
								"<html>Values to add in the JSON")),
				jsonValuesList);

		component.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		pane1.add(component);
		pane1.setOpaque(false);

		addPage("Values", pane1);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return null;
	}

	/**
	 * This method is called to open a mod element in the GUI
	 *
	 * @param json
	 */
	@Override protected void openInEditingMode(Json json) {
		jsonValuesList.setValues(json.values);

	}

	@Override public Json getElementFromGUI() {
		Json json = new Json(modElement);
		json.values = jsonValuesList.getValues();

		return json;
	}

	@Override public @Nullable URI getContextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-json");
	}
}
