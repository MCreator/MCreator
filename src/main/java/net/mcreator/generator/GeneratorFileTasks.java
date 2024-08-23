/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.io.FileIO;
import net.mcreator.io.TrackingFileIO;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.ModelUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorFileTasks {

	public static void runFileTasks(Generator generator, @Nullable List<?> setupTasks) {
		if (setupTasks == null)
			return;

		setupTasks.forEach(task -> {
			String taskType = (String) ((Map<?, ?>) task).get("task");
			switch (taskType) {
			case "copy_file" -> {
				File from = new File(GeneratorTokens.replaceTokens(generator.getWorkspace(),
						(String) ((Map<?, ?>) task).get("from")));
				File to = new File(
						GeneratorTokens.replaceTokens(generator.getWorkspace(), (String) ((Map<?, ?>) task).get("to")));
				if (generator.getWorkspace().getFolderManager().isFileInWorkspace(to) && from.isFile())
					FileIO.copyFile(from, to);
			}
			case "copy_and_resize_image" -> {
				File from = new File(GeneratorTokens.replaceTokens(generator.getWorkspace(),
						(String) ((Map<?, ?>) task).get("from")));
				File to = new File(
						GeneratorTokens.replaceTokens(generator.getWorkspace(), (String) ((Map<?, ?>) task).get("to")));
				int w = Integer.parseInt(GeneratorTokens.replaceTokens(generator.getWorkspace(),
						(String) ((Map<?, ?>) task).get("width")));
				int h = Integer.parseInt(GeneratorTokens.replaceTokens(generator.getWorkspace(),
						(String) ((Map<?, ?>) task).get("height")));
				if (generator.getWorkspace().getFolderManager().isFileInWorkspace(to) && from.isFile()) {
					try {
						BufferedImage image = ImageIO.read(from);
						BufferedImage resized = ImageUtils.toBufferedImage(ImageUtils.resize(image, w, h));
						ImageIO.write(resized, "png", to);
					} catch (IOException e) {
						generator.getLogger().warn("Failed to read image file for resizing", e);
					}
				} else if (generator.getWorkspace().getFolderManager().isFileInWorkspace(to)) {
					try {
						BufferedImage resized = ImageUtils.toBufferedImage(
								ImageUtils.resize(UIRES.getBuiltIn("fallback").getImage(), w, h));
						ImageIO.write(resized, "png", to);
					} catch (IOException e) {
						generator.getLogger().warn("Failed to read image file for resizing", e);
					}
				}
			}
			case "copy_models" -> {
				File to = new File(
						GeneratorTokens.replaceTokens(generator.getWorkspace(), (String) ((Map<?, ?>) task).get("to")));

				if (generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to, "model.dummy"))) {
					if (((Map<?, ?>) task).get("cleanupBeforeCopy") != null && Boolean.parseBoolean(
							((Map<?, ?>) task).get("cleanupBeforeCopy").toString())) {
						// empty directory to remove stale model files
						FileIO.emptyDirectory(to);
					}

					List<Model> modelList = Model.getModels(generator.getWorkspace());

					switch (((Map<?, ?>) task).get("type").toString()) {
					case "OBJ":
						for (Model model : modelList) {
							if (model.getType() == Model.Type.OBJ) {
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> FileIO.copyFile(f, new File(to, f.getName())));
							}
						}
						break;
					case "OBJ_inlinetextures":
						String prefix = GeneratorTokens.replaceTokens(generator.getWorkspace(),
								(String) ((Map<?, ?>) task).get("prefix"));
						for (Model model : modelList) {
							if (model.getType() == Model.Type.OBJ) {
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> ModelUtils.copyOBJorMTLApplyTextureMapping(
												generator.getWorkspace(), f, new File(to, f.getName()), model, prefix));
							}
						}
						break;
					case "JSON":
						for (Model model : modelList) {
							if (model.getType() == Model.Type.JSON) {
								FileIO.copyFile(model.getFile(), new File(to, model.getFile().getName()));
							}
						}
						break;
					case "JSON_noinlinetextures":
						for (Model model : modelList) {
							if (model.getType() == Model.Type.JSON) {
								String jsonorig = FileIO.readFileToString(model.getFile());
								String notextures = ModelUtils.removeInlineTexturesSectionFromJSONModel(jsonorig);
								TrackingFileIO.writeFile(generator, notextures, new File(to, model.getFile().getName()));
							}
						}
						break;
					case "JAVA_viatemplate":
						String template = GeneratorTokens.replaceTokens(generator.getWorkspace(),
								(String) ((Map<?, ?>) task).get("template"));
						for (Model model : modelList) {
							if (model.getType() == Model.Type.JAVA) {
								String modelCode = FileIO.readFileToString(model.getFile());
								try {
									modelCode = generator.getTemplateGeneratorFromName("templates")
											.generateFromTemplate(template, new HashMap<>(
													Map.of("modelname", model.getReadableName(), "model", modelCode,
															"modelregistryname",
															RegistryNameFixer.fromCamelCase(model.getReadableName()))));
								} catch (TemplateGeneratorException e) {
									generator.getLogger()
											.error("Failed to generate code for model: {}", model.getFile(), e);
								}
								ClassWriter.writeClassToFile(generator.getWorkspace(), modelCode,
										new File(to, model.getReadableName() + ".java"), true);
							}
						}
						break;
					}
				}
			}
			}
		});
	}

}
