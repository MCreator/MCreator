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
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.ModelUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GeneratorFileTasks {

	public static void runFileTasks(Generator generator, List<?> setupTaks) {
		if (setupTaks != null) {
			setupTaks.forEach(task -> {
				String taskType = (String) ((Map<?, ?>) task).get("task");
				switch (taskType) {
				case "empty_dir" -> {
					String dir = (String) ((Map<?, ?>) task).get("dir");
					List<?> excludes_raw = (List<?>) ((Map<?, ?>) task).get("excludes");
					List<String> excludes = new ArrayList<>();
					if (excludes_raw != null) {
						for (Object o : excludes_raw)
							excludes.add(GeneratorTokens.replaceTokens(generator.getWorkspace(), (String) o));
					}
					if (generator.getWorkspace().getFolderManager().isFileInWorkspace(
							new File(GeneratorTokens.replaceTokens(generator.getWorkspace(), dir)))) {
						FileIO.emptyDirectory(new File(GeneratorTokens.replaceTokens(generator.getWorkspace(), dir)),
								excludes.toArray(new String[0]));
					}
				}
				case "sync_dir" -> {
					String from = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("to"));
					if (generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to))) {
						FileIO.emptyDirectory(
								new File(to)); // first delete existing contents of the destination directory
						FileIO.copyDirectory(new File(from), new File(to));
					}
				}
				case "copy_file" -> {
					String from = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("to"));
					if (generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to)) && new File(
							from).isFile())
						FileIO.copyFile(new File(from), new File(to));
				}
				case "copy_and_resize_image" -> {
					String from = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("to"));
					int w = Integer.parseInt(GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("width")));
					int h = Integer.parseInt(GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("height")));
					if (generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to)) && new File(
							from).isFile()) {
						try {
							BufferedImage image = ImageIO.read(new File(from));
							BufferedImage resized = ImageUtils.toBufferedImage(ImageUtils.resize(image, w, h));
							ImageIO.write(resized, "png", new File(to));
						} catch (IOException e) {
							generator.getLogger().warn("Failed to read image file for resizing", e);
						}
					} else if (generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to))) {
						try {
							BufferedImage resized = ImageUtils.toBufferedImage(
									ImageUtils.resize(UIRES.getBuiltIn("fallback").getImage(), w, h));
							ImageIO.write(resized, "png", new File(to));
						} catch (IOException e) {
							generator.getLogger().warn("Failed to read image file for resizing", e);
						}
					}
				}
				case "copy_models" -> {
					String to = GeneratorTokens.replaceTokens(generator.getWorkspace(),
							(String) ((Map<?, ?>) task).get("to"));
					if (!generator.getWorkspace().getFolderManager().isFileInWorkspace(new File(to, "model.dummy")))
						break;

					List<Model> modelList = Model.getModels(generator.getWorkspace());

					String type = (String) ((Map<?, ?>) task).get("type");
					switch (type) {
					case "OBJ":
						for (Model model : modelList)
							if (model.getType() == Model.Type.OBJ)
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> FileIO.copyFile(f, new File(to, f.getName())));
						break;
					case "OBJ_inlinetextures":
						String prefix = GeneratorTokens.replaceTokens(generator.getWorkspace(),
								(String) ((Map<?, ?>) task).get("prefix"));
						for (Model model : modelList)
							if (model.getType() == Model.Type.OBJ) {
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> ModelUtils.copyOBJorMTLApplyTextureMapping(f,
												new File(to, f.getName()), model, prefix));
							}
						break;
					case "JSON":
						for (Model model : modelList)
							if (model.getType() == Model.Type.JSON)
								FileIO.copyFile(model.getFile(), new File(to, model.getFile().getName()));
						break;
					case "JSON_noinlinetextures":
						for (Model model : modelList)
							if (model.getType() == Model.Type.JSON) {
								String jsonorig = FileIO.readFileToString(model.getFile());
								String notextures = ModelUtils.removeInlineTexturesSectionFromJSONModel(jsonorig);
								FileIO.writeStringToFile(notextures, new File(to, model.getFile().getName()));
							}
						break;
					case "JAVA_viatemplate":
						String template = GeneratorTokens.replaceTokens(generator.getWorkspace(),
								(String) ((Map<?, ?>) task).get("template"));
						for (Model model : modelList)
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
											.error("Failed to generate code for model: " + model.getFile(), e);
								}
								ClassWriter.writeClassToFile(generator.getWorkspace(), modelCode,
										new File(to, model.getReadableName() + ".java"), true);
							}
						break;
					}
				}
				}
			});
		}
	}

}
