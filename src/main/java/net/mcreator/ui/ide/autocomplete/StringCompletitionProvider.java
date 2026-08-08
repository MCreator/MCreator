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

package net.mcreator.ui.ide.autocomplete;

import net.mcreator.ui.init.UIRES;
import net.mcreator.workspace.Workspace;
import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringCompletitionProvider extends DefaultCompletionProvider {

	public StringCompletitionProvider(Workspace workspace) {
		Map<String, String> enLangMap = workspace.getLanguageMap().get("en_us");
		if (enLangMap != null) {
			Set<String> localizationKeys = enLangMap.keySet();
			List<Completion> list = new ArrayList<>(localizationKeys.size() * 2);
			Set<String> addedCompletions = new HashSet<>();
			for (String localKeyTest : localizationKeys) {
				String[] data = localKeyTest.split("\\.");
				String langKey = localKeyTest;
				if (data.length > 1)
					langKey = data[data.length - 1];
				String summary = "Inserts a Minecraft localization system text resource key for the entry " + localKeyTest
						+ ".<br><br>EN text for this entry: " + enLangMap.get(localKeyTest);
				if (!langKey.equals(localKeyTest) && addedCompletions.add(langKey)) {
					final String shortKey = langKey;
					list.add(new BasicCompletion(this, localKeyTest, "Localization text short key", summary) {
						@Override public String getInputText() {
							return shortKey;
						}
						@Override public Icon getIcon() {
							return UIRES.get("16px.large");
						}
					});
				}
				if (addedCompletions.add(localKeyTest)) {
					list.add(new BasicCompletion(this, localKeyTest, "Localization text key", summary) {
						@Override public Icon getIcon() {
							return UIRES.get("16px.large");
						}
					});
				}
			}
			addCompletions(list);
		}
	}

	@Override
	public String getAlreadyEnteredText(JTextComponent comp) {
		int caret = comp.getCaretPosition();
		Document doc = comp.getDocument();
		Element root = doc.getDefaultRootElement();
		int lineIndex = root.getElementIndex(caret);
		Element lineElem = root.getElement(lineIndex);
		int lineStart = lineElem.getStartOffset();
		try {
			String lineUntilPosition = doc.getText(lineStart, caret - lineStart);
			int lastQuote = findLastUnescapedQuote(lineUntilPosition);
			if (lastQuote != -1) {
				return lineUntilPosition.substring(lastQuote + 1);
			}
		} catch (BadLocationException ignored) {
		}
		return super.getAlreadyEnteredText(comp);
	}

	public static int findLastUnescapedQuote(String lineUntilPosition) {
		for (int i = lineUntilPosition.length() - 1; i >= 0; i--) {
			if (lineUntilPosition.charAt(i) == '"') {
				int backslashes = 0;
				for (int j = i - 1; j >= 0 && lineUntilPosition.charAt(j) == '\\'; j--) {
					backslashes++;
				}
				if (backslashes % 2 == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	public static boolean isInsideString(String lineUntilPosition) {
		int quotes = 0;
		for (int i = 0; i < lineUntilPosition.length(); i++) {
			if (lineUntilPosition.charAt(i) == '"') {
				int backslashes = 0;
				for (int j = i - 1; j >= 0 && lineUntilPosition.charAt(j) == '\\'; j--) {
					backslashes++;
				}
				if (backslashes % 2 == 0) {
					quotes++;
				}
			}
		}
		return quotes % 2 != 0;
	}
}
