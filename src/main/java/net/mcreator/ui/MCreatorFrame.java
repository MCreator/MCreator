/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui;

import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.BlockingGlassPane;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.SquareLoaderIcon;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.AppIcon;
import net.mcreator.ui.init.BackgroundLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.notifications.INotificationConsumer;
import net.mcreator.ui.notifications.NotificationsRenderer;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class MCreatorFrame extends JFrame
		implements IWorkspaceProvider, IGeneratorProvider, INotificationConsumer {

	protected final MCreatorApplication application;

	protected final Workspace workspace;

	private final long windowUID;

	private final NotificationsRenderer notificationsRenderer;

	private final StatusBar statusBar;

	private final JPanel mainContent;

	public MCreatorFrame(@Nullable MCreatorApplication application, @Nonnull Workspace workspace) {
		this.windowUID = System.currentTimeMillis();
		this.workspace = workspace;
		this.application = application;

		setLayout(new BorderLayout(0, 0));

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() > 2140 && screenSize.getHeight() > 1250)
			setSize(2140, 1250);
		else if (screenSize.getWidth() > 1574 && screenSize.getHeight() > 970)
			setSize(1574, 967);
		else if (screenSize.getWidth() > 1290 && screenSize.getHeight() > 795)
			setSize(1290, 791);
		else
			setSize(1002, 640);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (OS.getOS() == OS.MAC)
			getRootPane().putClientProperty("apple.awt.fullscreenable", true);

		if (PreferencesManager.PREFERENCES.hidden.fullScreen.get())
			setExtendedState(JFrame.MAXIMIZED_BOTH);

		setIconImages(AppIcon.getAppIcons());
		setLocationRelativeTo(null);

		this.statusBar = new StatusBar(this);

		Image bgimage = getBackgroundImage();
		if (bgimage != null) {
			mainContent = new ImagePanel(bgimage);
			((ImagePanel) mainContent).setKeepRatio(true);
		} else {
			mainContent = new JPanel();
			mainContent.setBackground(Theme.current().getSecondAltBackgroundColor());
		}
		mainContent.setLayout(new BorderLayout());

		this.notificationsRenderer = new NotificationsRenderer(mainContent);

		add("Center", mainContent);
		add("South", statusBar);
	}

	protected void setMainContent(JComponent component) {
		mainContent.removeAll();
		mainContent.add(component, BorderLayout.CENTER);
	}

	protected JComponent getPreloaderPane() {
		JPanel wrap = new BlockingGlassPane();
		JLabel loading = L10N.label("workspace.loading");
		loading.setIconTextGap(5);
		loading.setFont(loading.getFont().deriveFont(16f));
		loading.setForeground(Theme.current().getAltForegroundColor());
		loading.setIcon(new SquareLoaderIcon(5, 1, Theme.current().getForegroundColor()));
		wrap.add(PanelUtils.totalCenterInPanel(loading));
		return wrap;
	}

	@Override public boolean equals(Object mcreator) {
		if (mcreator instanceof MCreatorFrame theothermcreator) {
			if (theothermcreator.workspace != null && workspace != null)
				return theothermcreator.workspace.getFileManager().getWorkspaceFile()
						.equals(workspace.getFileManager().getWorkspaceFile());
			else
				return theothermcreator.windowUID == windowUID;
		}
		return false;
	}

	@Override public int hashCode() {
		if (workspace != null)
			return workspace.getFileManager().getWorkspaceFile().hashCode();
		return Long.valueOf(windowUID).hashCode();
	}

	public MCreatorApplication getApplication() {
		return application;
	}

	@Override public @Nonnull Workspace getWorkspace() {
		return workspace;
	}

	@Override public NotificationsRenderer getNotificationsRenderer() {
		return notificationsRenderer;
	}

	public StatusBar getStatusBar() {
		return statusBar;
	}

	public boolean hasBackgroundImage() {
		return mainContent instanceof ImagePanel;
	}

	private Image getBackgroundImage() {
		UserFolderManager.getFileFromUserFolder("backgrounds").mkdirs();

		// Load backgrounds depending on the background source
		List<Image> bgimages = new ArrayList<>();
		switch (PreferencesManager.PREFERENCES.ui.backgroundSource.get()) {
		case "All":
			bgimages.addAll(BackgroundLoader.loadThemeBackgrounds());
			bgimages.addAll(BackgroundLoader.loadUserBackgrounds());
			break;
		case "Current theme":
			bgimages = BackgroundLoader.loadThemeBackgrounds();
			break;
		case "Custom":
			bgimages = BackgroundLoader.loadUserBackgrounds();
			break;
		}

		Image bgimage = null;
		if (!bgimages.isEmpty()) {
			bgimage = ListUtils.getRandomItem(bgimages);
			float avg = ImageUtils.getAverageLuminance(ImageUtils.toBufferedImage(bgimage));
			if (avg > 0.15) {
				avg = (float) Math.min(avg * 1.7, 0.85);
				bgimage = ImageUtils.drawOver(new ImageIcon(bgimage), new ImageIcon(
						ImageUtils.emptyImageWithSize(bgimage.getWidth(this), bgimage.getHeight(this),
								new Color(0.12f, 0.12f, 0.12f, avg)))).getImage();
			}
		}
		return bgimage;
	}

}
