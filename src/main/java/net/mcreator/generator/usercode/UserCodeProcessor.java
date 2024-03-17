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

package net.mcreator.generator.usercode;

import net.mcreator.io.FileIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class UserCodeProcessor {

	private static final Logger LOG = LogManager.getLogger(UserCodeProcessor.class);

	private static final String USER_CODE_BLOCK_START = " Start of user code block ";
	private static final String USER_CODE_BLOCK_END = " End of user code block ";

	public static String processUserCode(File currentCodeFile, String newCode, String lineCommentStart) {
		// If the new code does not contain user code blocks, we can return the new code
		if (!newCode.contains(USER_CODE_BLOCK_START) || !newCode.contains(USER_CODE_BLOCK_END))
			return newCode;

		if (currentCodeFile.isFile()) {
			String currentCode = FileIO.readFileToString(currentCodeFile);
			return processUserCode(currentCode, newCode, lineCommentStart);
		}

		// If the current code is null, we can return the new code
		return newCode;
	}

	public static String processUserCode(@Nullable String currentCode, String newCode, String lineCommentStart) {
		// If the current code is null, we can return the new code
		if (currentCode == null || currentCode.isBlank())
			return newCode;

		// If the new code does not contain user code blocks, we can return the new code
		if (!newCode.contains(USER_CODE_BLOCK_START) || !newCode.contains(USER_CODE_BLOCK_END))
			return newCode;

		// If the current code does not contain user code blocks, we can return the new code
		if (!currentCode.contains(USER_CODE_BLOCK_START) || !currentCode.contains(USER_CODE_BLOCK_END))
			return newCode;

		Map<String, String> userCodeBlocks = getUserCodeBlocks(currentCode, lineCommentStart);

		// If the user code blocks are empty, we can return the new code directly
		if (userCodeBlocks.isEmpty())
			return newCode;

		return updateUserBlocks(newCode, userCodeBlocks, lineCommentStart);
	}

	private static Map<String, String> getUserCodeBlocks(String code, String lineCommentStart) {
		Map<String, String> userCodeBlocks = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(new StringReader(code))) {
			String currentUserCodeBlock = null;
			StringBuilder currentUserCodeBlockContent = new StringBuilder();

			final String codeBlockStart = lineCommentStart + USER_CODE_BLOCK_START;

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.strip();

				if (line.startsWith(codeBlockStart)) {
					currentUserCodeBlock = line.substring(codeBlockStart.length()).strip();
					currentUserCodeBlockContent = new StringBuilder();
				} else if (line.equals(lineCommentStart + USER_CODE_BLOCK_END + currentUserCodeBlock)) {
					String currentUserCodeBlockContentString = currentUserCodeBlockContent.toString();
					if (currentUserCodeBlock != null && !currentUserCodeBlockContentString.isBlank())
						userCodeBlocks.put(currentUserCodeBlock, currentUserCodeBlockContentString);
					currentUserCodeBlock = null;
				} else if (currentUserCodeBlock != null) {
					currentUserCodeBlockContent.append(line).append("\n");
				}
			}
		} catch (Exception e) {
			LOG.warn("Failed to parse user code blocks", e);
		}

		return userCodeBlocks;
	}

	private static String updateUserBlocks(String code, Map<String, String> userCodeBlocks, String lineCommentStart) {
		StringBuilder newCode = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new StringReader(code))) {
			final String codeBlockStart = lineCommentStart + USER_CODE_BLOCK_START;

			String line;
			while ((line = reader.readLine()) != null) {
				line = line.strip();
				newCode.append(line).append("\n");
				if (line.startsWith(codeBlockStart)) {
					String currentUserCodeBlock = line.substring(codeBlockStart.length()).strip();
					String userCodeBlockContent = userCodeBlocks.get(currentUserCodeBlock);
					if (userCodeBlockContent != null)
						newCode.append(userCodeBlockContent);
				}
			}
		} catch (Exception e) {
			LOG.warn("Failed to parse user code blocks", e);
		}
		return newCode.toString();
	}

}
