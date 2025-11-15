/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.plugin.events.workspace;

import net.mcreator.gradle.GradleResultCode;
import net.mcreator.plugin.MCREvent;
import net.mcreator.ui.MCreator;

public class WorkspaceBuildFinishedEvent extends MCREvent {

	private final MCreator mcreator;
	private final GradleResultCode gradleStatus;

	/**
	 * <p>MCreator triggers this event when it completes the task, meaning it is executed after the result of the build task is known.
	 * The build can either be successful or not.</p>
	 *
	 * @param gradleStatus The result obtained after trying to build the workspace
	 */
	public WorkspaceBuildFinishedEvent(MCreator mcreator, GradleResultCode gradleStatus) {
		this.mcreator = mcreator;
		this.gradleStatus = gradleStatus;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public GradleResultCode getGradleStatus() {
		return gradleStatus;
	}

	public static class BuildError extends WorkspaceBuildFinishedEvent {

		private final String out;

		private final String err;

		/**
		 * <p>This event is only triggered when the build task fails due to any type of error. The {@link WorkspaceBuildFinishedEvent} is called right after this event.</p>
		 *
		 * @param gradleStatus The result obtained after trying to build the workspace
		 * @param out          String containing data from out stream
		 * @param err          String containing data from err stream
		 */
		public BuildError(MCreator mcreator, GradleResultCode gradleStatus, String out, String err) {
			super(mcreator, gradleStatus);
			this.out = out;
			this.err = err;
		}

		public String getOut() {
			return out;
		}

		public String getErr() {
			return err;
		}
	}

	public static class BuildSuccessful extends WorkspaceBuildFinishedEvent {

		/**
		 * <p>This event is only triggered when the build task succeed. The {@link WorkspaceBuildFinishedEvent} is called right after this event.</p>
		 */
		public BuildSuccessful(MCreator mcreator) {
			super(mcreator, GradleResultCode.STATUS_OK);
		}
	}
}
