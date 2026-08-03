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

package net.mcreator.java;

import net.mcreator.workspace.Workspace;
import org.fife.rsta.ac.java.JarManager;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.rjc.ast.TypeDeclaration;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.mcreator.ui.component.MonacoEditorPanel;

public class DeclarationFinder {
	public static final Pattern SEPARATORS_BEHIND = Pattern.compile("[^a-zA-Z0-9_$.]");
	public static final Pattern SEPARATORS_AHEAD = Pattern.compile("[^a-zA-Z0-9_$]");

	private static TypeDeclaration getLatestChild(TypeDeclaration parent, int caret) {
		if (parent.getChildTypeAtOffset(caret) == null) {//main declaration
			return parent;
		} else {
			return getLatestChild(parent.getChildTypeAtOffset(caret), caret);
		}
	}

	private static boolean isValidSeparatorContained(String code, boolean behind) {
		Pattern r = behind ? SEPARATORS_BEHIND : SEPARATORS_AHEAD;
		Matcher m = r.matcher(code);
		return m.find();
	}

	public static class InClassPosition {
		public int caret;
		public boolean openInReadOnly = true;
		public File virtualFile;
		public File classFileNode; //null if current class
	}

}