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

import com.formdev.flatlaf.ui.FlatLineBorder;
import net.mcreator.io.OS;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SquareLoaderIcon;
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

	private final JLabel titleLabel = new JLabel();
	private final DefaultListModel<ProgressUnit> listModel = new DefaultListModel<>();
	private final JList<ProgressUnit> progressUnits = new JList<>(listModel);

	@Nullable private MCreator mcreator = null;

	public ProgressDialog(Window w, String title) {
		super(w, title, true);

		if (w instanceof MCreator mcreatorInst)
			this.mcreator = mcreatorInst;

		setClosable(false);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		// If we use undecorated Dialog on Linux, the dialog contents flicker (#4757)
		boolean customDecoration = OS.getOS() != OS.LINUX;
		if (customDecoration) {
			setUndecorated(true);

			JPanel contentPane = new JPanel() {
				@Override protected void paintComponent(Graphics g) {
					Graphics2D g2d = (Graphics2D) g;
					g2d.setColor(Theme.current().getBackgroundColor());
					g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
					super.paintComponent(g);
				}
			};
			contentPane.setBorder(
					new FlatLineBorder(new Insets(0, 0, 0, 0), UIManager.getColor("PopupMenu.borderColor"), 1, 15));
			contentPane.setLayout(new BorderLayout(0, 0));
			contentPane.setOpaque(false);
			setContentPane(contentPane);

			setBackground(new Color(0, 0, 0, 0));

			titleLabel.setText(title);
			titleLabel.setBorder(BorderFactory.createEmptyBorder(7, 10, 2, 10));
			titleLabel.setForeground(Theme.current().getAltForegroundColor());
			add("North", titleLabel);
		}

		progressUnits.setCellRenderer(new Render());
		progressUnits.setOpaque(false);
		progressUnits.setBorder(null);

		JScrollPane panes = new JScrollPane(progressUnits);
		panes.setOpaque(false);
		panes.getViewport().setOpaque(false);
		panes.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 3));

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
			setOpaque(false);
			setLayout(new BorderLayout());

			JPanel stap = new JPanel(new BorderLayout(2, 0));
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
