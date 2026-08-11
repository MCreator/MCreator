/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

public class TraceUtil {

	private static final int MAX_TRACE_ENTRIES = 3;

	private static final String TRACE_SEPARATOR = " > ";

	public static String tryToFindMCreatorInvoker() {
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		StringBuilder trace = new StringBuilder();
		String lastSimpleClassName = null;
		int entries = 0;
		for (int i = 0; i < stackTrace.length && entries < MAX_TRACE_ENTRIES; i++) {
			StackTraceElement element = stackTrace[i];
			String className = element.getClassName();
			if (!className.startsWith("net.mcreator") || className.equals(TraceUtil.class.getName())) {
				continue;
			}
			int lastDot = className.lastIndexOf('.');
			String simpleClassName = lastDot >= 0 ? className.substring(lastDot + 1) : className;
			int lineNumber = element.getLineNumber();
			if (simpleClassName.equals(lastSimpleClassName)) {
				trace.append(':').append(lineNumber);
			} else {
				if (!trace.isEmpty()) {
					trace.insert(0, TRACE_SEPARATOR);
				}
				trace.insert(0, simpleClassName + ":" + lineNumber);
				lastSimpleClassName = simpleClassName;
				entries++;
			}
		}
		return trace.isEmpty() ? "unknown" : trace.toString();
	}

}
