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

package net.mcreator.vcs.diff;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ListDiffTest {

	@Test public void getListDiff() {
		List<TestObject> first = new ArrayList<>(Arrays.asList(new TestObject("aaa", 0), new TestObject("aaa", 1)));
		List<TestObject> second = new ArrayList<>(
				Arrays.asList(new TestObject("bbb", 0), new TestObject("ddd", 2), new TestObject("ccc", 3)));
		DiffResult<TestObject> result = ListDiff.getListDiff(first, second);
		assertEquals(result.getAdded().size(), 2);
		assertEquals(result.getRemoved().size(), 1);
		assertEquals(result.getChanged().size(), 1);
	}

	private static class TestObject {

		String fieldA;
		int fieldB;

		TestObject(String fieldA, int fieldB) {
			this.fieldA = fieldA;
			this.fieldB = fieldB;
		}

		@Override public boolean equals(Object o) {
			return o instanceof TestObject && ((TestObject) o).fieldB == fieldB;
		}

		@Override public int hashCode() {
			return fieldB;
		}
	}
}