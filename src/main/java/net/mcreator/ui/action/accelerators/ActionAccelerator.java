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

package net.mcreator.ui.action.accelerators;

import javax.swing.*;
import java.awt.event.KeyEvent;

/**
 * <p>This class is used by the {@link net.mcreator.ui.action.BasicAction} accelerators.</p>
 */
public class ActionAccelerator extends Accelerator {

	/**
	 * <p>A constructor with no default accelerator</p>
	 *
	 * @param id <p>The String used to identify the accelerator when saving and loading them. This value is also used to get the localized name of the action.</p>
	 */
	public ActionAccelerator(String id) {
		this(id, KeyEvent.VK_UNDEFINED, 0);
	}

	public ActionAccelerator(String id, int keyCode, int modifiers) {
		super("action." + id, KeyStroke.getKeyStroke(keyCode, modifiers));
	}
}
