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
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.vcs.BranchesPopup;
import net.mcreator.util.DesktopUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class StatusBar extends JPanel {

	private static final Logger LOG = LogManager.getLogger();

	private final JLabel messages = new JLabel("");
	private final JLabel gradleMessages = new JLabel("Gradle idle");

	private final GradleIndicator gradleIndicator = new GradleIndicator();

	private final MCreator mcreator;

	private final JLabel currentBranch = new JLabel();

	public StatusBar(MCreator mcreator) {
		super(new BorderLayout(0, 0));

		this.mcreator = mcreator;

		JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
		left.setOpaque(false);

		currentBranch.setVisible(false);
		currentBranch.setIcon(
				new ImageIcon(ImageUtils.darken(ImageUtils.toBufferedImage(UIRES.get("16px.vcs").getImage()))));
		currentBranch.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				new BranchesPopup(mcreator.getWorkspace().getVCS(), mcreator).show(currentBranch, 0, 0);
			}
		});
		currentBranch.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
		currentBranch.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		ComponentUtils.deriveFont(currentBranch, 12);

		left.add(new JEmptyBox(5, 5));

		JLabel info = new JLabel(UIRES.get("info"));
		info.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				mcreator.actionRegistry.aboutMCreator.doAction();
			}
		});
		info.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(info);
		left.add(new JEmptyBox(3, 3));

		JLabel donate = new JLabel(UIRES.get("donate"));
		donate.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/donate");
			}
		});
		donate.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(donate);
		left.add(new JEmptyBox(3, 3));

		JLabel preferences = new JLabel(UIRES.get("settings"));
		preferences.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				new PreferencesDialog(mcreator, null);
			}
		});
		preferences.setCursor(new Cursor(Cursor.HAND_CURSOR));
		left.add(preferences);
		left.add(new JEmptyBox(10, 10));

		left.add(currentBranch);

		messages.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		left.add(messages);

		add("West", left);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 1));
		right.setOpaque(false);

		gradleMessages.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		right.add(gradleMessages);

		ComponentUtils.deriveFont(gradleMessages, 12);
		ComponentUtils.deriveFont(messages, 12);

		right.add(new JEmptyBox(3, 3));

		right.add(gradleIndicator);

		right.add(new JEmptyBox(5, 5));

		add("East", right);

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setPreferredSize(new Dimension(22, 22));
		setBorder(BorderFactory
				.createMatteBorder(1, 0, 0, 0, ((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).darker()));

		addToolTipReader();

		reloadVCSStatus();
	}

	private void addToolTipReader() {
		Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
			if (e.getID() == 504 && e.getSource() instanceof JComponent) {
				JComponent component = (JComponent) e.getSource();
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

	public void reloadVCSStatus() {
		if (mcreator.getWorkspace().getVCS() != null) {
			currentBranch.setVisible(true);
			try {
				currentBranch.setText(mcreator.getWorkspace().getVCS().getGit().getRepository().getBranch());
			} catch (IOException ignored) {
			}
		} else {
			currentBranch.setVisible(false);
		}
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
				g.setColor((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
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
