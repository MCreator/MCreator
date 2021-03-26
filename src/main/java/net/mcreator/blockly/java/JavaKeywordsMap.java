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

package net.mcreator.blockly.java;

import net.mcreator.minecraft.ElementUtil;
import net.mcreator.plugin.PluginLoader;

import java.util.HashMap;

public final class JavaKeywordsMap {

	public static final HashMap<String, String> BINARY_OPERATORS = new HashMap<String, String>() {{
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

		// math calc binary operations
		put("ADD", "+");
		put("MINUS", "-");
		put("MULTIPLY", "*");
		put("DIVIDE", "/");
		put("MOD", "%");
		put("BAND", "&");
		put("BOR", "|");
		put("BXOR", "^");
	}};

	public static final HashMap<String, String> MATH_OPERATORS = new HashMap<String, String>() {{
		// single input math operations
		put("ROOT", "sqrt");
		put("ABS", "abs");
		put("LN", "log");
		put("LOG10", "log10");
		put("SIN", "sin");
		put("COS", "cos");
		put("TAN", "tan");
		put("ASIN", "asin");
		put("ACOS", "acos");
		put("ATAN", "atan");
		put("ROUND", "round");
		put("ROUNDUP", "ceil");
		put("ROUNDDOWN", "floor");
		put("RAD2DEG", "toDegrees");
		put("DEG2RAD", "toRadians");

		// dual input math operations
		put("POWER", "pow");
		put("MIN", "min");
		put("MAX", "max");
	}};

	public static final HashMap<String, String> MATH_CONSTANTS = new HashMap<String, String>() {{
		put("PI", "Math.PI");
		put("E", "Math.E");
		put("RANDOM", "Math.random()");
		put("INFINITY", "Double.POSITIVE_INFINITY");
		put("NINFINITY", "Double.NEGATIVE_INFINITY");
		put("NAN", "Double.NaN");
	}};

	public static final HashMap<String, String[]> VARIABLE_TYPES = new HashMap<String, String[]>() {{
		put("Number", new String[] { "double", "0" });
		put("Boolean", new String[] { "boolean", "false" });
		put("String", new String[] { "String", "\"\"" });
		put("MCItem", new String[] { "ItemStack", "ItemStack.EMPTY" });
		put("DyeColor", new String[] { "DyeColor", "DyeColor.WHITE"});
	}};

}
