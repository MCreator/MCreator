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

import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import net.mcreator.element.parts.TextureHolder;
import net.mcreator.io.mcp.protocol.SchemaDescription;
import net.mcreator.io.mcp.tool.ToolResult;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.workspace.resources.AnimationImportActions;
import net.mcreator.ui.action.impl.workspace.resources.ModelImportActions;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.mcp.MCreatorMcpTool;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.resources.Animation;
import net.mcreator.workspace.resources.TexturedModel;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ImportModelTool extends MCreatorMcpTool<ImportModelTool.Args> {

	private static final Pattern TEXTURE_REFERENCE = Pattern.compile("\"#(.*?)\"");

	public static class Args {
		public Type type;
		@SchemaDescription("Model source: Java class source code (e.g. from Blockbench) for JAVA and JAVA_ANIMATION, Minecraft JSON model for JSON, Bedrock geometry JSON for BEDROCK")
		public String code;
		@SchemaDescription("Model name for JSON, ignored for other types where the class name or geometry identifier is used")
		@Nullable public String name;
		@SchemaDescription("For JSON, maps each texture key referenced in the model as \"#key\" (without #) to an existing block texture name")
		@Nullable public Map<String, String> textures;

		public enum Type {
			JAVA, JAVA_ANIMATION, JSON, BEDROCK
		}
	}

	public ImportModelTool(Supplier<MCreator> currentMCreator) {
		super(currentMCreator, Args.class);
	}

	@Override public String getName() {
		return "import_model";
	}

	@Override public String getDescription() {
		return """
				Imports a custom model or model animation into the workspace and returns the name to reference it with \
				in mod elements supporting this model type (see get_mod_element_schema).\
				JAVA imports an entity model from Java source code in the Java model format of the current generator, \
				the class name is prefixed with Model if missing.\
				JAVA_ANIMATION imports Java model animation definitions, subanimations are referenced as <name>.<subanimation>.\
				JSON imports a Minecraft JSON block or item model, requires name and textures mapping every texture \
				reference in the model to an existing block texture.\
				BEDROCK imports a Bedrock geometry model named after its geometry identifier.""";
	}

	@Override protected Boolean getReadOnlyHint() {
		return false;
	}

	@Override protected CompletableFuture<ToolResult> call(MCreator mcreator, Args input) {
		if (input.type == null) {
			return completedError("type must be provided");
		}
		if (input.code == null || input.code.isBlank()) {
			return completedError("code must be provided");
		}

		Map<String, Object> response = new HashMap<>();
		try {
			String name = switch (input.type) {
				case JAVA -> importJavaModel(mcreator, input.code);
				case JAVA_ANIMATION -> importJavaModelAnimation(mcreator, input.code, response);
				case JSON -> importJSONModel(mcreator, input);
				case BEDROCK -> importBedrockModel(mcreator, input.code);
			};
			response.put("success", true);
			response.put("name", name);
		} catch (IllegalArgumentException e) {
			return completedError(e.getMessage());
		}

		ThreadUtil.runOnSwingThreadAndWait(() -> {
			mcreator.reloadWorkspaceTabContents();
			if (mcreator.getTabs().getCurrentTab().getContent() instanceof ModElementGUI<?> modElementGUI)
				modElementGUI.reloadDataLists();
		});

		return completedObject(response);
	}

	private static String importJavaModel(MCreator mcreator, String code) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("model_java")) {
			throw new IllegalArgumentException("Java models are not supported by the current generator");
		}

		String modelName;
		try {
			modelName = ModelImportActions.importJavaModel(null, mcreator.getWorkspace(), code);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Failed to import model: " + e.getMessage());
		}
		if (modelName == null) {
			throw new IllegalArgumentException("Failed to import model");
		}
		return modelName;
	}

	private static String importJavaModelAnimation(MCreator mcreator, String code, Map<String, Object> response) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("model_animations_java")) {
			throw new IllegalArgumentException("Java model animations are not supported by the current generator");
		}

		String animationName;
		try {
			animationName = AnimationImportActions.importJavaModelAnimation(null, mcreator.getWorkspace(), code);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Failed to import animation: " + e.getMessage());
		}
		if (animationName == null) {
			throw new IllegalArgumentException("Failed to import animation");
		}

		for (Animation animation : Animation.getAnimations(mcreator.getWorkspace())) {
			if (animation.getName().equals(animationName)) {
				response.put("subanimations", animation.getSubanimations());
				break;
			}
		}
		return animationName;
	}

	private static String importJSONModel(MCreator mcreator, Args input) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("model_json")) {
			throw new IllegalArgumentException("JSON models are not supported by the current generator");
		}
		if (input.name == null || input.name.isBlank()) {
			throw new IllegalArgumentException("name must be provided for JSON models");
		}

		String modelName = RegistryNameFixer.fix(input.name);
		if (modelName.isEmpty()) {
			throw new IllegalArgumentException("Invalid model name");
		}
		if (new File(mcreator.getFolderManager().getModelsDir(), modelName + ".json").isFile()) {
			throw new IllegalArgumentException("Model " + modelName + " already exists");
		}

		try {
			if (!JsonParser.parseString(input.code).isJsonObject()) {
				throw new IllegalArgumentException("code must be a JSON object");
			}
		} catch (JsonSyntaxException e) {
			throw new IllegalArgumentException("code is not valid JSON: " + e.getMessage());
		}

		Map<String, TextureHolder> textureMap = new HashMap<>();
		Matcher matcher = TEXTURE_REFERENCE.matcher(input.code);
		while (matcher.find()) {
			String key = matcher.group(1);
			String texture = input.textures != null ? input.textures.get(key) : null;
			if (texture == null || texture.isBlank()) {
				throw new IllegalArgumentException("textures does not define texture for key " + key);
			}
			if (!mcreator.getFolderManager().getTextureFile(texture, TextureType.BLOCK).isFile()) {
				throw new IllegalArgumentException("Block texture " + texture + " does not exist");
			}
			textureMap.put(key, new TextureHolder(mcreator.getWorkspace(), texture));
		}

		ModelImportActions.importJSONModel(mcreator.getWorkspace(), modelName, input.code,
				Map.of("default", new TexturedModel.TextureMapping("default", textureMap)));

		return modelName + ":default";
	}

	private static String importBedrockModel(MCreator mcreator, String code) {
		if (!mcreator.getGeneratorStats().hasBaseCoverage("model_bedrock")) {
			throw new IllegalArgumentException("Bedrock models are not supported by the current generator");
		}

		try {
			return ModelImportActions.importBedrockModel(mcreator.getWorkspace(), code);
		} catch (RuntimeException e) {
			throw new IllegalArgumentException("Failed to import model: " + e.getMessage());
		}
	}

}
