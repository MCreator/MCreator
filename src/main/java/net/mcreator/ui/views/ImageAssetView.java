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

package net.mcreator.ui.views;

import net.mcreator.io.tree.FileNode;
import net.mcreator.io.zip.ZipIO;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.canvas.CanvasRenderer;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class ImageAssetView extends ViewBase {

	private static final Logger LOG = LogManager.getLogger("Image Asset View");

	private final String name, path;
	private int scale;

	private final JToggleButton showTiles = L10N.togglebutton("dialog.image_viewer.show_tiles");
	private final JLabel asset, imageInfo = new JLabel("");

	public ImageAssetView(MCreator mcreator, FileNode node) {
		super(mcreator);

		File libFile = new File(node.incrementalPath.split(":%:")[0]);
		String path = node.incrementalPath.split(":%:")[1];
		if (path.startsWith("/"))
			path = path.substring(1);
		this.name = FilenameUtilsPatched.getName(path);
		this.path = path;

		asset = new JLabel() {
			@Override protected void paintComponent(Graphics g) {
				if (showTiles.isSelected()) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setPaint(CanvasRenderer.buildCheckerboardPattern());
					g2d.fillRect(0, 0, getWidth(), getHeight());
					g2d.dispose();
				}
				super.paintComponent(g);
			}
		};
		asset.setHorizontalAlignment(JLabel.CENTER);

		BufferedImage image = Objects.requireNonNull(ZipIO.readFileInZip(libFile, path, (file, entry) -> {
			try {
				return ImageIO.read(file.getInputStream(entry));
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
				return null;
			}
		}));
		updateAsset(image, 10);

		JScrollPane sp = new JScrollPane(asset);
		sp.setOpaque(false);
		sp.setBorder(null);
		sp.getViewport().setOpaque(false);
		sp.getViewport().setBorder(null);

		int maxScale;
		if (Math.max(image.getWidth(), image.getHeight()) < 200) {
			maxScale = 50;
		} else if (Math.max(image.getWidth(), image.getHeight()) < 500) {
			maxScale = 20;
		} else if (Math.max(image.getWidth(), image.getHeight()) < 1000) {
			maxScale = 10;
		} else {
			maxScale = 2;
		}
		sp.addMouseWheelListener(event -> {
			if (event.isControlDown()) {
				int x = scale + event.getWheelRotation();
				if (x < 1)
					x = 1;
				if (x > maxScale)
					x = maxScale;
				if (x != scale)
					updateAsset(image, x);
			}
		});

		imageInfo.setForeground((Color) UIManager.get("MCreatorLAF.GRAY_COLOR"));

		showTiles.setMargin(new Insets(1, 40, 1, 40));
		showTiles.addActionListener(e -> asset.repaint());

		JButton resetScale = L10N.button("dialog.image_viewer.reset_scale");
		resetScale.setMargin(new Insets(1, 40, 1, 40));
		resetScale.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		resetScale.setFocusPainted(false);
		resetScale.addActionListener(e -> updateAsset(image, 2));

		JPanel topBar = new JPanel(new BorderLayout());
		topBar.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 2));
		topBar.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		topBar.add("West", imageInfo);
		topBar.add("East", PanelUtils.join(showTiles, resetScale));

		add("North", topBar);
		add("Center", sp);
	}

	private void updateAsset(BufferedImage image, int scale) {
		this.scale = scale;
		asset.setIcon(
				new ImageIcon(ImageUtils.resize(image, image.getWidth() * scale / 2, image.getHeight() * scale / 2)));
		imageInfo.setText(L10N.t("dialog.image_viewer.info_bar", name, image.getWidth(), image.getHeight(), scale * 50));
	}

	public static boolean isFileSupported(String fileName) {
		return Arrays.asList("png", "gif").contains(FilenameUtilsPatched.getExtension(fileName));
	}

	@Override public ViewBase showView() {
		MCreatorTabs.Tab existing = mcreator.mcreatorTabs.showTabOrGetExisting(path);
		if (existing == null) {
			mcreator.mcreatorTabs.addTab(new MCreatorTabs.Tab(this, path, false));
			return this;
		}
		return (ViewBase) existing.getContent();
	}

	@Override public String getViewName() {
		return name;
	}

}
