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

package net.mcreator.ui.laf.renderer;

import net.mcreator.ui.init.UIRES;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.DecoratableIcon;

import javax.swing.*;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class RSTAIcons {

	private static final Logger LOG = LogManager.getLogger("RSTA icons");

	private static final Map<Icon, Icon> lookup_cache = new IdentityHashMap<>();

	public static Icon themeRSTAIcon(Icon icon) {
		if (icon instanceof DecoratableIcon decoratableIcon) {
			return RSTAIcons.rstaIconToThemeIcon(decoratableIcon);
		} else if (icon instanceof ImageIcon imageIcon) {
			return RSTAIcons.rstaIconToThemeIcon(imageIcon);
		}

		return icon;
	}

	public static Icon rstaIconToThemeIcon(ImageIcon imageIcon) {
		if (lookup_cache.containsKey(imageIcon))
			return lookup_cache.get(imageIcon);

		if (imageIcon.getDescription().contains("org/fife/rsta/ac/java")) {
			return UIRES.get("rsta." + FilenameUtils.getBaseName(imageIcon.getDescription()));
		}

		return imageIcon;
	}

	public static Icon rstaIconToThemeIcon(DecoratableIcon icon) {
		return lookup_cache.computeIfAbsent(icon, key -> {
			try {
				Class<?> decoratableIconClass = Class.forName("org.fife.rsta.ac.java.DecoratableIcon");

				Field mainIconFiled = decoratableIconClass.getDeclaredField("mainIcon");
				mainIconFiled.setAccessible(true);
				Icon mainIcon = (Icon) mainIconFiled.get(key);

				DecoratableIcon newIcon;
				if (mainIcon instanceof DecoratableIcon decoratableIcon) {
					newIcon = new DecoratableIcon(rstaIconToThemeIcon(decoratableIcon));
				} else if (mainIcon instanceof ImageIcon imageIcon) {
					newIcon = new DecoratableIcon(rstaIconToThemeIcon(imageIcon));
				} else {
					newIcon = new DecoratableIcon(mainIcon);
				}

				Field decorationsFiled = decoratableIconClass.getDeclaredField("decorations");
				decorationsFiled.setAccessible(true);
				List<?> decorationsList = (List<?>) decorationsFiled.get(key);

				if (decorationsList != null) {
					for (Object obj : decorationsList) {
						if (obj instanceof DecoratableIcon decoratableIcon) {
							newIcon.addDecorationIcon(rstaIconToThemeIcon(decoratableIcon));
						} else if (obj instanceof ImageIcon imageIcon) {
							newIcon.addDecorationIcon(rstaIconToThemeIcon(imageIcon));
						} else if (obj instanceof Icon _icon) {
							newIcon.addDecorationIcon(_icon);
						}
					}
				}

				return newIcon;
			} catch (Exception e) {
				LOG.error("Failed to load icon: " + key, e);
				return null;
			}
		});
	}

}
