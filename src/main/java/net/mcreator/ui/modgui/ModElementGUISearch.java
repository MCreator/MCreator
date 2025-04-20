/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.HtmlUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModElementGUISearch extends JTextField {

	private final ModElementGUI<?> modElementGUI;

	public ModElementGUISearch(ModElementGUI<?> modElementGUI) {
		this.modElementGUI = modElementGUI;

		addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				super.focusLost(e);
				setText(null);
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == KeyEvent.VK_ENTER) {
					setText(null);
				}
			}
		});

		getDocument().addDocumentListener(new DocumentListener() {

			@Override public void removeUpdate(DocumentEvent arg0) {
				search(getText());
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				search(getText());
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				search(getText());
			}

		});

		ComponentUtils.deriveFont(this, 12);
		setOpaque(false);
		setPreferredSize(new Dimension(240, 20));
		setBackground(ColorUtils.applyAlpha(getBackground(), 150));
		putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);
		putClientProperty("TextField.margin", new Insets(0, 0, 0, 0));
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (getText().isEmpty()) {
			g.drawImage(UIRES.get("searchsmall").getImage(), getWidth() - 12 - 4, getHeight() / 2 - 12 / 2, null);
			g.setFont(g.getFont().deriveFont(10f));
			g.setColor(new Color(120, 120, 120));
			g.drawString(L10N.t("elementgui.search"), 8, 16);
		}
	}

	private void search(@Nullable String searchText) {
		List<ModElementGUIPage> pages = modElementGUI.getPages();
		int firstMatchPageIndex = -1;
		for (int i = 0; i < pages.size(); ++i) {
			ModElementGUIPage page = pages.get(i);
			JComponent component = page.getComponent();
			boolean found = highlightMatchingLabels(component, searchText);
			if (found) {
				firstMatchPageIndex = i;
			}
		}
		if (pages.size() > 1 && firstMatchPageIndex != -1) {
			pages.get(firstMatchPageIndex).showThisPage();
		}
	}

	private boolean highlightMatchingLabels(Container rootComponent, @Nullable String searchText) {
		if (rootComponent == null)
			return false;

		if (searchText != null)
			searchText = searchText.toLowerCase(Locale.ROOT);

		boolean found = false;
		for (Component comp : rootComponent.getComponents()) {
			if (comp instanceof JLabel label) {
				if (handleHighlighting(label, label::getText, label::setText, searchText))
					found = true;
			} else if (comp instanceof AbstractButton button) {
				if (handleHighlighting(button, button::getText, button::setText, searchText))
					found = true;
			} else if (comp instanceof Container container) {
				// Recurse into sub-containers
				boolean subfound = highlightMatchingLabels(container, searchText);
				if (subfound) {
					found = true;
				}
			}
		}

		// Refresh UI after changes
		rootComponent.revalidate();
		rootComponent.repaint();

		return found;
	}

	private static String highlightIgnoreCase(String originalText, String searchText) {
		if (originalText == null || searchText == null || searchText.isEmpty())
			return originalText;
		Pattern pattern = Pattern.compile(Pattern.quote(searchText), Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(originalText);
		StringBuilder result = new StringBuilder();
		while (matcher.find()) {
			matcher.appendReplacement(result, "<b>" + matcher.group() + "</b>");
		}
		matcher.appendTail(result);
		return result.toString();
	}

	private static class SearchResultBorder extends CompoundBorder {

		private final String originalText;

		public SearchResultBorder(Border originalBorder, String originalText) {
			this.insideBorder = originalBorder;
			this.originalText = originalText;
		}

		@Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			super.paintBorder(c, g, x, y, width, height);
			g.setColor(ColorUtils.applyAlpha(Theme.current().getInterfaceAccentColor(), 75));
			g.fillRect(x, y, width, height);
		}

		@Override public Insets getBorderInsets(Component c) {
			return insideBorder.getBorderInsets(c);
		}

		public Border getOriginalBorder() {
			return insideBorder;
		}

		public String getOriginalText() {
			return originalText;
		}

	}

	private static boolean handleHighlighting(JComponent component, Supplier<String> getText, Consumer<String> setText,
			String searchText) {
		String labelText;
		Border labelBorder = component.getBorder();
		boolean found = false;
		if (labelBorder instanceof SearchResultBorder searchResultBorder) {
			labelText = searchResultBorder.getOriginalText() != null ?
					HtmlUtils.html2text(searchResultBorder.getOriginalText().toLowerCase(Locale.ROOT)) :
					"";
		} else {
			labelText = getText.get() != null ? HtmlUtils.html2text(getText.get().toLowerCase(Locale.ROOT)) : "";
		}
		if (searchText != null && !searchText.isBlank() && labelText.contains(searchText)) {
			found = true;
			if (labelBorder instanceof SearchResultBorder searchResultBorder) {
				// Use html to bold the matched text
				String originalText = searchResultBorder.getOriginalText();
				String highlightedText = highlightIgnoreCase(originalText, searchText);
				if (!originalText.strip().startsWith("<html>")) {
					setText.accept("<html>" + highlightedText);
				} else {
					setText.accept(highlightedText);
				}
			} else {
				component.setBorder(new SearchResultBorder(component.getBorder(), getText.get()));
			}
		} else {
			// Remove highlight if it was previously highlighted
			if (labelBorder instanceof SearchResultBorder searchResultBorder) {
				component.setBorder(searchResultBorder.getOriginalBorder());
				setText.accept(searchResultBorder.getOriginalText());
			}
		}
		return found;
	}

}
