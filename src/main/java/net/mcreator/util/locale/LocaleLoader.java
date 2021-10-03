/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.util.locale;

import net.mcreator.plugin.PluginLoader;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocaleLoader {

	private static final UTF8Control control = new UTF8Control();

	public static ResourceBundle getBundle(String baseName, Locale locale) {
		try {
			return control.newBundle(baseName, locale, null, PluginLoader.INSTANCE, false);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
