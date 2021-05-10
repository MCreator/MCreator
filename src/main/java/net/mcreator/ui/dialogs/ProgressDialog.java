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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.Locale;

public class ProgressDialog extends MCreatorDialog {

	private static final Logger LOG = LogManager.getLogger("Progress Dialog");

	private final DefaultListModel<ProgressUnit> lModel = new DefaultListModel<>();
	private final JList<ProgressUnit> progress = new JList<>(lModel);
	private final JLabel topInfo = new JLabel();

	@Nullable private MCreator mcreator = null;

	public ProgressDialog(Window w, String title) {
		super(w, title, true);

		if (w instanceof MCreator) {
			mcreator = (MCreator) w;
		}

		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		setClosable(false);
		setCursor(new Cursor(Cursor.WAIT_CURSOR));

		progress.setCellRenderer(new Render());
		progress.setOpaque(false);
		JScrollPane panes = new JScrollPane(progress);
		panes.setOpaque(false);
		panes.setBorder(null);
		panes.getViewport().setOpaque(false);
		panes.setPreferredSize(new Dimension(600, 280));

		progress.setBorder(null);
		JPanel pan = new JPanel();
		pan.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		pan.setLayout(new BorderLayout());
		pan.add("Center", panes);

		topInfo.setForeground(Color.white);
		ComponentUtils.deriveFont(topInfo, 10);
		topInfo.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		topInfo.setOpaque(true);
		topInfo.setBorder(
				BorderFactory.createMatteBorder(2, 2, 4, 2, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		add("North", topInfo);
		add("Center", pan);

		pan.setBorder(BorderFactory.createMatteBorder(0, 4, 4, 4, (Color) UIManager.get("MCreatorLAF.DARK_ACCENT")));

		setSize(450, 280);
		setLocationRelativeTo(w);
	}

	public void hideAll() {
		Thread lo = new Thread(() -> {
			if (mcreator != null)
				mcreator.getApplication().getTaskbarIntegration().clearState(mcreator);

			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				LOG.error(e.getMessage(), e);
			}

			SwingUtilities.invokeLater(() -> setVisible(false));
		});
		lo.start();
	}

	public void setTopInfoText(String text) {
		SwingUtilities.invokeLater(() -> topInfo.setText(text.toUpperCase(Locale.ENGLISH)));
	}

	public void addProgress(final ProgressUnit unit1a) {
		SwingUtilities.invokeLater(() -> {
			if (mcreator != null) {
				mcreator.getApplication().getTaskbarIntegration().clearState(mcreator);
				mcreator.getApplication().getTaskbarIntegration().setIntermediateProgress(mcreator);
			}

			unit1a.mcreator = this.mcreator;

			lModel.addElement(unit1a);
			progress.updateUI();
		});
	}

	public void refreshDisplay() {
		SwingUtilities.invokeLater(progress::updateUI);
	}

	static class Render extends JPanel implements ListCellRenderer<ProgressUnit> {

		private final ImageIcon complete = UIRES.get("18px.ok");
		private final ImageIcon remove = UIRES.get("18px.remove");
		private final ImageIcon warning = UIRES.get("18px.warning");

		@Override
		public Component getListCellRendererComponent(JList<? extends ProgressUnit> list, ProgressUnit ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			removeAll();
			setLayout(new BorderLayout());
			setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			JLabel status = new JLabel();
			status.setForeground(Color.white);
			ComponentUtils.deriveFont(status, 12);

			JPanel stap = new JPanel(new BorderLayout());
			stap.setOpaque(false);

			status.setText(ma.name);

			if (ma.status == ProgressUnit.Status.LOADING) {
				ImageIcon loading = UIRES.get("16px.loading.gif");
				loading.setImageObserver((img, infoflags, x, y, width, height) -> {
					try {
						if ((infoflags & (FRAMEBITS | ALLBITS)) != 0) {
							Rectangle rect = list.getCellBounds(index, index);
							list.repaint(rect);
						}
						return (infoflags & (ALLBITS | ABORT)) == 0;
					} catch (Exception e) {
						return (infoflags & (ALLBITS | ABORT)) == 0;
					}
				});
				JLabel status2 = new JLabel(loading) {
					@Override public boolean imageUpdate(Image img, int infoflags, int x, int y, int w, int h) {
						repaint();
						return true;
					}
				};
				status2.repaint();
				stap.add("East", PanelUtils.centerInPanel(status2));

				JProgressBar bar = new JProgressBar();
				bar.setIndeterminate(ma.inf);
				bar.setMaximum(100);
				bar.setBorder(BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 1));
				bar.setOpaque(false);
				if (!ma.inf)
					bar.setValue(ma.percent);
				if (bar.getValue() > 0)
					stap.add("West", PanelUtils.centerInPanel(bar));
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

	public static class ProgressUnit {
		Status status;
		String name;
		long time;
		private final long pv;
		private int percent;
		boolean inf = false;

		@Nullable private MCreator mcreator;

		public ProgressUnit(String name) {
			this.name = name;
			status = Status.LOADING;
			pv = System.currentTimeMillis();
		}

		public void ok() {
			status = Status.COMPLETE;
			time = System.currentTimeMillis() - pv;
		}

		public void err() {
			status = Status.ERROR;
			time = System.currentTimeMillis() - pv;

			if (mcreator != null)
				mcreator.getApplication().getTaskbarIntegration().setErrorIndicator(mcreator);
		}

		public void warn() {
			status = Status.WARNING;
			time = System.currentTimeMillis() - pv;

			if (mcreator != null)
				mcreator.getApplication().getTaskbarIntegration().setWarningIndicator(mcreator);
		}

		public void setPercent(int percent) {
			this.percent = percent;

			if (mcreator != null)
				mcreator.getApplication().getTaskbarIntegration().setProgressState(mcreator, percent);
		}

		enum Status {
			LOADING, COMPLETE, ERROR, WARNING
		}

	}
}
