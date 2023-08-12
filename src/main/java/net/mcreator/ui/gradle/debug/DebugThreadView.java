/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.gradle.debug;

import com.sun.jdi.ThreadReference;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

public class DebugThreadView extends JList<ThreadReference> {

	public DebugThreadView() {
		setCellRenderer(new ThreadRenderer());

		ListSelectionModel selectionModel = new DefaultListSelectionModel() {
			@Override public void setSelectionInterval(int index0, int index1) {
			}
		};

		setSelectionModel(selectionModel);
		setOpaque(false);
	}

	public void updateThreadList(List<ThreadReference> threadList) {
		DefaultListModel<ThreadReference> model = new DefaultListModel<>();
		for (ThreadReference thread : threadList)
			model.addElement(thread);
		setModel(model);
	}

	private static class ThreadRenderer extends JLabel implements ListCellRenderer<ThreadReference> {

		@Override
		public Component getListCellRendererComponent(JList<? extends ThreadReference> list, ThreadReference thread,
				int index, boolean isSelected, boolean cellHasFocus) {
			try {
				setText("<html><font size=4>" + thread.name() + "</font><br><small>" + L10N.t("debug.threads.status",
						thread.threadGroup().name(), convertThreadStatus(thread.status()).toLowerCase(Locale.ENGLISH))
						+ "</small></html>");

				setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

				if (thread.isAtBreakpoint()) {
					setBackground(new Color(255, 90, 90));
					setForeground(new Color(42, 42, 42));
				} else {
					setBackground(list.getBackground());

					switch (thread.status()) {
					case ThreadReference.THREAD_STATUS_RUNNING -> setForeground(new Color(113, 136, 99));
					case ThreadReference.THREAD_STATUS_SLEEPING -> setForeground(new Color(88, 94, 131));
					case ThreadReference.THREAD_STATUS_WAIT -> setForeground(new Color(99, 129, 152));
					default -> setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
					}
				}

				setOpaque(false);
			} catch (Exception ignored) {
				// VM may be gone when we try to update the renderer
			}

			return this;
		}

		private String convertThreadStatus(int status) {
			return switch (status) {
				case ThreadReference.THREAD_STATUS_ZOMBIE -> "Zombie";
				case ThreadReference.THREAD_STATUS_RUNNING -> "Running";
				case ThreadReference.THREAD_STATUS_SLEEPING -> "Sleeping";
				case ThreadReference.THREAD_STATUS_MONITOR -> "Monitor";
				case ThreadReference.THREAD_STATUS_WAIT -> "Wait";
				case ThreadReference.THREAD_STATUS_NOT_STARTED -> "Not Started";
				default -> "Unknown";
			};
		}

	}
}