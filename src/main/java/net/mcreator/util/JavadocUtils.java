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

package net.mcreator.util;

import org.apache.commons.text.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class JavadocUtils {

	private static final Pattern COMMENT_START = Pattern.compile("(?m)^\\h*/\\*+\\h?");
	private static final Pattern COMMENT_END = Pattern.compile("(?m)\\h*\\*/\\h*$");
	private static final Pattern COMMENT_LINE_PREFIX = Pattern.compile("(?m)^\\h*\\*\\h?");

	private static final Pattern TAG_CODE = Pattern.compile("\\{@code\\h+([^}]+)}");
	private static final Pattern TAG_LINK = Pattern.compile("\\{@link\\h+([^}]+)}");
	private static final Pattern TAG_LINKPLAIN = Pattern.compile("\\{@linkplain\\h+([^}]+)}");
	private static final Pattern TAG_LITERAL = Pattern.compile("\\{@literal\\h+([^}]+)}");
	private static final Pattern TAG_VALUE = Pattern.compile("\\{@value\\h+([^}]+)}");

	private static final Pattern TAG_PARAM_TYPE = Pattern.compile("(?m)^\\h*@param\\h+<\\h*([^>]+?)\\h*>");
	private static final Pattern TAG_PARAM = Pattern.compile("(?m)^\\h*@param\\h+([\\w$]+)");
	private static final Pattern TAG_RETURN = Pattern.compile("(?m)^\\h*@return");
	private static final Pattern TAG_THROWS = Pattern.compile("(?m)^\\h*@throws\\h+((?:\\([^)]*\\)|<[^>]*>|\\S)+)");
	private static final Pattern TAG_EXCEPTION = Pattern.compile("(?m)^\\h*@exception\\h+((?:\\([^)]*\\)|<[^>]*>|\\S)+)");
	private static final Pattern TAG_SEE = Pattern.compile("(?m)^\\h*@see\\h+((?:\\([^)]*\\)|<[^>]*>|\\S)+)");
	private static final Pattern TAG_SINCE = Pattern.compile("(?m)^\\h*@since\\h+(.+)");
	private static final Pattern TAG_DEPRECATED = Pattern.compile("(?m)^\\h*@deprecated");
	private static final Pattern TAG_IMPL_SPEC = Pattern.compile("(?m)^\\h*@implSpec");
	private static final Pattern TAG_IMPL_NOTE = Pattern.compile("(?m)^\\h*@implNote");
	private static final Pattern TAG_API_NOTE = Pattern.compile("(?m)^\\h*@apiNote");

	private static final Pattern PRE_SPLIT = Pattern.compile("(?i)(?=<pre>)|(?<=</pre>)");

	public static String formatJavadoc(String docSummary) {
		if (docSummary == null || docSummary.trim().isEmpty()) {
			return null;
		}

		String text = docSummary;

		text = COMMENT_START.matcher(text).replaceAll("");
		text = COMMENT_END.matcher(text).replaceAll("");
		text = COMMENT_LINE_PREFIX.matcher(text).replaceAll("");

		text = TAG_CODE.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code>"));
		text = TAG_LINK.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code>"));
		text = TAG_LINKPLAIN.matcher(text).replaceAll(mr -> Matcher.quoteReplacement(StringEscapeUtils.escapeHtml3(mr.group(1))));
		text = TAG_LITERAL.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code>"));
		text = TAG_VALUE.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code>"));

		text = TAG_PARAM_TYPE.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>Type Parameters:</b><br>&nbsp;&nbsp;<code>&lt;" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "&gt;</code> - "));
		text = TAG_PARAM.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>Parameters:</b><br>&nbsp;&nbsp;<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code> - "));
		text = TAG_RETURN.matcher(text).replaceAll("<br><b>Returns:</b><br>&nbsp;&nbsp;");
		text = TAG_THROWS.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>Throws:</b><br>&nbsp;&nbsp;<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code> - "));
		text = TAG_EXCEPTION.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>Throws:</b><br>&nbsp;&nbsp;<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code> - "));
		text = TAG_SEE.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>See Also:</b><br>&nbsp;&nbsp;<code>" + StringEscapeUtils.escapeHtml3(mr.group(1)) + "</code>"));
		text = TAG_SINCE.matcher(text).replaceAll(mr -> Matcher.quoteReplacement("<br><b>Since:</b><br>&nbsp;&nbsp;" + StringEscapeUtils.escapeHtml3(mr.group(1))));
		text = TAG_DEPRECATED.matcher(text).replaceAll("<br><b>Deprecated:</b><br>&nbsp;&nbsp;");
		text = TAG_IMPL_SPEC.matcher(text).replaceAll("<br><b>Implementation Requirements:</b><br>&nbsp;&nbsp;");
		text = TAG_IMPL_NOTE.matcher(text).replaceAll("<br><b>Implementation Note:</b><br>&nbsp;&nbsp;");
		text = TAG_API_NOTE.matcher(text).replaceAll("<br><b>API Note:</b><br>&nbsp;&nbsp;");

		text = deduplicateHeader(text, "<br><b>Type Parameters:</b><br>");
		text = deduplicateHeader(text, "<br><b>Parameters:</b><br>");
		text = deduplicateHeader(text, "<br><b>Throws:</b><br>");
		text = deduplicateHeader(text, "<br><b>See Also:</b><br>");

		text = text.replace("\r", "");
		String[] parts = PRE_SPLIT.split(text);
		StringBuilder sb = new StringBuilder();
		for (String part : parts) {
			if (part.toLowerCase().startsWith("<pre>")) {
				sb.append(part);
			} else {
				sb.append(part.replace("\n", "<br>"));
			}
		}

		return sb.toString();
	}

	private static String deduplicateHeader(String text, String headerHtml) {
		int firstIdx = text.indexOf(headerHtml);
		if (firstIdx != -1) {
			String prefix = text.substring(0, firstIdx + headerHtml.length());
			String rest = text.substring(firstIdx + headerHtml.length());
			rest = rest.replace(headerHtml, "<br>");
			return prefix + rest;
		}
		return text;
	}
}