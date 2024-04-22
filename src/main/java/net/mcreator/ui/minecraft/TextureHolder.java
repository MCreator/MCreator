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

package net.mcreator.ui.minecraft;

import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VButton;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextureHolder extends VButton {

	private String id = "";
	private final TypedTextureSelectorDialog td;

	private ActionListener actionListener;

	private final int size;

	private boolean removeButtonHover;

	private boolean uvFlip;

	public TextureHolder(TypedTextureSelectorDialog td) {
		this(td, 70);
	}

	public TextureHolder(TypedTextureSelectorDialog td, int size) {
		super("");
		this.td = td;

		this.size = size;

		setMargin(new Insets(0, 0, 0, 0));
		setPreferredSize(new Dimension(this.size, this.size));
		td.getConfirmButton().addActionListener(event -> {
			if (td.list.getSelectedValue() != null) {
				id = FilenameUtilsPatched.removeExtension(td.list.getSelectedValue().getName());
				setIcon(new ImageIcon(
						ImageUtils.resize(new ImageIcon(td.list.getSelectedValue().toString()).getImage(), this.size)));
				td.setVisible(false);
				if (actionListener != null)
					actionListener.actionPerformed(new ActionEvent(this, 0, ""));
				getValidationStatus();
				setToolTipText(id);
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (isEnabled()) {
					if (e.getX() > 1 && e.getX() < 11 && e.getY() < getHeight() - 1 && e.getY() > getHeight() - 11
							&& !id.isEmpty()) {
						id = "";
						setIcon(null);
						getValidationStatus();
						setToolTipText(null);
					} else {
						td.setVisible(true);
					}
					repaint();
				}
			}

			@Override public void mouseExited(MouseEvent e) {
				removeButtonHover = false;
				repaint();
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				removeButtonHover =
						e.getX() > 1 && e.getX() < 11 && e.getY() < getHeight() - 1 && e.getY() > getHeight() - 11;
				repaint();
			}
		});
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (!id.isEmpty()) {
			ImageIcon removeIcon;
			if (removeButtonHover || !isEnabled()) {
				removeIcon = ImageUtils.changeSaturation(UIRES.get("18px.remove"), 0.6f);
			} else {
				removeIcon = UIRES.get("18px.remove");
			}
			g.drawImage(removeIcon.getImage(), 1, getHeight() - 12, 11, 11, null);
		}
	}

	public String getID() {
		return id;
	}

	public void setTextureFromTextureName(String texture) {
		if (texture != null && !texture.isEmpty()) {
			id = texture;
			setToolTipText(texture);
			setIcon(new ImageIcon(ImageUtils.resize(
					td.getMCreator().getFolderManager().getTextureImageIcon(id, td.getTextureType()).getImage(),
					this.size)));
		}
	}

	public boolean hasTexture() {
		return id != null && !id.isEmpty();
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public TextureHolder setFlipUV(boolean uvFlip) {
		this.uvFlip = uvFlip;
		repaint();
		return this;
	}

	@Override public void setIcon(Icon icon) {
		if (icon == null) {
			super.setIcon(null);
		} else {
			super.setIcon(new Icon() {
				@Override public int getIconHeight() {
					return icon.getIconHeight();
				}

				@Override public int getIconWidth() {
					return icon.getIconWidth();
				}

				@Override public synchronized void paintIcon(Component c, Graphics g, int x, int y) {
					Graphics2D g2 = (Graphics2D) g.create();
					if (uvFlip) {
						g2.translate(icon.getIconWidth(), icon.getIconHeight());
						g2.scale(-1, -1);
					}
					icon.paintIcon(c, g2, x, y);
					g2.dispose();
				}
			});
		}
	}
}
