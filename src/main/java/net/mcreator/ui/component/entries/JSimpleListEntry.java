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

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;

public abstract class JSimpleListEntry<T> extends JPanel {

	protected final JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private final JButton remove = new JButton(UIRES.get("16px.clear"));
	private final JButton moveUp = new JButton(UIRES.get("18px.up"));
	private final JButton moveDown = new JButton(UIRES.get("18px.down"));

	protected final JPanel parent;

	public JSimpleListEntry(JPanel parent, List<? extends JSimpleListEntry<T>> entryList) {
		this.parent = parent;

		setBackground((Theme.current().getAltBackgroundColor()).darker());
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JComponent container = PanelUtils.expandHorizontally(this);

		line.setOpaque(false);

		parent.add(container);

		remove.setText(L10N.t("simple_list_entry.remove"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
			entryRemovedByUserHandler();
		});

		moveUp.setToolTipText(L10N.t("simple_list_entry.move_up"));
		moveUp.addActionListener(e -> {
			int i = entryList.indexOf(this);
			if (i > 0) {
				Collections.swap(entryList, i - 1, i);
				swapEntries(parent, i - 1, i);
				parent.revalidate();
				parent.repaint();
			}
		});

		moveDown.setToolTipText(L10N.t("simple_list_entry.move_down"));
		moveDown.addActionListener(e -> {
			int i = entryList.indexOf(this);
			if (i >= 0 && i < entryList.size() - 1) {
				Collections.swap(entryList, i, i + 1);
				swapEntries(parent, i, i + 1);
				parent.revalidate();
				parent.repaint();
			}
		});

		add(PanelUtils.westAndCenterElement(PanelUtils.join(moveUp, moveDown),
				PanelUtils.centerAndEastElement(line, PanelUtils.join(remove))));

		parent.revalidate();
		parent.repaint();
	}

	private static void swapEntries(JPanel parent, int thisIndex, int otherIndex) {
		Component[] components = parent.getComponents();
		parent.removeAll();
		var thisComp = components[thisIndex];
		components[thisIndex] = components[otherIndex];
		components[otherIndex] = thisComp;
		for (var comp : components)
			parent.add(comp);
	}

	protected void entryRemovedByUserHandler() {
	}

	public void reloadDataLists() {
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		remove.setEnabled(enabled);
		moveUp.setEnabled(enabled);
		moveDown.setEnabled(enabled);
		setEntryEnabled(enabled);
	}

	protected abstract void setEntryEnabled(boolean enabled);

	public abstract T getEntry();

	public abstract void setEntry(T entry);

}
