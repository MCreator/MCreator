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

package net.mcreator.ui.validation.component;

import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class VTextField extends JTextField implements IValidable {

	private Validator validator = null;
	private Validator.ValidationResult currentValidationResult = null;
	private boolean showPassed = true;

	private boolean mouseInInfoZone = false;

	private Cursor oldCursor = null;
	private String customDefaultMessage;

	public VTextField() {
		this(0);
	}

	public VTextField(int length) {
		super(length);

		addMouseMotionListener(new MouseMotionAdapter() {
			@Override public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				int x = e.getX();
				if (x >= getWidth() - 18 && !mouseInInfoZone) {
					mouseInInfoZone = true;
					oldCursor = getCursor();
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					repaint();

				} else if (x < getWidth() - 18 && mouseInInfoZone) {
					mouseInInfoZone = false;
					setCursor(oldCursor);
					repaint();
				}
			}
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseExited(MouseEvent mouseEvent) {
				super.mouseExited(mouseEvent);
				mouseInInfoZone = false;
				repaint();
			}
		});

	}

	@Override public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		g.drawImage(UIRES.get("16px.info").getImage(), getWidth() - 14, 1, 13, 13, null);

		if (currentValidationResult != null) {

			if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.WARNING) {
				g.setColor(new Color(238, 229, 113));
				g.drawImage(UIRES.get("18px.warning").getImage(), getWidth() - 14, 13, 13, 13, null);
			} else if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.ERROR) {
				g.setColor(new Color(204, 108, 108));
				g.drawImage(UIRES.get("18px.remove").getImage(), getWidth() - 14, 13, 13, 13, null);
			} else if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.PASSED
					&& showPassed) {
				g.setColor(new Color(79, 192, 121));
				g.drawImage(UIRES.get("18px.ok").getImage(), getWidth() - 14, 13, 13, 13, null);
			}

			if (currentValidationResult.getValidationResultType() == Validator.ValidationResultType.ERROR
					|| currentValidationResult.getValidationResultType() == Validator.ValidationResultType.WARNING) {
				Color old = g.getColor();
				g.setColor(new Color(old.getRed(), old.getGreen(), old.getBlue(), 40));
				g.fillRect(1, 1, getWidth() - 2, getHeight() - 2);
			}
		}

		if (mouseInInfoZone) {
			g.setColor(new Color(67, 67, 67, 255));
			g.fillRect(2, 2, getWidth() - 16, 11);
			g.setFont(getFont().deriveFont(10.0f));
			g.setColor(Color.white);
			String message = "This input field is validated";
			if (customDefaultMessage != null)
				message = customDefaultMessage;
			if (currentValidationResult != null && currentValidationResult.getMessage() != null
					&& !currentValidationResult.getMessage().equals(""))
				message = currentValidationResult.getMessage();
			g.drawString(message, 3, 11);
		}

	}

	public void enableRealtimeValidation() {
		addKeyListener(new KeyAdapter() {
			@Override public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				getValidationStatus();
			}
		});
	}

	@Override public Validator.ValidationResult getValidationStatus() {

		Validator.ValidationResult validationResult = validator == null ? null : validator.validate();

		this.currentValidationResult = validationResult;

		//repaint as new validation status might have to be rendered
		ThreadUtil.runOnSwingThread(this::repaint);

		return validationResult;
	}

	@Override public void setValidator(Validator validator) {
		this.validator = validator;
	}

	@Override public Validator getValidator() {
		return validator;
	}

	public boolean isShowPassed() {
		return showPassed;
	}

	public void setShowPassed(boolean showPassed) {
		this.showPassed = showPassed;
	}

	public String getCustomDefaultMessage() {
		return customDefaultMessage;
	}

	public void setCustomDefaultMessage(String customDefaultMessage) {
		this.customDefaultMessage = customDefaultMessage;
	}

}
