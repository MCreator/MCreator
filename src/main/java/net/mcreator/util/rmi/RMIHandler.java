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

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class RMIHandler {
	public static IRMIWorkspaceOpener launchClient(int port)
			throws MalformedURLException, NotBoundException, RemoteException {
		return (IRMIWorkspaceOpener) Naming.lookup(
				"rmi://localhost:" + port + "/mcreator");
	}

	public static void launchServer(int port) throws RemoteException, AlreadyBoundException, MalformedURLException {
		LocateRegistry.createRegistry(port);
		Naming.bind("rmi://localhost:" + port + "/mcreator",
				MCreatorRMIWorkspaceOpenListener.getInstance());
	}
}
