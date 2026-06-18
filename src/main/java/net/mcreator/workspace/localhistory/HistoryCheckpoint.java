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

package net.mcreator.workspace.localhistory;

import net.mcreator.ui.init.L10N;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Supplier;

public record HistoryCheckpoint(String hash, String name, int timestamp,
                                Supplier<Future<List<DiffEntry>>> diffFutureSupplier) {

	public String getTimestampString() {
		return Instant.ofEpochSecond(timestamp).atZone(ZoneId.systemDefault())
				.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)
						.withLocale(L10N.getOSLocale()));
	}

	public record DiffEntry(ChangeType changeType, String affectedPath) {}

	public enum ChangeType {
		ADD, REMOVE, MODIFY, RENAME, COPY
	}

}
