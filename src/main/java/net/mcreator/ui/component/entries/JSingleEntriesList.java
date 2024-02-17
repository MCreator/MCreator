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

package net.mcreator.ui.component.entries;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class JSingleEntriesList<T extends JPanel, U> extends JEntriesList {

	protected final List<T> entryList = new ArrayList<>();
	protected final JPanel entries = new JPanel(new GridLayout(0, 1, 0, 2));

	protected JPanel topbar = new JPanel(new FlowLayout(FlowLayout.LEFT));

	public JSingleEntriesList(MCreator mcreator, IHelpContext gui) {
		super(mcreator, new BorderLayout(), gui);
		setOpaque(false);

		topbar.setBackground(Theme.current().getAltBackgroundColor());

		topbar.add(add);
		add("North", topbar);

		entries.setOpaque(false);

		JScrollPane scrollPane = new JScrollPane(PanelUtils.pullElementUp(entries)) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setOpaque(false);
		scrollPane.getVerticalScrollBar().setUnitIncrement(15);
		add("Center", scrollPane);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		entryList.forEach(e -> e.setEnabled(enabled));
	}

	public abstract List<U> getEntries();

	public abstract void setEntries(List<U> box);

}
