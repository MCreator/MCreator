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

package net.mcreator.ui.dialogs.wysiwyg;

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.wysiwyg.WYSIWYGEditor;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class AbstractWYSIWYGDialog<T extends GUIComponent> extends MCreatorDialog {

	private final WYSIWYGEditor editor;

	@Nullable private T editingComponent;

	public AbstractWYSIWYGDialog(WYSIWYGEditor editor, @Nullable T editingComponent) {
		super(editor.mcreator);
		this.editingComponent = editingComponent;
		this.editor = editor;
	}

	public @Nullable T getEditingComponent() {
		return editingComponent;
	}

	public void setEditingComponent(@Nullable T editingComponent) {
		this.editingComponent = editingComponent;
	}

	public WYSIWYGEditor getEditor() {
		return editor;
	}

	/**
	 * A helper method to transform text to unique machine name when needed.
	 * <p>
	 * This method is not thread safe!
	 *
	 * @param componentList List of existing components to check for duplicates
	 * @param prefix Optional prefix
	 * @param text Input UI text to transform. Can be empty.
	 * @return UI text transformed to a machine name
	 */
	@Nonnull public static String textToMachineName(Collection<GUIComponent> componentList, @Nullable String prefix, @Nonnull String text) {
		// limit text length to 32 characters
		text = StringUtils.left(text, 32);

		// cleanup the text (transliterate, lowercase, remove spaces)
		String name = RegistryNameFixer.fix(text)
				// then remove non-Java compatible parts that could remain
				.replace(".", "")
				.replace("-", "")
				.replace("/", "");

		// remove underscore from start and end if present
		name = StringUtils.stripStart(name, "_");
		name = StringUtils.stripEnd(name, "_");

		// trim string
		name = name.trim();

		// check if the name is empty
		if (name.isEmpty()) {
			name = "empty";
		}

		if (prefix != null) {
			name = prefix + name;
		}

		// now we need to find a unique name if it is not already
		Set<String> usedNames = componentList.stream().map(GUIComponent::getName).collect(Collectors.toSet());

		if (usedNames.contains(name)) { // not unique
			int i = 1;
			while (usedNames.contains(name + i)) {
				i++;
			}
			name = name + i;
		}

		return name;
	}

}
