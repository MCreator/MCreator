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

import net.mcreator.element.parts.TextureHolder;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VButton;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.resources.Texture;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class TextureSelectionButton extends VButton {

	@Nullable private Texture selectedTexture = null;

	private final TypedTextureSelectorDialog td;
	private final int size;
	private boolean removeButtonHover;
	private boolean uvFlip;

	private ActionListener actionListener;

	public TextureSelectionButton(TypedTextureSelectorDialog td) {
		this(td, 70);
	}

	public TextureSelectionButton(TypedTextureSelectorDialog td, int size) {
		super("");
		this.td = td;
		this.size = size;

		setMargin(new Insets(0, 0, 0, 0));
		setPreferredSize(new Dimension(this.size, this.size));
		td.getConfirmButton().addActionListener(event -> {
			td.setVisible(false);
			Texture texture = td.list.getSelectedValue();
			if (texture != null) {
				setTexture(texture);
				if (actionListener != null)
					actionListener.actionPerformed(new ActionEvent(this, 0, ""));
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (isEnabled()) {
					if (e.getX() > 1 && e.getX() < 11 && e.getY() < getHeight() - 1 && e.getY() > getHeight() - 11
							&& selectedTexture != null) {
						selectedTexture = null;
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

	protected void setTexture(@Nullable Texture texture) {
		if (texture != null) {
			selectedTexture = texture;
			setIcon(new ImageIcon(
					ImageUtils.resize(texture.getTextureIcon(td.getMCreator().getWorkspace()).getImage(), this.size)));
			getValidationStatus();
			setToolTipText(selectedTexture.getTextureName());
		}
	}

	public void setTexture(@Nullable TextureHolder texture) {
		if (texture != null) {
			setTexture(texture.toTexture(td.getTextureType()));
		}
	}

	public TextureHolder getTextureHolder() {
		return new TextureHolder(td.getMCreator().getWorkspace(), selectedTexture);
	}

	public boolean hasTexture() {
		return selectedTexture != null && !selectedTexture.getTextureName().isEmpty();
	}

	public void setActionListener(ActionListener actionListener) {
		this.actionListener = actionListener;
	}

	public TextureSelectionButton setFlipUV(boolean uvFlip) {
		this.uvFlip = uvFlip;
		repaint();
		return this;
	}

	@Override public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (selectedTexture != null) {
			ImageIcon removeIcon;
			if (removeButtonHover || !isEnabled()) {
				removeIcon = ImageUtils.changeSaturation(UIRES.get("18px.remove"), 0.6f);
			} else {
				removeIcon = UIRES.get("18px.remove");
			}
			g.drawImage(removeIcon.getImage(), 1, getHeight() - 12, 11, 11, null);
		}
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
