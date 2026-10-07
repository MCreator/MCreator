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

package net.mcreator.unit.blockly.java;

import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProcedureCodeOptimizerTest {

	@Test public void removeParentheses() {
		// paired surrounding parentheses are removed, one layer at a time
		assertEquals("a + b", ProcedureCodeOptimizer.removeParentheses("(a + b)"));
		assertEquals("(a + b)", ProcedureCodeOptimizer.removeParentheses("((a + b))"));

		// unpaired or missing surrounding parentheses are kept
		assertEquals("(a) + (b)", ProcedureCodeOptimizer.removeParentheses("(a) + (b)"));
		assertEquals("a + b", ProcedureCodeOptimizer.removeParentheses("a + b"));

		// blacklisted characters at the top nesting level prevent removal
		assertEquals("(a + b)", ProcedureCodeOptimizer.removeParentheses("(a + b)", "+-"));
		assertEquals("a * (b + c)", ProcedureCodeOptimizer.removeParentheses("(a * (b + c))", "+-"));

		// string contents are ignored
		assertEquals("\"a + (\"", ProcedureCodeOptimizer.removeParentheses("(\"a + (\")", "+-"));

		// comment contents are ignored
		assertEquals("a /* + ) */ * b", ProcedureCodeOptimizer.removeParentheses("(a /* + ) */ * b)", "+-"));
		assertEquals("a // + x\n * b", ProcedureCodeOptimizer.removeParentheses("(a // + x\n * b)", "+"));

		// char literal contents are ignored, including parentheses and blacklisted characters
		assertEquals("foo(')') + b", ProcedureCodeOptimizer.removeParentheses("(foo(')') + b)"));
		assertEquals("c == '+'", ProcedureCodeOptimizer.removeParentheses("(c == '+')", "+-"));

		// single-quoted JavaScript strings are handled like char literals
		assertEquals("print('(hello)')", ProcedureCodeOptimizer.removeParentheses("(print('(hello)'))"));

		// marker comments at the beginning are preserved, even after leading whitespace
		assertEquals("/*@int*/a", ProcedureCodeOptimizer.removeParentheses("/*@int*/(a)"));
		assertEquals("/*@ItemStack*/a", ProcedureCodeOptimizer.removeParentheses(" /*@ItemStack*/(a)"));
		assertEquals("/*@BlockState*/(a + b)",
				ProcedureCodeOptimizer.removeParentheses("/*@BlockState*/(a + b)", "+-"));
	}

	@Test public void removeMarkers() {
		assertEquals("(int)a", ProcedureCodeOptimizer.toInt("(a)"));
		assertEquals("(int)(a + b)", ProcedureCodeOptimizer.toInt("(a + b)"));
		assertEquals("/*@int*/a", ProcedureCodeOptimizer.toInt("/*@int*/(a)"));
		assertEquals("(float)a", ProcedureCodeOptimizer.toFloat("(a)"));
		assertEquals("/*@float*/a", ProcedureCodeOptimizer.toFloat("/*@float*/(a)"));
		assertEquals("(double)a", ProcedureCodeOptimizer.toDouble("(a)"));
		assertEquals("(double)a instanceof B", ProcedureCodeOptimizer.toDouble("a instanceof B"));
		assertEquals("(a) + b", ProcedureCodeOptimizer.removeMarkers("/*@int*/(a) + /*@BlockState*/b"));
	}

}
