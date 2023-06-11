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
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiFunction;

public class ReferencesFinder {

	public static List<ModElement> searchModElementUsages(Workspace workspace, ModElement element) {
		List<ModElement> elements = new ArrayList<>();

		String query = new DataListEntry.Custom(element).getName();
		for (ModElement me : workspace.getModElements()) {
			GeneratableElement ge = me.getGeneratableElement();
			if (!matchFields(ge, String.class, (a, t) -> {
				ElementReference ref = a.getAnnotation(ElementReference.class);
				return ref != null && !List.of(ref.defaultValues()).contains(t) ? ref.customPrefix() + t : null;
			}, false).isEmpty()) {
				elements.add(me);
			} else if (matchFields(ge, MappableElement.class, (a, t) -> t, false).stream()
					.anyMatch(e -> e.getUnmappedValue().equals(query))) {
				elements.add(me);
			} else if (matchFields(ge, Procedure.class, (a, t) -> {
				if (t.getName() != null && t.getName().equals("") && t.getName().equals("null"))
					return t;
				return null;
			}, false).stream().anyMatch(e -> element.getName().equals(e.getName()))) {
				elements.add(me);
			} else if (!matchFields(ge, String.class,
					(a, t) -> a.isAnnotationPresent(BlocklyXML.class) && t.contains(query) ? t : null, false).isEmpty()) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (matchFields(me.getGeneratableElement(), String.class, (a, t) -> {
				TextureReference ref = a.getAnnotation(TextureReference.class);
				if (ref != null && ref.value() == type && !List.of(ref.defaultValues()).contains(t))
					return t;
				return null;
			}, false).stream().anyMatch(
					e -> workspace.getFolderManager().getTextureFile(FilenameUtilsPatched.removeExtension(e), type)
							.equals(texture))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchModelUsages(Workspace workspace, Model model) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (!matchFields(me.getGeneratableElement(), Model.class, (a, t) -> {
				if (model.equals(t) || TexturedModel.getModelTextureMapVariations(model).contains(t))
					return t;
				return null;
			}, true).isEmpty()) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchSoundUsages(Workspace workspace, SoundElement sound) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (matchFields(me.getGeneratableElement(), Sound.class, (a, t) -> t, false).stream()
					.anyMatch(e -> e.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchStructureUsages(Workspace workspace, String structure) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (matchFields(me.getGeneratableElement(), String.class,
					(a, t) -> a.isAnnotationPresent(StructureReference.class) ? t : null, false).contains(structure))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchGlobalVariableUsages(Workspace workspace, String variableName) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (!matchFields(me.getGeneratableElement(), String.class, (a, t) -> {
				if (a.isAnnotationPresent(BlocklyXML.class) && t.contains(
						"<field name=\"VAR\">global:" + variableName + "</field>"))
					return t;
				return null;
			}, false).isEmpty()) {
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
			} else if (!matchFields(ge, String.class, (a, t) -> {
				if (a.isAnnotationPresent(BlocklyXML.class) && t.contains(localizationKey))
					return t;
				return null;
			}, false).isEmpty()) {
				elements.add(me);
			}
		}

		return elements;
	}

	private static <T> List<T> matchFields(@Nullable Object source, Class<T> clazz,
			BiFunction<AccessibleObject, T, T> condition, boolean methods) {
		List<T> retVal = new ArrayList<>();
		if (source == null)
			return retVal;

		for (Field field : source.getClass().getFields()) {
			if (!Modifier.isStatic(field.getModifiers())) {
				try {
					field.setAccessible(true);
					checkValue(retVal, field.get(source), field, clazz, condition, methods);
				} catch (IllegalAccessException | IllegalArgumentException ignored) {
				}
			}
		}
		if (methods) {
			for (Method method : source.getClass().getMethods()) {
				if (!Modifier.isStatic(method.getModifiers())) {
					try {
						method.setAccessible(true);
						checkValue(retVal, method.invoke(source), method, clazz, condition, true);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ignored) {
					}
				}
			}
		}

		return retVal;
	}

	@SuppressWarnings("unchecked")
	private static <T> void checkValue(List<T> valuesList, Object value, AccessibleObject field, Class<T> clazz,
			BiFunction<AccessibleObject, T, T> condition, boolean methods) {
		if (value == null)
			return;

		if (clazz.isAssignableFrom(value.getClass())) {
			T t = condition == null ? (T) value : condition.apply(field, (T) value);
			if (t != null)
				valuesList.add(t);
		} else if (!methods) { // prevent calling e.g. close() methods
			if (Collection.class.isAssignableFrom(value.getClass())) {
				for (Object obj : (Collection<?>) value) {
					if (obj == null)
						continue;

					if (clazz.isAssignableFrom(obj.getClass())) {
						T t = condition == null ? (T) obj : condition.apply(field, (T) obj);
						if (t != null)
							valuesList.add(t);
					} else {
						valuesList.addAll(matchFields(obj, clazz, condition, false));
					}
				}
			} else if (value.getClass().getModule() != Object.class.getModule()) {
				valuesList.addAll(matchFields(value, clazz, condition, false));
			}
		}
	}
}
