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

import com.sun.jdi.IncompatibleThreadStateException;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.*;
import com.sun.jdi.request.StepRequest;
import net.mcreator.java.ClassFinder;
import net.mcreator.java.DeclarationFinder;
import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.SquareLoaderIcon;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class DebugPanel extends JPanel {

	private static final Logger LOG = LogManager.getLogger("DebugPanel");

	public static final Color DEBUG_COLOR = new Color(239, 50, 61);

	private static final String WAITING_TO_CONNECT = "waiting_to_connect";
	private static final String DEBUGGING = "debugging";

	private final MCreator mcreator;

	@Nullable private JVMDebugClient debugClient = null;

	private final CardLayout cardLayout = new CardLayout();

	private final DebugThreadView debugThreadView = new DebugThreadView();

	private final DebugFramesView debugFramesView = new DebugFramesView();

	private final JButton resume = new JButton(UIRES.get("16px.debug_resume"));
	private final JButton stepOver = new JButton(UIRES.get("16px.debug_step_over"));
	private final JButton stepInto = new JButton(UIRES.get("16px.debug_step_into"));
	private final JButton stepOut = new JButton(UIRES.get("16px.debug_step_out"));

	private EventSet lastSuspendedEventSet = null;
	private BreakpointEvent lastBreakpointEvent = null;

	private final JPanel markers = new JPanel(new GridLayout(-1, 2, 5, 5));

	private final JPanel markersParent = new JPanel();
	private final CardLayout markersLayout = new CardLayout();

	private final List<DebugMarker> debugMarkers = new ArrayList<>();

	public DebugPanel(MCreator mcreator) {
		this.mcreator = mcreator;

		setBackground(Theme.current().getSecondAltBackgroundColor());

		setLayout(cardLayout);

		setPreferredSize(new Dimension(1400, 280));

		markersParent.setOpaque(false);

		JPanel waitingToConnect = new JPanel(new BorderLayout());
		waitingToConnect.setOpaque(false);
		JLabel loading = L10N.label("debug.loading");
		loading.setIconTextGap(5);
		loading.setFont(loading.getFont().deriveFont(16f));
		loading.setForeground(Theme.current().getAltForegroundColor());
		loading.setIcon(new SquareLoaderIcon(5, 1, Theme.current().getForegroundColor()));
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

		debugFramesView.setBorder(BorderFactory.createTitledBorder(L10N.t("debug.frames")));

		debugging.add("Center", PanelUtils.westAndCenterElement(threadsScroll, debugFramesView));

		JLabel nomarkers = L10N.label("debug.no_markers");
		nomarkers.setFont(loading.getFont().deriveFont(13f));
		nomarkers.setForeground(Theme.current().getAltForegroundColor());
		JComponent nomarkerwrap = PanelUtils.totalCenterInPanel(nomarkers);
		nomarkerwrap.setPreferredSize(new Dimension(475, 0));

		markers.setOpaque(false);
		JScrollPane markersScroll = new JScrollPane();
		JViewport viewport = new JViewport() {
			@Override public Dimension getPreferredSize() {
				return new Dimension(475, super.getPreferredSize().height);
			}
		};
		viewport.setView(PanelUtils.pullElementUp(markers));
		markersScroll.setViewport(viewport);
		markersScroll.setOpaque(false);
		markersScroll.setBorder(null);
		markersScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		markersScroll.getViewport().setOpaque(false);

		markersParent.setBorder(BorderFactory.createTitledBorder(L10N.t("debug.frames")));
		markersParent.setLayout(markersLayout);
		markersParent.add(markersScroll, "markers");
		markersParent.add(nomarkerwrap, "no_markers");
		debugging.add("East", markersParent);

		markersLayout.show(markersParent, "no_markers");

		JToolBar bar = new JToolBar();
		bar.setOrientation(JToolBar.VERTICAL);
		bar.setOpaque(false);
		bar.setBorder(BorderFactory.createEmptyBorder(8, 2, 0, 0));
		bar.setFloatable(false);
		debugging.add("West", bar);

		resume.setToolTipText(L10N.t("debug.resume"));
		resume.addActionListener(e -> {
			if (debugClient != null) {
				if (lastSuspendedEventSet != null) {
					lastSuspendedEventSet.resume();
				} else {
					VirtualMachine vm = debugClient.getVirtualMachine();
					if (vm != null) {
						for (ThreadReference thread : vm.allThreads()) {
							thread.resume();
						}
					}
				}
				markVMResumed();
			}
		});
		bar.add(resume);

		JButton stop = new JButton(UIRES.get("16px.stop"));
		stop.setToolTipText(L10N.t("debug.stop"));
		stop.addActionListener(e -> mcreator.getGradleConsole().cancelTask());
		bar.add(stop);

		bar.addSeparator();

		stepOver.setToolTipText(L10N.t("debug.step_over"));
		stepOver.addActionListener(e -> {
			if (debugClient != null) {
				VirtualMachine vm = debugClient.getVirtualMachine();
				if (vm != null) {
					StepRequest stepRequest = vm.eventRequestManager()
							.createStepRequest(lastBreakpointEvent.thread(), StepRequest.STEP_LINE,
									StepRequest.STEP_OVER);
					stepRequest.enable();
					resume.doClick();
				}
			}
		});
		bar.add(stepOver);

		stepInto.setToolTipText(L10N.t("debug.step_into"));
		stepInto.addActionListener(e -> {
			if (debugClient != null) {
				VirtualMachine vm = debugClient.getVirtualMachine();
				if (vm != null) {
					StepRequest stepRequest = vm.eventRequestManager()
							.createStepRequest(lastBreakpointEvent.thread(), StepRequest.STEP_LINE,
									StepRequest.STEP_INTO);
					stepRequest.enable();
					resume.doClick();
				}
			}
		});
		bar.add(stepInto);

		stepOut.setToolTipText(L10N.t("debug.step_out"));
		stepOut.addActionListener(e -> {
			if (debugClient != null) {
				VirtualMachine vm = debugClient.getVirtualMachine();
				if (vm != null) {
					StepRequest stepRequest = vm.eventRequestManager()
							.createStepRequest(lastBreakpointEvent.thread(), StepRequest.STEP_LINE,
									StepRequest.STEP_OUT);
					stepRequest.enable();
					resume.doClick();
				}
			}
		});
		bar.add(stepOut);

		debugging.add("South", new JEmptyBox(2, 2));

		add(debugging, DEBUGGING);

		setVisible(false);
	}

	public void startDebug(@Nonnull JVMDebugClient debugClient) {
		this.debugClient = debugClient;
		this.debugClient.addEventListener((vm, eventSet, resumed) -> {
			if (!resumed) {
				for (Event event : eventSet) {
					if (event instanceof BreakpointEvent breakpointEvent) {
						SwingUtilities.invokeLater(() -> {
							try {
								debugFramesView.showFrames(breakpointEvent.thread().frames());
							} catch (IncompatibleThreadStateException ignored) {
							}
						});
						lastBreakpointEvent = breakpointEvent;
						break;
					} else if (event instanceof StepEvent stepEvent) {
						stepEvent.request().disable();

						SwingUtilities.invokeLater(() -> {
							try {
								debugFramesView.showFrames(stepEvent.thread().frames());
							} catch (IncompatibleThreadStateException ignored) {
							}
						});

						try {
							DeclarationFinder.InClassPosition position = ClassFinder.fqdnToInClassPosition(
									mcreator.getWorkspace(), stepEvent.location().declaringType().name(),
									"mod.mcreator", mcreator.getGenerator().getProjectJarManager());
							if (position != null) {
								SwingUtilities.invokeLater(() -> {
									CodeEditorView codeEditorView = ProjectFileOpener.openFileSpecific(mcreator,
											position.classFileNode, position.openInReadOnly, position.caret,
											position.virtualFile);
									if (codeEditorView != null)
										codeEditorView.jumpToLine(stepEvent.location().lineNumber());
								});
							}
						} catch (Exception e) {
							LOG.warn("Failed to open file", e);
						}

						break;
					}
				}
				lastSuspendedEventSet = eventSet;
				SwingUtilities.invokeLater(this::markVMSuspended);
			}

			for (Event event : eventSet) {
				if (event instanceof VMStartEvent) {
					initiateDebugSession();
				}
			}
		});

		new Thread(() -> {
			while (this.debugClient != null) {
				VirtualMachine vm = this.debugClient.getVirtualMachine();
				if (vm != null) {
					SwingUtilities.invokeLater(() -> {
						try {
							debugThreadView.updateThreadList(vm.allThreads());
						} catch (Exception ignored) {
						}
					});
				}
				try {
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

		cardLayout.show(this, WAITING_TO_CONNECT);
		setVisible(true);
	}

	public void addMarker(DebugMarker marker) {
		markersLayout.show(markersParent, "markers");
		markers.add(marker);
		markers.revalidate();
		markers.repaint();
		debugMarkers.add(marker);
	}

	public void stopDebug() {
		setVisible(false);
		this.debugClient = null;
	}

	private void initiateDebugSession() {
		new Thread(() -> DebugMarkersHandler.handleDebugMarkers(this), "DebugMarkerLoader").start();

		SwingUtilities.invokeLater(() -> {
			markVMResumed();

			cardLayout.show(this, DEBUGGING);

			mcreator.mcreatorTabs.getTabs().forEach(tab -> {
				if (tab.getContent() instanceof CodeEditorView cev) {
					if (cev.getBreakpointHandler() != null) {
						cev.getBreakpointHandler().newDebugClient(debugClient);
					}
				}
			});
		});
	}

	private void markVMSuspended() {
		resume.setEnabled(true);
		stepOver.setEnabled(true);
		stepInto.setEnabled(true);
		stepOut.setEnabled(true);
	}

	private void markVMResumed() {
		resume.setEnabled(false);
		stepOver.setEnabled(false);
		stepInto.setEnabled(false);
		stepOut.setEnabled(false);

		debugFramesView.hideFrames();
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	@Nullable public JVMDebugClient getDebugClient() {
		return debugClient;
	}

}
