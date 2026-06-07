/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.workspace.localhistory;

import net.mcreator.gradle.GradleResultCode;
import net.mcreator.gradle.GradleStateListener;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.gradle.GradleConsole;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.math.TimeUtils;
import net.mcreator.workspace.localhistory.HistoryCheckpoint;
import net.mcreator.workspace.localhistory.LocalHistoryException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class LocalHistoryPanel extends JPanel {

	private final MCreator mcreator;

	private final JList<HistoryCheckpoint> checkpointList;
	private final DefaultTableModel diffModel;
	private final CardLayout diffCardLayout;
	private final JPanel diffContent;
	private final CardLayout mainCardLayout;
	private final JPanel mainContent;

	private final JButton revertCheckpoint;
	private final JMenuItem resetHistory;

	@Nullable private SwingWorker<List<HistoryCheckpoint.DiffEntry>, Void> diffWorker;

	public LocalHistoryPanel(MCreator mcreator) {
		super(new BorderLayout());
		setOpaque(false);

		this.mcreator = mcreator;

		checkpointList = new JList<>();
		checkpointList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		checkpointList.setFixedCellHeight(48);
		checkpointList.setBackground(Theme.current().getSecondAltBackgroundColor());
		checkpointList.setCellRenderer(new CheckpointListCellRenderer());
		ComponentUtils.deriveFont(checkpointList, 13);

		JScrollPane checkpointScroll = new JScrollPane(checkpointList);
		checkpointScroll.setBackground(Theme.current().getSecondAltBackgroundColor());

		diffModel = new DefaultTableModel(new Object[] { "", "" }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable diffTable = new JTable(diffModel);
		diffTable.setRowHeight(28);
		diffTable.setCellSelectionEnabled(false);
		diffTable.setRowSelectionAllowed(true);
		diffTable.setColumnSelectionAllowed(false);
		diffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		diffTable.getColumnModel().getColumn(0).setMaxWidth(90);
		diffTable.getColumnModel().getColumn(0).setPreferredWidth(75);
		diffTable.setDefaultRenderer(Object.class,
				new DiffTableCellRenderer(diffTable.getDefaultRenderer(Object.class)));
		diffTable.setShowVerticalLines(false);
		diffTable.setBackground(Theme.current().getSecondAltBackgroundColor());
		diffTable.setTableHeader(null);

		JScrollPane diffScroll = new JScrollPane(diffTable);
		diffScroll.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 3));

		JLabel emptyHistoryLabel = new JLabel(L10N.t("dialog.local_history.no_history"), SwingConstants.CENTER);
		emptyHistoryLabel.setForeground(Theme.current().getAltForegroundColor());
		ComponentUtils.deriveFont(emptyHistoryLabel, 13);

		JLabel noChangesLabel = new JLabel(L10N.t("dialog.local_history.no_changes"), SwingConstants.CENTER);
		noChangesLabel.setForeground(Theme.current().getAltForegroundColor());
		ComponentUtils.deriveFont(noChangesLabel, 13);

		diffCardLayout = new CardLayout();
		diffContent = new JPanel(diffCardLayout);
		diffContent.setOpaque(false);
		diffContent.add(diffScroll, "table");
		diffContent.add(PanelUtils.totalCenterInPanel(noChangesLabel), "empty");

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, checkpointScroll, diffContent);
		splitPane.setDividerLocation((int) (mcreator.getHeight() * 0.55));
		splitPane.setResizeWeight(1);
		splitPane.setOpaque(false);

		mainCardLayout = new CardLayout();
		mainContent = new JPanel(mainCardLayout);
		mainContent.setOpaque(false);
		mainContent.add(PanelUtils.totalCenterInPanel(emptyHistoryLabel), "empty");
		mainContent.add(splitPane, "history");

		JToolBar topBar = new JToolBar();
		topBar.setOpaque(false);
		topBar.setFloatable(false);

		revertCheckpoint = L10N.button("dialog.local_history.revert");
		revertCheckpoint.setIcon(UIRES.get("16px.rwd"));
		revertCheckpoint.addActionListener(_ -> revertToSelectedCheckpoint());

		mcreator.getGradleConsole().addGradleStateListener(new GradleStateListener() {
			@Override public void taskStarted(String taskName) {
				updateRevertButtonState();
			}

			@Override public void taskFinished(GradleResultCode result) {
				updateRevertButtonState();
			}
		});

		topBar.add(revertCheckpoint);
		topBar.add(Box.createHorizontalGlue());

		JPopupMenu moreOptionsMenu = new JPopupMenu();
		resetHistory = new JMenuItem(L10N.t("dialog.local_history.reset"));
		JMenuItem optimizeStorage = new JMenuItem(L10N.t("dialog.local_history.optimize"));
		moreOptionsMenu.add(resetHistory);
		moreOptionsMenu.add(optimizeStorage);

		optimizeStorage.addActionListener(_ -> {
			boolean success = mcreator.getWorkspace().getHistoryManager().optimizeStorage();
			if (success) {
				JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.local_history.optimize_success.message"),
						L10N.t("dialog.local_history.optimize.title"), JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.local_history.optimize_failed.message"),
						L10N.t("dialog.local_history.optimize.title"), JOptionPane.WARNING_MESSAGE);
			}
		});

		resetHistory.addActionListener(_ -> {
			int option = JOptionPane.showConfirmDialog(mcreator, L10N.t("dialog.local_history.reset_confirm"),
					L10N.t("dialog.local_history.reset"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				mcreator.getWorkspace().resetLocalHistory();
				registerCheckpointListener();
				reloadContent();
			}
		});

		JButton moreOptions = new JButton(UIRES.get("more"));
		moreOptions.addActionListener(_ -> moreOptionsMenu.show(moreOptions, 0, moreOptions.getHeight()));
		topBar.add(moreOptions);

		checkpointList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			loadSelectedCheckpointDiff();
		});

		add("Center", mainContent);
		add("North", topBar);

		registerCheckpointListener();
		reloadContent();
	}

	private void registerCheckpointListener() {
		mcreator.getWorkspace().getHistoryManager().setCheckpointListener(() -> reloadContent(true));
	}

	public void reloadContent() {
		reloadContent(false);
	}

	private void reloadContent(boolean selectNewestCheckpoint) {
		cancelDiffWorker();

		@Nullable String selectedHash = null;
		if (!selectNewestCheckpoint) {
			HistoryCheckpoint selected = checkpointList.getSelectedValue();
			if (selected != null) {
				selectedHash = selected.hash();
			}
		}

		List<HistoryCheckpoint> checkpoints = mcreator.getWorkspace().getHistoryManager().getCheckpoints();
		checkpointList.setListData(checkpoints.toArray(HistoryCheckpoint[]::new));

		resetHistory.setEnabled(!checkpoints.isEmpty());
		checkpointList.setEnabled(!checkpoints.isEmpty());
		revertCheckpoint.setEnabled(false);

		if (checkpoints.isEmpty()) {
			diffModel.setRowCount(0);
			diffCardLayout.show(diffContent, "empty");
			mainCardLayout.show(mainContent, "empty");
			checkpointList.clearSelection();
			return;
		}

		mainCardLayout.show(mainContent, "history");

		if (selectedHash != null) {
			for (int i = 0; i < checkpoints.size(); i++) {
				if (checkpoints.get(i).hash().equals(selectedHash)) {
					checkpointList.setSelectedIndex(i);
					return;
				}
			}
		}

		checkpointList.setSelectedIndex(0);
	}

	private void revertToSelectedCheckpoint() {
		if (isGradleRunning()) {
			return;
		}

		HistoryCheckpoint selected = checkpointList.getSelectedValue();
		if (selected == null) {
			return;
		}

		int checkpointsToRevert = checkpointList.getSelectedIndex() + 1;
		long timeBackMillis = Math.max(0, System.currentTimeMillis() - selected.timestamp() * 1000L);
		timeBackMillis = (timeBackMillis / TimeUtils.ONE_MINUTE) * TimeUtils.ONE_MINUTE;

		int option = JOptionPane.showConfirmDialog(mcreator,
				L10N.t("dialog.local_history.revert_confirm", checkpointsToRevert,
						TimeUtils.millisToLongDHMS(timeBackMillis)), L10N.t("dialog.local_history.revert"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (option != JOptionPane.YES_OPTION) {
			return;
		}

		try {
			mcreator.getWorkspace().getHistoryManager().revertToCheckpoint(selected);
			mcreator.reloadWorkspaceFromFileSystem();
			this.reloadContent();
		} catch (LocalHistoryException e) {
			JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.local_history.revert_failed.message"),
					L10N.t("dialog.local_history.revert"), JOptionPane.ERROR_MESSAGE);
		}
	}

	private boolean isGradleRunning() {
		return mcreator.getGradleConsole().getStatus() == GradleConsole.RUNNING;
	}

	private void updateRevertButtonState() {
		HistoryCheckpoint selected = checkpointList.getSelectedValue();
		boolean gradleRunning = isGradleRunning();
		revertCheckpoint.setEnabled(selected != null && !gradleRunning);
		if (selected != null && gradleRunning) {
			revertCheckpoint.setToolTipText(L10N.t("action.gradle.disabled"));
		} else {
			revertCheckpoint.setToolTipText(null);
		}
	}

	private void loadSelectedCheckpointDiff() {
		HistoryCheckpoint selected = checkpointList.getSelectedValue();
		updateRevertButtonState();

		cancelDiffWorker();

		if (selected == null) {
			diffModel.setRowCount(0);
			diffCardLayout.show(diffContent, "empty");
			return;
		}

		diffCardLayout.show(diffContent, "table");

		diffWorker = new SwingWorker<>() {
			@Override protected List<HistoryCheckpoint.DiffEntry> doInBackground() {
				return selected.diffSupplier().get();
			}

			@Override protected void done() {
				if (isCancelled()) {
					return;
				}

				try {
					List<HistoryCheckpoint.DiffEntry> entries = get();
					populateDiffTable(diffModel, entries);
					diffCardLayout.show(diffContent, entries.isEmpty() ? "empty" : "table");
				} catch (Exception ignored) {
					diffModel.setRowCount(0);
					diffCardLayout.show(diffContent, "empty");
				}
			}
		};
		diffWorker.execute();
	}

	private void cancelDiffWorker() {
		if (diffWorker != null) {
			diffWorker.cancel(true);
			diffWorker = null;
		}
	}

	private static void populateDiffTable(DefaultTableModel diffModel, List<HistoryCheckpoint.DiffEntry> entries) {
		diffModel.setRowCount(0);
		for (HistoryCheckpoint.DiffEntry entry : entries) {
			diffModel.addRow(new Object[] { entry.changeType(), entry.affectedPath() });
		}
	}

	private static String getChangeTypeLabel(HistoryCheckpoint.ChangeType changeType) {
		return switch (changeType) {
			case ADD -> L10N.t("local_history.change.add");
			case MODIFY -> L10N.t("local_history.change.modify");
			case REMOVE -> L10N.t("local_history.change.remove");
			case RENAME -> L10N.t("local_history.change.rename");
			case COPY -> L10N.t("local_history.change.copy");
		};
	}

	private static Color getChangeTypeColor(HistoryCheckpoint.ChangeType changeType) {
		return switch (changeType) {
			case ADD -> new Color(120, 175, 110);
			case MODIFY -> new Color(92, 168, 220);
			case REMOVE -> new Color(163, 163, 163);
			case RENAME -> new Color(118, 192, 161);
			case COPY -> new Color(144, 78, 214);
		};
	}

	private static class CheckpointListCellRenderer implements ListCellRenderer<HistoryCheckpoint> {
		@Override
		public Component getListCellRendererComponent(JList<? extends HistoryCheckpoint> list,
				HistoryCheckpoint checkpoint, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = new JLabel();
			label.setOpaque(true);
			label.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));

			Color titleColor;
			Color subtitleColor;
			if (isSelected) {
				label.setBackground(Theme.current().getInterfaceAccentColor());
				titleColor = Theme.current().getBackgroundColor();
				subtitleColor = Theme.current().getAltBackgroundColor();
			} else {
				label.setBackground(Theme.current().getSecondAltBackgroundColor());
				titleColor = Theme.current().getForegroundColor();
				subtitleColor = Theme.current().getAltForegroundColor();
			}

			label.setText(
					"<html><b><font color='" + ColorUtils.formatColor(titleColor) + "'>" + StringUtils.abbreviateString(
							checkpoint.name(), 40) + "</font></b><br><font size='-1' color='" + ColorUtils.formatColor(
							subtitleColor) + "'>" + checkpoint.getTimestampString() + "</font>");
			label.setToolTipText(checkpoint.name());
			return label;
		}
	}

	private record DiffTableCellRenderer(TableCellRenderer fallback) implements TableCellRenderer {

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component component = fallback.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
					column);
			if (column == 0 && value instanceof HistoryCheckpoint.ChangeType changeType
					&& component instanceof JLabel label) {
				label.setText(getChangeTypeLabel(changeType));
				if (!isSelected) {
					label.setForeground(getChangeTypeColor(changeType));
				}
			}
			return component;
		}
	}

}
