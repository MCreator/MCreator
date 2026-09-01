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

import net.mcreator.java.JavaCodeScanner;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JavaCodeScannerTest {

	@Test public void maskStringsAndComments() {
		// plain code is unchanged
		assertEquals("int a = b + c;", JavaCodeScanner.maskStringsAndComments("int a = b + c;"));

		// string literals, including escaped quotes and escaped backslashes before the closing quote
		assertEquals("String s = " + " ".repeat(8) + ";",
				JavaCodeScanner.maskStringsAndComments("String s = \"a(b)+c\";"));
		assertEquals("x = " + " ".repeat(6) + " + y;", JavaCodeScanner.maskStringsAndComments("x = \"a\\\"b\" + y;"));
		assertEquals(" ".repeat(5) + " + b", JavaCodeScanner.maskStringsAndComments("\"a\\\\\" + b"));

		// char literals, including quote and escaped quote contents
		assertEquals("c == " + " ".repeat(3), JavaCodeScanner.maskStringsAndComments("c == '\"'"));
		assertEquals("c == " + " ".repeat(4), JavaCodeScanner.maskStringsAndComments("c == '\\''"));

		// line comments keep their terminating newline
		assertEquals("a " + " ".repeat(5) + "\nc", JavaCodeScanner.maskStringsAndComments("a // b(\nc"));

		// block comments, including unterminated ones
		assertEquals("a" + " ".repeat(5) + "b", JavaCodeScanner.maskStringsAndComments("a/*x*/b"));
		assertEquals("a" + " ".repeat(3), JavaCodeScanner.maskStringsAndComments("a/*b"));

		// a slash right after a closed block comment does not start a line comment, but a full // does
		assertEquals(" ".repeat(5) + "/b", JavaCodeScanner.maskStringsAndComments("/*a*//b"));
		assertEquals(" ".repeat(8), JavaCodeScanner.maskStringsAndComments("/*a*///b"));

		// text blocks, including quotes inside them and code following them
		String textBlock = "\"\"\"a\"\"b\"\"\"";
		assertEquals(" ".repeat(textBlock.length()), JavaCodeScanner.maskStringsAndComments(textBlock));
		String textBlockWithCode = "\"\"\"\nab \"q\" cd\n\"\"\" + x";
		assertEquals(" ".repeat(textBlockWithCode.length() - 4) + " + x",
				JavaCodeScanner.maskStringsAndComments(textBlockWithCode));

		// comment markers inside strings and quotes inside comments have no effect
		assertEquals("a = " + " ".repeat(6) + ";", JavaCodeScanner.maskStringsAndComments("a = \"//no\";"));
		assertEquals("a" + " ".repeat(7) + "b", JavaCodeScanner.maskStringsAndComments("a/*\"(\"*/b"));

		// output length always matches input length
		String code = "foo(\"a\\\"b\", 'x') /* c */ + '\\\\' // tail";
		assertEquals(code.length(), JavaCodeScanner.maskStringsAndComments(code).length());
	}

	@Test public void scan() {
		// every character is reported in order with its region
		StringBuilder regions = new StringBuilder();
		assertTrue(JavaCodeScanner.scan("a\"b\"'c'//d", (_, _, region) -> {
			regions.append(switch (region) {
				case CODE -> 'C';
				case STRING -> 'S';
				case CHAR_LITERAL -> 'L';
				case COMMENT -> 'M';
			});
			return true;
		}));
		assertEquals("CSSSLLLMMM", regions.toString());

		// reported indices match the scanned code
		String code = "a + \"b\" // c";
		assertTrue(JavaCodeScanner.scan(code, (index, c, _) -> {
			assertEquals(code.charAt(index), c);
			return true;
		}));

		// returning false aborts the scan
		int[] visited = { 0 };
		assertFalse(JavaCodeScanner.scan("abcdef", (_, c, _) -> {
			visited[0]++;
			return c != 'c';
		}));
		assertEquals(3, visited[0]);
	}

}
