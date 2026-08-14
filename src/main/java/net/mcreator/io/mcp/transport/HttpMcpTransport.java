/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.io.mcp.transport;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

public class HttpMcpTransport implements McpTransport {

	private static final Logger LOG = LogManager.getLogger(HttpMcpTransport.class);

	private static final int maxToolDurationSeconds = 120;

	private final int port;
	private HttpServer server;
	private final Map<String, StreamSession> sessions = new ConcurrentHashMap<>();
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	public HttpMcpTransport(int port) {
		this.port = port;
	}

	@Override public void start(McpHandler handler) throws IOException {
		InetAddress loopbackAddress = InetAddress.getLoopbackAddress();
		server = HttpServer.create(new InetSocketAddress(loopbackAddress, port), 0);

		server.createContext("/mcp", exchange -> {
			if (!isOriginAllowed(exchange.getRequestHeaders().getFirst("Origin"))) {
				LOG.warn("Rejected /mcp request with disallowed origin: {}",
						exchange.getRequestHeaders().getFirst("Origin"));
				exchange.sendResponseHeaders(403, -1);
				return;
			}

			String method = exchange.getRequestMethod();
			if ("GET".equalsIgnoreCase(method)) {
				handleGet(exchange);
			} else if ("POST".equalsIgnoreCase(method)) {
				handlePost(exchange, handler);
			} else {
				exchange.sendResponseHeaders(405, -1);
			}
		});

		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		scheduler.scheduleAtFixedRate(() -> sessions.forEach((id, session) -> {
			try {
				session.sendComment("");
			} catch (IOException e) {
				LOG.debug("Session {} heart-beat failed, closing session", id);
				sessions.remove(id);
				session.close();
			}
		}), 15, 15, TimeUnit.SECONDS);

		LOG.info("MCP HTTP Server (Streamable HTTP) started on {}:{} at /mcp", loopbackAddress.getHostAddress(), port);
	}

	/**
	 * The Origin header is only sent by browsers, so requests without it (regular MCP clients) are allowed.
	 * Browser origins other than local ones are rejected to prevent DNS rebinding and drive-by requests
	 * from web pages, as this server is otherwise unauthenticated.
	 */
	private static boolean isOriginAllowed(@Nullable String origin) {
		if (origin == null || origin.isBlank())
			return true;
		try {
			String host = new URI(origin).getHost();
			return host != null && (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1") || host.equals(
					"[::1]") || host.equals("::1"));
		} catch (URISyntaxException e) {
			return false;
		}
	}

	private void handleGet(HttpExchange exchange) throws IOException {
		String sessionId = UUID.randomUUID().toString();
		exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
		exchange.getResponseHeaders().add("Cache-Control", "no-cache");
		exchange.getResponseHeaders().add("Connection", "keep-alive");
		exchange.getResponseHeaders().add("Mcp-Session-Id", sessionId);
		exchange.sendResponseHeaders(200, 0);

		StreamSession session = new StreamSession(exchange);
		sessions.put(sessionId, session);

		LOG.info("New Streamable HTTP session established: {}", sessionId);
	}

	private void handlePost(HttpExchange exchange, McpHandler handler) throws IOException {
		try {
			String sessionId = exchange.getRequestHeaders().getFirst("Mcp-Session-Id");
			String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			CompletableFuture<String> responseFuture = handler.handleMessage(sessionId != null ? sessionId : "default",
					requestBody);

			// Per MCP Streamable HTTP spec, the response to a POSTed request is returned in the POST body,
			// also for session-bound requests. The SSE stream is only used for server-initiated messages.
			try {
				String response = responseFuture.get(maxToolDurationSeconds, TimeUnit.SECONDS);
				if (response != null) {
					byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
					exchange.getResponseHeaders().add("Content-Type", "application/json");
					exchange.sendResponseHeaders(200, bytes.length);
					try (OutputStream os = exchange.getResponseBody()) {
						os.write(bytes);
					}
				} else {
					// notifications and responses produce no response message, acknowledge with 202
					exchange.sendResponseHeaders(202, -1);
				}
			} catch (Exception e) {
				LOG.error("Timeout or error waiting for response", e);
				exchange.sendResponseHeaders(500, -1);
			}
		} catch (Exception e) {
			LOG.error("Error handling POST request", e);
			exchange.sendResponseHeaders(500, -1);
		}
	}

	@Override public void stop() {
		scheduler.shutdown();
		if (server != null) {
			server.stop(0);
			LOG.info("MCP HTTP Server stopped");
		}
		sessions.values().forEach(StreamSession::close);
		sessions.clear();
	}

	@Override public void sendMessage(String sessionId, String message) {
		StreamSession session = sessions.get(sessionId);
		if (session != null) {
			try {
				session.sendEvent("message", message);
			} catch (IOException e) {
				LOG.error("Failed to send message to session {}", sessionId, e);
				sessions.remove(sessionId);
				session.close();
			}
		} else {
			if (sessionId != null && !sessionId.equals("default")) {
				LOG.warn("Attempted to send message to unknown or closed session: {}", sessionId);
			}
		}
	}

	private static class StreamSession {
		private final HttpExchange exchange;
		private final OutputStream os;
		private boolean closed = false;

		public StreamSession(HttpExchange exchange) {
			this.exchange = exchange;
			this.os = exchange.getResponseBody();
		}

		public synchronized void sendEvent(String event, String data) throws IOException {
			if (closed)
				throw new IOException("Session closed");
			String payload = "event: " + event + "\ndata: " + data + "\n\n";
			os.write(payload.getBytes(StandardCharsets.UTF_8));
			os.flush();
		}

		public synchronized void sendComment(String comment) throws IOException {
			if (closed)
				throw new IOException("Session closed");
			String payload = ": " + comment + "\n\n";
			os.write(payload.getBytes(StandardCharsets.UTF_8));
			os.flush();
		}

		public synchronized void close() {
			if (closed)
				return;
			closed = true;
			try {
				exchange.close();
			} catch (Exception ignored) {
			}
		}
	}
}
