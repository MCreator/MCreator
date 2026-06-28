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

package net.mcreator.io.mcp.tool;

import com.google.gson.JsonObject;
import net.mcreator.io.mcp.McpJson;
import net.mcreator.io.mcp.protocol.McpSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

/**
 * Base class for MCP tools with a typed input model.
 * Subclass to add tool-specific APIs (e.g. actionable interfaces) alongside {@link #call}.
 *
 * @param <I> input type used for JSON Schema generation and argument deserialization
 */
public abstract class McpTool<I> implements IMcpTool {

	private static final Logger LOG = LogManager.getLogger(McpTool.class);

	private final Class<I> inputType;
	private final Class<?> outputType;

	protected McpTool(Class<I> inputType) {
		this(inputType, null);
	}

	protected McpTool(Class<I> inputType, Class<?> outputType) {
		this.inputType = inputType;
		this.outputType = outputType;
	}

	@Override public Class<I> getInputType() {
		return inputType;
	}

	@Override public Class<?> getOutputType() {
		return outputType;
	}

	@Override public McpSchema.ToolAnnotations getAnnotations() {
		return null;
	}

	@Override public final CompletableFuture<McpSchema.CallToolResponse> invoke(JsonObject arguments) {
		I input = McpJson.fromJson(arguments, inputType);
		return call(input).thenApply(ToolResult::toResponse);
	}

	/**
	 * Handles a tool invocation with deserialized, typed arguments.
	 */
	protected abstract CompletableFuture<ToolResult> call(I input);

	protected static CompletableFuture<ToolResult> completed(ToolResult result) {
		return CompletableFuture.completedFuture(result);
	}

	protected static CompletableFuture<ToolResult> completedText(String text) {
		return completed(ToolResult.text(text));
	}

	protected static CompletableFuture<ToolResult> completedError(String message) {
		LOG.warn("Tool reported error: {}", message);
		return completed(ToolResult.error(message));
	}

	protected static CompletableFuture<ToolResult> completedError(String message, Throwable cause) {
		LOG.warn("Tool reported error: {}", message, cause);
		return completed(ToolResult.error(message, cause));
	}

	protected static CompletableFuture<ToolResult> completedObject(Object structured) {
		return completed(ToolResult.object(structured));
	}
}
