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

import net.mcreator.java.JavaCodeScanner;
import net.mcreator.java.JavaConventions;

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
	}

	private record ScopeBlock(StringBuilder text) {
		ScopeBlock() {
			this(new StringBuilder());
		}

		ScopeBlock(String initialText) {
			this(new StringBuilder(initialText));
		}
	}

	private static final Pattern TYPE_DECL_PATTERN = Pattern.compile(
			"\\b([A-Z][A-Za-z0-9_.]*)(?:<([^>]+)>)?(?:\\[])*\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\b");
	private static final Pattern FOR_PATTERN = Pattern.compile(
			"for\\s*\\(\\s*([A-Z][A-Za-z0-9_.]*)(?:<[^>]*>)?(?:\\[])*\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\s*:");
	private static final Pattern LAMBDA_PARAM_PATTERN = Pattern.compile(
			"\\(\\s*([A-Z][A-Za-z0-9_.]*)(?:<[^>]*>)?(?:\\[])*\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\s*\\)");
	private static final Pattern VAR_ASSIGN_PATTERN = Pattern.compile(
			"\\bvar\\s+([a-zA-Z_$][a-zA-Z0-9_$]*)\\s*=\\s*(?:new\\s+)?([A-Z][A-Za-z0-9_.]*)(?:<([^>]+)>)?");

	private static final Pattern DECL_PATTERN = Pattern.compile(
			"\\b((?:boolean|byte|char|short|int|long|float|double|[A-Z][A-Za-z0-9_.]*(?:<[^>]+>)?)(?:\\[\\])*)\\s+(?!(?:boolean|byte|char|short|int|long|float|double|void|class|interface|enum|record|extends|implements|throws|return|new|public|private|protected|static|final|abstract|default)\\b)([a-zA-Z_$][a-zA-Z0-9_$]*)\\b");

	public static VarTypeInfo findLocalVariableType(String codeBeforeCursor, String base) {
		if (codeBeforeCursor == null || base == null || base.isEmpty())
			return null;

		String strippedCode = JavaCodeScanner.maskStringsAndComments(codeBeforeCursor);

		Matcher mDecl = TYPE_DECL_PATTERN.matcher(strippedCode);
		VarTypeInfo lastType = null;
		while (mDecl.find()) {
			if (base.equals(mDecl.group(3)))
				lastType = new VarTypeInfo(mDecl.group(1), mDecl.group(2));
		}
		if (lastType != null)
			return lastType;

		Matcher mFor = FOR_PATTERN.matcher(strippedCode);
		while (mFor.find()) {
			if (base.equals(mFor.group(2)))
				lastType = new VarTypeInfo(mFor.group(1));
		}
		if (lastType != null)
			return lastType;

		Matcher mLambda = LAMBDA_PARAM_PATTERN.matcher(strippedCode);
		while (mLambda.find()) {
			if (base.equals(mLambda.group(2)))
				lastType = new VarTypeInfo(mLambda.group(1));
		}
		if (lastType != null)
			return lastType;

		Matcher mVar = VAR_ASSIGN_PATTERN.matcher(strippedCode);
		while (mVar.find()) {
			if (base.equals(mVar.group(1)))
				lastType = new VarTypeInfo(mVar.group(2), mVar.group(3));
		}
		return lastType;
	}

	private static int findSplitIndex(StringBuilder sb) {
		int depth = 0, lastComma = -1;
		for (int i = sb.length() - 1; i >= 0; i--) {
			char c = sb.charAt(i);
			if (c == ')') {
				depth++;
			} else if (c == '(') {
				if (depth > 0) {
					depth--;
				} else {
					return lastComma != -1 ? lastComma + 1 : i + 1;
				}
			} else if (depth == 0) {
				if (c == ',') {
					if (lastComma == -1)
						lastComma = i;
				} else if (c == '=') {
					char prev = i > 0 ? sb.charAt(i - 1) : ' ';
					char next = i + 1 < sb.length() ? sb.charAt(i + 1) : ' ';
					if (prev != '=' && prev != '<' && prev != '>' && prev != '!' && next != '=' && next != '>') {
						return i + 1;
					}
				} else if (c == ';' || c == '{' || c == '}') {
					return i + 1;
				}
			}
		}
		return 0;
	}

	public static Map<String, String> getLocalVariables(String codeBeforeCursor) {
		Map<String, String> vars = new LinkedHashMap<>();
		if (codeBeforeCursor == null || codeBeforeCursor.isEmpty())
			return vars;

		String strippedCode = JavaCodeScanner.maskStringsAndComments(codeBeforeCursor);

		Deque<ScopeBlock> stack = new ArrayDeque<>();
		stack.push(new ScopeBlock());

		for (int i = 0; i < strippedCode.length(); i++) {
			char c = strippedCode.charAt(i);
			if (c == '{') {
				String childHeader = "";
				if (!stack.isEmpty()) {
					ScopeBlock current = stack.peek();
					int split = findSplitIndex(current.text);
					if (split < current.text.length()) {
						childHeader = current.text.substring(split);
						current.text.setLength(split);
					}
				}
				stack.push(new ScopeBlock(childHeader));
			} else if (c == '}') {
				if (stack.size() > 1) {
					stack.pop();
				}
			} else {
				if (!stack.isEmpty()) {
					stack.peek().text.append(c);
				}
			}
		}

		StringBuilder activeCode = new StringBuilder();
		for (ScopeBlock block : stack) {
			activeCode.append(block.text).append('\n');
		}

		Matcher m = DECL_PATTERN.matcher(activeCode);
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