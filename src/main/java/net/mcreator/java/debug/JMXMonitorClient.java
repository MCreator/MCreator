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

package net.mcreator.java.debug;

import com.sun.management.OperatingSystemMXBean;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.ThreadMXBean;
import java.net.ServerSocket;
import java.util.Map;

public class JMXMonitorClient {

	private static final Logger LOG = LogManager.getLogger("JMXDebugClient");

	private boolean stopRequested = false;

	@Nullable private JMXConnector jmxConnector;

	public JMXMonitorClient(Map<String, String> environment, int refreshInterval) {
		int jmxPort = findAvailablePort();

		//@formatter:off
		String javaToolOptions =
				" -Dcom.sun.management.jmxremote.port=" + jmxPort +
				" -Dcom.sun.management.jmxremote.local.only=true" +
				" -Dcom.sun.management.jmxremote.ssl=false" +
				" -Dcom.sun.management.jmxremote.authenticate=false";
		//@formatter:on
		if (environment.containsKey("JAVA_TOOL_OPTIONS")) {
			environment.put("JAVA_TOOL_OPTIONS", environment.get("JAVA_TOOL_OPTIONS") + " " + javaToolOptions);
		} else {
			environment.put("JAVA_TOOL_OPTIONS", javaToolOptions);
		}

		new Thread(() -> {
			jmxConnector = connectToJMX(jmxPort, 10000);
			if (jmxConnector != null) {
				LOG.info("Connected to JMX. Host: localhost, port: {}", jmxPort);

				try {
					MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

					MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
							ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

					OperatingSystemMXBean osMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
							ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

					while (isActive()) {
						MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
						System.out.println("Heap Memory Used: " + (heapMemoryUsage.getUsed() / 1024 / 1024) + " MB");
						System.out.println("Heap Memory Max: " + (heapMemoryUsage.getMax() / 1024 / 1024) + " MB");
						System.err.println("CPU usage: " + osMXBean.getProcessCpuLoad() * 100 + "%");

						try {
							//noinspection BusyWait
							Thread.sleep(refreshInterval);
						} catch (InterruptedException ignored) {
						}
					}
				} catch (Exception e) {
					LOG.warn("Error while running JMX debug client", e);
				} finally {
					LOG.info("Disconnecting from JMX");
					stop();
				}
			}
		}, "JMXDebugClient").start();
	}

	private JMXConnector connectToJMX(int port, int timeoutms) {
		// try to connect until connection is established or task is cancelled
		long startedAt = System.currentTimeMillis();
		while (isActive()) {
			try {
				if (System.currentTimeMillis() - startedAt > timeoutms) {
					return null;
				}

				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://localhost:" + port + "/jmxrmi");
				return JMXConnectorFactory.connect(url);
			} catch (Exception e) {
				try {
					//noinspection BusyWait
					Thread.sleep(2000);
				} catch (InterruptedException ignored) {
				}
			}
		}
		return null;
	}

	private int findAvailablePort() {
		int port;
		try (ServerSocket socket = new ServerSocket(0)) {
			port = socket.getLocalPort();
		} catch (IOException e) {
			LOG.warn("Failed to find available port for debugging, using default 5006", e);
			return 5006;
		}
		return port;
	}

	public boolean isActive() {
		return !stopRequested;
	}

	public void stop() {
		this.stopRequested = true;

		if (jmxConnector != null) {
			try {
				jmxConnector.close();
			} catch (IOException ignored) {
				// JMX connector may be already disconnected at this point
			}
		}
	}

}
