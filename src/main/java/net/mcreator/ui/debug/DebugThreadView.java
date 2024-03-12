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

package net.mcreator.ui.debug;

import com.sun.jdi.ThreadReference;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Locale;

import static net.mcreator.ui.debug.DebugPanel.DEBUG_COLOR;

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

	private static class ThreadRenderer extends JPanel implements ListCellRenderer<ThreadReference> {

		private final JLabel name = new JLabel();
		private final JLabel status = new JLabel();

		ThreadRenderer() {
			setLayout(new BorderLayout());
			add(name, BorderLayout.NORTH);
			add(status, BorderLayout.SOUTH);
			ComponentUtils.deriveFont(status, 10);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends ThreadReference> list, ThreadReference thread,
				int index, boolean isSelected, boolean cellHasFocus) {
			try {
				name.setText(thread.name());
				status.setText(L10N.t("debug.threads.status", thread.threadGroup().name(),
						convertThreadStatus(thread.status()).toLowerCase(Locale.ENGLISH)));

				setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

				if (thread.isAtBreakpoint()) {
					setBackground(DEBUG_COLOR);
					name.setForeground(new Color(42, 42, 42));
					status.setForeground(new Color(42, 42, 42));
				} else {
					setBackground(list.getBackground());

					Color foreground = switch (thread.status()) {
						case ThreadReference.THREAD_STATUS_RUNNING -> Theme.current().getForegroundColor();
						case ThreadReference.THREAD_STATUS_WAIT -> new Color(168, 168, 168);
						case ThreadReference.THREAD_STATUS_SLEEPING -> new Color(108, 108, 108);
						default -> Theme.current().getAltForegroundColor();
					};
					name.setForeground(foreground);
					status.setForeground(foreground);
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