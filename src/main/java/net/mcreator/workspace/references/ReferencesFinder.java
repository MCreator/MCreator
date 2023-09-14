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
import net.mcreator.workspace.IWorkspaceProvider;
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
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class ReferencesFinder {

	public static List<ModElement> searchModElementUsages(Workspace workspace, ModElement element) {
		List<ModElement> elements = new ArrayList<>();

		String query = new DataListEntry.Custom(element).getName();
		for (ModElement me : workspace.getModElements()) {
			if (me.equals(element))
				continue;

			GeneratableElement ge = me.getGeneratableElement();
			if (anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(ElementReference.class), (a, t) -> {
				ElementReference ref = a.getAnnotation(ElementReference.class);
				return !List.of(ref.defaultValues()).contains(t) && query.equals(ref.customPrefix() + t);
			})) {
				elements.add(me);
			} else if (anyValueMatches(ge, MappableElement.class, e -> true,
					(a, t) -> t.getUnmappedValue().equals(query))) {
				elements.add(me);
			} else if (anyValueMatches(ge, Procedure.class, e -> true,
					(a, t) -> t.getName() != null && !t.getName().isEmpty() && !t.getName().equals("null")
							&& element.getName().equals(t.getName()))) {
				elements.add(me);
			} else if (anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class),
					(a, t) -> t.contains(query) || t.contains(element.getName()))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyValueMatches(me.getGeneratableElement(), String.class, e -> {
				TextureReference ref = e.getAnnotation(TextureReference.class);
				return ref != null && ref.value() == type;
			}, (a, t) -> {
				TextureReference ref = a.getAnnotation(TextureReference.class);
				return !List.of(ref.defaultValues()).contains(t) && workspace.getFolderManager()
						.getTextureFile(FilenameUtilsPatched.removeExtension(t), type).equals(texture);
			})) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchModelUsages(Workspace workspace, Model model) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyValueMatches(me.getGeneratableElement(), Model.class,
					e -> e.isAnnotationPresent(ModelReference.class),
					(a, t) -> model.equals(t) || TexturedModel.getModelTextureMapVariations(model).contains(t))) {
				elements.add(me);
			}
		}

		return elements;
	}

	public static List<ModElement> searchSoundUsages(Workspace workspace, SoundElement sound) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyValueMatches(me.getGeneratableElement(), Sound.class, e -> true,
					(a, t) -> t.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchStructureUsages(Workspace workspace, String structure) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyValueMatches(me.getGeneratableElement(), String.class,
					e -> e.isAnnotationPresent(StructureReference.class), (a, t) -> t.equals(structure)))
				elements.add(me);
		}

		return elements;
	}

	public static List<ModElement> searchGlobalVariableUsages(Workspace workspace, String variableName) {
		List<ModElement> elements = new ArrayList<>();

		for (ModElement me : workspace.getModElements()) {
			if (anyValueMatches(me.getGeneratableElement(), String.class, e -> e.isAnnotationPresent(BlocklyXML.class),
					(a, t) -> t.contains("<field name=\"VAR\">global:" + variableName + "</field>"))) {
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
			} else if (anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class),
					(a, t) -> t.contains(localizationKey))) {
				elements.add(me);
			}
		}

		return elements;
	}

	/**
	 * Checks if values acquired from any valid fields or methods meet the specified condition.
	 *
	 * @param source    The object to extract values from.
	 * @param clazz     The class of values to be checked.
	 * @param validIf   The predicate used to check if a field/method is considered valid.
	 * @param condition The predicate defining the condition that the acquired values should pass.
	 * @param <T>       The type of values to be checked.
	 * @return Whether any value extracted from valid fields/methods on the {@code source} object pass the condition.
	 */
	public static <T> boolean anyValueMatches(@Nullable Object source, Class<T> clazz,
			Predicate<AccessibleObject> validIf, BiPredicate<AccessibleObject, T> condition) {
		if (source == null)
			return false;

		for (Field field : source.getClass().getFields()) {
			if (!Modifier.isStatic(field.getModifiers()) && validIf.test(field)) {
				try {
					field.setAccessible(true);
					if (checkValue(field.get(source), field, clazz, validIf, condition))
						return true;
				} catch (IllegalAccessException | IllegalArgumentException ignored) {
				}
			}
		}
		for (Method method : source.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) && validIf.test(method)) {
				try {
					method.setAccessible(true);
					if (checkValue(method.invoke(source), method, clazz, validIf, condition))
						return true;
				} catch (IllegalArgumentException | ReflectiveOperationException ignored) {
				}
			}
		}

		return false;
	}

	/**
	 * Checks if the value (or its components) acquired from the given field/method passes the specified condition.
	 *
	 * @param value     The extracted value that should be checked.
	 * @param field     The field/method the {@code value} was acquired.
	 * @param clazz     The class of values to be checked.
	 * @param validIf   The predicate used to check if the field/method is considered valid.
	 * @param condition The predicate defining the condition that the acquired values should pass.
	 * @param <T>       The type of values to be checked.
	 * @return Whether the provided value or any value extracted from valid fields/methods on the {@code value} object
	 * passes the provided condition.
	 */
	@SuppressWarnings("unchecked")
	private static <T> boolean checkValue(@Nullable Object value, AccessibleObject field, Class<T> clazz,
			Predicate<AccessibleObject> validIf, BiPredicate<AccessibleObject, T> condition) {
		if (value == null)
			return false;

		if (clazz.isInstance(value)) { // value of specified type
			return condition == null || condition.test(field, (T) value);
		} else if (value instanceof Object[] array) { // array of values
			for (Object obj : array) {
				if (checkValue(obj, field, clazz, validIf, condition))
					return true;
			}
		} else if (value instanceof Iterable<?> list) { // list of values
			return listHasMatches(list, field, clazz, validIf, condition);
		} else if (value instanceof Map<?, ?> map) { // map with values
			return listHasMatches(map.keySet(), field, clazz, validIf, condition) || listHasMatches(map.values(), field,
					clazz, validIf, condition);
		} else if (isCustomObject(value)) { // value of unknown type
			return anyValueMatches(value, clazz, validIf, condition);
		}

		return false;
	}

	/**
	 * Checks if any value (or its components) on the list acquired from the passed field/method meets given condition.
	 *
	 * @param list      The extracted list of values that should be checked.
	 * @param field     The field/method the {@code value} was acquired.
	 * @param clazz     The class of values to be checked.
	 * @param validIf   The predicate used to check if the field/method is considered valid.
	 * @param condition The predicate defining the condition that the acquired values should pass.
	 * @param <T>       The type of values to be checked.
	 * @return Whether the provided value or any value extracted from valid fields/methods on the {@code value} object
	 * passes the provided condition.
	 */
	private static <T> boolean listHasMatches(Iterable<?> list, AccessibleObject field, Class<T> clazz,
			Predicate<AccessibleObject> validIf, BiPredicate<AccessibleObject, T> condition) {
		for (Object obj : list) {
			if (checkValue(obj, field, clazz, validIf, condition))
				return true;
		}
		return false;
	}

	/**
	 * Checks if class of the passed value is from this module and not related to the technical part of the application.
	 * Scanning values that do not pass this condition will most probably lead to a {@link StackOverflowError}.
	 * <br>NOTE: If needed values are instances of a class not contained in this module, they will still be checked.
	 *
	 * @param value The value that should be checked.
	 * @return Whether it is safe to scan the {@code value} object deeper.
	 */
	private static boolean isCustomObject(Object value) {
		return value.getClass().getModule() == ReferencesFinder.class.getModule()
				&& !(value instanceof IWorkspaceProvider); // prevent from being stuck in app structure
	}

}
