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

import net.mcreator.ui.laf.MCreatorTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.io.IOException;
import java.io.StringReader;

public class ConsolePane extends JTextPane {

	private static final Logger LOG = LogManager.getLogger("Console");

	private HTMLEditorKit kit;

	public static boolean DEBUG_CONTENTS_TO_LOG = false;

	private boolean transaction = false;
	private StringBuilder transactionBuffer = new StringBuilder();

	public ConsolePane() {
		setEditable(false);

		putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
		setFont(MCreatorTheme.console_font);

		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		setSelectedTextColor((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		setSelectionColor((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		setCursor(new Cursor(Cursor.TEXT_CURSOR));
		setBorder(null);

		clearConsole();
	}

	public void clearConsole() {
		setEditorKit(kit = new HTMLEditorKit());
	}

	public void insertString(String s, SimpleAttributeSet set) {
		if (DEBUG_CONTENTS_TO_LOG)
			LOG.info(s.trim());

		insertHTML("<span " + parseSimpleAttributeSetToCSS(set) + ">" + s.replace("<", "&lt;").replace(">", "&gt;")
				.replaceAll("([\\r\\n])", "<br>") + "</span>");
	}

	public void insertLink(String link, String text, String textAfter, SimpleAttributeSet set) {
		StyleConstants.setUnderline(set, true);

		StringBuilder linkHTML = new StringBuilder();
		linkHTML.append("<span><a href=\"" + "file:").append(link).append("\" ")
				.append(parseSimpleAttributeSetToCSS(set)).append(">")
				.append(text.replace("<", "&lt;").replace(">", "&gt;")).append("</a></span>");

		StyleConstants.setUnderline(set, false);

		linkHTML.append("<span ").append(parseSimpleAttributeSetToCSS(set)).append(">").append(textAfter)
				.append("</span>");

		insertHTML(linkHTML.toString());
	}

	private String parseSimpleAttributeSetToCSS(SimpleAttributeSet set) {
		Color fgund = StyleConstants.getForeground(set);
		Color bgund = StyleConstants.getBackground(set);
		int fsize = StyleConstants.getFontSize(set);
		String fg = "color:rgb(" + fgund.getRed() + "," + fgund.getGreen() + "," + fgund.getBlue() + ");";
		String bg = "background-color:rgb(" + bgund.getRed() + "," + bgund.getGreen() + "," + bgund.getBlue() + ");";
		return "style=\"" + (StyleConstants.isUnderline(set) ? "text-decoration:underline;" : "") + "font-size:" + fsize
				+ "px;" + fg + bg + "cursor:text;white-space:nowrap;font-family:'" + getFont().getFamily() + "';"
				+ "\"";
	}

	private void insertHTML(String htmlContent) {
		if (transaction) {
			transactionBuffer.append(htmlContent);
			return;
		}

		try {
			kit.read(new StringReader(htmlContent.replaceAll(" (?![^<]*>)", "&#32;").replace("\t", "&#32;&#32;&#32;")),
					getDocument(), getDocument().getLength());
		} catch (BadLocationException | IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void beginTransaction() {
		if (transaction)
			return;

		transaction = true;
		transactionBuffer = new StringBuilder();
	}

	public void endTransaction() {
		transaction = false;
		insertHTML(transactionBuffer.toString());
	}

	@Override public void setCaretPosition(int position) {
		if (!transaction)
			super.setCaretPosition(position);
	}
}
