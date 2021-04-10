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

import net.mcreator.ui.init.L10N;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.List;

public class Theme {
	private String id;
	private String name;
	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;
	@Nullable private List<ColorTheme> colorThemes;
	private ImageIcon icon;

	public String getID() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		String translated = L10N.t("imageTheme." + id + ".description");
		if (description != null)
			return description;
		else if (!translated.equals("imageTheme." + id + ".description"))
			return translated;
		else
			return null;
	}

	@Nullable public String getCredits() {
		return credits;
	}

	@Nullable public String getVersion() {
		return version;
	}

	@Nullable public List<ColorTheme> getColorThemes() {
		return colorThemes;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	@Override public String toString() {
		return getID() + ": " + getName();
	}
}
