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

package net.mcreator.java.debug;

import com.sun.jdi.*;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.CancellationToken;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;

public class JVMDebugClient {

	private static final Logger LOG = LogManager.getLogger("JVMDebugClient");

	private CancellationToken gradleTaskCancellationToken;
	private boolean stopRequested = false;

	@Nullable private VirtualMachine virtualMachine;
	private int vmDebugPort;

	public void init(BuildLauncher task, CancellationToken token) {
		this.gradleTaskCancellationToken = token;
		this.vmDebugPort = findAvailablePort();

		task.addJvmArguments("-Xdebug", "-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + vmDebugPort);

		new Thread(() -> {
			try {
				virtualMachine = connectToRemoteVM(vmDebugPort);
				if (virtualMachine != null) {
					LOG.info("Connected to remote VM: " + virtualMachine.name() + "host: localhost, port: " + vmDebugPort);

					// Start listening for events (e.g., watchpoint hits)
					EventQueue eventQueue = virtualMachine.eventQueue();
					while (isActive()) {
						EventSet eventSet = eventQueue.remove();
						for (Event event : eventSet) {
							LOG.debug("Received event: " + event.toString());

							if (event instanceof BreakpointEvent breakpointEvent) {
								// Handle the breakpoint event
								System.out.println("Breakpoint hit at line: " + breakpointEvent.location().lineNumber());
							}

							// TODO: handle event
						}
						eventSet.resume();
					}

					LOG.info("Disconnecting from remote VM");
					virtualMachine.dispose();
				}
			} catch (VMDisconnectedException ignored) {
				// VMDisconnectedException is thrown when the remote VM is disconnected
			} catch (Exception e) {
				LOG.warn("Failed to connect to remote VM", e);
			}
		}, "JVMDebugClient").start();
	}

	private VirtualMachine connectToRemoteVM(int port) {
		AttachingConnector connector = findConnector();
		if (connector == null) {
			LOG.warn("Failed to find connector for remote VM");
			return null;
		}

		Map<String, Connector.Argument> arguments = connector.defaultArguments();
		arguments.get("hostname").setValue("localhost");
		arguments.get("port").setValue(String.valueOf(port));

		try {
			long endTime = System.currentTimeMillis() + 10 * 1000;
			while (System.currentTimeMillis() < endTime && isActive()) {
				try {
					return connector.attach(arguments);
				} catch (IOException e) {
					//noinspection BusyWait
					Thread.sleep(1000); // Wait for 1 second before retrying
				}
			}

			throw new TimeoutException("Failed to connect to remote VM in a given time");
		} catch (Exception e) {
			LOG.warn("Failed to connect to remote VM", e);
			return null;
		}
	}

	private AttachingConnector findConnector() {
		for (AttachingConnector connector : Bootstrap.virtualMachineManager().attachingConnectors()) {
			if ("com.sun.jdi.SocketAttach".equals(connector.name())) {
				return connector;
			}
		}
		return null;
	}

	private int findAvailablePort() {
		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		} catch (IOException e) {
			LOG.warn("Failed to find available port for debugging, using default 5005", e);
			return 5005;
		}
		return port;
	}

	public boolean isActive() {
		return !gradleTaskCancellationToken.isCancellationRequested() && !stopRequested;
	}

	public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

	public void stop() {
		if (virtualMachine != null) {
			virtualMachine.dispose();
			this.stopRequested = true;
		}
	}

	public void setBreakpoint(Breakpoint breakpoint) throws AbsentInformationException {
		if (virtualMachine != null) {
			ReferenceType classType = virtualMachine.classesByName(breakpoint.getClassname()).get(0);

			List<Location> locations = classType.locationsOfLine(breakpoint.getLine());
			if (locations.isEmpty())
				throw new IllegalArgumentException("Invalid line number: " + breakpoint.getLine());

			Location location = locations.get(0);

			EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
			BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
			breakpointRequest.enable();
			breakpoint.setBreakpointRequest(breakpointRequest);
		}
	}

}
