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
import java.awt.*;
import java.awt.event.KeyEvent;

public class Accelerator {

	public static final int CTRL = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
	public static final int CTRL_SHIFT = CTRL | KeyEvent.SHIFT_DOWN_MASK;
	public static final int CTRL_ALT = CTRL | KeyEvent.ALT_DOWN_MASK;

	private final String id;
	private final Type type;
	private final KeyStroke defaultKeyStroke;
	private KeyStroke keyStroke;

	protected Accelerator(String id, Type type, KeyStroke keyStroke) {
		this.id = type + "." + id;
		this.type = type;
		this.defaultKeyStroke = keyStroke;
		this.keyStroke = keyStroke;
	}

	public String getID() {
		return id;
	}

	public Type getType() {
		return type;
	}

	public KeyStroke getKeyStroke() {
		return keyStroke;
	}

	public void changeKey(KeyStroke key) {
		this.keyStroke = key;
	}

	public Accelerator reset() {
		this.keyStroke = defaultKeyStroke;
		return this;
	}

	public static class ActionAccelerator extends Accelerator {

		public ActionAccelerator(String id, int keyCode, int modifiers) {
			super(id, Type.ACTION, KeyStroke.getKeyStroke(keyCode, modifiers));
		}
	}

	public enum Type {
		ACTION, MOD_ELEMENT;

		@Override public String toString() {
			return name().toLowerCase();
		}
	}
}
