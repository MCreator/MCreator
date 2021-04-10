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

public class ColorTheme {
	private String id;
	private RGB blackAccent;
	private RGB darkAccent;
	private RGB lightAccent;
	private RGB grayColor;
	private RGB brightColor;
	private String blocklyCSSFile;
	private String codeEditorFile;

	public String getID() {
		return id;
	}

	public RGB getBlackAccent() {
		return blackAccent;
	}

	public RGB getDarkAccent() {
		return darkAccent;
	}

	public RGB getLightAccent() {
		return lightAccent;
	}

	public RGB getGrayColor() {
		return grayColor;
	}

	public RGB getBrightColor() {
		return brightColor;
	}

	public String getBlocklyCSSFile() {
		if (blocklyCSSFile != null)
			return blocklyCSSFile;
		else
			return id;
	}

	public void setBlocklyCSSFile(String blocklyCSSFile) {
		this.blocklyCSSFile = blocklyCSSFile;
	}

	public String getCodeEditorFile() {
		if (codeEditorFile != null)
			return codeEditorFile;
		else
			return id;
	}

	public void setCodeEditorFile(String codeEditorFile) {
		this.codeEditorFile = codeEditorFile;
	}

	public static class RGB {
		private int red;
		private int green;
		private int blue;

		public Color getRGB() {
			return new Color(red, green, blue);
		}
	}
}
