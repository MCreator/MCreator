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

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMStartEvent;
import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.WrapLayout;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.PlainToolbarBorder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DebugPanel extends JToolBar {

	private static final String WAITING_TO_CONNECT = "waiting_to_connect";
	private static final String DEBUGGING = "debugging";

	private final MCreator mcreator;

	@Nullable private JVMDebugClient debugClient = null;

	private final CardLayout cardLayout = new CardLayout();

	private final DebugThreadView debugThreadView = new DebugThreadView();

	private final JButton resume = L10N.button("debug.resume");

	private EventSet lastEventSet = null;

	private final JPanel markers = new JPanel(new WrapLayout(FlowLayout.LEFT));

	private final JPanel markersParent = new JPanel();
	private final CardLayout markersLayout = new CardLayout();

	private final List<DebugMarker> debugMarkers = new ArrayList<>();

	public DebugPanel(MCreator mcreator) {
		this.mcreator = mcreator;

		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		setBorder(new PlainToolbarBorder());
		setLayout(cardLayout);

		setPreferredSize(new Dimension(800, 310));

		markersParent.setOpaque(false);

		JPanel waitingToConnect = new JPanel(new BorderLayout());
		waitingToConnect.setOpaque(false);
		JLabel loading = L10N.label("debug.loading");
		loading.setFont(loading.getFont().deriveFont(16f));
		loading.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));
		loading.setIcon(UIRES.get("16px.loading.gif"));
		waitingToConnect.add("Center", PanelUtils.totalCenterInPanel(loading));
		add(waitingToConnect, WAITING_TO_CONNECT);

		JPanel debugging = new JPanel(new BorderLayout(5, 5));
		debugging.setOpaque(false);

		JScrollPane threadsScroll = new JScrollPane(debugThreadView);
		threadsScroll.setOpaque(false);
		threadsScroll.setBorder(null);
		threadsScroll.getViewport().setOpaque(false);
		threadsScroll.setBorder(BorderFactory.createTitledBorder(L10N.t("debug.threads")));
		threadsScroll.setPreferredSize(new Dimension(300, 0));
		debugging.add("West", threadsScroll);
		add(debugging, DEBUGGING);

		JLabel nomarkers = L10N.label("debug.no_markers");
		nomarkers.setFont(loading.getFont().deriveFont(14f));
		nomarkers.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		markers.setOpaque(false);
		JScrollPane markersScroll = new JScrollPane(markers);
		markersScroll.setOpaque(false);
		markersScroll.setBorder(null);
		markersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		markersScroll.getViewport().setOpaque(false);
		debugging.add("West", markersScroll);

		markersParent.setLayout(markersLayout);
		markersParent.add(markers, "markers");
		markersParent.add(PanelUtils.totalCenterInPanel(nomarkers), "no_markers");
		markersParent.setBorder(BorderFactory.createTitledBorder(L10N.t("debug.markers")));
		debugging.add("Center", markersParent);

		markersLayout.show(markersParent, "no_markers");

		JToolBar bar = new JToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
		bar.setFloatable(false);
		debugging.add("North", bar);

		resume.setIcon(UIRES.get("16px.fwd"));
		resume.addActionListener(e -> {
			if (debugClient != null) {
				if (lastEventSet != null) {
					lastEventSet.resume();
					resume.setEnabled(false);
				}
			}
		});
		bar.add(resume);

		JButton stop = L10N.button("debug.stop");
		stop.setIcon(UIRES.get("16px.stop.gif"));
		stop.addActionListener(e -> mcreator.getGradleConsole().cancelTask());
		bar.add(stop);

		debugging.add("South", new JEmptyBox(2, 2));

		setVisible(false);
	}

	public void startDebug(@Nonnull JVMDebugClient debugClient) {
		this.debugClient = debugClient;
		this.debugClient.addEventListener((vm, eventSet, resumed) -> {
			if (!resumed) {
				lastEventSet = eventSet;
				resume.setEnabled(true);
			}

			for (Event event : eventSet) {
				if (event instanceof VMStartEvent) {
					initiateDebugSession();
				}
			}
		});

		new Thread(() -> {
			while (this.debugClient != null) {
				try {
					VirtualMachine vm = this.debugClient.getVirtualMachine();
					if (vm != null) {
						debugThreadView.updateThreadList(vm.allThreads());
					}

					//noinspection BusyWait
					Thread.sleep(1000);
				} catch (Exception ignored) {
				}
			}
		}, "DebugPanelUpdater").start();

		for (DebugMarker marker : debugMarkers) {
			marker.remove();
		}
		debugMarkers.clear();
		markers.removeAll();
		markersLayout.show(markersParent, "no_markers");

		resume.setEnabled(false);

		cardLayout.show(this, WAITING_TO_CONNECT);
		setVisible(true);
	}

	public void addMarker(DebugMarker marker) {
		markersLayout.show(markersParent, "markers");
		markers.add(marker);
		debugMarkers.add(marker);
	}

	public void stopDebug() {
		setVisible(false);
		this.debugClient = null;
	}

	private void initiateDebugSession() {
		cardLayout.show(this, DEBUGGING);

		mcreator.mcreatorTabs.getTabs().forEach(tab -> {
			if (tab.getContent() instanceof CodeEditorView cev) {
				if (cev.getBreakpointHandler() != null) {
					cev.getBreakpointHandler().newDebugClient(debugClient);
				}
			}
		});

		new Thread(() -> DebugMarkersHandler.handleDebugMarkers(this), "DebugMarkerLoader").start();
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	@Nullable public JVMDebugClient getDebugClient() {
		return debugClient;
	}

}
