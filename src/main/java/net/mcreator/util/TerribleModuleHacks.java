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

package net.mcreator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TerribleModuleHacks {

	private static final Logger LOG = LogManager.getLogger("Terrible module hacks");

	public static void openAllFor(Module moduleToOpenFor) {
		ModuleLayer.boot().modules()
				.forEach(module -> module.getPackages().forEach(pn -> addOpens(module, pn, moduleToOpenFor)));
	}

	public static void openMCreatorRequirements() {
		// Required by: LafUtil - to apply custom CSS styles
		ModuleLayer.boot().findModule("java.desktop")
				.ifPresent(module -> addOpens(module, "sun.awt", net.mcreator.ui.laf.LafUtil.class.getModule()));
		ModuleLayer.boot().findModule("java.desktop").ifPresent(
				module -> addOpens(module, "javax.swing.text.html", net.mcreator.ui.laf.LafUtil.class.getModule()));

		// Required by: BlocklyPanel - for transparency
		ModuleLayer.boot().findModule("javafx.web").ifPresent(module -> addOpens(module, "com.sun.javafx.webkit",
				net.mcreator.ui.blockly.BlocklyPanel.class.getModule()));
		ModuleLayer.boot().findModule("javafx.web").ifPresent(
				module -> addOpens(module, "com.sun.webkit", net.mcreator.ui.blockly.BlocklyPanel.class.getModule()));
	}

	public static void addOpens(Module where, String pn, Module toadd) {
		try {
			Method method = where.getClass()
					.getDeclaredMethod("implAddExportsOrOpens", String.class, Module.class, boolean.class,
							boolean.class);
			method.setAccessible(true);
			method.invoke(where, pn, toadd, true, true);
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			LOG.error("Failed to open module: " + where.getName(), e);
		}
	}

}
