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

package net.mcreator.unit.java;

import net.mcreator.java.JavaConventions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaConventionsTest {

	@Test public void convertToValidClassName() {
		assertEquals("ClassName", JavaConventions.convertToValidClassName("className"));
		assertEquals("ClassName3", JavaConventions.convertToValidClassName("3className3"));
		assertEquals("ClassName", JavaConventions.convertToValidClassName("_className"));
		assertEquals("ClassName", JavaConventions.convertToValidClassName("ČlassName"));
	}

}