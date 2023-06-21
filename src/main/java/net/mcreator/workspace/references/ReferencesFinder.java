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

package net.mcreator.workspace.references;

import net.mcreator.blockly.data.BlocklyXML;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.Sound;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.TexturedModel;

import javax.annotation.Nullable;
import java.io.File;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiPredicate;

public class ReferencesFinder {

	public static List<ModElement> searchModElementUsages(Workspace workspace, ModElement element) {
		List<ModElement> elements = new ArrayList<>();

		String query = new DataListEntry.Custom(element).getName();
		for (ModElement me : workspace.getModElements()) {
			GeneratableElement ge = me.getGeneratableElement();
			if (anyFieldMatches(ge, String.class, (a, t) -> {
				ElementReference ref = a.getAnnotation(ElementReference.class);
				return ref != null && !List.of(ref.defaultValues()).contains(t) && element.getName()
						.equals(ref.customPrefix() + t);
			})) {
				elements.add(me);
			} else if (anyFieldMatches(ge, MappableElement.class, (a, t) -> t.getUnmappedValue().equals(query))) {
				elements.add(me);
			} else if (anyFieldMatches(ge, Procedure.class,
					(a, t) -> t.getName() != null && !t.getName().equals("") && !t.getName().equals("null")
							&& element.getName().equals(t.getName()))) {
				elements.add(me);
			} else if (anyFieldMatches(ge, String.class,
					(a, t) -> a.isAnnotationPresent(BlocklyXML.class) && t.contains(query))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyFieldMatches(me.getGeneratableElement(), String.class, (a, t) -> {
				TextureReference ref = a.getAnnotation(TextureReference.class);
				return ref != null && ref.value() == type && !List.of(ref.defaultValues()).contains(t)
						&& workspace.getFolderManager().getTextureFile(FilenameUtilsPatched.removeExtension(t), type)
						.equals(texture);
			})) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchModelUsages(Workspace workspace, Model model) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyFieldMatches(me.getGeneratableElement(), Model.class,
					(a, t) -> model.equals(t) || TexturedModel.getModelTextureMapVariations(model).contains(t))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchSoundUsages(Workspace workspace, SoundElement sound) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyFieldMatches(me.getGeneratableElement(), Sound.class,
					(a, t) -> t.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchStructureUsages(Workspace workspace, String structure) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyFieldMatches(me.getGeneratableElement(), String.class,
					(a, t) -> a.isAnnotationPresent(StructureReference.class) && t.equals(structure)))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchGlobalVariableUsages(Workspace workspace, String variableName) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyFieldMatches(me.getGeneratableElement(), String.class,
					(a, t) -> a.isAnnotationPresent(BlocklyXML.class) && t.contains(
							"<field name=\"VAR\">global:" + variableName + "</field>"))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchLocalizationKeyUsages(Workspace workspace, String localizationKey) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			GeneratableElement ge = me.getGeneratableElement();
			if (ge != null && workspace.getGenerator().getElementLocalizationKeys(ge).contains(localizationKey)) {
				elements.add(me);
			} else if (anyFieldMatches(ge, String.class,
					(a, t) -> a.isAnnotationPresent(BlocklyXML.class) && t.contains(localizationKey))) {
				elements.add(me);
			}
		}

		return elements;
	}

	private static <T> boolean anyFieldMatches(@Nullable Object source, Class<T> clazz,
			BiPredicate<AccessibleObject, T> mapper) {
		if (source == null)
			return false;

		for (Field field : source.getClass().getFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				try {
					field.setAccessible(true);
					if (checkValue(field.get(source), field, clazz, mapper))
						return true;
				} catch (IllegalAccessException | IllegalArgumentException ignored) {
				}
			}
		}
		for (Method method : source.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) && clazz.isAssignableFrom(method.getReturnType())
					&& method.isAnnotationPresent(SafeToCallMethod.class)) {
				try {
					method.setAccessible(true);
					if (checkValue(method.invoke(source), method, clazz, mapper))
						return true;
				} catch (IllegalArgumentException | ReflectiveOperationException ignored) {
				}
			}
		}

		return false;
	}

	@SuppressWarnings("unchecked")
	private static <T> boolean checkValue(@Nullable Object value, AccessibleObject field, Class<T> clazz,
			BiPredicate<AccessibleObject, T> mapper) {
		if (value == null)
			return false;

		if (clazz.isAssignableFrom(value.getClass())) {
			return mapper == null || mapper.test(field, (T) value);
		} else if (Collection.class.isAssignableFrom(value.getClass())) {
			for (Object obj : (Collection<?>) value) {
				if (obj != null && clazz.isAssignableFrom(obj.getClass())) {
					if (mapper == null || mapper.test(field, (T) obj))
						return true;
				} else if (anyFieldMatches(obj, clazz, mapper)) {
					return true;
				}
			}
		} else if (value.getClass().getModule() != Object.class.getModule()) {
			return anyFieldMatches(value, clazz, mapper);
		}

		return false;
	}

}
