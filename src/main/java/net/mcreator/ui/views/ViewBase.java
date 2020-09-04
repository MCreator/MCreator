/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.views;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;

import javax.swing.*;
import java.awt.*;

public abstract class ViewBase extends JPanel {

	protected MCreator mcreator;

	protected ViewBase(MCreator mcreator) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public abstract String getViewName();

	public ImageIcon getViewIcon() {
		return null;
	}

	public ViewBase showView() {
		mcreator.mcreatorTabs.addTab(new MCreatorTabs.Tab(this));
		return this;
	}

}
