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

package net.mcreator.workspace;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.interfaces.IOtherModElementsDependent;
import net.mcreator.element.types.interfaces.IResourcesDependent;
import net.mcreator.element.types.interfaces.IXMLProvider;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.resources.Model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReferencesFinder {

	public static List<ModElement> searchModElementUsages(Workspace workspace, ModElement element) {
		List<ModElement> elements = new ArrayList<>();

		String query = new DataListEntry.Custom(element).getName();
		for (ModElement me : workspace.getModElements()) {
			GeneratableElement ge = me.getGeneratableElement();
			if (ge instanceof IOtherModElementsDependent mod) {
				if (mod.getUsedElementNames().contains(query)) {
					elements.add(me);
				} else if (mod.getUsedElementMappings().stream()
						.anyMatch(e -> e != null && e.getUnmappedValue().equals(query))) {
					elements.add(me);
				} else if (mod.getUsedProcedures().stream()
						.anyMatch(e -> e != null && element.getName().equals(e.getName()))) {
					elements.add(me);
				}
			} else if (ge instanceof IXMLProvider provider && provider.getXML().contains(query)) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IResourcesDependent res && res.getTextures(type).stream()
					.anyMatch(e -> !e.equals("") && workspace.getFolderManager()
							.getTextureFile(FilenameUtilsPatched.removeExtension(e), type).equals(texture))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchModelUsages(Workspace workspace, Model model) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IResourcesDependent res && res.getModels().contains(model))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchSoundUsages(Workspace workspace, SoundElement sound) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IResourcesDependent res && res.getSounds().stream()
					.anyMatch(e -> e.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchStructureUsages(Workspace workspace, String structure) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IResourcesDependent res && res.getStructures()
					.contains(structure))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchGlobalVariableUsages(Workspace workspace, String variableName) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IXMLProvider provider && provider.getXML()
					.contains("<field name=\"VAR\">global:" + variableName + "</field>"))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchLocalizationKeyUsages(Workspace workspace, String localizationKey) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (me.getGeneratableElement() instanceof IXMLProvider provider && provider.getXML()
					.contains(localizationKey) || me.getGeneratableElement() != null && workspace.getGenerator()
					.getElementLocalizationKeys(me.getGeneratableElement()).contains(localizationKey))
				elements.add(me);
		}

		return elements;
	}
}
