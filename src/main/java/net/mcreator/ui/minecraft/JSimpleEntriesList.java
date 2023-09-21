/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class JSimpleEntriesList<T extends JPanel, U> extends JEntriesList {

	protected final List<T> entryList = new ArrayList<>();
	protected final JPanel entries = new JPanel(new GridLayout(0, 1, 0, 2));

	protected JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public JSimpleEntriesList(MCreator mcreator, LayoutManager layout, IHelpContext gui) {
		super(mcreator, layout, gui);
		setOpaque(false);

		topbar.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add.setText(L10N.t("elementgui.common.add_bounding_box"));
		topbar.add(add);
		add("North", topbar);

		entries.setOpaque(false);

		add("Center", new JScrollPane(PanelUtils.pullElementUp(entries)));
	}

	public abstract List<U> getEntries();

	public abstract void setEntries(List<U> box);

}
