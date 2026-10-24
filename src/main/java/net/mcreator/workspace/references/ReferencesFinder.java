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
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.generator.GeneratorWrapper;
import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.resources.Animation;
import net.mcreator.workspace.resources.Model;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
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
		return searchModElementUsages(workspace, List.of(element));
	}

	/**
	 * Searches for usages of the provided mod elements in the workspace.
	 * @param workspace Workspace to look for usages in.
	 * @param elements  Collection of mod elements to look for usages of.
	 * @return Set of mod elements that reference any of the provided mod elements in their generatable element.
	 * 		   The set does not include the provided mod elements themselves.
	 */
	public static Set<ModElement> searchModElementUsages(Workspace workspace, Collection<ModElement> elements) {
		if (elements.isEmpty())
			return Set.of();

		final Set<String> elementNames = elements.stream().map(ModElement::getName).collect(Collectors.toSet());
		final Pattern procedureXmlPattern = Pattern.compile(">(?:CUSTOM:)?(" + joinQuoted(elementNames) + ")([.:]\\w+?)?</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(ModElementReference.class), (a, t) -> {
					ModElementReference ref = a.getAnnotation(ModElementReference.class);
					return !Set.of(ref.defaultValues()).contains(t) && elementNames.contains(
							GeneratorWrapper.getElementPlainName(t));
				}) ||
				anyValueMatches(ge, MappableElement.class, e -> e.isAnnotationPresent(ModElementReference.class), (_, t) ->
						elementNames.contains(GeneratorWrapper.getElementPlainName(t.getUnmappedValue()))
				) ||
				anyValueMatches(ge, Procedure.class, e -> e.isAnnotationPresent(ModElementReference.class), (_, t) ->
					elementNames.contains(t.getName())
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (_, t) ->
					procedureXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).filter(me -> !elementNames.contains(me.getName())).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchTextureUsages(Workspace workspace, File texture, TextureType type) {
		return searchTextureUsages(workspace, List.of(texture), type);
	}

	public static Set<ModElement> searchTextureUsages(Workspace workspace, Collection<File> textures, TextureType type) {
		if (textures.isEmpty())
			return Set.of();

		HashSet<File> textureSet = new HashSet<>(textures);
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> {
					TextureReference ref = e.getAnnotation(TextureReference.class);
					return ref != null && ref.value() == type;
				}, (a, t) -> {
					TextureReference ref = a.getAnnotation(TextureReference.class);
					if (!Set.of(ref.defaultValues()).contains(t) && t.indexOf(':') == -1) {
						for (String template : ref.files()) {
							String file = template.isEmpty() ? t : template.formatted(t);
							if (textureSet.contains(workspace.getFolderManager()
									.getTextureFile(FilenameUtilsPatched.removeExtension(file), type)))
								return true;
						}
					}
					return false;
				}) ||
				anyValueMatches(ge, TextureHolder.class, e -> {
					TextureReference ref = e.getAnnotation(TextureReference.class);
					return ref != null && ref.value() == type;
				}, (a, t) -> {
					TextureReference ref = a.getAnnotation(TextureReference.class);
					String textureName = t.getRawTextureName();
					// Because TextureHolder isCustomObject true, we need to repeat validIf condition because it is skipped for custom objects
					if ((ref != null && ref.value() == type) && !Set.of(ref.defaultValues()).contains(textureName) && textureName.indexOf(':') == -1) {
						for (String template : ref.files()) {
							File toLookup;
							if (template.isEmpty()) {
								try {
									toLookup = t.toFile(type);
								} catch (IOException e) {
									return false;
								}
							} else {
								toLookup = workspace.getFolderManager()
										.getTextureFile(FilenameUtilsPatched.removeExtension(template.formatted(t)), type);
							}
							if (textureSet.contains(toLookup))
								return true;
						}
					}
					return false;
				})
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchModelUsages(Workspace workspace, Model model) {
		return searchModelUsages(workspace, List.of(model));
	}

	public static Set<ModElement> searchModelUsages(Workspace workspace, Collection<Model> models) {
		if (models.isEmpty())
			return Set.of();

		Set<Model> modelSet = new HashSet<>(models);
		Set<File> modelFiles = models.stream().map(Model::getFile).filter(Objects::nonNull).collect(Collectors.toSet());
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, Model.class, e -> {
					ResourceReference ref = e.getAnnotation(ResourceReference.class);
					return ref != null && ref.value().equals("model");
				}, (_, t) ->
					modelFiles.contains(t.getFile()) || modelSet.contains(t)
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchAnimationUsages(Workspace workspace, Collection<Animation> animations) {
		if (animations.isEmpty())
			return Set.of();

		final Set<String> animationNames = animations.stream().map(Animation::getName).collect(Collectors.toSet());
		return getGeneratableElements(workspace).parallelStream()
				.filter(ge ->
						anyValueMatches(ge, net.mcreator.element.parts.Animation.class, e -> {
									ResourceReference ref = e.getAnnotation(ResourceReference.class);
									return ref != null && ref.value().equals("animation");
								}, (_, t) -> animationNames.contains(GeneratorWrapper.getElementPlainName(t.getUnmappedValue())))
				)
				.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchSoundUsages(Workspace workspace, Collection<SoundElement> sounds) {
		if (sounds.isEmpty())
			return Set.of();

		final Set<String> soundNames = sounds.stream().map(SoundElement::getName).collect(Collectors.toSet());
		final Pattern soundXmlPattern = Pattern.compile(">CUSTOM:(" + joinQuoted(soundNames) + ")</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, Sound.class, e -> {
						ResourceReference ref = e.getAnnotation(ResourceReference.class);
						return ref != null && ref.value().equals("sound");
					}, (_, t) ->
					soundNames.contains(t.getUnmappedValue().replaceFirst("CUSTOM:", ""))
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (_, t) ->
					soundXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchStructureUsages(Workspace workspace, Collection<String> structures) {
		if (structures.isEmpty())
			return Set.of();

		final Set<String> structureSet = new HashSet<>(structures);
		final Pattern structureXmlPattern = Pattern.compile(">(" + joinQuoted(structureSet) + ")</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> {
						ResourceReference ref = e.getAnnotation(ResourceReference.class);
						return ref != null && ref.value().equals("structure");
					}, (_, t) ->
					structureSet.contains(t)
				) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (_, t) ->
					structureXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchGlobalVariableUsages(Workspace workspace, Collection<String> variableNames) {
		if (variableNames.isEmpty())
			return Set.of();

		final Pattern variableXmlPattern = Pattern.compile("<field name=\"VAR\">global:(" + joinQuoted(variableNames) + ")</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (_, t) ->
					variableXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	public static Set<ModElement> searchLocalizationKeyUsages(Workspace workspace, Collection<String> localizationKeys) {
		if (localizationKeys.isEmpty())
			return Set.of();

		final Set<String> keySet = new HashSet<>(localizationKeys);
		final Pattern keyXmlPattern = Pattern.compile(">(" + joinQuoted(keySet) + ")</field>");
		return getGeneratableElements(workspace).parallelStream()
			.filter(ge ->
				(ge != null && !Collections.disjoint(workspace.getGenerator().getElementLocalizationKeys(ge), keySet)) ||
				anyValueMatches(ge, String.class, e -> e.isAnnotationPresent(BlocklyXML.class), (_, t) ->
					keyXmlPattern.matcher(t).find()
				)
			)
			.map(GeneratableElement::getModElement).collect(Collectors.toSet());
	}

	//@formatter:on

	private static String joinQuoted(Collection<String> values) {
		return values.stream().map(Pattern::quote).collect(Collectors.joining("|"));
	}

	/**
	 * Checks if values acquired from any valid fields or methods meet the specified condition.
	 * <p/>
	 * Additionally, the following conditions are applied to determine whether a field/method is valid and whether its value should be checked:
	 * - method/field is not static
	 * - method has no parameters
	 * - method type/field type is part of the data model or validIf passes
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
			if (!Modifier.isStatic(field.getModifiers()) && isMemberEligible(field, field.getType(), validIf)) {
				try {
					if (checkValue(field.get(source), field, clazz, validIf, condition))
						return true;
				} catch (ReflectiveOperationException ignored) {
				}
			}
		}
		for (Method method : source.getClass().getMethods()) {
			if (!Modifier.isStatic(method.getModifiers()) && method.getParameterCount() == 0 && isMemberEligible(method,
					method.getReturnType(), validIf)) {
				try {
					if (checkValue(method.invoke(source), method, clazz, validIf, condition))
						return true;
				} catch (ReflectiveOperationException ignored) {
				}
			}
		}

		return false;
	}

	private static boolean isMemberEligible(AccessibleObject member, Class<?> memberType,
			@Nullable Predicate<AccessibleObject> validIf) {
		return validIf != null && validIf.test(member) || GeneratableElement.isDataModelObject(memberType);
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
			return (GeneratableElement.isDataModelObject(value.getClass()) || validIf == null || validIf.test(field))
					&& (condition == null || condition.test(field, (T) value));
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
		} else if (GeneratableElement.isDataModelObject(
				value.getClass())) { // value of unknown type but from MCreator system, do recursive check
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

}
