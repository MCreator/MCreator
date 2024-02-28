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
import java.util.List;

public abstract class JSimpleListEntry<T> extends JPanel {

	protected final JPanel line = new JPanel(new FlowLayout(FlowLayout.LEFT));

	private final JButton remove = new JButton(UIRES.get("16px.clear"));

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

		add(PanelUtils.centerAndEastElement(line, PanelUtils.join(remove)));

		parent.revalidate();
		parent.repaint();
	}

	protected void entryRemovedByUserHandler() {
	}

	public void reloadDataLists() {
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		remove.setEnabled(enabled);
		setEntryEnabled(enabled);
	}

	protected abstract void setEntryEnabled(boolean enabled);

	public abstract T getEntry();

	public abstract void setEntry(T entry);

}
