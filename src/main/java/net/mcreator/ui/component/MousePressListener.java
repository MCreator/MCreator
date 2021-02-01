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

package net.mcreator.ui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public abstract class MousePressListener extends MouseAdapter {

	private int eventCnt = 0;

	Timer timer = new Timer("dbc", false);

	@Override public final void mousePressed(MouseEvent e) {
		pressFiltered(e, 0);

		eventCnt = e.getClickCount();
		if (e.getClickCount() == 1) {
			timer.schedule(new TimerTask() {
				@Override public void run() {
					pressFiltered(e, eventCnt);
					eventCnt = 0;
				}
			}, 250);
		}
	}

	public abstract void pressFiltered(MouseEvent e, int clicks);

}
