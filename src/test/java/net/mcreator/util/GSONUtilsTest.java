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

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GSONUtilsTest {

	@Test public void testGSONCompare() {
		TestObject objA = new TestObject("test", 1);
		TestObject objB = new TestObject("test", 1);
		TestObject objC = new TestObject("ees", 1);

		assertTrue(GSONCompare.deepEquals(objA, objA));
		assertTrue(GSONCompare.deepEquals(objA, objB));
		assertFalse(GSONCompare.deepEquals(objA, objC));
		assertFalse(GSONCompare.deepEquals(objB, objC));
	}

	@Test public void testGSONClone() {
		TestObject objA = new TestObject("test", 1);
		TestObject objA_clone = GSONClone.deepClone(objA, TestObject.class);
		assertTrue(GSONCompare.deepEquals(objA, objA_clone));
	}

	private static class TestObject {

		String fieldA;
		int fieldB;

		TestObject(String fieldA, int fieldB) {
			this.fieldA = fieldA;
			this.fieldB = fieldB;
		}
	}

}