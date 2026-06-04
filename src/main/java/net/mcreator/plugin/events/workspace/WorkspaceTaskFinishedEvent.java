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

/**
 * <p>This is a class grouping different {@link MCREvent} related to Gradle tasks that are triggered once a task is finished.</p>
 */
public class WorkspaceTaskFinishedEvent extends MCREvent {

	private final MCreator mcreator;
	private final GradleResultCode gradleStatus;

	/**
	 * <p>This constructor alone does nothing. It is used to group all related events. MCreator only trigger sub-events.</p>
	 */
	public WorkspaceTaskFinishedEvent(MCreator mcreator, GradleResultCode gradleStatus) {
		this.mcreator = mcreator;
		this.gradleStatus = gradleStatus;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public GradleResultCode getGradleStatus() {
		return gradleStatus;
	}

	/**
	 * <p>MCreator triggers this event when it completes a task, meaning it is executed after the result of the task is known.
	 * The result can either be successful or not.</p>
	 */
	public static class TaskCompleted extends WorkspaceTaskFinishedEvent {

		/**
		 * <p>This event is triggered after MCreator completes the task. When this event is called, we already know the result of the code.</p>
		 *
		 * @param gradleStatus The result obtained after trying to build the workspace
		 */
		public TaskCompleted(MCreator mcreator, GradleResultCode gradleStatus) {
			super(mcreator, gradleStatus);
		}
	}

	/**
	 * <p>This event is only triggered when the task fails due to any type of error. The {@link WorkspaceTaskFinishedEvent.TaskCompleted} is called right after this event.</p>
	 */
	public static class TaskError extends WorkspaceTaskFinishedEvent {

		private final String out;

		private final String err;

		/**
		 * <p>This event is only triggered when the task fails due to any type of error. The {@link WorkspaceTaskFinishedEvent.TaskCompleted} is called right after this event.</p>
		 *
		 * @param gradleStatus The result obtained after trying to build the workspace
		 * @param out          String containing data from out stream
		 * @param err          String containing data from err stream
		 */
		public TaskError(MCreator mcreator, GradleResultCode gradleStatus, String out, String err) {
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

	/**
	 * <p>This event is only triggered when the task succeed. The {@link WorkspaceTaskFinishedEvent.TaskCompleted} is called right after this event.</p>
	 */
	public static class TaskSuccessful extends WorkspaceTaskFinishedEvent {

		/**
		 * <p>This event is only triggered when the task succeed. The {@link WorkspaceTaskFinishedEvent.TaskCompleted} is called right after this event.</p>
		 */
		public TaskSuccessful(MCreator mcreator) {
			super(mcreator, GradleResultCode.STATUS_OK);
		}
	}
}