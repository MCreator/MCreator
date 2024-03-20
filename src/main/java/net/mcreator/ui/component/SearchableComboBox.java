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

package net.mcreator.ui.component;

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.validation.component.VComboBox;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SearchableComboBox<T> extends VComboBox<T> implements KeyListener, FocusListener {

	private String searchTerm = "";

	private List<T> entries = new ArrayList<>();

	private boolean dropDownVisible = false;

	public SearchableComboBox(T[] data) {
		super(data);
		entries.addAll(Arrays.asList(data));
		init();
	}

	public SearchableComboBox() {
		init();
	}

	private void init() {
		addKeyListener(this);
		addFocusListener(this);
		addPopupMenuListener(new PopupMenuListener() {
			@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				dropDownVisible = true;
				clearSearch();
			}

			@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				dropDownVisible = false;
				clearSearch();
			}

			@Override public void popupMenuCanceled(PopupMenuEvent e) {
				dropDownVisible = false;
				clearSearch();
			}
		});
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		if (canSearch()) {
			g.drawImage(UIRES.get("searchsmall").getImage(), getWidth() - 12 - 22, getHeight() / 2 - 12 / 2, null);
		}

		if (!searchTerm.isEmpty()) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setFont(g.getFont().deriveFont(12.0f));
			g2.setColor(new Color(50, 50, 50, 230));
			g2.fillRect(0, 0, g.getFontMetrics().stringWidth(searchTerm) + 3, 17);
			g2.setColor(Theme.current().getForegroundColor());
			g2.drawString(searchTerm, 1, 12);
		}
	}

	private boolean canSearch() {
		return dropDownVisible && entries.size() > 10;
	}

	@Override public void addItem(T item) {
		super.addItem(item);
		entries.add(item);
	}

	@Override public void removeAllItems() {
		super.removeAllItems();
		this.entries = new ArrayList<>();
	}

	private void comboFilter() {
		Object selected = super.getSelectedItem();

		super.removeAllItems();
		entries.forEach(super::addItem);

		if (searchTerm.isEmpty() || !canSearch()) {
			super.setSelectedItem(selected);
		} else {
			List<T> entriesFiltered = new ArrayList<>();
			ComboBoxModel<T> model = getModel();
			int size = model.getSize();
			for (int i = 0; i < size; i++) {
				T element = model.getElementAt(i);
				if (element.toString().toLowerCase(Locale.ENGLISH).contains(searchTerm.toLowerCase(Locale.ENGLISH))) {
					entriesFiltered.add(element);
				}
			}

			if (!entriesFiltered.isEmpty()) {
				super.removeAllItems();
				entriesFiltered.forEach(super::addItem);
			}
		}
	}

	private void clearSearch() {
		searchTerm = "";
		comboFilter();
	}

	@Override public void focusGained(FocusEvent e) {
		clearSearch();
	}

	@Override public void focusLost(FocusEvent e) {
		clearSearch();
	}

	@Override public void keyTyped(KeyEvent e) {
	}

	@Override public void keyPressed(KeyEvent e) {
		if (canSearch()) {
			if (Character.isLetterOrDigit(e.getKeyChar()) || e.getKeyChar() == '_' || e.getKeyChar() == '-'
					|| e.getKeyChar() == ':' || e.getKeyChar() == ' ') {
				searchTerm += e.getKeyChar();
				comboFilter();
			} else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
				if (!searchTerm.isEmpty()) {
					searchTerm = searchTerm.substring(0, searchTerm.length() - 1);
					comboFilter();
				} else {
					searchTerm = "";
					comboFilter();
				}
			} else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
				searchTerm = "";
				comboFilter();
			} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				searchTerm = "";
				repaint();
			}
		}
	}

	@Override public void keyReleased(KeyEvent e) {
	}

}