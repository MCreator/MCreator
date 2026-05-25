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

package net.mcreator.blockly.javascript;

import java.util.HashMap;

public final class JavaScriptKeywordsMap {

	public static final HashMap<String, String> BINARY_LOGIC_OPERATORS = new HashMap<>() {{
		// logic binary operations
		put("EQ", "==");
		put("NEQ", "!=");
		put("AND", "&&");
		put("OR", "||");
		put("XOR", "^");

		// math logic binary operations
		put("LT", "<");
		put("LTE", "<=");
		put("GT", ">");
		put("GTE", ">=");
	}};

	public static final HashMap<String, String> MATH_METHODS = new HashMap<>() {{
		// single input math operations
		put("ROOT", "Math.sqrt");
		put("CUBEROOT", "Math.cbrt");
		put("ABS", "Math.abs");
		put("LN", "Math.log");
		put("LOG10", "Math.log10");
		put("SIN", "Math.sin");
		put("COS", "Math.cos");
		put("TAN", "Math.tan");
		put("ASIN", "Math.asin");
		put("ACOS", "Math.acos");
		put("ATAN", "Math.atan");
		put("ROUND", "Math.round");
		put("ROUNDUP", "Math.ceil");
		put("ROUNDDOWN", "Math.floor");
		put("SIGNUM", "Math.sign");

		// inline multiplier style
		put("RAD2DEG", "(180 / Math.PI) * ");
		put("DEG2RAD", "(Math.PI / 180) * ");

		// dual input math operations
		put("POWER", "Math.pow");
		put("MIN", "Math.min");
		put("MAX", "Math.max");
		put("ATAN2", "Math.atan2");
		put("HYPOT", "Math.hypot");
	}};

	public static final HashMap<String, String> BINARY_MATH_OPERATORS = new HashMap<>() {{
		// math calc binary operations
		put("ADD", "+");
		put("MINUS", "-");
		put("MULTIPLY", "*");
		put("DIVIDE", "/ "); // The space prevents accidental inline comments from int/float markers
		put("MOD", "%");
		put("BAND", "&");
		put("BOR", "|");
		put("BXOR", "^");
	}};

	public static final HashMap<String, String> MATH_CONSTANTS = new HashMap<>() {{
		put("PI", "Math.PI");
		put("E", "Math.E");
		put("RANDOM", "Math.random()");
		put("NORMAL", "(Math.sqrt(-2*Math.log(Math.random()))*Math.cos(2*Math.PI*Math.random()))");
		put("INFINITY", "Infinity");
		put("NINFINITY", "-Infinity");
		put("NAN", "NaN");
	}};

}
