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

package net.mcreator.ui.component;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.geom.Point2D;

public class SquareLoaderIcon implements Icon {

	private final Square[] squares = new Square[7];

	private final int size;
	private final int spacing;

	@Nullable private JComponent owner;

	@Nullable private final Color foreground;

	public SquareLoaderIcon(int size, int spacing) {
		this(null, size, spacing, null);
	}

	public SquareLoaderIcon(int size, int spacing, @Nullable Color foreground) {
		this(null, size, spacing, foreground);
	}

	public SquareLoaderIcon(@Nullable JComponent owner, int size, int spacing, @Nullable Color foreground) {
		this.owner = owner;
		this.size = size;
		this.spacing = spacing;
		this.foreground = foreground;

		squares[0] = new Square(0);
		squares[1] = new Square(2);
		squares[2] = new Square(5);
		squares[3] = new Square(6);
		squares[4] = new Square(7);
		squares[5] = new Square(8);
		squares[6] = new Square(4);

		if (owner != null) {
			initializeComponent(owner);
		}
	}

	private Square getBlockAt(int block) {
		Square foundSquare = null;
		for (Square square : squares) {
			if (square.currentBlock == block) {
				foundSquare = square;
				break;
			}
		}
		return foundSquare;
	}

	@Override public void paintIcon(Component c, Graphics g, int x, int y) {
		if (owner == null && c instanceof JComponent jc) {
			initializeComponent(owner = jc);
		}

		for (Square square : squares) {
			g.setColor(foreground != null ? foreground : c.getForeground());
			g.fillRect(x + (int) Math.round(square.getX()), y + (int) Math.round(square.getY()), size, size);
		}
	}

	private void initializeComponent(JComponent component) {
		final Timer timer = new Timer(20, e -> {
			if (component.isVisible()) {
				for (Square square : squares) {
					square.tick();
				}
				component.repaint();
			}
		});
		timer.start();

		component.addAncestorListener(new AncestorListener() {
			@Override public void ancestorAdded(AncestorEvent event) {
			}

			@Override public void ancestorRemoved(AncestorEvent event) {
				timer.stop();
			}

			@Override public void ancestorMoved(AncestorEvent event) {
			}
		});
	}

	@Override public int getIconWidth() {
		return size * 3 + spacing * 2;
	}

	@Override public int getIconHeight() {
		return size * 3 + spacing * 2;
	}

	private class Square {

		private double x;
		private double y;

		private int currentBlock;
		private int targetBlock;

		Square(int currentBlock) {
			setCurrentBlock(currentBlock);
			setTargetBlock(currentBlock);
		}

		void setCurrentBlock(int currentBlock) {
			this.currentBlock = currentBlock;
			Point2D position = getPositionForBlock(currentBlock);
			x = position.getX();
			y = position.getY();
		}

		void setTargetBlock(int targetBlock) {
			this.targetBlock = targetBlock;
		}

		void tick() {
			if (currentBlock == 4)
				return; // middle square is fixed

			// Determine next target block if we are on the target block
			if (currentBlock == targetBlock) {
				Square previousBlock = getBlockAt(getPreviousBlockPos(targetBlock));
				Square nextBlock = getBlockAt(getNextBlockPos(targetBlock));
				if (previousBlock == null || nextBlock == null) {
					int nextBlockPos = getNextBlockPos(targetBlock);
					if (getBlockAt(nextBlockPos) == null) {
						setTargetBlock(nextBlockPos);
					}
				}
			}

			// Move to target block
			Point2D position = getPositionForBlock(targetBlock);
			double dx = position.getX() - x;
			double dy = position.getY() - y;
			if (Math.abs(dx) >= getMoveStep())
				x += Math.signum(dx) * getMoveStep();
			if (Math.abs(dy) >= getMoveStep())
				y += Math.signum(dy) * getMoveStep();
			if (Math.abs(dx) < getMoveStep() && Math.abs(dy) < getMoveStep())
				setCurrentBlock(targetBlock);
		}

		double getX() {
			return x;
		}

		double getY() {
			return y;
		}

		private double getMoveStep() {
			return size / 7.5;
		}

		private Point2D getPositionForBlock(int block) {
			return switch (block) {
				case 0 -> new Point2D.Double(0, 0);
				case 1 -> new Point2D.Double(size + spacing, 0);
				case 2 -> new Point2D.Double(size * 2 + spacing * 2, 0);
				case 3 -> new Point2D.Double(0, size + spacing);
				case 4 -> new Point2D.Double(size + spacing, size + spacing);
				case 5 -> new Point2D.Double(size * 2 + spacing * 2, size + spacing);
				case 6 -> new Point2D.Double(0, size * 2 + spacing * 2);
				case 7 -> new Point2D.Double(size + spacing, size * 2 + spacing * 2);
				case 8 -> new Point2D.Double(size * 2 + spacing * 2, size * 2 + spacing * 2);
				default -> new Point2D.Double(x, y);
			};
		}

		private int getNextBlockPos(int block) {
			return switch (block) {
				case 0 -> 1;
				case 1 -> 2;
				case 2 -> 5;
				case 3 -> 0;
				case 5 -> 8;
				case 6 -> 3;
				case 7 -> 6;
				case 8 -> 7;
				default -> 4;
			};
		}

		private int getPreviousBlockPos(int block) {
			return switch (block) {
				case 0 -> 3;
				case 1 -> 0;
				case 2 -> 1;
				case 3 -> 6;
				case 5 -> 2;
				case 6 -> 7;
				case 7 -> 8;
				case 8 -> 5;
				default -> 4;
			};
		}

	}

}
