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

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
	private static final int sessionIdleTimeoutMinutes = 30;

	private final int port;
	private HttpServer server;
	private final Map<String, Session> sessions = new ConcurrentHashMap<>();
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
			} else if ("DELETE".equalsIgnoreCase(method)) {
				handleDelete(exchange);
			} else {
				exchange.sendResponseHeaders(405, -1);
			}
		});

		server.setExecutor(Executors.newCachedThreadPool());
		server.start();

		scheduler.scheduleAtFixedRate(() -> sessions.forEach((id, session) -> {
			StreamSession stream = session.stream;
			if (stream != null) {
				try {
					stream.sendComment("");
					session.touch();
				} catch (IOException e) {
					LOG.debug("Session {} heart-beat failed, closing its SSE stream", id);
					session.detachStream(stream);
				}
			} else if (session.isIdleLongerThan(sessionIdleTimeoutMinutes)) {
				LOG.debug("Removing idle session {}", id);
				sessions.remove(id);
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
		String sessionId = exchange.getRequestHeaders().getFirst("Mcp-Session-Id");
		if (sessionId == null || sessionId.isBlank()) {
			exchange.sendResponseHeaders(400, -1);
			return;
		}
		Session session = sessions.get(sessionId);
		if (session == null) {
			exchange.sendResponseHeaders(404, -1); // per spec, client is expected to start a new session on 404
			return;
		}

		exchange.getResponseHeaders().add("Content-Type", "text/event-stream");
		exchange.getResponseHeaders().add("Cache-Control", "no-cache");
		exchange.getResponseHeaders().add("Connection", "keep-alive");
		exchange.sendResponseHeaders(200, 0);

		StreamSession stream = new StreamSession(exchange);
		try {
			// JDK HttpServer does not flush the status line and headers of a chunked response until
			// the first body write, so prime the stream so the client sees the response immediately
			stream.sendComment("connected");
		} catch (IOException e) {
			stream.close();
			throw e;
		}

		session.attachStream(stream);
		session.touch();

		LOG.info("SSE stream opened for session {}", sessionId);
	}

	private void handlePost(HttpExchange exchange, McpHandler handler) throws IOException {
		try {
			String requestBody = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			String sessionId = exchange.getRequestHeaders().getFirst("Mcp-Session-Id");
			String newSessionId = null;
			if (sessionId != null && !sessionId.isBlank()) {
				Session session = sessions.get(sessionId);
				if (session == null) {
					exchange.sendResponseHeaders(404, -1); // per spec, client is expected to start a new session on 404
					return;
				}
				session.touch();
			} else if (isInitializeRequest(requestBody)) {
				// Per spec, the session ID is assigned on the initialize request response
				newSessionId = UUID.randomUUID().toString();
				sessions.put(newSessionId, new Session());
				sessionId = newSessionId;
				LOG.info("New Streamable HTTP session established: {}", sessionId);
			} else {
				// Lenient mode: also serve requests without a session ID, so simple clients
				// that ignore the Mcp-Session-Id header and don't use SSE can still use the server
				sessionId = "default";
			}

			CompletableFuture<String> responseFuture = handler.handleMessage(sessionId, requestBody);

			// Per MCP Streamable HTTP spec, the response to a POSTed request is returned in the POST body.
			// The SSE stream is only used for server-initiated messages.
			try {
				String response = responseFuture.get(maxToolDurationSeconds, TimeUnit.SECONDS);
				if (newSessionId != null)
					exchange.getResponseHeaders().add("Mcp-Session-Id", newSessionId);
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

	private void handleDelete(HttpExchange exchange) throws IOException {
		String sessionId = exchange.getRequestHeaders().getFirst("Mcp-Session-Id");
		if (sessionId == null || sessionId.isBlank()) {
			exchange.sendResponseHeaders(400, -1);
			return;
		}
		Session session = sessions.remove(sessionId);
		if (session == null) {
			exchange.sendResponseHeaders(404, -1);
			return;
		}
		session.closeStream();
		LOG.info("Session {} terminated by the client", sessionId);
		exchange.sendResponseHeaders(204, -1);
	}

	private static boolean isInitializeRequest(String requestBody) {
		try {
			JsonElement root = JsonParser.parseString(requestBody);
			return root.isJsonObject() && root.getAsJsonObject().has("method") && "initialize".equals(
					root.getAsJsonObject().get("method").getAsString());
		} catch (Exception e) {
			return false;
		}
	}

	@Override public void stop() {
		scheduler.shutdown();
		if (server != null) {
			server.stop(0);
			LOG.info("MCP HTTP Server stopped");
		}
		sessions.values().forEach(Session::closeStream);
		sessions.clear();
	}

	@Override public void sendMessage(String sessionId, String message) {
		Session session = sessions.get(sessionId);
		StreamSession stream = session != null ? session.stream : null;
		if (stream != null) {
			try {
				stream.sendEvent("message", message);
			} catch (IOException e) {
				LOG.error("Failed to send message to session {}", sessionId, e);
				session.detachStream(stream);
			}
		} else {
			LOG.debug("No open SSE stream for session {}, server-initiated message was dropped", sessionId);
		}
	}

	private static class Session {

		private volatile long lastActive = System.currentTimeMillis();
		@Nullable private volatile StreamSession stream;

		void touch() {
			lastActive = System.currentTimeMillis();
		}

		boolean isIdleLongerThan(int minutes) {
			return System.currentTimeMillis() - lastActive > TimeUnit.MINUTES.toMillis(minutes);
		}

		synchronized void attachStream(StreamSession newStream) {
			StreamSession oldStream = stream;
			if (oldStream != null)
				oldStream.close();
			stream = newStream;
		}

		// only clears the field if the stream is still the current one, so a stale stream can't detach its replacement
		synchronized void detachStream(StreamSession expected) {
			if (stream == expected)
				stream = null;
			expected.close();
		}

		synchronized void closeStream() {
			StreamSession oldStream = stream;
			if (oldStream != null) {
				oldStream.close();
				stream = null;
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
