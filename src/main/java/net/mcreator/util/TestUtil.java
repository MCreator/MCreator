/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

public class TestUtil {

	private static boolean isTestingEnvironment = false;

	public static void enterTestingMode() {
		isTestingEnvironment = true;
	}

	public static boolean isTestingEnvironment() {
		return isTestingEnvironment;
	}

	public static void failIfTestingEnvironment() {
		if (isTestingEnvironment) {
			throw new RuntimeException();
		}
	}

	public static boolean isRunningInGitHubActions() {
		return "true".equals(System.getenv("GITHUB_ACTIONS"));
	}

}
