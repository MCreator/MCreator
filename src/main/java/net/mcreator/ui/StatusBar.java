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

package net.mcreator.ui;

import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatusBar extends JPanel {

	private final JLabel messages = new JLabel("");
	private final JLabel gradleMessages = L10N.label("gradle.idle");

	private final GradleIndicator gradleIndicator = new GradleIndicator();

	private final MCreator mcreator;

	public StatusBar(MCreator mcreator) {
		super(new BorderLayout(0, 0));

		this.mcreator = mcreator;

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
		left.setOpaque(false);

		left.add(new JEmptyBox(5, 5));

		JLabel info = new JLabel(UIRES.get("info"));
		info.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		info.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				mcreator.actionRegistry.aboutMCreator.doAction();
			}
		});
		info.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(info);
		left.add(new JEmptyBox(3, 3));

		JLabel donate = new JLabel(UIRES.get("donate"));
		donate.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		donate.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/donate");
			}
		});
		donate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(donate);
		left.add(new JEmptyBox(3, 3));

		JLabel preferences = new JLabel(UIRES.get("settings"));
		preferences.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		preferences.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				new PreferencesDialog(mcreator, null);
			}
		});
		preferences.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(preferences);
		left.add(new JEmptyBox(10, 10));

		messages.setForeground(Theme.current().getAltForegroundColor());
		messages.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		left.add(messages);

		add("West", left);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
		right.setOpaque(false);

		gradleMessages.setForeground(Theme.current().getAltForegroundColor());
		gradleMessages.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 0));
		right.add(gradleMessages);

		ComponentUtils.deriveFont(gradleMessages, 12);
		ComponentUtils.deriveFont(messages, 12);

		right.add(new JEmptyBox(3, 3));

		right.add(gradleIndicator);

		right.add(new JEmptyBox(5, 5));

		add("East", right);

		setBackground(Theme.current().getBackgroundColor());
		setPreferredSize(new Dimension(22, 22));
		setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, (Theme.current().getAltBackgroundColor()).darker()));

		addToolTipReader();
	}

	private void addToolTipReader() {
		Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
			if (e.getID() == 504 && e.getSource() instanceof JComponent component) {
				if (component.getToolTipText() != null) {
					setMessage(component.getToolTipText().replace("<br>", " ").replace("<br/>", " ")
							.replaceAll("<[^>]*>", ""));
				}
			}
		}, AWTEvent.MOUSE_EVENT_MASK);
	}

	private final ScheduledExecutorService messageRemover = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> scheduledFuture = null;

	private String persistentMessage = "";

	public void setMessage(String message) {
		messages.setText(StringUtils.abbreviateString(message, 95));

		if (scheduledFuture != null)
			scheduledFuture.cancel(false);
		scheduledFuture = messageRemover.schedule(() -> messages.setText(persistentMessage), 1, TimeUnit.SECONDS);
	}

	public void setPersistentMessage(String message) {
		this.persistentMessage = message;
		setMessage(message);
	}

	public void setGradleMessage(String message) {
		gradleMessages.setText(StringUtils.abbreviateString(message, 100));
	}

	public void reloadGradleIndicator() {
		gradleIndicator.repaint();
	}

	private class GradleIndicator extends JComponent {

		@Override public Dimension getPreferredSize() {
			return new Dimension(16, 16);
		}

		@Override protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			switch (mcreator.getGradleConsole().getStatus()) {
			case GradleConsole.READY:
				g.setColor(Theme.current().getAltForegroundColor());
				break;
			case GradleConsole.RUNNING:
				g.setColor(new Color(158, 247, 89));
				break;
			case GradleConsole.ERROR:
				g.setColor(new Color(0xFF5956));
				break;
			}
			if (mcreator.getGradleConsole().isGradleSetupTaskRunning())
				g.setColor(new Color(106, 247, 244));

			g.fillRect(4, 5, 8, 8);
		}
	}

}
