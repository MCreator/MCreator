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

package net.mcreator.themes;

import java.awt.*;

public class ColorScheme {
	private final String id;
	private final String altBackgroundColor;
	private final String backgroundColor;
	private final String actionColor;
	private final String foregroundColor;
	private final String borderColor;
	private String blocklyCSSFile;
	private String codeEditorFile;

	public ColorScheme(String id, String altBackgroundColor, String backgroundColor, String actionColor,
			String foregroundColor, String borderColor) {
		this.id = id;
		this.altBackgroundColor = altBackgroundColor;
		this.backgroundColor = backgroundColor;
		this.actionColor = actionColor;
		this.foregroundColor = foregroundColor;
		this.borderColor = borderColor;
		this.blocklyCSSFile = id;
		this.codeEditorFile = id;
	}

	public String getID() {
		return id;
	}

	public Color getAltBackgroundColor() {
		return Color.decode(altBackgroundColor);
	}

	public Color getBackgroundColor() {
		return Color.decode(backgroundColor);
	}

	public Color getActionColor() {
		return Color.decode(actionColor);
	}

	public Color getForegroundColor() {
		return Color.decode(foregroundColor);
	}

	public Color getBorderColor() {
		return Color.decode(borderColor);
	}

	public String getBlocklyCSSFile() {
		return blocklyCSSFile;
	}

	public void setBlocklyCSSFile(String blocklyCSSFile) {
		this.blocklyCSSFile = blocklyCSSFile;
	}

	public String getCodeEditorFile() {
		return codeEditorFile;
	}

	public void setCodeEditorFile(String codeEditorFile) {
		this.codeEditorFile = codeEditorFile;
	}
}
