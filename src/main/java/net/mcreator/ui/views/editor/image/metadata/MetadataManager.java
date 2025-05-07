/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.metadata;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 * Metadata File Format
 *
 * Byte layout:
 *
 *  0 -  3: File type identifier (byte[4]) - currently 0x0000_0000_0000_0000
 *  4 - 19: MD5 hash of the final rendered image file (byte[16])
 *          Used to verify if this metadata matches the rendered image.
 * 20 - 23: Length of the canvas JSON string (int) - canvasJSONStringLength
 * 24 - (24 + canvasJSONStringLength - 1): Canvas JSON string (byte[canvasJSONStringLength])
 *
 * Next 4 bytes:
 * [canvasEnd] - [canvasEnd + 3]: Number of images (int) - imageCount
 *
 * For each image (repeated imageCount times):
 *   0 -  3: Length of PNG byte data (int) - pngBytesLength
 *   4 - (4 + pngBytesLength - 1): PNG image data (byte[pngBytesLength])
 *
 */

public class MetadataManager {

	private static final Logger LOG = LogManager.getLogger(MetadataManager.class);

	@Nullable private static TextureType guessTextureType(Workspace workspace, File file) {
		for (TextureType textureType : TextureType.values()) {
			File folder = workspace.getFolderManager().getTexturesFolder(textureType);
			if (folder != null && file.getAbsolutePath().startsWith(folder.getAbsolutePath())) {
				return textureType;
			}
		}

		return null;
	}

	@Nonnull public static File getMetadataFile(Workspace workspace, File file) {
		TextureType textureType = guessTextureType(workspace, file);
		// if texture type exists, we store override relative to its type to maintain
		// metadata across different workspace folder structures
		if (textureType != null) {
			return new File(workspace.getFolderManager().getWorkspaceCacheDir(),
					"imageEditorMetadata/" + textureType.getID() + "/" + file.getName() + ".imgmeta");
		} else {
			String relativePath = file.getAbsolutePath()
					.substring(workspace.getWorkspaceFolder().getAbsolutePath().length());
			return new File(workspace.getFolderManager().getWorkspaceCacheDir(),
					"imageEditorMetadata/" + relativePath + ".imgmeta");
		}
	}

	public static Canvas loadCanvasForFile(Workspace workspace, File file, ImageMakerView canvasOwner)
			throws MetadataOutdatedException, NullPointerException {
		if (!PreferencesManager.PREFERENCES.imageEditor.storeMetadata.get())
			throw new NullPointerException("Metadata is disabled in preferences");

		File metadataFile = getMetadataFile(workspace, file);
		if (metadataFile.isFile()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(metadataFile))) {
				Canvas retval;

				// Read file header - unused for now
				dis.readInt();

				byte[] md5 = new byte[16];
				dis.read(md5);

				// Extract canvas JSON string
				int canvasJSONStringLength = dis.readInt();
				byte[] canvasJSONStringBytes = new byte[canvasJSONStringLength];
				dis.read(canvasJSONStringBytes);
				String canvasJSONString = new String(canvasJSONStringBytes, StandardCharsets.UTF_8);

				// Extract layer rasters
				int imageCount = dis.readInt();
				BufferedImage[] rasters = new BufferedImage[imageCount];
				for (int i = 0; i < imageCount; i++) {
					int pngBytesLength = dis.readInt();
					byte[] pngBytes = new byte[pngBytesLength];
					dis.read(pngBytes);
					try (ByteArrayInputStream bais = new ByteArrayInputStream(pngBytes)) {
						rasters[i] = ImageIO.read(bais);
					}
				}

				Gson gson = new GsonBuilder().setStrictness(Strictness.LENIENT)
						.registerTypeAdapter(Canvas.class, new Canvas.GSONAdapter(canvasOwner).setRasters(rasters))
						.create();
				retval = gson.fromJson(canvasJSONString, Canvas.class);

				if (!MessageDigest.isEqual(md5, filemd5(file))) {
					throw new MetadataOutdatedException("File " + file + " has changed, metadata is invalid", retval);
				}

				return retval;
			} catch (MetadataOutdatedException e) {
				throw e;
			} catch (Exception e) {
				LOG.warn("Failed to load metadata for {}", file, e);
			}
		}

		throw new NullPointerException("Could not correctly determine metadata for " + file);
	}

	public static void saveCanvas(Workspace workspace, File file, Canvas canvas) {
		if (!PreferencesManager.PREFERENCES.imageEditor.storeMetadata.get())
			return;

		File metadataFile = getMetadataFile(workspace, file);
		try (DataOutputStream das = new DataOutputStream(FileUtils.openOutputStream(metadataFile))) {
			das.writeInt(0); // File type identifier - unused for now

			das.write(filemd5(file));

			Gson gson = new GsonBuilder().registerTypeAdapter(Canvas.class,
					new Canvas.GSONAdapter(canvas.getImageMakerView())).create();
			String canvasJSONString = gson.toJson(canvas);
			byte[] canvasJSONStringBytes = canvasJSONString.getBytes(StandardCharsets.UTF_8);
			das.writeInt(canvasJSONStringBytes.length);
			das.write(canvasJSONStringBytes);

			BufferedImage[] layerImages = canvas.stream().map(Layer::getRaster).toArray(BufferedImage[]::new);
			das.writeInt(layerImages.length);
			for (BufferedImage layerImage : layerImages) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ImageIO.write(layerImage, "png", baos);
				byte[] pngBytes = baos.toByteArray();
				das.writeInt(pngBytes.length);
				das.write(pngBytes);
			}
		} catch (Exception e) {
			LOG.warn("Failed to save metadata for {}", file, e);
		}
	}

	private static byte[] filemd5(File file) throws NoSuchAlgorithmException, IOException {
		return MessageDigest.getInstance("MD5").digest(Files.readAllBytes(file.toPath()));
	}

}
