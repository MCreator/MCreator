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

import com.formdev.flatlaf.FlatClientProperties;
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
	protected final List<? extends JSimpleListEntry<T>> entryList;

	public JSimpleListEntry(JPanel parent, List<? extends JSimpleListEntry<T>> entryList) {
		this.parent = parent;
		this.entryList = entryList;

		setBackground((Theme.current().getAltBackgroundColor()).darker());
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

		JComponent container = PanelUtils.expandHorizontally(this);

		line.setOpaque(false);

		parent.add(container);

		remove.setText(L10N.t("simple_list_entry.remove"));
		remove.addActionListener(e -> {
			int thisIndex = entryList.indexOf(this);
			entryList.remove(this);
			// Update move up/down buttons of other entries
			if (!entryList.isEmpty()) {
				if (thisIndex == 0) // This entry was the first one
					entryList.getFirst().updateMoveButtons();
				else if (thisIndex == entryList.size()) // This entry was the last one
					entryList.getLast().updateMoveButtons();
			}
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
			entryRemovedByUserHandler();
		});

		moveUp.setToolTipText(L10N.t("simple_list_entry.move_up"));
		moveUp.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
		if (entryList.isEmpty()) { // At this point, this entry isn't in the entry list yet
			moveUp.setEnabled(false);
		}

		moveUp.addActionListener(e -> {
			int i = entryList.indexOf(this);
			if (i > 0) {
				swapEntries(parent, entryList, i - 1, i);
				parent.revalidate();
				parent.repaint();
			}
		});

		moveDown.setToolTipText(L10N.t("simple_list_entry.move_down"));
		moveDown.putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_BORDERLESS);
		moveDown.setEnabled(false); // The new entry is always the last one
		if (!entryList.isEmpty()) { // Enable the "move down" button of the second-to-last entry
			entryList.getLast().enableMoveDownButton();
		}

		moveDown.addActionListener(e -> {
			int i = entryList.indexOf(this);
			if (i >= 0 && i < entryList.size() - 1) {
				swapEntries(parent, entryList, i, i + 1);
				parent.revalidate();
				parent.repaint();
			}
		});

		add(PanelUtils.westAndCenterElement(PanelUtils.totalCenterInPanel(PanelUtils.join(moveUp, moveDown)),
				PanelUtils.centerAndEastElement(line, PanelUtils.totalCenterInPanel(PanelUtils.join(remove))), 5, 0));

		parent.revalidate();
		parent.repaint();
	}

	private static void swapEntries(JPanel parent, List<? extends JSimpleListEntry<?>> entryList, int thisIndex, int otherIndex) {
		Collections.swap(entryList, thisIndex, otherIndex);
		// Also swap entries in the parent panel
		Component[] components = parent.getComponents();
		parent.removeAll();
		var thisComp = components[thisIndex];
		components[thisIndex] = components[otherIndex];
		components[otherIndex] = thisComp;
		for (var comp : components)
			parent.add(comp);
		// Update move buttons of swapped entries
		entryList.get(thisIndex).updateMoveButtons();
		entryList.get(otherIndex).updateMoveButtons();
	}

	protected void entryRemovedByUserHandler() {
	}

	public void reloadDataLists() {
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		remove.setEnabled(enabled);
		updateMoveButtons();
		setEntryEnabled(enabled);
	}

	public void updateMoveButtons() {
		moveUp.setEnabled(this.isEnabled() && entryList.getFirst() != this);
		moveDown.setEnabled(this.isEnabled() && entryList.getLast() != this);
	}

	public void enableMoveDownButton() {
		moveDown.setEnabled(true);
	}

	protected abstract void setEntryEnabled(boolean enabled);

	public abstract T getEntry();

	public abstract void setEntry(T entry);

}
