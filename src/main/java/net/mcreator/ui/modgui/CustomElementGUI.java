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

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.CustomElement;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class CustomElementGUI extends ModElementGUI<CustomElement> {

	private final CodeEditorView codeEditorView;
	private final JTextField location = new JTextField(26);

	public CustomElementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();

		List<File> modElementFiles = mcreator.getGenerator().getModElementGeneratorTemplatesList(modElement).stream()
				.map(GeneratorTemplate::getFile).toList();

		File modElementFile = modElementFiles.get(0);

		codeEditorView = new CodeEditorView(mcreator, modElementFile);
	}

	@Override public ViewBase showView() {
		if (!modElement.isCodeLocked()) {
			return super.showView();
		}
		return this.codeEditorView.showView();
	}

	@Override protected void initGUI() {
		JPanel page1 = new JPanel(new BorderLayout(10, 10));
		JPanel warning = new JPanel(new GridLayout(2, 1, 15, 10));
		JPanel locationSettings = new JPanel(new GridLayout(1, 2, 15, 15));

		JLabel warningText = L10N.label("elementgui.custom_element.warning");
		JLabel ActualwarningText = L10N.label("elementgui.custom_element.warning_text");
		warningText.setForeground(Color.red);
		ActualwarningText.setForeground(Color.red);
		final Font textFont = new Font("Sans-Serif", Font.PLAIN, 16);
		warningText.setFont(textFont);
		ActualwarningText.setFont(textFont);
		warning.add(PanelUtils.northAndCenterElement(warningText, ActualwarningText));
		warning.setOpaque(false);

		locationSettings.add(HelpUtils.wrapWithHelpButton(this.withEntry("custom_element/location"),
				L10N.label("elementgui.custom_element.location")));
		locationSettings.add(location);
		ComponentUtils.deriveFont(location, 16);

		page1.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(PanelUtils.northAndCenterElement(warning, locationSettings))));
		page1.setOpaque(false);

		addPage(page1);
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(CustomElement generatableElement) {
		location.setText(generatableElement.location);
	}

	@Override public CustomElement getElementFromGUI() {
		CustomElement customElement = new CustomElement(modElement);
		customElement.location = location.getText();
		return customElement;
	}

}
