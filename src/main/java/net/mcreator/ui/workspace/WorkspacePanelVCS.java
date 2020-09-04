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

package net.mcreator.ui.workspace;

import net.mcreator.ui.action.impl.vcs.SetupVCSAction;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.vcs.BranchesPopup;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

class WorkspacePanelVCS extends JPanel implements IReloadableFilterable {

	private static final Logger LOG = LogManager.getLogger("VCS Panel");

	private final WorkspacePanel workspacePanel;

	private final JTable commits;
	private final TableRowSorter<TableModel> sorter;

	private final JButton switchBranch = new JButton("");

	WorkspacePanelVCS(WorkspacePanel workspacePanel) {
		super(new BorderLayout(0, 5));
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton uncommited = new JButton("Show local unsynced changes");
		uncommited.setIcon(UIRES.get("16px.info"));
		uncommited.setContentAreaFilled(false);
		uncommited.setOpaque(false);
		ComponentUtils.deriveFont(uncommited, 12);
		uncommited.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(uncommited);

		uncommited.addActionListener(e -> workspacePanel.mcreator.actionRegistry.showUnsyncedChanges.doAction());

		JButton checkout = new JButton("Jump to selected commit");
		checkout.setIcon(UIRES.get("16px.rwd"));
		checkout.setContentAreaFilled(false);
		checkout.setOpaque(false);
		ComponentUtils.deriveFont(checkout, 12);
		checkout.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(checkout);

		checkout.addActionListener(e -> checkoutToSelectedCommit());

		switchBranch.setIcon(UIRES.get("16px.vcs"));
		switchBranch.setContentAreaFilled(false);
		switchBranch.setOpaque(false);
		ComponentUtils.deriveFont(switchBranch, 12);
		switchBranch.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(switchBranch);

		switchBranch.addActionListener(
				e -> new BranchesPopup(workspacePanel.mcreator.getWorkspace().getVCS(), workspacePanel.mcreator)
						.show(switchBranch, 4, 20));

		bar.add(switchBranch);

		add("North", bar);

		commits = new JTable(
				new DefaultTableModel(new Object[] { "ID", "Commit message", "Commit author", "Date" }, 0) {
					@Override public boolean isCellEditable(int row, int column) {
						return false;
					}
				});

		sorter = new TableRowSorter<>(commits.getModel());
		commits.setRowSorter(sorter);
		commits.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		commits.setSelectionBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		commits.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		commits.setSelectionForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		commits.setBorder(BorderFactory.createEmptyBorder());
		commits.setGridColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		commits.setRowHeight(38);
		ComponentUtils.deriveFont(commits, 13);

		commits.getColumnModel().getColumn(0).setMinWidth(58);
		commits.getColumnModel().getColumn(0).setMaxWidth(58);
		commits.getColumnModel().getColumn(0).setPreferredWidth(58);

		commits.getColumnModel().getColumn(1).setPreferredWidth(650);

		JTableHeader header = commits.getTableHeader();
		header.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		header.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));

		JScrollPane sp = new JScrollPane(commits);
		sp.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));

		sp.setColumnHeaderView(null);

		JPanel holder = new JPanel(new BorderLayout());
		holder.setOpaque(false);
		holder.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
		holder.add(sp);

		add("Center", holder);

		commits.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				if (mouseEvent.getClickCount() == 2) {
					checkoutToSelectedCommit();
				}
			}
		});
	}

	private void checkoutToSelectedCommit() {
		String shortCommitId = commits.getValueAt(commits.getSelectedRow(), 0).toString();

		if (shortCommitId != null) {
			try {
				Git git = workspacePanel.mcreator.getWorkspace().getVCS().getGit();
				for (RevCommit commit : git.log().add(git.getRepository().resolve(git.getRepository().getFullBranch()))
						.call()) {
					if (commit.abbreviate(7).name().equals(shortCommitId)) {
						int option = JOptionPane.showOptionDialog(workspacePanel.mcreator,
								"<html><b>Are you sure you want to jump to commit " + commit.getShortMessage()
										+ "?</b><br>"
										+ "All your local unsynced changes will be dropped after this action!<br><br>"
										+ "<small>Make sure you don't jump between different MCreator workspace version checkpoints!",
								"Jump to commit", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null,
								new String[] { "Jump to " + commit.abbreviate(7).name(), "Cancel" }, null);

						if (option == 0) {
							// track all so they can be stashed properly
							git.rm().addFilepattern(".").call();
							git.add().addFilepattern(".").call();

							// remove local changes attempt 1
							git.stashCreate().call();
							git.stashDrop().call();

							ObjectId currentBranchHead = git.getRepository().resolve(Constants.HEAD);
							String oldBranch = git.getRepository().getFullBranch();

							git.checkout().setName(commit.getName()).setStartPoint(commit.getName()).call();
							git.checkout().setName("tmpHistoryBranch").setCreateBranch(true).call();
							String branchName = git.getRepository().getFullBranch();
							git.merge().setStrategy(MergeStrategy.OURS).include(currentBranchHead)
									.setFastForward(MergeCommand.FastForwardMode.NO_FF)
									.setMessage("Jump back to commit " + commit.getName()).call();
							git.checkout().setName(oldBranch).call();
							git.merge().include(git.getRepository().resolve(branchName)).call();
							git.branchDelete().setBranchNames(branchName).call();

							// we might need to make another commit to commit the merge changes
							try {
								git.rm().addFilepattern(".").call();
								git.add().addFilepattern(".").call();
								git.commit().setAll(true).setAllowEmpty(false).setMessage("Jump cleanup commit").call();
							} catch (Exception ignored) {
							}

							workspacePanel.mcreator.getWorkspace().reloadFromFS();
							workspacePanel.updateMods();
							workspacePanel.mcreator.actionRegistry.buildWorkspace.doAction();
						}

						break;
					}
				}
			} catch (GitAPIException | IOException e) {
				LOG.error("Checkout failed!", e);
			}
		}
	}

	boolean panelShown() {
		return SetupVCSAction.setupVCSForWorkspaceIfNotYet(workspacePanel.mcreator);
	}

	public void reloadElements() {
		if (workspacePanel.mcreator.getWorkspace().getVCS() != null) {
			int row = commits.getSelectedRow();

			DefaultTableModel model = (DefaultTableModel) commits.getModel();
			model.setRowCount(0);

			Git git = workspacePanel.mcreator.getWorkspace().getVCS().getGit();
			try {
				for (RevCommit commit : git.log().add(git.getRepository().resolve(git.getRepository().getFullBranch()))
						.call()) {
					model.addRow(new Object[] { commit.abbreviate(7).name(), "<html><b>" + commit.getShortMessage(),
							commit.getAuthorIdent().getName(), commit.getAuthorIdent().getWhen() });
				}

				switchBranch
						.setText("Current branch: " + git.getRepository().getFullBranch().replace("refs/heads/", ""));
			} catch (Exception ignored) {
			}

			refilterElements();

			try {
				commits.setRowSelectionInterval(row, row);
			} catch (Exception ignored) {
			}
		}
	}

	public void refilterElements() {
		if (workspacePanel.mcreator.getWorkspace().getVCS() != null)
			sorter.setRowFilter(RowFilter.regexFilter(workspacePanel.search.getText()));
	}

}
