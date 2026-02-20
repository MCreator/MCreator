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

package net.mcreator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class JBRHacks {

	private static final Logger LOG = LogManager.getLogger(JBRHacks.class);

	public static void permanentlyDisableIoOverNioInThisThread() {
		try {
			Class<?> ioOverNio = Class.forName("com.jetbrains.internal.IoOverNio");
			Class<?> returnType = Class.forName("com.jetbrains.internal.IoOverNio$ThreadLocalCloseable");
			MethodHandle mh = MethodHandles.lookup()
					.findStatic(ioOverNio, "disableInThisThread", MethodType.methodType(returnType));
			mh.invoke();
		} catch (Throwable e) {
			LOG.warn("Failed to disable IO via NIO", e);
		}
	}

}
