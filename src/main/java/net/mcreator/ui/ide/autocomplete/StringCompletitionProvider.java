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
import org.fife.ui.autocomplete.DefaultCompletionProvider;

import javax.swing.*;
import java.util.Map;
import java.util.Set;

public class StringCompletitionProvider extends DefaultCompletionProvider {

	public StringCompletitionProvider(Workspace workspace) {
		Map<String, String> enLangMap = workspace.getLanguageMap().get("en_us");
		Set<String> localizationKeys = enLangMap.keySet();
		for (String localKeyTest : localizationKeys) {
			String[] data = localKeyTest.split("\\.");
			String langKey = localKeyTest;
			if (data.length > 1)
				langKey = data[1];
			String summary = "Inserts a Minecraft localization system text resource key for the entry " + langKey
					+ ".<br><br>EN text for this entry: " + enLangMap.get(localKeyTest);
			addCompletion(new BasicCompletion(this, langKey, "Localization text short key", summary) {
				@Override public Icon getIcon() {
					return UIRES.get("16px.large.gif");
				}
			});
			addCompletion(new BasicCompletion(this, localKeyTest, "Localization text key", summary) {
				@Override public Icon getIcon() {
					return UIRES.get("16px.large.gif");
				}
			});
		}
	}
}
