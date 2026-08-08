/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will b
 *
 *
 * e useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.ide.autocomplete;

import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaCompletionProvider;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;

/**
 * MCreator installs a separate {@link JavaLanguageSupport} per code editor so each editor can use the
 * jar manager of its own workspace. Some library internals (e.g. SourceParamChoicesProvider invoked by
 * parameter assistance when a method completion is inserted) instead look the Java language support up
 * through the global {@link LanguageSupportFactory}, whose instance was never installed on the editor,
 * which made such lookups fail with a NullPointerException. This class is registered as the factory's
 * Java language support and routes those lookups to the per-editor support stored as a client property
 * on the text area.
 */
public class JavaLanguageSupportBridge extends JavaLanguageSupport {

	private static final String PER_EDITOR_SUPPORT = "mcreator.perEditorJavaLanguageSupport";

	/**
	 * Registers this bridge as the factory's Java language support and links the given text area to the
	 * language support instance actually installed on it. Must be called before any factory lookup for
	 * Java language support happens, so the factory instantiates the bridge and not the default support.
	 *
	 * @param textArea The text area the given language support was installed on.
	 * @param support  The language support installed on the given text area.
	 */
	public static void bridge(RSyntaxTextArea textArea, JavaLanguageSupport support) {
		LanguageSupportFactory.get().addLanguageSupport(SyntaxConstants.SYNTAX_STYLE_JAVA,
				JavaLanguageSupportBridge.class.getName());
		textArea.putClientProperty(PER_EDITOR_SUPPORT, support);
	}

	@Override public JavaCompletionProvider getCompletionProvider(RSyntaxTextArea textArea) {
		if (textArea.getClientProperty(PER_EDITOR_SUPPORT) instanceof JavaLanguageSupport perEditorSupport)
			return perEditorSupport.getCompletionProvider(textArea);
		return null;
	}

}
