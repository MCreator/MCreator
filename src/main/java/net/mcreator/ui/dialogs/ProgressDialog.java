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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SquareLoaderIcon;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProgressDialog extends MCreatorDialog {

	private final JLabel titleLabel;
	private final DefaultListModel<ProgressUnit> listModel = new DefaultListModel<>();
	private final JList<ProgressUnit> progressUnits = new JList<>(listModel);

	@Nullable private MCreator mcreator = null;

	public ProgressDialog(Window w, String title) {
		super(w, title, true);

		setLayout(new BorderLayout(0, 0));

		if (w instanceof MCreator mcreatorInst)
			this.mcreator = mcreatorInst;

		setBackground(Theme.current().getBackgroundColor());

		setClosable(false);
		setUndecorated(true);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		titleLabel = new JLabel(title);
		titleLabel.setBackground(Theme.current().getAltBackgroundColor());
		titleLabel.setOpaque(true);
		titleLabel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(8, 5, 0, 0, Theme.current().getBackgroundColor()),
				BorderFactory.createMatteBorder(0, 4, 0, 0, Theme.current().getAltBackgroundColor())));
		ComponentUtils.deriveFont(titleLabel, 13);
		add("North", titleLabel);

		progressUnits.setCellRenderer(new Render());
		progressUnits.setOpaque(false);
		progressUnits.setBorder(null);

		JScrollPane panes = new JScrollPane(progressUnits);
		panes.getViewport().setOpaque(false);
		panes.setPreferredSize(new Dimension(600, 280));
		panes.setBackground(Theme.current().getBackgroundColor());
		panes.setBorder(BorderFactory.createMatteBorder(4, 8, 4, 4, Theme.current().getBackgroundColor()));

		((JComponent) getContentPane()).setBorder(
				BorderFactory.createMatteBorder(0, 5, 0, 0, Theme.current().getAltBackgroundColor()));

		add("Center", panes);

		setSize(450, 280);
		setLocationRelativeTo(w);
	}

	public void hideDialog() {
		ThreadUtil.runOnSwingThread(() -> setVisible(false));
	}

	@Override public void setTitle(String title) {
		super.setTitle(title);
		// setTitle can be called before the titleLabel is initialized
		if (titleLabel != null)
			titleLabel.setText(title);
	}

	@Override public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (!visible && mcreator != null)
			mcreator.getApplication().getTaskbarIntegration().clearState(mcreator);
	}

	public void addProgressUnit(final ProgressUnit progressUnit) {
		ThreadUtil.runOnSwingThread(() -> {
			if (mcreator != null) {
				mcreator.getApplication().getTaskbarIntegration().clearState(mcreator);
				mcreator.getApplication().getTaskbarIntegration().setIntermediateProgress(mcreator);
			}

			progressUnit.progressDialog = this;

			listModel.addElement(progressUnit);
		});
	}

	public static class ProgressUnit {

		private final String name;

		private Status status;
		private int percent;

		@Nullable private ProgressDialog progressDialog;

		public ProgressUnit(String name) {
			this.name = name;
			status = Status.LOADING;
		}

		public void markStateOk() {
			status = Status.COMPLETE;

			if (progressDialog != null) {
				ThreadUtil.runOnSwingThread(() -> progressDialog.progressUnits.repaint());
			}
		}

		public void markStateError() {
			status = Status.ERROR;

			if (progressDialog != null) {
				ThreadUtil.runOnSwingThread(() -> progressDialog.progressUnits.repaint());

				if (progressDialog.mcreator != null)
					progressDialog.mcreator.getApplication().getTaskbarIntegration()
							.setErrorIndicator(progressDialog.mcreator);
			}
		}

		public void markStateWarning() {
			status = Status.WARNING;

			if (progressDialog != null) {
				ThreadUtil.runOnSwingThread(() -> progressDialog.progressUnits.repaint());

				if (progressDialog.mcreator != null)
					progressDialog.mcreator.getApplication().getTaskbarIntegration()
							.setWarningIndicator(progressDialog.mcreator);
			}
		}

		public void setPercent(int percent) {
			this.percent = percent;

			if (progressDialog != null) {
				ThreadUtil.runOnSwingThread(() -> progressDialog.progressUnits.repaint());

				if (progressDialog.mcreator != null)
					progressDialog.mcreator.getApplication().getTaskbarIntegration()
							.setProgressState(progressDialog.mcreator, percent);
			}
		}

		enum Status {
			LOADING, COMPLETE, ERROR, WARNING
		}

	}

	private static class Render extends JPanel implements ListCellRenderer<ProgressUnit> {

		private final ImageIcon complete = UIRES.get("18px.ok");
		private final ImageIcon remove = UIRES.get("18px.remove");
		private final ImageIcon warning = UIRES.get("18px.warning");

		private final Map<ProgressUnit, Icon> LOADER_CACHE = new HashMap<>();

		@Override
		public Component getListCellRendererComponent(JList<? extends ProgressUnit> list, ProgressUnit ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			removeAll();
			setLayout(new BorderLayout());
			setBackground(Theme.current().getBackgroundColor());

			JPanel stap = new JPanel(new BorderLayout());
			stap.setOpaque(false);

			JLabel status = new JLabel();
			status.setText(ma.name);

			if (ma.status == ProgressUnit.Status.LOADING) {
				JLabel status2 = new JLabel(LOADER_CACHE.computeIfAbsent(ma,
						e -> new SquareLoaderIcon(list, 4, 1, Theme.current().getForegroundColor())));
				status2.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 2));
				stap.add("East", PanelUtils.centerInPanel(status2));

				JProgressBar bar = new JProgressBar(0, 100);
				bar.setValue(ma.percent);
				if (bar.getValue() > 0)
					stap.add("West", PanelUtils.totalCenterInPanel(bar));
			} else if (ma.status == ProgressUnit.Status.COMPLETE) {
				stap.add("East", PanelUtils.centerInPanel(new JLabel(complete)));
			} else if (ma.status == ProgressUnit.Status.ERROR) {
				stap.add("East", PanelUtils.centerInPanel(new JLabel(remove)));
			} else if (ma.status == ProgressUnit.Status.WARNING) {
				stap.add("East", PanelUtils.centerInPanel(new JLabel(warning)));
			}

			add("West", status);
			add("East", stap);
			return this;
		}
	}

}
