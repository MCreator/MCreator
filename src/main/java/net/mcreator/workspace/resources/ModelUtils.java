/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.workspace.resources;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.javagl.obj.Mtl;
import de.javagl.obj.MtlReader;
import de.javagl.obj.MtlWriter;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.JSONWriter;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ModelUtils {

	private static final Logger LOG = LogManager.getLogger(ModelUtils.class);

	public static String removeInlineTexturesSectionFromJSONModel(String jsonModel) {
		try {
			JsonElement json = JsonParser.parseString(jsonModel);
			json.getAsJsonObject().remove("textures");
			return JSONWriter.gson.toJson(json);
		} catch (Exception e) {
			LOG.info("Failed to remove inline textures from JSON model");
		}
		return jsonModel;
	}

	public static void copyOBJorMTLApplyTextureMapping(File objFrom, File objTo, Model model, String prefix) {
		if (FilenameUtils.getExtension(objFrom.getName()).equalsIgnoreCase("mtl")) {
			Map<String, TexturedModel.TextureMapping> textureMappingMap = TexturedModel
					.getTextureMappingsForModel(model);

			if (textureMappingMap != null && textureMappingMap.containsKey("default")) {
				Map<String, String> textureMap = textureMappingMap.get("default").getTextureMap();

				try {
					List<Mtl> mtlList = MtlReader.read(new FileInputStream(objFrom));
					for (Mtl mtlElement : mtlList) {
						String elementName = mtlElement.getName();
						if (textureMap.containsKey(elementName)) {
							mtlElement.setMapKd(prefix + textureMap.get(elementName));
						}
					}

					MtlWriter.write(mtlList, new FileOutputStream(objTo));
				} catch (Exception ignore) {
				}

				return;
			}
		}

		// fallback
		FileIO.copyFile(objFrom, objTo);
	}

}
