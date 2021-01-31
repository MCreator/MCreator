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

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ReplaceBar extends JPanel {

	private final JTextField jtf1 = new JTextField(40);
	private final JTextField jtf2 = new JTextField(40);

	private final JCheckBox cb2 = new JCheckBox("Regex");
	private final JCheckBox cb3 = new JCheckBox("Match Case");
	private final JCheckBox cb4 = new JCheckBox("Words");
	private final JCheckBox cb5 = new JCheckBox("Selection");

	private final RTextArea ra;

	ReplaceBar(RTextArea ra) {
		super(new BorderLayout(1, 1));

		this.ra = ra;

		final JLabel matches = new JLabel();
		matches.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		JToolBar top = new JToolBar();
		top.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		top.setBorder(null);
		top.setFloatable(false);

		JToolBar bottom = new JToolBar();
		bottom.setBorder(null);
		bottom.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		bottom.setFloatable(false);

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
					ReplaceBar.this.setVisible(false);
				}
			}
		});

		jtf2.addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ReplaceBar.this.setVisible(false);
				}
			}
		});

		JButton replace = L10N.button("action.replace");
		JButton replaceAll = L10N.button("action.replace_all");

		replace.addActionListener(actionEvent -> {
			SearchContext context = new SearchContext();
			context.setSearchFor(jtf1.getText());
			context.setReplaceWith(jtf2.getText());
			context.setMatchCase(cb3.isSelected());
			context.setRegularExpression(cb2.isSelected());
			context.setWholeWord(cb4.isSelected());
			context.setSearchSelectionOnly(cb5.isSelected());
			SearchEngine.replace(ra, context);
		});

		replaceAll.addActionListener(actionEvent -> {
			SearchContext context = new SearchContext();
			context.setSearchFor(jtf1.getText());
			context.setReplaceWith(jtf2.getText());
			context.setMatchCase(cb3.isSelected());
			context.setRegularExpression(cb2.isSelected());
			context.setWholeWord(cb4.isSelected());
			context.setSearchSelectionOnly(cb5.isSelected());
			SearchEngine.replaceAll(ra, context);
		});

		bottom.add(jtf2);
		bottom.add(Box.createHorizontalStrut(10));
		bottom.add(replace);
		bottom.add(Box.createHorizontalStrut(10));
		bottom.add(replaceAll);

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		top.add(jtf1);
		top.add(Box.createHorizontalStrut(10));
		top.add(cb3);
		top.add(cb2);
		top.add(cb4);
		top.add(cb5);
		top.add(Box.createHorizontalStrut(10));
		top.add(matches);
		top.add(Box.createHorizontalGlue());

		cb3.setOpaque(false);
		cb2.setOpaque(false);
		cb5.setOpaque(false);
		cb4.setOpaque(false);

		cb3.setForeground(new Color(0xE2E2E2));
		cb2.setForeground(new Color(0xE2E2E2));
		cb4.setForeground(new Color(0xE2E2E2));
		cb5.setForeground(new Color(0xE2E2E2));

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		jtf1.setOpaque(true);

		jtf2.setMaximumSize(jtf1.getPreferredSize());
		jtf2.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		jtf2.setOpaque(true);

		JButton close = new JButton(UIRES.get("close_small"));
		close.setContentAreaFilled(false);
		close.setFocusPainted(false);
		close.setMargin(new Insets(0, 0, 0, 0));
		close.addActionListener(event -> setVisible(false));

		top.add(close);

		add("North", top);
		add("South", bottom);

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

}