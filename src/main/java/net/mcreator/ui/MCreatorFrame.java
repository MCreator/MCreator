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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.BlockingGlassPane;
import net.mcreator.ui.component.ImagePanel;
import net.mcreator.ui.component.SquareLoaderIcon;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.AppIcon;
import net.mcreator.ui.init.BackgroundLoader;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.notifications.INotificationConsumer;
import net.mcreator.ui.notifications.NotificationsRenderer;
import net.mcreator.ui.variants.modmaker.ModMaker;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public abstract class MCreatorFrame extends JFrame
		implements IWorkspaceProvider, IGeneratorProvider, INotificationConsumer {

	private static final List<Dimension> FRAME_SIZES = List.of(
			//@formatter:off
			new Dimension(1886, 1100),
			new Dimension(1600, 974),
			new Dimension(1310, 798),
			new Dimension(1022, 647)
			//@formatter:on
	);
	private static final double FRAME_SIZE_PADDING = 1.15;

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
		Dimension frameSizeToUse = FRAME_SIZES.getLast();
		for (Dimension size : FRAME_SIZES) {
			if (screenSize.getWidth() >= size.getWidth() * FRAME_SIZE_PADDING
					&& screenSize.getHeight() >= size.getHeight() * FRAME_SIZE_PADDING) {
				frameSizeToUse = size;
				break;
			}
		}
		setSize(frameSizeToUse);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		if (OS.getOS() == OS.MAC)
			getRootPane().putClientProperty("apple.awt.fullscreenable", true);

		if (PreferencesManager.PREFERENCES.hidden.fullScreen.get())
			setExtendedState(JFrame.MAXIMIZED_BOTH);

		setIconImages(AppIcon.getAppIcons());
		setLocationRelativeTo(null);

		this.statusBar = new StatusBar(this);

		Image bgimage = BackgroundLoader.getBackgroundImage();
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
		JPanel wrap;
		// For small workspaces, the preloader only briefly flashes, causing bad UX, so we don't show it in such cases
		if (this instanceof ModMaker && workspace.getModElements().size() > 10) {
			wrap = new BlockingGlassPane(true);
			wrap.add(ComponentUtils.bigCenteredText("workspace.loading",
					new SquareLoaderIcon(5, 1, Theme.current().getForegroundColor())));
		} else {
			wrap = new BlockingGlassPane(false);
		}
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

}
