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

package net.mcreator.plugin.events.workspace;

import net.mcreator.plugin.MCREvent;

import javax.annotation.Nonnull;
import java.io.File;

public class PreMCreatorCreatingEvent extends MCREvent.MCRCancelableEvent {
	/**
	 * When workspace created
	 */
	public static final int NEWING = 0;
	/**
	 * When workspace opened
	 */
	public static final int OPENING = 1;

	private int state = -1;
	private File workspaceFile = null;

	public PreMCreatorCreatingEvent(int state,@Nonnull File workspaceFile){
		this.state = state;
		this.workspaceFile = workspaceFile;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public File getWorkspaceFile() {
		return workspaceFile;
	}

	public void setWorkspaceFile(File workspaceFile) {
		this.workspaceFile = workspaceFile;
	}
}
