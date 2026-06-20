/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.mcp.tools;

import net.mcreator.generator.mapping.MappableElement;
import net.mcreator.generator.mapping.NameMapper;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.minecraft.TagType;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.TagElement;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class TagTool extends MCreatorMcpTool<TagTool.Args> {

	private static final int MAX_ENTRIES = 64;

	private static final Set<TagType> READ_ONLY = Set.of(TagType.PAINTING_VARIANTS, TagType.BANNER_PATTERNS,
			TagType.POINTS_OF_INTEREST, TagType.VILLAGER_TRADES);

	public static class Args {
		public String tagType;
		public String tagResourcePath;
		@Nullable public List<String> entries;
	}

	public TagTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "tag";
	}

	@Override public String getDescription() {
		return "Creates or updates a workspace tag (tagType + tagResourcePath). Adds entries if provided.";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("tags")) {
			return completedError("Generator does not support tags");
		}

		TagType tagType = parseTagType(input.tagType);
		if (tagType == null || READ_ONLY.contains(tagType)) {
			return completedError("Invalid tagType");
		}

		Workspace workspace = mcreator.getWorkspace();
		String resourcePath = parseResourcePath(workspace, input.tagResourcePath);
		if (resourcePath == null) {
			return completedError("Invalid tagResourcePath");
		}

		TagElement tag = new TagElement(tagType, resourcePath);
		boolean created = !workspace.getTagElements().containsKey(tag);
		if (created) {
			if (!Arrays.asList(TagType.visibleValues()).contains(tagType)) {
				return completedError("Invalid tagType");
			}
			workspace.addTagElement(tag);
		}

		if (input.entries == null || input.entries.isEmpty()) {
			mcreator.reloadWorkspaceTabContents();
			return completedText(created ? "Tag created" : "Tag already exists");
		}

		if (input.entries.size() > MAX_ENTRIES) {
			return completedError("Too many entries (max " + MAX_ENTRIES + ")");
		}

		ArrayList<TagElement.Entry> existing = workspace.getTagElements().get(tag);
		List<String> added = new ArrayList<>();
		List<String> rejected = new ArrayList<>();

		for (String raw : input.entries) {
			if (raw == null || raw.isBlank()) {
				rejected.add("(empty)");
				continue;
			}
			String entry = raw.trim();
			if (!isValidEntry(workspace, tagType, entry)) {
				rejected.add(raw.trim());
				continue;
			}
			TagElement.Entry tagEntry = TagElement.Entry.unmanaged(entry);
			if (existing.contains(tagEntry)) {
				continue;
			}
			existing.add(tagEntry);
			added.add(entry);
		}

		if (added.isEmpty()) {
			return completedError(rejected.isEmpty() ?
					"No new entries added" :
					"No entries added. Rejected: " + String.join(", ", rejected));
		}

		workspace.markDirty();
		mcreator.reloadWorkspaceTabContents();

		Map<String, Object> result = new LinkedHashMap<>();
		result.put("created", created);
		result.put("added", added);
		if (!rejected.isEmpty()) {
			result.put("rejected", rejected);
		}
		return completedObject(result);
	}

	@Nullable private static TagType parseTagType(@Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			return TagType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
		} catch (IllegalArgumentException ignored) {
			return null;
		}
	}

	@Nullable private static String parseResourcePath(Workspace workspace, @Nullable String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}

		String path = raw.trim();
		int colon = path.indexOf(':');
		String namespace = colon < 0 ? "minecraft" : path.substring(0, colon);
		String name = colon < 0 ? path : path.substring(colon + 1);

		if (name.isEmpty() || !isRegistryName(namespace) || !isRegistryName(name)) {
			return null;
		}

		if (namespace.equals(workspace.getWorkspaceSettings().getModID()) || namespace.equals("mod")) {
			namespace = "mod";
		} else {
			namespace = RegistryNameFixer.fix(namespace);
		}
		return namespace + ":" + RegistryNameFixer.fix(name);
	}

	private static boolean isRegistryName(String value) {
		String fixed = RegistryNameFixer.fix(value);
		return !fixed.isEmpty() && Character.isLetter(fixed.charAt(0))
				&& fixed.equals(RegistryNameFixer.fix(value.trim()));
	}

	private static boolean isValidEntry(Workspace workspace, TagType tagType, String entry) {
		if (entry.startsWith(NameMapper.MCREATOR_PREFIX)) {
			return MappableElement.validateReference(entry, workspace, null);
		}
		if (entry.startsWith(NameMapper.EXTERNAL_PREFIX)) {
			return !entry.substring(NameMapper.EXTERNAL_PREFIX.length()).isBlank();
		}
		if (entry.startsWith("#")) {
			return true;
		}
		return tagType.getMappableElementProvider().apply(workspace, entry).isValidReference();
	}

}
