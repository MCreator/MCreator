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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.chromium.MCreatorSchemeHandler;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import java.io.InputStream;

/**
 * Rewrites Blockly specific placeholders (language and Blockly theme) in mcreator scheme request paths.
 */
class BlocklyRequestHandler implements MCreatorSchemeHandler.RequestHandler {

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

	@Nullable @Override public InputStream handleRequest(@Nullable MCreator mcreator, String path) {
		return null; // this handler only rewrites request paths
	}

}
