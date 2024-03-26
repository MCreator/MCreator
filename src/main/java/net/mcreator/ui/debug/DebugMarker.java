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

import com.sun.jdi.event.BreakpointEvent;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static net.mcreator.ui.debug.DebugPanel.DEBUG_COLOR;

public class DebugMarker extends JPanel {

	private final Border IDLE = BorderFactory.createLineBorder(Theme.current().getAltBackgroundColor());
	private final Border ACTIVE = BorderFactory.createLineBorder(DEBUG_COLOR);

	private boolean active = false;

	private final AtomicInteger hitCountClient = new AtomicInteger(0);
	private final AtomicInteger hitCountServer = new AtomicInteger(0);
	private final AtomicInteger hitCountOther = new AtomicInteger(0);
	private final AtomicLong lastHit = new AtomicLong(0);

	private final JLabel hitCountClientLabel = new JLabel();
	private final JLabel hitCountServerLabel = new JLabel();
	private final JLabel hitCountOtherLabel = new JLabel();

	DebugMarker(DebugPanel debugPanel, String markerName) {
		setPreferredSize(new Dimension(230, 90));
		setLayout(new BorderLayout());
		setBackground(Theme.current().getBackgroundColor());

		String[] markerNameParts = markerName.split(":", 2);
		JLabel title = new JLabel();
		title.setText("<html>" + StringUtils.abbreviateString(markerNameParts[0], 30, true) + "<br><small>"
				+ StringUtils.abbreviateString(markerNameParts[1], 40) + "</html>");
		title.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		ModElement modElement = debugPanel.getMCreator().getWorkspace().getModElementByName(markerNameParts[0]);
		if (modElement != null) {
			JLabel icon = new JLabel(modElement.getElementIcon());
			icon.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 10));
			icon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			icon.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(java.awt.event.MouseEvent evt) {
					ModElementGUI<?> modElementGUI = modElement.getType()
							.getModElementGUI(debugPanel.getMCreator(), modElement, true);
					if (modElementGUI != null) {
						modElementGUI.showView();
					}
				}
			});
			add("East", icon);
		}

		add("North", title);

		JPanel hitCountPanel = new JPanel();
		hitCountPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		hitCountPanel.setLayout(new GridLayout(3, 1));
		hitCountPanel.add(hitCountServerLabel);
		hitCountPanel.add(hitCountClientLabel);
		hitCountPanel.add(hitCountOtherLabel);
		add("Center", hitCountPanel);

		hitCountClientLabel.setIcon(UIRES.get("16px.client"));
		hitCountServerLabel.setIcon(UIRES.get("16px.server"));
		hitCountOtherLabel.setIcon(UIRES.get("16px.any"));

		updateDisplay();
		setBorder(BorderFactory.createLineBorder(Theme.current().getBackgroundColor()));

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(java.awt.event.MouseEvent evt) {
				if (evt.getClickCount() == 2) {
					hitCountClient.set(0);
					hitCountServer.set(0);
					hitCountOther.set(0);
				}
			}
		});
	}

	protected void loaded() {
		active = true;
		new Thread(() -> {
			while (active) {
				try {
					SwingUtilities.invokeAndWait(this::updateDisplay);
					Thread.sleep(100);
				} catch (InterruptedException | InvocationTargetException ignored) {
				}
			}
		}, "DebugMarkerUpdater").start();
	}

	private void updateDisplay() {
		if (System.currentTimeMillis() - lastHit.get() < 200) {
			setBorder(ACTIVE);
		} else {
			setBorder(IDLE);
		}

		hitCountClientLabel.setText(L10N.t("debug.markers.client", hitCountClient.get()));
		hitCountServerLabel.setText(L10N.t("debug.markers.server", hitCountServer.get()));
		hitCountOtherLabel.setText(L10N.t("debug.markers.other", hitCountOther.get()));
	}

	protected void reportHit(BreakpointEvent event) {
		String threadName = event.thread().name().toLowerCase(Locale.ENGLISH);
		if (threadName.contains("client") || threadName.contains("render")) {
			hitCountClient.incrementAndGet();
		} else if (threadName.contains("server")) {
			hitCountServer.incrementAndGet();
		} else {
			hitCountOther.incrementAndGet();
		}
		lastHit.set(System.currentTimeMillis());
	}

	protected void remove() {
		setVisible(false);
		active = false;
	}

}
