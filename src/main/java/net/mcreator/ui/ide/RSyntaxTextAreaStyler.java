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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.laf.MCreatorTheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Style;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.io.IOException;
import java.util.Locale;

public class RSyntaxTextAreaStyler {

	private static final Logger LOG = LogManager.getLogger(RSyntaxTextAreaStyler.class);

	public static void style(RSyntaxTextArea te, RTextScrollPane sp, int initialFontSize) {
		try {
			Theme theme = Theme.load(PluginLoader.INSTANCE
					.getResourceAsStream("themes/" + ThemeLoader.CURRENT_THEME.getID() + "/styles/code_editor.xml"));

			if (!PreferencesManager.PREFERENCES.ide.editorTheme.equals("MCreator")) {
				theme = Theme.load(te.getClass().getResourceAsStream(
						"/org/fife/ui/rsyntaxtextarea/themes/" + PreferencesManager.PREFERENCES.ide.editorTheme
								.toLowerCase(Locale.ENGLISH) + ".xml"));
			} else {
				theme.scheme.setStyle(SyntaxScheme.RESERVED_WORD,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
				theme.scheme.setStyle(SyntaxScheme.RESERVED_WORD_2,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
				theme.scheme.setStyle(SyntaxScheme.LITERAL_BOOLEAN,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
				theme.scheme.setStyle(SyntaxScheme.LITERAL_NUMBER_DECIMAL_INT,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
				theme.scheme.setStyle(SyntaxScheme.LITERAL_NUMBER_FLOAT,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
				theme.scheme.setStyle(SyntaxScheme.LITERAL_NUMBER_HEXADECIMAL,
						new Style((Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
			}
			theme.matchedBracketBG = (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT");
			theme.matchedBracketFG = Color.white;
			theme.apply(te);
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage(), ioe);
		}

		SyntaxScheme ss = te.getSyntaxScheme();
		for (int i = 0; i < ss.getStyleCount(); i++)
			if (ss.getStyle(i) != null)
				ss.getStyle(i).font = MCreatorTheme.console_font.deriveFont((float) initialFontSize);
		te.setFont(MCreatorTheme.console_font.deriveFont((float) initialFontSize));
		te.revalidate();

		sp.addMouseWheelListener(mouseWheelEvent -> {
			if ((mouseWheelEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK && te
					.hasFocus()) {
				float current = te.getFont().getSize();

				current -= mouseWheelEvent.getWheelRotation();

				if (current > 48)
					return;

				if (current < 5)
					return;

				for (int i = 0; i < ss.getStyleCount(); i++)
					if (ss.getStyle(i) != null)
						ss.getStyle(i).font = ss.getStyle(i).font.deriveFont(current);
				te.setFont(te.getFont().deriveFont(current));

				sp.getVerticalScrollBar()
						.setValue(sp.getVerticalScrollBar().getValue() - mouseWheelEvent.getWheelRotation());
			}
		});
	}

}
