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
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.workspace.elements.ModElement;

import java.io.File;
import java.util.List;

public class CustomElementGUI extends ModElementGUI<CustomElement> {

	private final CodeEditorView codeEditorView;

	public CustomElementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);

		GeneratableElement generatableElement = new CustomElement(modElement);

		List<File> modElementFiles = mcreator.getGenerator().getModElementGeneratorTemplatesList(generatableElement)
				.stream().map(GeneratorTemplate::getFile).toList();

		File modElementFile = modElementFiles.get(0);

		// this element was just created, generate its file
		if (!editingMode) {
			// generate mod element code
			mcreator.getGenerator().generateElement(generatableElement);

			// add mod element to the list, it will be only added for the first time, otherwise refreshed
			modElement.setCodeLock(true);
			mcreator.getWorkspace().addModElement(modElement);
		}

		codeEditorView = new CodeEditorView(mcreator, modElementFile);
	}

	@Override public ViewBase showView() {
		return this.codeEditorView.showView();
	}

	@Override protected void initGUI() {
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(CustomElement generatableElement) {
	}

	@Override public CustomElement getElementFromGUI() {
		return null;
	}

}
