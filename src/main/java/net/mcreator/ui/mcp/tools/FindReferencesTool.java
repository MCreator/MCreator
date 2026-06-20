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

import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ReferencesFinder;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Comparator;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class FindReferencesTool extends MCreatorMcpTool<FindReferencesTool.Args> {

	public static class Args {
		public ReferenceType referenceType;
		public String name;
		@Nullable public TextureType textureType;

		public enum ReferenceType {
			MOD_ELEMENT, TEXTURE
		}
	}

	public FindReferencesTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "find_references";
	}

	@Override public String getDescription() {
		return """
				Returns mod element names that reference a mod element or texture.\
				Name should be exact and is case sentivie. For textures, also provide textureType.""";
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.referenceType == null) {
			return completedError("referenceType is required");
		}
		if (input.name == null || input.name.isBlank()) {
			return completedError("name is required");
		}

		Workspace workspace = mcreator.getWorkspace();
		return switch (input.referenceType) {
			case MOD_ELEMENT -> findModElementReferences(workspace, input.name.trim());
			case TEXTURE -> findTextureReferences(mcreator, input.name.trim(), input.textureType);
		};
	}

	private static CompletableFuture<ToolResult> findModElementReferences(Workspace workspace, String modElementName) {
		ModElement modElement = workspace.getModElementByName(modElementName);
		if (modElement == null) {
			return completedError("Mod element not found: " + modElementName);
		}

		Set<ModElement> references = ReferencesFinder.searchModElementUsages(workspace, modElement);
		return completed(ToolResult.collection(
				references.stream().map(ModElement::getName).sorted(Comparator.naturalOrder()).toList()));
	}

	private static CompletableFuture<ToolResult> findTextureReferences(MCreator mcreator, String textureName,
			@Nullable TextureType textureType) {
		if (textureType == null) {
			return completedError("textureType is required for texture references");
		}

		String textureIdentifier = RegistryNameFixer.fix(FilenameUtilsPatched.removeExtension(textureName));
		if (textureIdentifier.isEmpty()) {
			return completedError("Invalid texture name: " + textureName);
		}

		File textureFile = mcreator.getFolderManager().getTextureFile(textureIdentifier, textureType);
		if (!textureFile.isFile()) {
			return completedError("Texture not found: " + textureIdentifier + " (" + textureType.getID() + ")");
		}

		Set<ModElement> references = ReferencesFinder.searchTextureUsages(mcreator.getWorkspace(), textureFile,
				textureType);
		return completed(ToolResult.collection(
				references.stream().map(ModElement::getName).sorted(Comparator.naturalOrder()).toList()));
	}

}
