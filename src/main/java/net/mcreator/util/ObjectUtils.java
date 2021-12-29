/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class ObjectUtils {

	/**
	 * This method takes two objects of the same type and checks whether
	 * all fields declared in class of that type share the same values.
	 *
	 * @param objA The first object to compare
	 * @param objB The second object to compare
	 * @param <T>  The class checked, specified by the type of compared objects
	 * @return Whether all the fields of both objects declared in the given class have same values
	 */
	public static <T> boolean equalsByFields(T objA, T objB) {
		if (objA == null || objB == null)
			return false;

		AtomicBoolean retVal = new AtomicBoolean(true);
		List.of(objA.getClass().getDeclaredFields()).forEach(field -> {
			field.setAccessible(true);
			try {
				retVal.compareAndSet(true,
						Objects.equals(field.get(objA), field.get(objB)) || Objects.equals(field.get(objB),
								field.get(objA)));
			} catch (IllegalAccessException ignored) {
			}
		});

		return retVal.get();
	}
}
