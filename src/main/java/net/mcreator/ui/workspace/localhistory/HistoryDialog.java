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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.ColorUtils;
import net.mcreator.workspace.localhistory.HistoryCheckpoint;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.util.List;

public class HistoryDialog extends MCreatorDialog {

	private final MCreator mcreator;

	private final JList<HistoryCheckpoint> checkpointList;
	private final DefaultTableModel diffModel;
	private final CardLayout diffCardLayout;
	private final JPanel diffContent;
	private final CardLayout mainCardLayout;
	private final JPanel mainContent;

	private final JButton revertCheckpoint;
	private final JButton resetHistory;

	@Nullable private SwingWorker<List<HistoryCheckpoint.DiffEntry>, Void> diffWorker;

	public static void showHistoryDialog(MCreator mcreator) {
		new HistoryDialog(mcreator).setVisible(true);
	}

	private HistoryDialog(MCreator mcreator) {
		super(mcreator, L10N.t("dialog.local_history.title"), true);

		this.mcreator = mcreator;

		checkpointList = new JList<>();
		checkpointList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		checkpointList.setFixedCellHeight(48);
		checkpointList.setBackground(Theme.current().getSecondAltBackgroundColor());
		checkpointList.setCellRenderer(new CheckpointListCellRenderer());
		ComponentUtils.deriveFont(checkpointList, 13);

		JScrollPane checkpointScroll = new JScrollPane(checkpointList);
		checkpointScroll.setBackground(Theme.current().getSecondAltBackgroundColor());

		diffModel = new DefaultTableModel(
				new Object[] { L10N.t("dialog.local_history.column_change_type"),
						L10N.t("dialog.local_history.column_file") }, 0) {
			@Override public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		JTable diffTable = new JTable(diffModel);
		diffTable.setRowHeight(24);
		diffTable.setCellSelectionEnabled(false);
		diffTable.setRowSelectionAllowed(true);
		diffTable.setColumnSelectionAllowed(false);
		diffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		diffTable.getColumnModel().getColumn(0).setMaxWidth(120);
		diffTable.getColumnModel().getColumn(0).setPreferredWidth(100);
		diffTable.setDefaultRenderer(Object.class,
				new DiffTableCellRenderer(diffTable.getDefaultRenderer(Object.class)));
		diffTable.setShowVerticalLines(false);
		diffTable.setIntercellSpacing(new Dimension(0, 0));

		JScrollPane diffScroll = new JScrollPane(diffTable);

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

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, checkpointScroll, diffContent);
		splitPane.setContinuousLayout(true);
		splitPane.setDividerLocation(260);
		splitPane.setOpaque(false);
		splitPane.setBackground(Theme.current().getBackgroundColor());
		splitPane.setResizeWeight(0.35);

		mainCardLayout = new CardLayout();
		mainContent = new JPanel(mainCardLayout);
		mainContent.setOpaque(false);
		mainContent.add(PanelUtils.totalCenterInPanel(emptyHistoryLabel), "empty");
		mainContent.add(splitPane, "history");

		TransparentToolBar toolBar = new TransparentToolBar();
		toolBar.setLayout(new BoxLayout(toolBar, BoxLayout.X_AXIS));

		revertCheckpoint = L10N.button("dialog.local_history.revert");
		resetHistory = L10N.button("dialog.local_history.reset");
		JButton optimizeStorage = L10N.button("dialog.local_history.optimize");

		toolBar.add(revertCheckpoint);
		toolBar.add(Box.createHorizontalGlue());
		toolBar.add(resetHistory);
		toolBar.add(optimizeStorage);

		// TODO: implement revert checkpoint
		// TODO: implement optimize storage

		resetHistory.addActionListener(_ -> {
			int option = JOptionPane.showConfirmDialog(this, L10N.t("dialog.local_history.reset_confirm"),
					L10N.t("dialog.local_history.reset"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (option == JOptionPane.YES_OPTION) {
				mcreator.getWorkspace().resetLocalHistory();
				reloadContent();
			}
		});

		checkpointList.addListSelectionListener(e -> {
			if (e.getValueIsAdjusting()) {
				return;
			}
			loadSelectedCheckpointDiff();
		});

		add("Center", PanelUtils.northAndCenterElement(toolBar, mainContent));

		setSize(880, 520);
		setLocationRelativeTo(mcreator);

		reloadContent();
	}

	private void reloadContent() {
		cancelDiffWorker();
		diffModel.setRowCount(0);
		diffCardLayout.show(diffContent, "empty");

		List<HistoryCheckpoint> checkpoints = mcreator.getWorkspace().getHistoryManager().getCheckpoints();
		checkpointList.setListData(checkpoints.toArray(HistoryCheckpoint[]::new));

		resetHistory.setEnabled(!checkpoints.isEmpty());
		checkpointList.setEnabled(!checkpoints.isEmpty());
		revertCheckpoint.setEnabled(false);

		if (checkpoints.isEmpty()) {
			mainCardLayout.show(mainContent, "empty");
			checkpointList.clearSelection();
			return;
		}

		mainCardLayout.show(mainContent, "history");
		checkpointList.setSelectedIndex(0);
	}

	private void loadSelectedCheckpointDiff() {
		HistoryCheckpoint selected = checkpointList.getSelectedValue();
		revertCheckpoint.setEnabled(selected != null);

		cancelDiffWorker();

		if (selected == null) {
			diffModel.setRowCount(0);
			diffCardLayout.show(diffContent, "empty");
			return;
		}

		diffModel.setRowCount(0);
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
			case ADD -> new Color(96, 175, 110);
			case MODIFY -> new Color(214, 168, 78);
			case REMOVE -> new Color(224, 102, 96);
			case RENAME -> new Color(92, 168, 220);
			case COPY -> new Color(168, 118, 192);
		};
	}

	private static class CheckpointListCellRenderer implements ListCellRenderer<HistoryCheckpoint> {
		@Override
		public Component getListCellRendererComponent(JList<? extends HistoryCheckpoint> list,
				HistoryCheckpoint checkpoint, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = new JLabel();
			label.setOpaque(true);
			label.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));

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

			label.setText("<html><b><font color='" + ColorUtils.formatColor(titleColor) + "'>" + checkpoint.name()
					+ "</font></b><br><font size='-1' color='" + ColorUtils.formatColor(subtitleColor) + "'>"
					+ checkpoint.getTimestampString() + "</font>");
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
