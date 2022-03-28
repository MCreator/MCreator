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

package net.mcreator.util;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class UTF8Forcer {

	public static void forceGlobalUTF8() {
		try {
			System.setProperty("file.encoding" , "UTF-8");
			Field charset = Charset.class.getDeclaredField("defaultCharset");
			charset.setAccessible(true);
			charset.set(null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
