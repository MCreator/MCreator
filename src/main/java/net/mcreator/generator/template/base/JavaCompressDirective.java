/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.generator.template.base;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This directive captures the output generated inside its body (i.e., between its start-tag and end-tag)
 * and reduces all unbroken white-space sequences to a single white-space character. The inserted character
 * will be a line break if the replaced sequence contains line breaks, or a space otherwise. The very first
 * and very last unbroken white-space sequences will be completely removed.
 * <p>
 * Compared to the built-in compress directive, this one does not compress inside double quotes (Java strings
 * and multiline Java strings, but currently not for quotes inside multiline strings) to preserve the correct
 * user string contents.
 * <p>
 * It is also slightly faster than the built-in compress directive.
 */
public class JavaCompressDirective implements TemplateDirectiveModel {

	@Override public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		Writer result = new CompressingWriter(env.getOut());
		body.render(result);
		result.close();
	}

	private static class CompressingWriter extends Writer {

		private static final int BUFFER_SIZE = 8192;

		private enum State {
			TEXT,        // normal text outside quotes
			WHITESPACE,  // scanning whitespace outside quotes
			QUOTE,       // inside a string
			ESCAPED      // inside a string and just saw a backslash
		}

		private final char[] buffer = new char[BUFFER_SIZE];

		private State state = State.TEXT;
		private boolean whitespaceHasLineBreak = false;
		private boolean firstNonWhitespaceSeen = false;
		private int bufferPos = 0;

		private final Writer out;

		public CompressingWriter(Writer out) {
			this.out = out;
		}

		private void flushBuffer() throws IOException {
			if (bufferPos > 0) {
				out.write(buffer, 0, bufferPos);
				bufferPos = 0;
			}
		}

		private void writeChar(char c) throws IOException {
			if (bufferPos >= BUFFER_SIZE)
				flushBuffer();
			buffer[bufferPos++] = c;
		}

		private void flushWhitespaceIfNeeded() throws IOException {
			if (state == State.WHITESPACE) {
				if (firstNonWhitespaceSeen) {
					writeChar(whitespaceHasLineBreak ? '\n' : ' ');
				}
				whitespaceHasLineBreak = false;
				state = State.TEXT;
			}
		}

		@Override public void write(@Nonnull char[] cbuf, int off, int len) throws IOException {
			for (int i = off; i < off + len; i++) {
				char c = cbuf[i];

				switch (state) {
				case TEXT:
					if (Character.isWhitespace(c)) {
						state = State.WHITESPACE;
						whitespaceHasLineBreak = (c == '\n' || c == '\r');
					} else if (c == '"') {
						writeChar(c);
						state = State.QUOTE;
					} else {
						writeChar(c);
						firstNonWhitespaceSeen = true;
					}
					break;

				case WHITESPACE:
					if (Character.isWhitespace(c)) {
						if (c == '\n' || c == '\r')
							whitespaceHasLineBreak = true;
					} else if (c == '"') {
						flushWhitespaceIfNeeded();
						writeChar(c);
						state = State.QUOTE;
					} else {
						flushWhitespaceIfNeeded();
						writeChar(c);
						firstNonWhitespaceSeen = true;
					}
					break;

				case QUOTE:
					writeChar(c);
					if (c == '\\') {
						state = State.ESCAPED;
					} else if (c == '"') {
						state = State.TEXT;
						firstNonWhitespaceSeen = true;
					}
					break;

				case ESCAPED:
					writeChar(c);
					state = State.QUOTE;
					break;
				}
			}
		}

		@Override public void flush() throws IOException {
			flushWhitespaceIfNeeded();
			flushBuffer();
		}

		@Override public void close() throws IOException {
			flush();
		}

	}

}
