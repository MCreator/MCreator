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

package net.mcreator.ui.gradle;

import com.sun.jdi.event.Event;
import com.sun.jdi.event.VMStartEvent;
import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.PlainToolbarBorder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JToolBar {

	private static final String WAITING_TO_CONNECT = "waiting to connect";
	private static final String DEBUGGING = "debugging";

	private final MCreator mcreator;

	@Nullable private JVMDebugClient debugClient = null;

	private final CardLayout cardLayout = new CardLayout();

	public DebugPanel(MCreator mcreator) {
		this.mcreator = mcreator;

		setBorder(new PlainToolbarBorder());
		setLayout(cardLayout);

		setPreferredSize(new Dimension(0, 320));

		JPanel waitingToConnect = new JPanel(new BorderLayout());
		JLabel loading = L10N.label("debug.loading");
		loading.setFont(loading.getFont().deriveFont(16f));
		loading.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		loading.setIcon(UIRES.get("16px.loading.gif"));
		waitingToConnect.add("Center", PanelUtils.totalCenterInPanel(loading));
		add(waitingToConnect, WAITING_TO_CONNECT);

		JPanel debugging = new JPanel();
		add(debugging, DEBUGGING);

		setVisible(false);
	}

	public void startDebug(@Nonnull JVMDebugClient debugClient) {
		this.debugClient = debugClient;
		this.debugClient.addEventListener((vm, eventSet, resumed) -> {
			for (Event event : eventSet) {
				if (event instanceof VMStartEvent) {
					cardLayout.show(this, DEBUGGING);

					mcreator.mcreatorTabs.getTabs().forEach(tab -> {
						if (tab.getContent() instanceof CodeEditorView cev) {
							if (cev.getBreakpointHandler() != null) {
								cev.getBreakpointHandler().newDebugClient(debugClient);
							}
						}
					});
				}
			}
		});

		cardLayout.show(this, WAITING_TO_CONNECT);
		setVisible(true);
	}

	public void stopDebug() {
		this.debugClient = null;
		setVisible(false);
	}

}
