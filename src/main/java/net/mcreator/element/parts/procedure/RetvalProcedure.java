/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.element.parts.procedure;

import com.google.gson.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

public abstract class RetvalProcedure<T> extends Procedure {

	public static final Map<Class<? extends RetvalProcedure<?>>, JsonDeserializer<? extends RetvalProcedure<?>>> GSON_ADAPTERS = new HashMap<>() {{
		put(LogicProcedure.class, new LogicProcedure.GSONAdapter());
		put(NumberProcedure.class, new NumberProcedure.GSONAdapter());
		put(StringProcedure.class, new StringProcedure.GSONAdapter());
		put(StringListProcedure.class, new StringListProcedure.GSONAdapter());
	}};

	private final T fixedValue;

	public RetvalProcedure(String name, T fixedValue) {
		super(name);
		this.fixedValue = fixedValue;

		// A check to make sure tests fail if a procedure type is not registered
		if (!GSON_ADAPTERS.containsKey(this.getClass()))
			throw new RuntimeException("Retval procedure " + this.getClass().getSimpleName() + " is not registered!");
	}

	public T getFixedValue() {
		return fixedValue;
	}

	@Override public String toString() {
		return fixedValue.toString();
	}

}
