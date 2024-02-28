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

import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxScheme;
import org.fife.ui.rsyntaxtextarea.Theme;
import org.fife.ui.rtextarea.RTextScrollPane;

import java.awt.event.InputEvent;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class RSyntaxTextAreaStyler {

	private static final Logger LOG = LogManager.getLogger(RSyntaxTextAreaStyler.class);

	public static void style(RSyntaxTextArea te, RTextScrollPane sp, int initialFontSize) {
		try {
			Theme theme;

			if (PluginLoader.INSTANCE.getResourceAsStream(
					"themes/" + net.mcreator.ui.laf.themes.Theme.current().getID() + "/styles/code_editor.xml")
					!= null) {
				String themeXML = FileIO.readResourceToString(PluginLoader.INSTANCE,
						"themes/" + net.mcreator.ui.laf.themes.Theme.current().getID() + "/styles/code_editor.xml");
				themeXML = themeXML.replace("${mainTint}", Integer.toHexString(
						(net.mcreator.ui.laf.themes.Theme.current().getInterfaceAccentColor()).getRGB()).substring(2));
				theme = Theme.load(new ByteArrayInputStream(themeXML.getBytes(StandardCharsets.UTF_8)));
			} else {
				String themeXML = FileIO.readResourceToString(PluginLoader.INSTANCE,
						"themes/default_dark/styles/code_editor.xml");
				themeXML = themeXML.replace("${mainTint}", Integer.toHexString(
						(net.mcreator.ui.laf.themes.Theme.current().getInterfaceAccentColor()).getRGB()).substring(2));
				theme = Theme.load(new ByteArrayInputStream(themeXML.getBytes(StandardCharsets.UTF_8)));
			}

			if (!PreferencesManager.PREFERENCES.ide.editorTheme.get().equals("MCreator")) {
				theme = Theme.load(te.getClass().getResourceAsStream(
						"/org/fife/ui/rsyntaxtextarea/themes/" + PreferencesManager.PREFERENCES.ide.editorTheme.get()
								.toLowerCase(Locale.ENGLISH) + ".xml"));
			}

			theme.matchedBracketBG = net.mcreator.ui.laf.themes.Theme.current().getAltBackgroundColor();
			theme.matchedBracketFG = net.mcreator.ui.laf.themes.Theme.current().getForegroundColor();

			theme.apply(te);
		} catch (IOException ioe) {
			LOG.error(ioe.getMessage(), ioe);
		}

		SyntaxScheme ss = te.getSyntaxScheme();
		for (int i = 0; i < ss.getStyleCount(); i++)
			if (ss.getStyle(i) != null)
				ss.getStyle(i).font = net.mcreator.ui.laf.themes.Theme.current().getConsoleFont()
						.deriveFont((float) initialFontSize);
		te.setFont(net.mcreator.ui.laf.themes.Theme.current().getConsoleFont().deriveFont((float) initialFontSize));
		te.revalidate();

		sp.addMouseWheelListener(mouseWheelEvent -> {
			if ((mouseWheelEvent.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK
					&& te.hasFocus()) {
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
