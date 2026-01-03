/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.io;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OutputStreamEventHandler extends OutputStream {

	private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	private final LineReceiver lineReceiver;

	public OutputStreamEventHandler(@Nonnull LineReceiver lineReceiver) {
		this.lineReceiver = lineReceiver;
	}

	@Override public void write(int i) {
		buffer.write(i);
		if (i == '\n')
			event();
	}

	@Override public void write(@Nonnull byte[] b, int off, int len) {
		int start = off;
		int end = off + len;
		for (int i = off; i < end; i++) {
			if (b[i] == '\n') {
				buffer.write(b, start, i - start + 1);
				event();
				start = i + 1;
			}
		}
		if (start < end) {
			buffer.write(b, start, end - start);
		}
	}

	@Override public void flush() {
		if (buffer.size() > 0)
			event();
	}

	@Override public void close() {
		flush();
	}

	private void event() {
		String line = buffer.toString(StandardCharsets.UTF_8).replace("\r", "").replace("\n", "");
		lineReceiver.lineReceived(line);
		buffer.reset();
	}

	public interface LineReceiver {
		void lineReceived(String line);
	}

}