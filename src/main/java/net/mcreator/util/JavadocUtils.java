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

public final class JavadocUtils {

	public static String formatJavadoc(String docSummary) {
		if (docSummary == null || docSummary.trim().isEmpty()) {
			return null;
		}

		String text = docSummary;

		text = text.replaceAll("(?m)^\\s*/\\*+\\s*", "")
				   .replaceAll("(?m)\\s*\\*/\\s*$", "")
				   .replaceAll("(?m)^\\s*\\*\\s?", "");

		text = text.replaceAll("\\{@code\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@link\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@linkplain\\s+([^}]+)\\}", "$1");
		text = text.replaceAll("\\{@literal\\s+([^}]+)\\}", "<code>$1</code>");
		text = text.replaceAll("\\{@value\\s+([^}]+)\\}", "<code>$1</code>");

		text = text.replaceAll("(?m)^@param\\s+<(\\w+)>", "<br><b>Type Parameters:</b><br>&nbsp;&nbsp;<code>&lt;$1&gt;</code> - ");
		text = text.replaceAll("(?m)^@param\\s+(\\w+)", "<br><b>Parameters:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@return", "<br><b>Returns:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@throws\\s+(\\S+)", "<br><b>Throws:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@exception\\s+(\\S+)", "<br><b>Throws:</b><br>&nbsp;&nbsp;<code>$1</code> - ");
		text = text.replaceAll("(?m)^@see\\s+(\\S+)", "<br><b>See Also:</b><br>&nbsp;&nbsp;<code>$1</code>");
		text = text.replaceAll("(?m)^@since\\s+(.+)", "<br><b>Since:</b><br>&nbsp;&nbsp;$1");
		text = text.replaceAll("(?m)^@deprecated", "<br><b>Deprecated:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@implSpec", "<br><b>Implementation Requirements:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@implNote", "<br><b>Implementation Note:</b><br>&nbsp;&nbsp;");
		text = text.replaceAll("(?m)^@apiNote", "<br><b>API Note:</b><br>&nbsp;&nbsp;");

		text = text.replace("\r", "");
		String[] parts = text.split("(?i)(?=<pre>)|(?<=</pre>)");
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
}
