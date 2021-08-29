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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TerribleModuleHacks {

	public static void openAllUnnamed() {
		ModuleLayer.boot().modules().forEach(module -> module.getPackages()
				.forEach(pn -> addOpens(module, pn, ClassLoader.getSystemClassLoader().getUnnamedModule())));
	}

	public static void openMCreatorRequirements() {
		// Foxtrot core
		ModuleLayer.boot().findModule("java.desktop").ifPresent(
				module -> addOpens(module, "java.awt", foxtrot.pumps.ConditionalEventPump.class.getModule()));

		// MCreator theme
		ModuleLayer.boot().findModule("java.desktop").ifPresent(
				module -> addOpens(module, "sun.awt", net.mcreator.ui.laf.MCreatorLookAndFeel.class.getModule()));
		ModuleLayer.boot().findModule("java.desktop").ifPresent(module -> addOpens(module, "javax.swing.text.html",
				net.mcreator.ui.laf.MCreatorLookAndFeel.class.getModule()));

		// Blockly panel transparency
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
			e.printStackTrace();
		}
	}

}
