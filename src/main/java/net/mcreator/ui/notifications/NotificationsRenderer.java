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

package net.mcreator.ui.notifications;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class NotificationsRenderer {

	private final List<BalloonTip> tips = new ArrayList<>();

	private final JComponent anchor;

	public NotificationsRenderer(JComponent anchor) {
		this.anchor = anchor;
	}

	public void addNotification(@Nullable String title, String text, @Nullable ImageIcon icon,
			ActionButton... actionButtons) {
		JButton closeButton = new JButton();

		JPanel tipContents = new JPanel(new BorderLayout());

		if (title != null) {
			JLabel titleLabel = new JLabel("<html><b>" + title);
			titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 8, 0));
			tipContents.add("North", titleLabel);
		}

		if (actionButtons.length > 0) {
			JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			actionButtonsPanel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

			boolean first = true;
			for (ActionButton actionButton : actionButtons) {
				if (first)
					first = false;
				else
					actionButtonsPanel.add(new JEmptyBox(5, 5));

				JButton button = new JButton(actionButton.text);
				button.setMargin(new Insets(0, 5, 0, 5));
				if (actionButton.closePopup())
					button.addActionListener(e -> closeButton.doClick());
				button.addActionListener(actionButton.action);
				actionButtonsPanel.add(button);
			}

			tipContents.add("Center",
					PanelUtils.centerAndSouthElement(new JLabel("<html>" + text), actionButtonsPanel));
		} else {
			tipContents.add("Center", new JLabel("<html>" + text));
		}

		if (icon != null) {
			JLabel iconLabel = new JLabel(icon);
			iconLabel.setBorder(BorderFactory.createEmptyBorder(3, 1, 0, 8));
			tipContents.add("West", PanelUtils.pullElementUp(iconLabel));
		}

		BalloonTip balloonTip = new BalloonTip(this.anchor, tipContents,
				new EdgedBalloonStyle(Theme.current().getBackgroundColor(), Theme.current().getAltForegroundColor()),
				BalloonTip.Orientation.RIGHT_BELOW, BalloonTip.AttachLocation.SOUTHEAST, -10, 10, false);

		closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		closeButton.setContentAreaFilled(false);
		closeButton.setIcon(UIRES.get("close_small"));
		balloonTip.setCloseButton(closeButton, false);

		balloonTip.setVisible(true);

		if (!tips.isEmpty()) {
			BalloonTip previous = tips.get(tips.size() - 1);
			previous.setVisible(false);
			closeButton.addActionListener(e -> {
				previous.setVisible(true);
				tips.remove(balloonTip);
			});
		}

		tips.add(balloonTip);
	}

	public record ActionButton(String text, ActionListener action, boolean closePopup) {

		public ActionButton(String text, ActionListener action) {
			this(text, action, true);
		}

	}

}
