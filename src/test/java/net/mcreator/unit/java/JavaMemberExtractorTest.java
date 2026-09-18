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

package net.mcreator.unit.java;

import net.mcreator.java.JavaMemberExtractor;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaMemberExtractorTest {

	@Test public void getMemberList() {
		// unqualified identifiers are extracted; dot-qualified ones and reserved words are not
		Set<String> members = JavaMemberExtractor.getMemberList("int foo = bar + entity.getHealth();");
		assertTrue(members.contains("foo"));
		assertTrue(members.contains("bar"));
		assertTrue(members.contains("entity"));
		assertFalse(members.contains("getHealth")); // preceded by a dot
		assertFalse(members.contains("int")); // reserved word

		// string contents are ignored, including escaped quotes
		members = JavaMemberExtractor.getMemberList("String s = \"hello world\";");
		assertTrue(members.contains("String"));
		assertTrue(members.contains("s"));
		assertFalse(members.contains("hello"));
		assertFalse(members.contains("world"));
		members = JavaMemberExtractor.getMemberList("foo(\"a \\\" b\", baz);");
		assertTrue(members.contains("foo"));
		assertTrue(members.contains("baz"));
		assertFalse(members.contains("a"));
		assertFalse(members.contains("b"));

		// comment contents are ignored
		members = JavaMemberExtractor.getMemberList("foo(); // call bar\n/* baz */ qux();");
		assertTrue(members.contains("foo"));
		assertTrue(members.contains("qux"));
		assertFalse(members.contains("bar"));
		assertFalse(members.contains("baz"));

		// char literal contents are ignored, and a quote in a char literal does not desync the parser
		members = JavaMemberExtractor.getMemberList("char x = 'a';");
		assertTrue(members.contains("x"));
		assertFalse(members.contains("a"));
		members = JavaMemberExtractor.getMemberList("if (c == '\"')\n\tfoo(bar);");
		assertTrue(members.contains("c"));
		assertTrue(members.contains("foo"));
		assertTrue(members.contains("bar"));

		// text block contents are ignored
		members = JavaMemberExtractor.getMemberList("String s = \"\"\"\n\thello \"quoted\" text\n\t\"\"\" + suffix;");
		assertTrue(members.contains("suffix"));
		assertFalse(members.contains("hello"));
		assertFalse(members.contains("quoted"));
		assertFalse(members.contains("text"));

		// an identifier at the very end of the code is extracted
		assertTrue(JavaMemberExtractor.getMemberList("foo + bar").contains("bar"));
	}

}
