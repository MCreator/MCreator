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
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.text.BadLocationException;
import java.io.File;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeclarationFinder {
	public static final Pattern SEPARATORS_BEHIND = Pattern.compile("[^a-zA-Z0-9_$.]");
	public static final Pattern SEPARATORS_AHEAD = Pattern.compile("[^a-zA-Z0-9_$]");

	public static DeclarationFinder.InClassPosition getDeclarationOnPos(Workspace workspace, JavaParser parser,
			RSyntaxTextArea textArea, JarManager jarManager) {

		int caret = textArea.getCaretPosition();

		//----- seek for word clicked
		int startexp, endexp;
		//seek start
		for (int i = 1; true; i++) {
			try {
				String curr = textArea.getText(caret - i, i);
				if (isValidSeparatorContained(curr, true)) {
					startexp = caret - i + 1;
					break;
				}
			} catch (BadLocationException e) {
				startexp = 0;
				break;
			}
		}
		//seek end
		for (int i = 1; true; i++) {
			try {
				String curr = textArea.getText(caret, i);
				if (isValidSeparatorContained(curr, false)) {
					endexp = caret + i - 1;
					break;
				}
			} catch (BadLocationException e) {
				endexp = caret + i - 1;
				break;
			}
		}

		try {
			return checkForPossibleDeclarations(workspace, parser, textArea,
					textArea.getText(startexp, endexp - startexp), jarManager);
		} catch (BadLocationException e) {
			return null;
		}

	}

	private static InClassPosition checkForPossibleDeclarations(Workspace workspace, JavaParser parser,
			RSyntaxTextArea textArea, String clickedWord, JarManager jarManager) {
		int start, end;
		int caret = textArea.getCaretPosition();

		if (parser == null || parser.getCompilationUnit() == null)
			return null;

		Iterator<TypeDeclaration> i = parser.getCompilationUnit().getTypeDeclarationIterator();
		while (i.hasNext()) {
			TypeDeclaration td = i.next();
			start = td.getNameStartOffset(); // from beginning of name of declaration
			end = td.getBodyEndOffset(); // to the full end

			if (caret > start && caret <= end) {
				TypeDeclaration classNameInWhichWeAre = getLatestChild(td, caret);
				String code = textArea.getText();

				InClassPosition pos;

				pos = DeclarationChecker.checkForThisDeclaration(code, clickedWord, classNameInWhichWeAre);
				if (pos != null)
					return pos;

				pos = DeclarationChecker.checkForSuperDeclaration(workspace, clickedWord, classNameInWhichWeAre,
						parser.getCompilationUnit(), jarManager);
				if (pos != null)
					return pos;

				pos = DeclarationChecker
						.checkForClassDeclaration(workspace, clickedWord, parser.getCompilationUnit(), jarManager);
				if (pos != null)
					return pos;
			} else if (caret < start) {
				break;
			}
		}
		return null;
	}

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
		public int carret;
		public boolean openInReadOnly = true;
		public File virtualFile;
		public File classFileNode; //null if current class
	}

}
