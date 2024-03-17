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
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.event.*;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.ClassPrepareRequest;
import net.mcreator.gradle.GradleUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.CancellationToken;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.*;

public class JVMDebugClient {

	private static final Logger LOG = LogManager.getLogger("JVMDebugClient");

	private CancellationToken gradleTaskCancellationToken;
	private boolean stopRequested = false;

	@Nullable private VirtualMachine virtualMachine;
	private int vmDebugPort;

	private final Set<Breakpoint> breakpoints = new HashSet<>();

	private final List<JVMEventListener> eventListeners = new ArrayList<>();

	public void init(BuildLauncher task, CancellationToken token) {
		this.gradleTaskCancellationToken = token;
		this.vmDebugPort = findAvailablePort();

		Map<String, String> environment = GradleUtils.getEnvironment(GradleUtils.getJavaHome());
		environment.put("JAVA_TOOL_OPTIONS",
				"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + vmDebugPort);
		task.setEnvironmentVariables(environment);

		new Thread(() -> {
			try {
				virtualMachine = connectToRemoteVM(vmDebugPort);
				if (virtualMachine != null) {
					LOG.info("Connected to remote VM: " + virtualMachine.name() + "host: localhost, port: "
							+ vmDebugPort);

					virtualMachine.eventRequestManager().createClassPrepareRequest().enable();

					// Start listening for events (e.g., watchpoint hits)
					EventQueue eventQueue = virtualMachine.eventQueue();
					while (isActive()) {
						EventSet eventSet = eventQueue.remove();
						boolean shouldEventBlock = false;
						boolean shouldResumeOnBreakpoints = false;
						for (Event event : eventSet) {
							if (event instanceof BreakpointEvent breakpointEvent) {
								shouldEventBlock = true;
								for (Breakpoint breakpoint : breakpoints) {
									if (breakpoint.getBreakpointRequest() == breakpointEvent.request()) {
										if (breakpoint.getListener() != null) {
											if (breakpoint.getListener().breakpointHit(breakpoint, breakpointEvent)) {
												shouldResumeOnBreakpoints = true;
											}
										}
									}
								}
							} else if (event instanceof ClassPrepareEvent classPrepareEvent) {
								for (Breakpoint breakpoint : breakpoints) {
									if (!breakpoint.isLoaded() && classPrepareEvent.referenceType().name()
											.equals(breakpoint.getClassname())) {
										try {
											BreakpointRequest breakpointRequest = loadBreakpoint(
													classPrepareEvent.referenceType(), breakpoint.getLine());
											breakpoint.setBreakpointRequest(breakpointRequest);
											breakpoint.setLoaded(true);
										} catch (Exception e) {
											LOG.warn("Failed to load breakpoint", e);
										}
									}
								}

								classPrepareEvent.request().disable();
							} else if (event instanceof StepEvent) {
								shouldEventBlock = true;
							}
						}

						boolean resume = !shouldEventBlock || shouldResumeOnBreakpoints;
						if (resume)
							eventSet.resume();

						new ArrayList<>(eventListeners).forEach(e -> e.event(virtualMachine, eventSet, resume));
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

		// try to connect until connection is established or task is cancelled
		while (isActive()) {
			try {
				return connector.attach(arguments);
			} catch (IOException | IllegalConnectorArgumentsException e) {
				try {
					//noinspection BusyWait
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {
				}
			}
		}
		LOG.warn("Failed to connect to remote VM, task was cancelled before we could connect");

		return null;
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

	public void stop() {
		this.stopRequested = true;

		if (virtualMachine != null) {
			try {
				virtualMachine.dispose();
			} catch (Exception ignored) {
				// VM may be already disconnected at this point
			}
		}
	}

	public void addBreakpoint(Breakpoint breakpoint) throws Exception {
		if (virtualMachine != null) {
			if (!breakpoints.contains(breakpoint)) {
				List<ReferenceType> classes = virtualMachine.classesByName(breakpoint.getClassname());
				if (!classes.isEmpty()) {
					ReferenceType classType = classes.get(0);
					BreakpointRequest breakpointRequest = loadBreakpoint(classType, breakpoint.getLine());
					breakpoint.setBreakpointRequest(breakpointRequest);
					breakpoint.setLoaded(true);
				} else {
					ClassPrepareRequest request = virtualMachine.eventRequestManager().createClassPrepareRequest();
					request.addClassFilter(breakpoint.getClassname());
					request.enable();
				}
				breakpoints.add(breakpoint);
			} else {
				throw new IllegalArgumentException("Breakpoint already added: " + breakpoint.toString());
			}
		}
	}

	private BreakpointRequest loadBreakpoint(ReferenceType classType, int line) throws Exception {
		if (virtualMachine == null)
			throw new IllegalStateException("Virtual machine is not connected");

		List<Location> locations = classType.locationsOfLine(line);
		if (locations.isEmpty())
			throw new IllegalArgumentException("Invalid line number: " + line);

		Location location = locations.get(0);

		BreakpointRequest breakpointRequest = virtualMachine.eventRequestManager().createBreakpointRequest(location);
		breakpointRequest.enable();
		return breakpointRequest;
	}

	public void removeBreakpoint(Breakpoint breakpoint) {
		if (breakpoints.remove(breakpoint)) {
			if (breakpoint.getBreakpointRequest() != null) {
				breakpoint.getBreakpointRequest().disable();
			}
		}
	}

	public void addEventListener(JVMEventListener listener) {
		eventListeners.add(listener);
	}

	@Nullable public VirtualMachine getVirtualMachine() {
		return virtualMachine;
	}

}
