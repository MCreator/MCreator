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

import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * <p>A class used to save the default {@link KeyStroke} and the current one. Each accelerator is linked to a {@link net.mcreator.ui.action.BasicAction}
 * in its constructor.</p>
 */
public class Accelerator {

	public static final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
	public static final int CTRL_SHIFT = CTRL | KeyEvent.SHIFT_DOWN_MASK;
	public static final int CTRL_ALT = CTRL | KeyEvent.ALT_DOWN_MASK;

	/**
	 * <p>The String used to identify the accelerator when saving and loading them. This value is also used to get the localized name of the action.</p>
	 */
	private final String id;
	private final String section;
	/**
	 * <p>This is the default {@link KeyStroke} to use when the value is not found or the user rests the accelerator.</p>
	 */
	private final KeyStroke defaultKeyStroke;
	/**
	 * <p>This is the {@link KeyStroke} used by the code.</p>
	 */
	private KeyStroke keyStroke;

	public Accelerator(String id, KeyStroke keyStroke) {
		this.id = id;

		String[] strings = StringUtils.remove(id, "action.").split("\\.");
		if (strings.length > 1) {
			if (strings[0].equals("image_editor") || strings[1].equals("image_maker"))
				this.section = "image_maker";
			else
				this.section = strings[0];
		} else {
			this.section = "misc";
		}
		if (!AcceleratorsManager.INSTANCE.SECTIONS.contains(this.section))
			AcceleratorsManager.INSTANCE.SECTIONS.add(this.section);

		this.defaultKeyStroke = keyStroke;
		this.keyStroke = keyStroke;
	}

	public String getID() {
		return id;
	}

	public String getSection() {
		return section;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

	public void changeKey(KeyStroke key) {
		this.keyStroke = key;
	}

	/**
	 * <p>Set the key stroke to its default value</p>
	 *
	 * @return <p>The accelerator with the default key stroke</p>
	 */
	public Accelerator reset() {
		this.keyStroke = defaultKeyStroke;
		return this;
	}

	/**
	 * <p>This class is used by the {@link net.mcreator.ui.action.BasicAction} accelerators.</p>
	 */
	public static class ActionAccelerator extends Accelerator {

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
}
