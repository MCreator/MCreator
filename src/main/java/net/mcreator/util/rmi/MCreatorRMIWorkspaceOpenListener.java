/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.util.rmi;

import net.mcreator.ui.workspace.selector.WorkspaceOpenListener;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class MCreatorRMIWorkspaceOpenListener extends UnicastRemoteObject implements IRMIWorkspaceOpener {

	private static final Logger LOG = LogManager.getLogger("MCreatorRMIServer");

	private static final Object lock = new Object();

	private static MCreatorRMIWorkspaceOpenListener instance;
	public static  MCreatorRMIWorkspaceOpenListener getInstance() throws RemoteException {
		if (instance == null) instance = new MCreatorRMIWorkspaceOpenListener();
		return instance;
	}

	private WorkspaceOpenListener workspaceOpenListener;

	protected MCreatorRMIWorkspaceOpenListener() throws RemoteException {
	}

	public void setWorkspaceOpenListener(@Nonnull WorkspaceOpenListener workspaceOpenListener){
		synchronized (lock){
			lock.notifyAll();
		}
		this.workspaceOpenListener = workspaceOpenListener;
	}

	@Override public void openWorkspace(@Nonnull String workspaceFile) throws RemoteException {
		File fl;
		if (workspaceFile.startsWith("\"")&&workspaceFile.endsWith("\"")){
			fl = new File(workspaceFile.substring(1,workspaceFile.length()-1));
		} else {
			fl = new File(workspaceFile);
		}

		//exclude invalidate
		if (!FilenameUtils.isExtension(fl.getPath(),"mcreator")){
			return;
		}

		synchronized (lock) {
			if (workspaceOpenListener == null) {
				try {
					lock.wait(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			LOG.info("starting to open workspace"+fl);

			workspaceOpenListener.workspaceOpened(fl);
		}
	}
}
