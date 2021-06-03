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
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class OutputStreamEventHandler extends OutputStream {

	private final StringBuilder buffer = new StringBuilder();

	private final LineReceiver lineReceiver;

	public OutputStreamEventHandler(@Nonnull LineReceiver lineReceiver) {
		this.lineReceiver = lineReceiver;
	}

	@Override public void write(int i) {
		buffer.append((char) i);
		if (i == '\n')
			event();
	}

	@Override public void write(@Nonnull byte[] b, int off, int len) {
		int start = off;
		int finallen = off + len;
		for (int i = off; i < finallen; i++) {
			if (b[i] == '\n') {
				buffer.append(new String(b, start, i - start + 1, StandardCharsets.UTF_8));
				event();
				start = i + 1;
			}
		}

		if (start < finallen) {
			buffer.append(new String(b, start, finallen - start, StandardCharsets.UTF_8));
		}
	}

	private void event() {
		lineReceiver.lineReceived(buffer.toString().replaceAll("([\n\r])", ""));
		buffer.setLength(0);
	}

	public interface LineReceiver {
		void lineReceived(String line);
	}

}
