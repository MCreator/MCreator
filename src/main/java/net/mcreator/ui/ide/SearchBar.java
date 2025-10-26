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

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.SearchContext;
import org.fife.ui.rtextarea.SearchEngine;
import org.fife.ui.rtextarea.SearchResult;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class SearchBar extends JToolBar {

	private final JTextField jtf1 = new JTextField(40);
	private final JCheckBox cb2 = L10N.checkbox("ide.search.regex");
	private final JCheckBox cb3 = L10N.checkbox("ide.search.match_case");
	private final JCheckBox cb4 = L10N.checkbox("ide.search.whole_words");

	private final RTextArea ra;

	private final SearchContext context = new SearchContext();

	private final JLabel matches = new JLabel();

	SearchBar(RTextArea ra) {
		this.ra = ra;

		matches.setForeground(Theme.current().getAltForegroundColor());

		jtf1.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
		jtf1.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				updateSearch();
			}

			@Override public void removeUpdate(DocumentEvent e) {
				updateSearch();
			}

			@Override public void changedUpdate(DocumentEvent e) {
				updateSearch();
			}
		});

		jtf1.addKeyListener(new KeyAdapter() {
			@Override public void keyPressed(KeyEvent keyEvent) {
				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					SearchEngine.find(ra, context);
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					SearchBar.this.setVisible(false);
				}
			}
		});

		cb2.addActionListener(e -> updateSearch());
		cb3.addActionListener(e -> updateSearch());
		cb4.addActionListener(e -> updateSearch());

		setFloatable(false);
		setBackground(Theme.current().getBackgroundColor());

		add(jtf1);
		add(Box.createHorizontalStrut(10));
		add(cb3);
		add(cb2);
		add(cb4);
		add(Box.createHorizontalStrut(10));
		add(matches);

		add(Box.createHorizontalGlue());

		jtf1.setMaximumSize(jtf1.getPreferredSize());

		JButton close = new JButton(UIRES.get("close_small"));
		close.setContentAreaFilled(false);
		close.setFocusPainted(false);
		close.setMargin(new Insets(0, 0, 0, 0));
		close.addActionListener(event -> setVisible(false));
		add(close);

		setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

		addComponentListener(new ComponentAdapter() {
			@Override public void componentShown(ComponentEvent e) {
				super.componentShown(e);
				jtf1.requestFocus();
				jtf1.requestFocusInWindow();
			}

			@Override public void componentHidden(ComponentEvent e) {
				super.componentHidden(e);
				SearchContext context = new SearchContext("");
				context.setMarkAll(true);
				SearchEngine.markAll(ra, context);
			}
		});
	}

	private void updateSearch() {
		context.setSearchFor(jtf1.getText());
		context.setMatchCase(cb3.isSelected());
		context.setRegularExpression(cb2.isSelected());
		context.setWholeWord(cb4.isSelected());
		context.setSearchWrap(true);

		SearchResult marked = SearchEngine.markAll(ra, context);

		matches.setText(L10N.t("ide.search.results", marked.getMarkedCount()));
		if (marked.getMarkedCount() > 0) {
			matches.setForeground(Theme.current().getAltForegroundColor());
		} else {
			matches.setForeground(new Color(239, 96, 96));
		}
	}

	public JTextField getSearchField() {
		return jtf1;
	}

}
