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

package net.mcreator.ui.validation;

import net.mcreator.ui.init.UIRES;
import net.mcreator.util.image.IconUtils;

import javax.swing.*;
import java.awt.*;

public record ValidationResult(Type type, String message, boolean isBlocklyResult) {

	public static final ValidationResult PASSED = new ValidationResult(Type.PASSED, "");

	public ValidationResult(Type type, String message) {
		this(type, message, false);
	}

	public enum Type {
		//@formatter:off
		PASSED(new Color(0x93c54b), UIRES.get("18px.ok")),
		WARNING(new Color(0xf0c948), UIRES.get("18px.warning")),
		ERROR(new Color(0xe04442), UIRES.get("18px.remove"));
		//@formatter:on

		private final Color color;
		private final ImageIcon icon;

		Type(Color color, ImageIcon icon) {
			this.color = color;
			this.icon = icon;
		}

		public Color getColor() {
			return color;
		}

		public ImageIcon icon() {
			return icon;
		}

		public ImageIcon icon(int size) {
			return IconUtils.resize(icon, size);
		}
	}
}
