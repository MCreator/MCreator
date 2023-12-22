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
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
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
import java.lang.reflect.*;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReferencesFinder {

	/**
	 * Convenience method to provide a list of all generatable elements contained in the provided workspace in a thread-safe manner.
	 * List returned by this method can be used in parallel streams as long as all other methods in the said parallel stream are
	 * also thread safe. Thus, e.g. calling ModElementManager within the parallel stream later would still cause problems.
	 *
	 * @param workspace Workspace to obtain generatable elements from.
	 * @return List of all generatable elements contained in the provided workspace.
	 */
	private static List<GeneratableElement> getGeneratableElements(Workspace workspace) {
		return workspace.getModElements().stream().filter(me -> !me.isCodeLocked())
				.map(ModElement::getGeneratableElement).collect(Collectors.toList());
	}

	//@formatter:off

	public static Set<ModElement> searchModElementUsages(Workspace workspace, ModElement element) {
		final Pattern procedureXmlPattern = Pattern.compile(">(?:CUSTOM:)?" + element.getName() + "([.:]\\w+?)?</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(ModElementReference.class), (a, t) -> {
					ModElementReference ref = a.getAnnotation(ModElementReference.class);
					return !Set.of(ref.defaultValues()).contains(t) && element.getName().equals(
							GeneratorWrapper.getElementPlainName(t));
				}) ||
				anyValueMatches(ge, MappableElement.class, e -> e.isAnnotationPresent(ModElementReference.class), (a, t) ->
					element.getName().equals(GeneratorWrapper.getElementPlainName(t.getUnmappedValue()))
				) ||
				anyValueMatches(ge, Procedure.class, e -> e.isAnnotationPresent(ModElementReference.class), (a, t) ->
					element.getName().equals(t.getName())
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (a, t) ->
					procedureXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).filter(me -> !me.equals(element)).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> {
					TextureReference ref = e.getAnnotation(TextureReference.class);
					return ref != null && ref.value() == type;
				}, (a, t) -> {
					TextureReference ref = a.getAnnotation(TextureReference.class);
					if (!Set.of(ref.defaultValues()).contains(t)) {
						for (String template : ref.files()) {
							String file = template.isEmpty() ? t : template.formatted(t);
							if (workspace.getFolderManager()
									.getTextureFile(FilenameUtilsPatched.removeExtension(file), type).equals(texture))
								return true;
						}
					}
					return false;
				})
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchModelUsages(Workspace workspace, Model model) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, Model.class, e -> {
					ResourceReference ref = e.getAnnotation(ResourceReference.class);
					return ref != null && ref.value().equals("model");
				}, (a, t) ->
					model.equals(t) || TexturedModel.getModelTextureMapVariations(model).contains(t)
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchSoundUsages(Workspace workspace, SoundElement sound) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, Sound.class, e -> {
						ResourceReference ref = e.getAnnotation(ResourceReference.class);
						return ref != null && ref.value().equals("sound");
					}, (a, t) ->
					t.getUnmappedValue().replaceFirst("CUSTOM:", "").equals(sound.getName())
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (a, t) ->
					t.contains(">CUSTOM:" + sound.getName() + "</field>")
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchStructureUsages(Workspace workspace, String structure) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> {
						ResourceReference ref = e.getAnnotation(ResourceReference.class);
						return ref != null && ref.value().equals("structure");
					}, (a, t) ->
					t.equals(structure)
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (a, t) ->
					t.contains(">" + structure + "</field>")
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchGlobalVariableUsages(Workspace workspace, String variableName) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (a, t) ->
					t.contains("<field name=\"VAR\">global:" + variableName + "</field>")
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchLocalizationKeyUsages(Workspace workspace, String localizationKey) {
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				(ge != null && workspace.getGenerator().getElementLocalizationKeys(ge).contains(localizationKey)) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (a, t) ->
					t.contains(">" + localizationKey + "</field>")
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	//@formatter:on

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
			if (!Modifier.isStatic(field.getModifiers()) && (clazz.isAssignableFrom(field.getType()) || (validIf != null
					&& validIf.test(field)))) {
				try {
					if (checkValue(field.get(source), field, clazz, validIf, condition))
						return true;
				} catch (ReflectiveOperationException ignored) {
				}
			}
		}
		for (Method method : source.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0 && (
					clazz.isAssignableFrom(method.getReturnType()) || (validIf != null && validIf.test(method)))) {
				try {
					if (checkValue(method.invoke(source), method, clazz, validIf, condition))
						return true;
				} catch (ReflectiveOperationException ignored) {
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
	@SuppressWarnings("unchecked") private static <T> boolean checkValue(@Nullable Object value, AccessibleObject field,
			Class<T> clazz, @Nullable Predicate<AccessibleObject> validIf,
			@Nullable BiPredicate<AccessibleObject, T> condition) {
		if (value == null)
			return false;

		if (clazz.isInstance(value)) { // value of specified type
			return (isCustomObject(value) || validIf == null || validIf.test(field)) && (condition == null
					|| condition.test(field, (T) value));
		} else if (value instanceof Iterable<?> list) { // list of values
			return listHasMatches(list, field, clazz, validIf, condition);
		} else if (value instanceof Map<?, ?> map) { // map with values
			return listHasMatches(map.keySet(), field, clazz, validIf, condition) || listHasMatches(map.values(), field,
					clazz, validIf, condition);
		} else if (value.getClass().isArray()) { // array of values
			int length = Array.getLength(value);
			for (int i = 0; i < length; i++) {
				if (checkValue(Array.get(value, i), field, clazz, validIf, condition))
					return true;
			}
		} else if (isCustomObject(value)) { // value of unknown type but from MCreator system, do recursive check
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
			@Nullable Predicate<AccessibleObject> validIf, @Nullable BiPredicate<AccessibleObject, T> condition) {
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
		return (value.getClass().getModule() == MCreator.class.getModule() || PluginLoader.INSTANCE.getPluginModules()
				.contains(value.getClass().getModule()))
				&& !(value instanceof IWorkspaceProvider); // prevent from being stuck in app structure
	}

}
