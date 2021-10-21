/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.mcreator.util;

import static org.apache.commons.io.FilenameUtils.indexOfLastSeparator;

/**
 * Based on FilenameUtils from Apache Commons IO, but without Windows : separator check
 */
public class FilenameUtilsPatched {

	private static final String EMPTY_STRING = "";
	public static final char EXTENSION_SEPARATOR = '.';
	private static final int NOT_FOUND = -1;

	public static String getExtension(final String fileName) throws IllegalArgumentException {
		if (fileName == null) {
			return null;
		}
		final int index = indexOfExtension(fileName);
		if (index == NOT_FOUND) {
			return EMPTY_STRING;
		}
		return fileName.substring(index + 1);
	}

	public static int indexOfExtension(final String fileName) throws IllegalArgumentException {
		if (fileName == null) {
			return NOT_FOUND;
		}
		final int extensionPos = fileName.lastIndexOf(EXTENSION_SEPARATOR);
		final int lastSeparator = indexOfLastSeparator(fileName);
		return lastSeparator > extensionPos ? NOT_FOUND : extensionPos;
	}

	public static String getBaseName(final String fileName) {
		return removeExtension(getName(fileName));
	}

	public static String removeExtension(final String fileName) {
		if (fileName == null) {
			return null;
		}
		requireNonNullChars(fileName);

		final int index = indexOfExtension(fileName);
		if (index == NOT_FOUND) {
			return fileName;
		}
		return fileName.substring(0, index);
	}

	private static void requireNonNullChars(final String path) {
		if (path.indexOf(0) >= 0) {
			throw new IllegalArgumentException("Null byte present in file/path name. There are no "
					+ "known legitimate use cases for such data, but several injection attacks may use it");
		}
	}

	public static String getName(final String fileName) {
		if (fileName == null) {
			return null;
		}
		requireNonNullChars(fileName);
		final int index = indexOfLastSeparator(fileName);
		return fileName.substring(index + 1);
	}

}
