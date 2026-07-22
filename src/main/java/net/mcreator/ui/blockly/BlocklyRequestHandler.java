/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.blockly;

import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.chromium.MCreatorSchemeHandler;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * Rewrites Blockly specific placeholders (language and Blockly theme) in mcreator scheme request paths
 * and serves MC item icons for the Blockly MC item selector fields.
 */
class BlocklyRequestHandler implements MCreatorSchemeHandler.RequestHandler {

	private static final String MCITEM_ICON_PATH_PREFIX = "/blockly/mcitem/";

	private static final String blocklyThemeID;

	static {
		String _blocklyThemeID = Theme.current().getID();
		if (PluginLoader.INSTANCE.getResourceAsStream(
				String.format("themes/%s/styles/blockly.css", Theme.current().getID())) == null) {
			_blocklyThemeID = "default_dark"; // fallback to the default dark theme
		}
		blocklyThemeID = _blocklyThemeID;
	}

	@Override public String rewritePath(@Nullable MCreator mcreator, String path) {
		//@formatter:off
		return path
				.replace("__LANG__", L10N.getBlocklyLangName())
				.replace("__BLOCKLY_THEME_ID__", blocklyThemeID);
		//@formatter:on
	}

	@Nullable @Override public InputStream handleRequest(@Nullable MCreator mcreator, String path) throws Exception {
		if (mcreator != null && path.startsWith(MCITEM_ICON_PATH_PREFIX) && path.endsWith(".png")) {
			String name = URLDecoder.decode(
					path.substring(MCITEM_ICON_PATH_PREFIX.length(), path.length() - ".png".length()),
					StandardCharsets.UTF_8);
			ImageIcon image = new ImageIcon(ImageUtils.resize(MinecraftImageGenerator.generateItemSlot(), 36, 36));
			if (!name.isEmpty() && !name.equals("null"))
				image = ImageUtils.drawOver(image, MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), name), 2, 2,
						32, 32);

			ByteArrayOutputStream os = new ByteArrayOutputStream();
			ImageIO.write(ImageUtils.toBufferedImage(image.getImage()), "PNG", os);
			return new ByteArrayInputStream(os.toByteArray());
		}
		return null;
	}

}
