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

import javax.annotation.Nullable;

public class TestUtil {

	@Nullable private static Runnable failureHandler = null;

	public static void enterTestingMode(Runnable newFailureHandler) {
		if (newFailureHandler == null)
			throw new NullPointerException("Failure handler cannot be null");
		failureHandler = newFailureHandler;
	}

	public static boolean isTestingEnvironment() {
		return failureHandler != null;
	}

	public static void failIfTestingEnvironment() {
		if (failureHandler != null) {
			failureHandler.run();
		}
	}

	public static boolean isRunningInGitHubActions() {
		return "true".equals(System.getenv("GITHUB_ACTIONS"));
	}

}
