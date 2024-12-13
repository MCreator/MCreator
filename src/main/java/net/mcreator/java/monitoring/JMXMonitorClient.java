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

package net.mcreator.java.monitoring;

import com.sun.management.OperatingSystemMXBean;
import net.mcreator.util.NetworkUtils;
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
import java.util.Map;

public class JMXMonitorClient {

	private static final Logger LOG = LogManager.getLogger("JMXDebugClient");

	private boolean stopRequested = false;

	@Nullable private JMXConnector jmxConnector;

	private final JMXMonitorEventListener listener;

	public JMXMonitorClient(Map<String, String> environment, JMXMonitorEventListener listener, int refreshInterval) {
		this.listener = listener;

		int jmxPort = NetworkUtils.findAvailablePort(1099);
		if (jmxPort == -1) {
			LOG.warn("Failed to find available port for JMX monitoring");
			return;
		}

		//@formatter:off
		String javaToolOptions =
				" -Dcom.sun.management.jmxremote.port=" + jmxPort +
				" -Dcom.sun.management.jmxremote.local.only=true" +
				" -Dcom.sun.management.jmxremote.ssl=false" +
				" -Dcom.sun.management.jmxremote.authenticate=false";
		//@formatter:on
		if (environment.containsKey("JAVA_TOOL_OPTIONS")) {
			environment.put("JAVA_TOOL_OPTIONS", environment.get("JAVA_TOOL_OPTIONS").trim() + " " + javaToolOptions);
		} else {
			environment.put("JAVA_TOOL_OPTIONS", javaToolOptions);
		}

		new Thread(() -> {
			jmxConnector = connectToJMX(jmxPort, 60000);
			if (jmxConnector != null) {
				LOG.info("Connected to JMX. Host: localhost, port: {}", jmxPort);

				try {
					MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

					MemoryMXBean memoryMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
							ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);

					OperatingSystemMXBean osMXBean = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConnection,
							ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);

					listener.connected(jmxConnector);
					while (isActive()) {
						listener.dataRefresh(memoryMXBean, osMXBean);

						try {
							//noinspection BusyWait
							Thread.sleep(refreshInterval);
						} catch (InterruptedException ignored) {
						}
					}
				} catch (Exception e) {
					// Silently handle exceptions because we just stop monitoring in this case
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

	public boolean isActive() {
		return !stopRequested;
	}

	public void stop() {
		if (!this.stopRequested) {
			listener.disconnected();
		}

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
