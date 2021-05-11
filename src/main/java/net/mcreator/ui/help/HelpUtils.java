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

package net.mcreator.ui.help;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.EdgedBalloonStyle;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.DesktopUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.atomic.AtomicReference;

public class HelpUtils {

	public static Component wrapWithHelpButton(IHelpContext context, Component ca) {
		return wrapWithHelpButton(context, ca, null, SwingConstants.RIGHT);
	}

	public static Component wrapWithHelpButton(IHelpContext context, Component ca, int direction) {
		return wrapWithHelpButton(context, ca, null, direction);
	}

	public static Component wrapWithHelpButton(IHelpContext context, Component ca, @Nullable Color ac) {
		return wrapWithHelpButton(context, ca, ac, SwingConstants.RIGHT);
	}

	public static Component wrapWithHelpButton(IHelpContext context, Component ca, @Nullable Color ac, int direction) {
		JLabel lab = new JLabel(HelpLoader.hasFullHelp(context) ? UIRES.get("help") : UIRES.get("help_partial"));
		lab.setCursor(new Cursor(Cursor.HAND_CURSOR));

		AtomicReference<BalloonTip> balloonTipAtomicReference = new AtomicReference<>();
		lab.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				// lazy load tooltip for performance reasons
				if (balloonTipAtomicReference.get() == null) {
					JTextPane editorPane = new JTextPane();

					editorPane.setContentType("text/html");
					editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
					ComponentUtils.deriveFont(editorPane, 12);

					editorPane.setEditable(false);

					editorPane.addHyperlinkListener(he -> {
						if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							DesktopUtils.browseSafe(he.getURL().toString());
						}
					});

					editorPane.setText(HelpLoader.loadHelpFor(context));

					JScrollPane scrollPane = new JScrollPane(editorPane);
					scrollPane.setPreferredSize(new Dimension(335, 190));

					BalloonTip balloonTip = new BalloonTip(lab, scrollPane,
							new EdgedBalloonStyle((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
									(Color) UIManager.get("MCreatorLAF.GRAY_COLOR")), BalloonTip.Orientation.LEFT_BELOW,
							BalloonTip.AttachLocation.ALIGNED, 10, 10, false);

					balloonTip.setFocusable(true);
					balloonTip.addFocusListener(new FocusAdapter() {
						@Override public void focusLost(FocusEvent e) {
							super.focusLost(e);
							if (e.getOppositeComponent() != editorPane)
								balloonTip.setVisible(false);
						}
					});

					editorPane.addFocusListener(new FocusAdapter() {
						@Override public void focusLost(FocusEvent e) {
							super.focusLost(e);
							if (e.getOppositeComponent() != balloonTip)
								balloonTip.setVisible(false);
						}
					});

					balloonTip.setVisible(false);

					JButton closeButton = new JButton();
					closeButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
					closeButton.setContentAreaFilled(false);
					closeButton.setIcon(UIRES.get("close_small"));
					balloonTip.setCloseButton(closeButton, false);

					editorPane.setCaretPosition(0);

					balloonTipAtomicReference.set(balloonTip);
				}

				balloonTipAtomicReference.get().setVisible(!balloonTipAtomicReference.get().isVisible());

				if (balloonTipAtomicReference.get().isVisible())
					balloonTipAtomicReference.get().requestFocus(true);
			}
		});

		if (ac != null)
			ca.setForeground(ac);

		if (direction == SwingConstants.RIGHT)
			return PanelUtils.join(FlowLayout.LEFT, ca, lab);
		else {
			lab.setBorder(BorderFactory.createEmptyBorder(1, 0, 0, 3));
			return PanelUtils.join(FlowLayout.LEFT, 0, 0, lab, ca);
		}
	}
}
