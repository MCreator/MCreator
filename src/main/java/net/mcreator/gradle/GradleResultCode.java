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

package net.mcreator.gradle;

public enum GradleResultCode {

	//@formatter:off
	STATUS_OK(0),
	STATUS_UNKNOWN(1),
	STATUS_UNKNOWN_ERROR(-1),

	JAVA_JVM_CRASH_ERROR(-11),
	JAVA_XMX_INVALID_VALUE(-12),
	JAVA_XMS_INVALID_VALUE(-13),
	JAVA_JVM_HEAP_SPACE(-14),
	JAVA_RUN_CRASHED(-15),

	GRADLE_NO_INTERNET(-21),
	GRADLE_INTERNET_INTERRUPTED(-22),
	GRADLE_BUILD_FAILED(-23),
	GRADLE_REOBF_FAILED(-24),
	GRADLE_CACHEDATA_ERROR(-25),
	GRADLE_CACHEDATA_OUTDATED(-26);
	//@formatter:on

	private final int code;

	GradleResultCode(int code) {
		this.code = code;
	}

	public int getCode() {
		return code;
	}

	@Override public String toString() {
		return name();
	}

}