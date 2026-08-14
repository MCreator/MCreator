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

package net.mcreator.io.mcp;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.mcreator.io.mcp.protocol.JsonRpcRequest;
import net.mcreator.io.mcp.protocol.JsonRpcResponse;
import net.mcreator.io.mcp.protocol.JsonSchemaGenerator;
import net.mcreator.io.mcp.protocol.McpSchema;
import net.mcreator.io.mcp.tool.IMcpTool;
import net.mcreator.io.mcp.transport.McpTransport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class McpServer {

	private static final Logger LOG = LogManager.getLogger(McpServer.class);

	private final String name;
	private final String version;
	private final McpTransport transport;
	private final Gson gson = new GsonBuilder().create();
	private final JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator();
	private final ExecutorService executor = Executors.newCachedThreadPool();

	private final Map<String, IMcpTool> tools = new HashMap<>();

	public McpServer(String name, String version, McpTransport transport) {
		this.name = name;
		this.version = version;
		this.transport = transport;
	}

	public void start() throws IOException {
		transport.start(this::handleMessage);
	}

	public void stop() {
		transport.stop();
		executor.shutdown();
	}

	public void registerTool(IMcpTool tool) {
		tools.put(tool.getName(), tool);
	}

	private CompletableFuture<String> handleMessage(String sessionId, String message) {
		CompletableFuture<String> responseFuture = new CompletableFuture<>();
		executor.execute(() -> {
			try {
				JsonRpcRequest request = gson.fromJson(message, JsonRpcRequest.class);
				if (request == null) {
					responseFuture.complete(null);
					return;
				}

				if (!"2.0".equals(request.jsonrpc())) {
					JsonRpcResponse errorResponse = new JsonRpcResponse(request.id(),
							new JsonRpcResponse.JsonRpcError(-32600, "Invalid Request: expected jsonrpc 2.0"));
					responseFuture.complete(gson.toJson(errorResponse));
					return;
				}

				if (request.method() == null) {
					JsonObject raw = gson.fromJson(message, JsonObject.class);
					if (raw != null && (raw.has("result") || raw.has("error"))) {
						// a JSON-RPC response from the client to a server-initiated request, only acknowledge it
						responseFuture.complete(null);
					} else {
						JsonRpcResponse errorResponse = new JsonRpcResponse(request.id(),
								new JsonRpcResponse.JsonRpcError(-32600, "Invalid Request: missing method"));
						responseFuture.complete(gson.toJson(errorResponse));
					}
					return;
				}

				CompletableFuture<?> resultFuture;
				try {
					switch (request.method()) {
					case "initialize":
						resultFuture = CompletableFuture.completedFuture(handleInitialize());
						break;
					case "notifications/initialized":
						responseFuture.complete(null);
						return;
					case "tools/list":
						resultFuture = CompletableFuture.completedFuture(handleListTools());
						break;
					case "tools/call":
						resultFuture = handleCallTool(request.params());
						break;
					case "resources/list":
						resultFuture = CompletableFuture.completedFuture(new McpSchema.ResourceListResponse(List.of()));
						break;
					case "ping":
						resultFuture = CompletableFuture.completedFuture(new JsonObject());
						break;
					default:
						LOG.warn("Method not found: {}", request.method());
						if (request.id() != null && !request.id().isJsonNull()) {
							JsonRpcResponse errorResponse = new JsonRpcResponse(request.id(),
									new JsonRpcResponse.JsonRpcError(-32601, "Method not found"));
							responseFuture.complete(gson.toJson(errorResponse));
						} else {
							responseFuture.complete(null);
						}
						return;
					}
				} catch (Exception e) {
					LOG.error("Error processing method {}", request.method(), e);
					if (request.id() != null && !request.id().isJsonNull()) {
						JsonRpcResponse errorResponse = new JsonRpcResponse(request.id(),
								new JsonRpcResponse.JsonRpcError(-32603, "Internal error: " + e.getMessage()));
						responseFuture.complete(gson.toJson(errorResponse));
					} else {
						responseFuture.complete(null);
					}
					return;
				}

				if (request.id() != null && !request.id().isJsonNull()) {
					resultFuture.thenAccept(result -> {
						JsonRpcResponse rpcResponse = new JsonRpcResponse(request.id(), gson.toJsonTree(result));
						responseFuture.complete(gson.toJson(rpcResponse));
					}).exceptionally(e -> {
						LOG.error("Error in async processing of method {}", request.method(), e);
						JsonRpcResponse errorResponse = new JsonRpcResponse(request.id(),
								new JsonRpcResponse.JsonRpcError(-32603, "Internal error: " + e.getMessage()));
						responseFuture.complete(gson.toJson(errorResponse));
						return null;
					});
				} else {
					responseFuture.complete(null);
				}
			} catch (com.google.gson.JsonSyntaxException e) {
				LOG.error("JSON parse error", e);
				JsonRpcResponse errorResponse = new JsonRpcResponse(null,
						new JsonRpcResponse.JsonRpcError(-32700, "Parse error"));
				responseFuture.complete(gson.toJson(errorResponse));
			} catch (Exception e) {
				LOG.error("Unexpected error handling message", e);
				JsonRpcResponse errorResponse = new JsonRpcResponse(null,
						new JsonRpcResponse.JsonRpcError(-32603, "Internal error"));
				responseFuture.complete(gson.toJson(errorResponse));
			}
		});
		return responseFuture;
	}

	private McpSchema.InitializeResponse handleInitialize() {
		McpSchema.ServerCapabilities caps = new McpSchema.ServerCapabilities(Map.of(),
				new McpSchema.ResourceCapability(false, false));
		return new McpSchema.InitializeResponse("2025-11-25", caps, new McpSchema.Implementation(name, version));
	}

	private McpSchema.ToolListResponse handleListTools() {
		List<McpSchema.Tool> toolList = new ArrayList<>();
		for (IMcpTool tool : tools.values()) {
			Object outputSchema =
					tool.getOutputType() != null ? schemaGenerator.generateSchema(tool.getOutputType()) : null;
			toolList.add(new McpSchema.Tool(tool.getName(), tool.getDescription(),
					schemaGenerator.generateSchema(tool.getInputType()), tool.getAnnotations(), outputSchema));
		}
		return new McpSchema.ToolListResponse(toolList);
	}

	private CompletableFuture<McpSchema.CallToolResponse> handleCallTool(JsonElement params) {
		McpSchema.CallToolRequest req = gson.fromJson(params, McpSchema.CallToolRequest.class);
		IMcpTool tool = tools.get(req.name());
		if (tool != null) {
			JsonObject arguments = req.arguments() != null && req.arguments().isJsonObject() ?
					req.arguments().getAsJsonObject() :
					new JsonObject();
			return tool.invoke(arguments);
		}
		return CompletableFuture.failedFuture(new RuntimeException("Tool not found: " + req.name()));
	}

}
