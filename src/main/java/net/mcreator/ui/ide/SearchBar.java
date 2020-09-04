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

package net.mcreator.ui.ide;

import net.mcreator.ui.init.UIRES;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchBar extends JToolBar {

	private final JTextField jtf1 = new JTextField(40);
	private final JCheckBox cb2 = new JCheckBox("Regex");
	private final JCheckBox cb3 = new JCheckBox("Match Case");
	private final JCheckBox cb4 = new JCheckBox("Words");
	private final JCheckBox cb5 = new JCheckBox("Selection");

	private final RTextArea ra;

	SearchBar(RTextArea ra) {
		this.ra = ra;

		final JLabel matches = new JLabel();
		matches.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		jtf1.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent keyEvent) {
				super.keyReleased(keyEvent);
				SearchContext context = new SearchContext();
				context.setSearchFor(jtf1.getText());
				context.setMatchCase(cb3.isSelected());
				context.setRegularExpression(cb2.isSelected());
				context.setWholeWord(cb4.isSelected());
				context.setSearchSelectionOnly(cb5.isSelected());
				context.setSearchWrap(true);

				SearchResult marked = SearchEngine.markAll(ra, context);

				matches.setText(marked.getMarkedCount() + " results");
				if (marked.getMarkedCount() > 0) {
					matches.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
				} else {
					matches.setForeground(new Color(239, 96, 96));
				}

				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					SearchEngine.find(ra, context);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					SearchBar.this.setVisible(false);
				}
			}
		});

		setFloatable(false);
		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		add(jtf1);
		add(Box.createHorizontalStrut(10));
		add(cb3);
		add(cb2);
		add(cb4);
		add(cb5);
		add(Box.createHorizontalStrut(10));
		add(matches);

		add(Box.createHorizontalGlue());

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		jtf1.setOpaque(true);

		JButton close = new JButton(UIRES.get("close_small"));
		close.setContentAreaFilled(false);
		close.setFocusPainted(false);
		close.setMargin(new Insets(0, 0, 0, 0));
		close.addActionListener(event -> setVisible(false));
		add(close);

		setBorder(BorderFactory.createEmptyBorder());
	}

	@Override public void setVisible(boolean is) {
		super.setVisible(is);
		if (is) {
			jtf1.requestFocus();
			jtf1.requestFocusInWindow();
		} else {
			SearchContext context = new SearchContext("");
			context.setMarkAll(true);
			SearchEngine.markAll(ra, context);
		}
	}

	@Override public Component add(Component component) {
		component.setForeground(new Color(0xE2E2E2));
		if (component instanceof JComponent)
			((JComponent) component).setOpaque(false);
		return super.add(component);
	}

}
