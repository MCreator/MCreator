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

package net.mcreator.ui.gradle;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConsoleSearchBar extends JToolBar {

	private final JTextField jtf1 = new JTextField(40);
	private final JCheckBox cb3 = L10N.checkbox("dialog.console_search.match");
	private final JLabel results = L10N.label("dialog.console_search.no_result");

	private Highlighter highlighter;

	private DocumentListener dos;
	private KeyAdapter keyAdapter;

	private int index = 0;
	private int max = 0;
	private String oldSearch = "";

	ConsoleSearchBar() {
		setFloatable(false);
		setBackground(Theme.current().getSecondAltBackgroundColor());

		add(jtf1);
		add(Box.createHorizontalStrut(10));
		add(cb3);
		add(Box.createHorizontalStrut(10));
		add(results);

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

		results.setForeground(Theme.current().getAltForegroundColor());
	}

	public void reinstall(ConsolePane consolePane) {
		highlighter = consolePane.getHighlighter();
		Document doc = consolePane.getDocument();

		doc.removeDocumentListener(dos);
		jtf1.removeKeyListener(keyAdapter);

		doc.addDocumentListener(dos = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc);
			}

			@Override public void removeUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc);
			}

			@Override public void changedUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc);
			}
		});

		jtf1.addKeyListener(keyAdapter = new KeyAdapter() {
			@Override public void keyReleased(KeyEvent keyEvent) {
				super.keyReleased(keyEvent);

				if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
					index++;
				} else if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
					ConsoleSearchBar.this.setVisible(false);
				}

				updateSearch(consolePane, doc);
			}
		});
	}

	private void updateSearch(ConsolePane pan, Document doc) {
		removeHighlights();

		String searchFor = jtf1.getText();

		if (!oldSearch.equals(searchFor)) {
			max = 0;
			index = 0;
		}
		oldSearch = searchFor;

		if (searchFor.isEmpty()) {
			markNoResults();
			return;
		}

		try {
			if (index >= max && max > 0)
				index = 0;

			int pos = 0;
			int findLength = searchFor.length();
			max = 0;
			while (pos + findLength <= doc.getLength()) {
				String match = doc.getText(pos, findLength);
				if ((match.equals(searchFor) && cb3.isSelected()) || (match.equalsIgnoreCase(searchFor)
						&& !cb3.isSelected())) {
					if (max == index) {
						pan.setSelectionStart(pos);
						pan.setSelectionEnd(pos + findLength);
						pan.getCaret().setSelectionVisible(true);
					}
					max++;
					highlighter.addHighlight(pos, pos + findLength, highlightPainter);
					pos += findLength;
				} else {
					pos++;
				}
			}

			if (max > 0) {
				results.setText((index + 1) + " out of " + max);
			} else if (max == 0) {
				markNoResults();
			}
		} catch (Exception ignored) {
			markNoResults();
		}
	}

	private void markNoResults() {
		index = max = 0;
		results.setText("No results");
		removeHighlights();
	}

	private void removeHighlights() {
		Highlighter.Highlight[] hilites = highlighter.getHighlights();
		for (Highlighter.Highlight highlight : hilites) {
			if (highlight.getPainter() instanceof SearchResultHighlightPainter) {
				highlighter.removeHighlight(highlight);
			}
		}
	}

	@Override public void setVisible(boolean is) {
		super.setVisible(is);
		if (is) {
			jtf1.setText("");
			jtf1.requestFocus();
			jtf1.requestFocusInWindow();
		} else {
			removeHighlights();
		}
	}

	public JTextField getSearchField() {
		return jtf1;
	}

	private final Highlighter.HighlightPainter highlightPainter = new SearchResultHighlightPainter();

	private static class SearchResultHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		SearchResultHighlightPainter() {
			super(Theme.current().getInterfaceAccentColor());
		}
	}

}