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

package net.mcreator.generator;

import net.mcreator.element.GeneratableElement;
import net.mcreator.generator.template.TemplateExpressionParser;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.TagElement;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

public class TagsUtils {

	public static void generateTagsFiles(Generator generator, Workspace workspace, Map<?, ?> tagsSpecification) {
		workspace.getTagElements().entrySet().parallelStream().forEach(tag -> {
			File tagFile = getTagFileFor(workspace, tag.getKey());
			if (tagFile != null) {
				try {
					Map<String, Object> datamodel = new HashMap<>();
					datamodel.put("tag", tag.getKey());
					datamodel.put("type", tag.getKey().type().name().toLowerCase(Locale.ENGLISH));
					datamodel.put("elements", tag.getValue().stream()
							.map(e -> tag.getKey().type().getMappableElementProvider()
									.apply(workspace, TagElement.getEntryName(e))).toList());
					String json = generator.getTemplateGeneratorFromName("templates")
							.generateFromTemplate(tagsSpecification.get("template").toString(), datamodel);
					JSONWriter.writeJSONToFile(json, tagFile);
				} catch (TemplateGeneratorException e) {
					generator.getLogger().error("Failed to generate code for tag: " + tag.getKey(), e);
				}
			}
		});
	}

	@Nullable public static File getTagFileFor(Workspace workspace, TagElement tagElement) {
		String rawName = (String) workspace.getGeneratorConfiguration().getTagsSpecification().get("name");

		String name = GeneratorTokens.replaceTokens(workspace,
				rawName.replace("@namespace", tagElement.getMinecraftNamespace(workspace))
						.replace("@name", tagElement.getName()).replace("@folder", tagElement.type().getFolder()));

		File tagFile = new File(name);
		if (workspace.getFolderManager().isFileInWorkspace(tagFile)) {
			return tagFile;
		} else {
			return null;
		}
	}

	public static void processDefinitionToTags(Generator generator, GeneratableElement element, @Nullable List<?> tags,
			boolean deleteMode) {
		if (tags != null) {
			for (Object template : tags) {
				Map<?, ?> map = (Map<?, ?>) template;
				TagElement tag = TagElement.fromString((String) map.get("tag"));

				boolean shouldSkip = TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(generator, map,
						element);

				if (map.containsKey("entryprovider")) {
					@SuppressWarnings("unchecked") Collection<String> entryprovider = (Collection<String>) TemplateExpressionParser.processFTLExpression(
							generator, (String) map.get("entryprovider"), element);
					if (entryprovider != null) {
						for (String entry : entryprovider) {
							handleTagEntryEntry(generator, tag, entry, deleteMode || shouldSkip);
						}
					}
				} else if (map.containsKey("entry")) {
					String entry = GeneratorTokens.replaceTokens(generator.getWorkspace(), ((String) map.get("entry"))
									//@formatter:off
									.replace("@modid", generator.getWorkspace().getWorkspaceSettings().getModID())
									.replace("@registryname", element.getModElement().getRegistryName())
							//@formatter:on
					);

					handleTagEntryEntry(generator, tag, entry, deleteMode || shouldSkip);
				} else {
					handleTagEntryEntry(generator, tag, "CUSTOM:" + element.getModElement().getName(),
							deleteMode || shouldSkip);
				}
			}
		}
	}

	private static void handleTagEntryEntry(Generator generator, TagElement tag, String entry, boolean delete) {
		String entryManaged = TagElement.makeEntryManaged(entry);

		List<String> entries = generator.getWorkspace().getTagElements().get(tag);

		if (delete) {
			// only delete the entry if it is present in the list as managed
			if (entries != null && entries.contains(entryManaged)) {
				if (entries.size() == 1) { // only current/our entry is present, delete the tag itself
					generator.getWorkspace().removeTagElement(tag);
				} else {
					generator.getWorkspace().getTagElements().get(tag).remove(entryManaged);
				}
			}
		} else {
			if (entries == null) { // tag does not exist yet, create it
				generator.getWorkspace().addTagElement(tag);
				generator.getWorkspace().getTagElements().get(tag).add(entryManaged);
			}
			// only add this entry if it does not already exist in managed or unmanaged form
			else if (!entries.contains(entryManaged) && !entries.contains(entry)) {
				// We add managed entries to the beginning of the list
				generator.getWorkspace().getTagElements().get(tag).add(0, entryManaged);
			}
		}
	}

}
