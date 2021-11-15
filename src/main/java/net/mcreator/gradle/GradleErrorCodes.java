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

public final class GradleErrorCodes {

	public static final int STATUS_OK = 0;
	public static final int STATUS_UNKNOWN = 1;

	public static final int STATUS_UNKNOWN_ERROR = -1;

	public static final int JAVA_JVM_CRASH_ERROR = -11;
	public static final int JAVA_XMX_INVALID_VALUE = -12;
	public static final int JAVA_XMS_INVALID_VALUE = -13;
	public static final int JAVA_JVM_HEAP_SPACE = -14;
	public static final int JAVA_RUN_CRASHED = -15;

	public static final int GRADLE_NO_INTERNET = -21;
	public static final int GRADLE_INTERNET_INTERRUPTED = -22;
	public static final int GRADLE_BUILD_FAILED = -23;
	public static final int GRADLE_REOBF_FAILED = -24;
	public static final int GRADLE_CACHEDATA_ERROR = -25;
	public static final int GRADLE_CACHEDATA_OUTDATED = -26;

	public static String toString(int errorCode) {

		return switch (errorCode) {
			case STATUS_OK -> "STATUS_OK";
			case STATUS_UNKNOWN -> "STATUS_UNKNOWN";
			case STATUS_UNKNOWN_ERROR -> "STATUS_UNKNOWN_ERROR";
			case JAVA_JVM_CRASH_ERROR -> "JAVA_JVM_CRASH_ERROR";
			case JAVA_XMX_INVALID_VALUE -> "JAVA_XMX_INVALID_VALUE";
			case JAVA_XMS_INVALID_VALUE -> "JAVA_XMS_INVALID_VALUE";
			case JAVA_JVM_HEAP_SPACE -> "JAVA_JVM_HEAP_SPACE";
			case GRADLE_NO_INTERNET -> "GRADLE_NO_INTERNET";
			case GRADLE_INTERNET_INTERRUPTED -> "GRADLE_INTERNET_INTERRUPTED";
			case GRADLE_BUILD_FAILED -> "GRADLE_BUILD_FAILED";
			case GRADLE_REOBF_FAILED -> "GRADLE_REOBF_FAILED";
			default -> "";
		};

	}

}