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
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.ui.ide.autocomplete;

import net.mcreator.java.JavaConventions;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;

import javax.swing.text.Segment;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class LocalVariableResolver {

	public record VarTypeInfo(String rawType, List<String> genericArgs) {
		public VarTypeInfo(String rawType, String genericArg) {
			this(rawType, genericArg != null && !genericArg.isEmpty() ?
					Arrays.stream(genericArg.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList() :
					Collections.emptyList());
		}

		public VarTypeInfo(String rawType) {
			this(rawType, Collections.emptyList());
		}

		public String genericArg() {
			return genericArgs.isEmpty() ? null : genericArgs.getLast();
		}
	}

	private record ScopeBlock(StringBuilder text, int headerStartInParent) {
		ScopeBlock(int headerStartInParent) {
			this(new StringBuilder(), headerStartInParent);
		}
	}

	public static String stripCommentsAndStrings(String code) {
		if (code == null || code.isEmpty())
			return "";

		TokenMaker tm = TokenMakerFactory.getDefaultInstance().getTokenMaker(SyntaxConstants.SYNTAX_STYLE_JAVA);
		StringBuilder sb = new StringBuilder(code.length());
		String[] lines = code.split("\n", -1);
		int tokenType = Token.NULL;

		synchronized (tm) {
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				char[] chars = line.toCharArray();
				Segment segment = new Segment(chars, 0, chars.length);
				Token token = tm.getTokenList(segment, tokenType, 0);
				while (token != null && token.isPaintable()) {
					int type = token.getType();
					if (!token.isComment() && type != Token.LITERAL_STRING_DOUBLE_QUOTE
							&& type != Token.ERROR_STRING_DOUBLE && type != Token.LITERAL_CHAR
							&& type != Token.ERROR_CHAR && type != Token.LITERAL_BACKQUOTE) {
						sb.append(token.getLexeme());
					} else {
						sb.append(' ');
					}
					token = token.getNextToken();
				}
				tokenType = tm.getLastTokenTypeOnLine(segment, tokenType);
				if (i < lines.length - 1) {
					sb.append('\n');
				}
			}
		}

		return sb.toString();
	}

	public static VarTypeInfo findLocalVariableType(String codeBeforeCursor, String base) {
		if (codeBeforeCursor == null || base == null || base.isEmpty())
			return null;

		String strippedCode = stripCommentsAndStrings(codeBeforeCursor);

		// Match standard / generic / array declarations
		Matcher mDecl = Pattern.compile(
						"\\b([A-Z][A-Za-z0-9_.]*)(?:<([^>]+)>)?(?:\\[])*\\s+" + Pattern.quote(base) + "\\b")
				.matcher(strippedCode);
		VarTypeInfo lastType = null;
		while (mDecl.find()) {
			String raw = mDecl.group(1);
			String gen = mDecl.group(2);
			List<String> genericArgs = Collections.emptyList();
			if (gen != null && !gen.isEmpty()) {
				genericArgs = Arrays.stream(gen.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
			}
			lastType = new VarTypeInfo(raw, genericArgs);
		}
		if (lastType != null)
			return lastType;

		// Match foreach
		Pattern pFor = Pattern.compile(
				"for\\s*\\(\\s*([A-Z][A-Za-z0-9_.]*)(?:<[^>]*>)?(?:\\[])*\\s+" + Pattern.quote(base) + "\\s*:");
		Matcher mFor = pFor.matcher(strippedCode);
		while (mFor.find()) {
			lastType = new VarTypeInfo(mFor.group(1));
		}
		if (lastType != null)
			return lastType;

		// Match lambda
		Pattern pLambda = Pattern.compile(
				"\\(\\s*([A-Z][A-Za-z0-9_.]*)(?:<[^>]*>)?(?:\\[])*\\s+" + Pattern.quote(base) + "\\s*\\)");
		Matcher mLambda = pLambda.matcher(strippedCode);
		while (mLambda.find()) {
			lastType = new VarTypeInfo(mLambda.group(1));
		}
		if (lastType != null)
			return lastType;

		// Match var assignment
		Pattern pVar = Pattern.compile(
				"\\bvar\\s+" + Pattern.quote(base) + "\\s*=\\s*(?:new\\s+)?([A-Z][A-Za-z0-9_.]*)(?:<([^>]+)>)?");
		Matcher mVar = pVar.matcher(strippedCode);
		while (mVar.find()) {
			String raw = mVar.group(1);
			String gen = mVar.group(2);
			List<String> genericArgs = Collections.emptyList();
			if (gen != null && !gen.isEmpty()) {
				genericArgs = Arrays.stream(gen.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
			}
			lastType = new VarTypeInfo(raw, genericArgs);
		}
		return lastType;
	}

	private static int findHeaderStart(StringBuilder sb) {
		int depth = 0;
		for (int j = sb.length() - 1; j >= 0; j--) {
			char c = sb.charAt(j);
			if (c == ')') {
				depth++;
			} else if (c == '(') {
				if (depth > 0) {
					depth--;
				} else {
					break;
				}
			} else if (depth == 0) {
				if (c == ';' || c == '{' || c == '}') {
					return j + 1;
				}
			}
		}
		return 0;
	}

	public static Map<String, String> getLocalVariables(String codeBeforeCursor) {
		Map<String, String> vars = new LinkedHashMap<>();
		if (codeBeforeCursor == null || codeBeforeCursor.isEmpty())
			return vars;

		String strippedCode = stripCommentsAndStrings(codeBeforeCursor);

		Deque<ScopeBlock> stack = new ArrayDeque<>();
		stack.push(new ScopeBlock(0));

		for (int i = 0; i < strippedCode.length(); i++) {
			char c = strippedCode.charAt(i);
			if (c == '{') {
				int headerStart = 0;
				if (stack.peek() != null) {
					headerStart = findHeaderStart(stack.peek().text);
				}
				stack.push(new ScopeBlock(headerStart));
			} else if (c == '}') {
				if (stack.size() > 1) {
					ScopeBlock closed = stack.pop();
					if (stack.peek() != null) {
						stack.peek().text.setLength(closed.headerStartInParent);
					}
				}
			} else {
				if (stack.peek() != null) {
					stack.peek().text.append(c);
				}
			}
		}

		StringBuilder activeCode = new StringBuilder();
		for (ScopeBlock block : stack) {
			activeCode.append(block.text).append('\n');
		}

		Matcher m = Pattern.compile(
						"\\b(boolean|byte|char|short|int|long|float|double|[A-Z][A-Za-z0-9_.]*(?:<[^>]+>)?(?:\\[])*)\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\b")
				.matcher(activeCode);
		while (m.find()) {
			String type = m.group(1);
			String name = m.group(2);
			if (type.contains("."))
				type = type.substring(type.lastIndexOf('.') + 1);
			if (!JavaConventions.JAVA_RESERVED_WORDS.contains(name)) {
				vars.putIfAbsent(name, type);
			}
		}

		return vars;
	}
}