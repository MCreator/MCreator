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

package net.mcreator.ui;

import net.mcreator.ui.init.UIRES;

import javax.annotation.Nullable;
import java.awt.*;

public class TaskbarIntegration {

	@Nullable private Taskbar taskbar = null;

	public TaskbarIntegration() {
		if (Taskbar.isTaskbarSupported()) {
			taskbar = Taskbar.getTaskbar();
			if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE))
				taskbar.setIconImage(UIRES.get("icon").getImage());
		}
	}

	public void setProgressState(Window w, int progress) {
		if (taskbar != null && taskbar.isSupported(Taskbar.Feature.PROGRESS_VALUE_WINDOW))
			taskbar.setWindowProgressValue(w, progress);
	}

	public void setIntermediateProgress(Window w) {
		if (taskbar != null && taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW))
			taskbar.setWindowProgressState(w, Taskbar.State.INDETERMINATE);
	}

	public void clearState(Window w) {
		if (taskbar != null && taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW)) {
			setProgressState(w, -1);
			taskbar.setWindowProgressState(w, Taskbar.State.NORMAL);
			taskbar.setWindowProgressState(w, Taskbar.State.OFF);
		}
	}

	public void setWarningIndicator(Window w) {
		if (taskbar != null && taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW))
			taskbar.setWindowProgressState(w, Taskbar.State.PAUSED);
	}

	public void setErrorIndicator(Window w) {
		if (taskbar != null && taskbar.isSupported(Taskbar.Feature.PROGRESS_STATE_WINDOW))
			taskbar.setWindowProgressState(w, Taskbar.State.ERROR);
	}

}
