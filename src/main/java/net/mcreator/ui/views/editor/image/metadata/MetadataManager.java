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

import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/*
 Metadata file format
 0-15: MD5 hash of the final rendered image file (byte[16]) - used to compare if this metadata matches the rendered image
 16-19: length of canvas JSON string (int) - canvasJSONStringLength
 20-?: canvas JSON string (byte[canvasJSONStringLength])
 ?-[? + 4]: number of images (int)
 For each image
 0-3: length of PNG bytes (int) - pngBytesLength
 4-?: PNG bytes (byte[pngBytesLength])
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

	@Nullable public static File getMetadataFile(Workspace workspace, File file) {
		TextureType textureType = guessTextureType(workspace, file);
		if (textureType == null)
			return null;
		return new File(workspace.getFolderManager().getWorkspaceCacheDir(),
				"imageEditorMetadata/" + textureType.getID() + "/" + file.getName() + ".imgmeta");
	}

	public static Canvas loadCanvasForFile(Workspace workspace, File file)
			throws MetadataOutdatedException, NullPointerException {
		File metadataFile = getMetadataFile(workspace, file);
		if (metadataFile != null && metadataFile.isFile()) {
			try (DataInputStream dis = new DataInputStream(new FileInputStream(metadataFile))) {
				Canvas retval = null;

				byte[] md5 = new byte[16];
				dis.read(md5);

				if (!MessageDigest.isEqual(md5, filemd5(file))) {
					throw new MetadataOutdatedException("File " + file + " has changed, metadata is invalid", retval);
				}

				int canvasJSONStringLength = dis.readInt();
				byte[] canvasJSONStringBytes = new byte[canvasJSONStringLength];
				dis.read(canvasJSONStringBytes);
				String canvasJSONString = new String(canvasJSONStringBytes, StandardCharsets.UTF_8);
				// TODO: parse canvasString to Canvas object

				int imageCount = dis.readInt();
				BufferedImage[] layerImages = new BufferedImage[imageCount];
				for (int i = 0; i < imageCount; i++) {
					int pngBytesLength = dis.readInt();
					byte[] pngBytes = new byte[pngBytesLength];
					dis.read(pngBytes);
					try (ByteArrayInputStream bais = new ByteArrayInputStream(pngBytes)) {
						layerImages[i] = ImageIO.read(bais);
					}
				}
				// TODO: load layerImages to canvas layers

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
		File metadataFile = getMetadataFile(workspace, file);
		if (metadataFile != null) {
			try (DataOutputStream das = new DataOutputStream(FileUtils.openOutputStream(metadataFile))) {
				das.write(filemd5(file));

				String canvasJSONString = ""; // TODO: serialize canvas to JSON
				byte[] canvasJSONStringBytes = canvasJSONString.getBytes(StandardCharsets.UTF_8);
				das.writeInt(canvasJSONStringBytes.length);
				das.write(canvasJSONStringBytes);

				BufferedImage[] layerImages = new BufferedImage[] {}; // TODO: get layer images from canvas
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
	}

	private static byte[] filemd5(File file) throws NoSuchAlgorithmException, IOException {
		return MessageDigest.getInstance("MD5").digest(Files.readAllBytes(file.toPath()));
	}

}
