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

import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ConsoleSearchBar extends JToolBar {

	private final JTextField jtf1 = new JTextField(40);
	private final JCheckBox cb3 = L10N.checkbox("console.search.match_case");
	private final JLabel results = L10N.label("console.search.no_results");

	private Highlighter hilite;

	private DocumentListener dos;
	private KeyAdapter keyAdapter;

	ConsoleSearchBar() {
		setFloatable(false);
		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		add(jtf1);
		add(Box.createHorizontalStrut(10));
		add(cb3);
		add(Box.createHorizontalStrut(10));
		add(results);

		jtf1.setMaximumSize(jtf1.getPreferredSize());
		jtf1.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		jtf1.setOpaque(true);

		results.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
	}

	public void reinstall(ConsolePane consolePane) {
		hilite = consolePane.getHighlighter();
		Document doc = consolePane.getDocument();

		doc.removeDocumentListener(dos);
		jtf1.removeKeyListener(keyAdapter);

		doc.addDocumentListener(dos = new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc, hilite);
			}

			@Override public void removeUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc, hilite);
			}

			@Override public void changedUpdate(DocumentEvent e) {
				updateSearch(consolePane, doc, hilite);
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

				updateSearch(consolePane, doc, hilite);
			}
		});
	}

	private int index = 0;
	private String oldSearch = "";

	private void updateSearch(ConsolePane pan, Document doc, Highlighter hilite) {
		removeHighlights(hilite);

		String searchFor = jtf1.getText();

		if (!oldSearch.equals(searchFor)) {
			index = 0;
		}
		oldSearch = searchFor;

		if (searchFor.equals("")) {
			results.setText(L10N.t("console.search.no_results"));
			return;
		}

		try {
			int pos = 0;
			int findLength = searchFor.length();
			int max = -1;
			while (pos + findLength <= doc.getLength()) {
				String match = doc.getText(pos, findLength);
				if ((match.equals(searchFor) && cb3.isSelected()) || (match.equalsIgnoreCase(searchFor) && !cb3
						.isSelected())) {
					max++;
					if (max == index) {
						pan.setSelectionStart(pos);
						pan.setSelectionEnd(pos + findLength);
						pan.getCaret().setSelectionVisible(true);
					}
					hilite.addHighlight(pos, pos + findLength, highlightPainter);
				}
				pos++;
			}

			if (max > 0)
				results.setText(L10N.t("console.search.out_of", index, max));
			else if (max == 0)
				results.setText(L10N.t("console.search.no_results"));

			if (index >= max && max >= 0) {
				index = 0;
			}
		} catch (Exception ignored) {
		}
	}

	private void removeHighlights(Highlighter hilite) {
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (Highlighter.Highlight hilite1 : hilites) {
			if (hilite1.getPainter() instanceof SearchResultHighlightPainter) {
				hilite.removeHighlight(hilite1);
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
			removeHighlights(hilite);
		}
	}

	@Override public Component add(Component component) {
		component.setForeground(new Color(0xE2E2E2));
		if (component instanceof JComponent)
			((JComponent) component).setOpaque(false);
		return super.add(component);
	}

	private final Highlighter.HighlightPainter highlightPainter = new SearchResultHighlightPainter();

	private static class SearchResultHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
		SearchResultHighlightPainter() {
			super((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		}
	}

}