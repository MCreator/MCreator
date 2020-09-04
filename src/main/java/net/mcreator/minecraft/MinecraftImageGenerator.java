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

package net.mcreator.minecraft;

import net.mcreator.element.parts.MItemBlock;
import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageTransformUtil;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class MinecraftImageGenerator {

	private static final Logger LOG = LogManager.getLogger("MC Img Gen");

	public static BufferedImage generateBackground(int width, int height) {
		if (height < 10)
			height = 10;
		if (width < 10)
			width = 10;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		g.setColor(new Color(198, 198, 198)); //filler color
		g.fillRect(3, 3, width - 6, height - 6);

		g.setColor(new Color(255, 255, 255)); //top fat border color
		g.fillRect(2, 0, width - 5, 3);
		g.fillRect(0, 2, 3, height - 5);
		g.drawRect(3, 3, 0, 0);

		g.setColor(new Color(198, 198, 198)); //transit pixel border color
		g.drawRect(width - 3, 2, 0, 0);
		g.drawRect(2, height - 3, 0, 0);

		g.setColor(new Color(85, 85, 85)); //bottom fat border color
		g.fillRect(3, height - 3, width - 5, 3);
		g.fillRect(width - 3, 3, 3, height - 5);
		g.drawRect(width - 4, height - 4, 0, 0);

		g.setColor(new Color(0, 0, 0)); //outer border color
		g.drawLine(2, 0, width - 4, 0); //rob zgornji
		g.drawLine(width - 1, 3, width - 1, height - 4); //rob desno
		g.drawLine(3, height - 1, width - 4, height - 1); //rob spodaj
		g.drawLine(0, 2, 0, height - 4); //rob levi

		g.drawLine(2, 0, 0, 2); //kot levo zgoraj
		g.drawLine(width - 4, 0, width - 1, 3); //kot desno zgoraj
		g.drawLine(width - 1, height - 3, width - 3, height - 1); //kot desno spodaj
		g.drawLine(0, height - 4, 3, height - 1); //kot levo spodaj

		return bi;
	}

	public static BufferedImage generateButton(int width, int height) {
		if (height < 5)
			height = 5;
		if (width < 5)
			width = 5;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();

		g.setColor(new Color(111, 111, 111));
		g.fillRect(1, 1, width - 2, height - 2);

		g.setColor(new Color(255, 255, 255, 70));
		g.drawLine(1, 1, width - 2, 1);
		g.drawLine(1, 2, 1, height - 2);

		g.setColor(new Color(0, 0, 0, 70));
		g.fillRect(1, height - 3, width - 2, 2);
		g.drawLine(width - 2, 1, width - 2, height - 4);

		g.setColor(new Color(0, 0, 0)); //outer border color
		g.drawLine(0, 0, width - 1, 0); //upper edge
		g.drawLine(width - 1, 1, width - 1, height - 2); //edge right
		g.drawLine(0, height - 1, width - 1, height - 1); //edge bottom
		g.drawLine(0, 1, 0, height - 2); //edge left

		return bi;
	}

	public static BufferedImage generateTextField(int width, int height) {
		if (height < 3)
			height = 3;
		if (width < 3)
			width = 3;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();

		g.setColor(new Color(0, 0, 0)); //filler color
		g.fillRect(1, 1, width - 2, height - 2);

		g.setColor(new Color(162, 162, 162)); //outer border color
		g.drawLine(0, 0, width - 1, 0); //rob zgornji
		g.drawLine(width - 1, 1, width - 1, height - 2); //rob desno
		g.drawLine(0, height - 1, width - 1, height - 1); //rob spodaj
		g.drawLine(0, 1, 0, height - 2); //rob levi

		return bi;
	}

	public static BufferedImage generateItemSlot() {
		int width = 18, height = 18;
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();

		g.setColor(new Color(139, 139, 139)); //filler color
		g.fillRect(0, 0, width, height);

		g.setColor(new Color(0, 0, 0, 135)); //top border color
		g.drawLine(0, 0, width - 1, 0); //rob zgornji
		g.drawLine(0, 1, 0, height - 1); //rob levi

		g.setColor(new Color(255, 255, 255, 210)); //bottom border color
		g.drawLine(width - 1, 0, width - 1, height - 1); //rob desno
		g.drawLine(0, height - 1, width - 2, height - 1); //rob spodaj

		return bi;
	}

	public static BufferedImage generateInventorySlots() {
		int width = 176, height = 166;
		int startx = 7;
		int start1y = 83, start2y = 141;
		BufferedImage slot = generateItemSlot();
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) bi.getGraphics();

		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 3; j++) {
				g.drawImage(slot, i * 18 + startx, j * 18 + start1y, null);
			}
		for (int i = 0; i < 9; i++) {
			g.drawImage(slot, i * 18 + startx, start2y, null);
		}

		return bi;
	}

	public static class Preview {

		public static Image generateArmorPreviewFrame1() {
			BufferedImage image = new BufferedImage(320, 160, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setColor(Color.white);
			g.setFont(g.getFont().deriveFont(12.0f));
			g.drawString("HELMET", 5, 72);
			g.drawRect(0, 0, 159, 77);
			g.drawString("BOOTS", 5, 155);
			g.drawRect(0, 79, 78, 80);
			g.drawString("BODY", 87, 155);
			g.drawRect(80, 79, 205, 80);
			return image;
		}

		public static Image generateArmorPreviewFrame2() {
			BufferedImage image = new BufferedImage(320, 160, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = (Graphics2D) image.getGraphics();
			g.setColor(Color.white);
			g.setFont(g.getFont().deriveFont(12.f));
			g.drawRect(0, 75, 227, 84);
			g.drawString("LEGGINGS", 160, 91);
			return image;
		}

		private static void drawTwoSlotRecipe(Graphics2D graphics2D, Workspace workspace, MItemBlock input,
				MItemBlock result) {
			int slotOffsetY = 9;
			int oSlotOffsetY = 9;

			//box 1
			graphics2D.drawLine(1, slotOffsetY, 8, slotOffsetY);
			graphics2D.drawLine(1, 9 + slotOffsetY, 8, 9 + slotOffsetY);
			graphics2D.drawLine(0, slotOffsetY, 0, 9 + slotOffsetY);
			graphics2D.drawLine(9, slotOffsetY, 9, 9 + slotOffsetY);

			//box 2
			graphics2D.drawLine(19, oSlotOffsetY, 26, oSlotOffsetY);
			graphics2D.drawLine(19, 9 + oSlotOffsetY, 26, 9 + oSlotOffsetY);
			graphics2D.drawLine(18, oSlotOffsetY, 18, 9 + oSlotOffsetY);
			graphics2D.drawLine(27, oSlotOffsetY, 27, 9 + oSlotOffsetY);

			//elements
			graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils
							.toBufferedImage(MCItem.getBlockIconBasedOnName(workspace, input.getUnmappedValue()).getImage())),
					1, 1 + slotOffsetY, 8, 8, null);
			graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils
							.toBufferedImage(MCItem.getBlockIconBasedOnName(workspace, result.getUnmappedValue()).getImage())),
					19, 1 + oSlotOffsetY, 8, 8, null);
		}

		/**
		 * <p>This method generates blasting recipe images.</p>
		 *
		 * @param input  Input of the recipe.
		 * @param result Result of the recipe.
		 * @return Returns the generated image.
		 */
		public static BufferedImage generateBlastingPreviewPicture(Workspace workspace, MItemBlock input,
				MItemBlock result) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(new Color(190, 190, 190, 65));

			drawTwoSlotRecipe(graphics2D, workspace, input, result);

			//explosion
			graphics2D.drawPolygon(getStarPolygon(14, 13, 4, 2, 6, 0.5235987755982988));

			graphics2D.dispose();

			return icon;
		}

		private static Polygon getStarPolygon(int x, int y, int r, int innerR, int vertexCount, double startAngle) {
			int[] xcoord = new int[vertexCount * 2];
			int[] ycoord = new int[vertexCount * 2];
			double addAngle = 2 * Math.PI / vertexCount;
			double angle = startAngle;
			double innerAngle = startAngle + Math.PI / vertexCount;
			for (int i = 0; i < vertexCount; i++) {
				xcoord[i * 2] = (int) Math.round(r * Math.cos(angle)) + x;
				ycoord[i * 2] = (int) Math.round(r * Math.sin(angle)) + y;
				angle += addAngle;
				xcoord[i * 2 + 1] = (int) Math.round(innerR * Math.cos(innerAngle)) + x;
				ycoord[i * 2 + 1] = (int) Math.round(innerR * Math.sin(innerAngle)) + y;
				innerAngle += addAngle;
			}
			return new Polygon(xcoord, ycoord, vertexCount * 2);
		}

		/**
		 * <p>This method generates smoking recipe images.</p>
		 *
		 * @param input  Input of the recipe.
		 * @param result Result of the recipe.
		 * @return Returns the generated image.
		 */
		public static BufferedImage generateSmokingPreviewPicture(Workspace workspace, MItemBlock input,
				MItemBlock result) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(new Color(190, 190, 190, 65));

			drawTwoSlotRecipe(graphics2D, workspace, input, result);

			//smoke
			graphics2D.drawLine(11, 11, 11, 16);
			graphics2D.drawLine(13, 11, 14, 16);
			graphics2D.drawLine(16, 11, 16, 16);

			graphics2D.dispose();

			return icon;
		}

		/**
		 * <p>This method generates stone cutter recipe images.</p>
		 *
		 * @param input  Input of the recipe.
		 * @param result Result of the recipe.
		 * @return Returns the generated image.
		 */
		public static BufferedImage generateStoneCuttingPreviewPicture(Workspace workspace, MItemBlock input,
				MItemBlock result) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(new Color(190, 190, 190, 65));

			drawTwoSlotRecipe(graphics2D, workspace, input, result);

			//saw
			graphics2D.drawOval(11, 11, 5, 5);

			graphics2D.dispose();

			return icon;
		}

		/**
		 * <p>This method generates campfire recipe images.</p>
		 *
		 * @param input  Input of the recipe.
		 * @param result Result of the recipe.
		 * @return Returns the generated image.
		 */
		public static BufferedImage generateCampfirePreviewPicture(Workspace workspace, MItemBlock input,
				MItemBlock result) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(new Color(190, 190, 190, 65));

			drawTwoSlotRecipe(graphics2D, workspace, input, result);

			//campfire
			graphics2D.drawLine(12, 11, 12, 16);
			graphics2D.drawLine(15, 11, 15, 16);
			graphics2D.drawLine(11, 12, 16, 12);
			graphics2D.drawLine(11, 15, 16, 15);

			graphics2D.dispose();

			return icon;
		}

		/**
		 * <p>This method generates recipe images.</p>
		 *
		 * @param recipe <p>The recipe field is an ArrayList of Images. If containing 1 element, it generates furnace recipe picture. If it contains 9 elements it creates a crafting recipe and are inserted as shown in the table:</p> <table summary="Recipe slot IDs"><tr><td>0</td><td>1</td><td>2</td></tr><tr><td>3</td><td>4</td><td>5</td></tr><tr><td>6</td><td>7</td><td>8</td></tr></table> <p>Null elements are ignored/not drawn.</p>
		 * @param result Result of a recipe is only drawn on furnace recipes.
		 * @return Returns generated image.
		 */
		public static BufferedImage generateRecipePreviewPicture(Workspace workspace, MItemBlock[] recipe,
				MItemBlock result) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			graphics2D.setColor(new Color(190, 190, 190, 65));

			if (recipe.length == 1) {
				drawTwoSlotRecipe(graphics2D, workspace, recipe[0], result);

				//arrow
				graphics2D.drawLine(11, 14, 16, 14);

				graphics2D.drawLine(15, 13, 15, 13);
				graphics2D.drawLine(15, 15, 15, 15);

			} else if (recipe.length == 9) {
				graphics2D.drawLine(0, 1, 0, 26);
				graphics2D.drawLine(9, 1, 9, 26);
				graphics2D.drawLine(18, 1, 18, 26);
				graphics2D.drawLine(27, 1, 27, 26);
				graphics2D.drawLine(0, 0, 27, 0);
				graphics2D.drawLine(1, 9, 8, 9);
				graphics2D.drawLine(1, 18, 8, 18);
				graphics2D.drawLine(10, 9, 17, 9);
				graphics2D.drawLine(10, 18, 17, 18);
				graphics2D.drawLine(19, 9, 26, 9);
				graphics2D.drawLine(19, 18, 26, 18);
				graphics2D.drawLine(0, 27, 27, 27);

				for (int i = 0; i < 9; i++)
					if (recipe[i] != null) {
						int x = (i % 3) * 9 + 1, y = (i / 3) * 9 + 1;
						graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils.toBufferedImage(
								MCItem.getBlockIconBasedOnName(workspace, recipe[i].getUnmappedValue()).getImage())), x,
								y, 8, 8, null);
					}
			}

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates achievement images.</p>
		 *
		 * @param achievementIcon <p>Achievement's icon</p>
		 * @param name            <p>Name of the achievement</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateAchievementPreviewPicture(Workspace workspace, MItemBlock achievementIcon,
				String name) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setColor(new Color(255, 255, 255, 180));

			graphics2D.drawLine(0, 8, 27, 8);
			graphics2D.drawLine(0, 19, 27, 19);
			graphics2D.drawLine(0, 9, 0, 18);
			graphics2D.drawLine(27, 9, 27, 18);

			graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils.toBufferedImage(
					MCItem.getBlockIconBasedOnName(workspace, achievementIcon.getUnmappedValue()).getImage())), 2, 10,
					8, 8, null);

			graphics2D.setFont(new Font(null, Font.PLAIN, 9));

			GradientPaint gp = new GradientPaint(22, 14, new Color(255, 255, 255, 180), 26, 14,
					new Color(255, 255, 255, 0));

			graphics2D.setPaint(gp);
			graphics2D.drawString(StringUtils.abbreviateString(name, 3, false), 10, 17);

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates command images.</p>
		 *
		 * @param command <p>The command.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateCommandPreviewPicture(String command) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setColor(new Color(255, 255, 255, 180));

			graphics2D.drawLine(3, 16, 6, 10);

			graphics2D.setFont(new Font(null, Font.PLAIN, 9));
			graphics2D.setPaint(
					new GradientPaint(16, 14, new Color(255, 255, 255, 180), 24, 14, new Color(255, 255, 255, 0)));
			graphics2D.drawString(StringUtils.abbreviateString(command, 4, false).toUpperCase(Locale.ENGLISH), 7, 17);

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates key binding images.</p>
		 *
		 * @param keybind <p>The key binding.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateKeybindPreviewPicture(String keybind) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setColor(new Color(255, 255, 255, 180));

			String text;
			if (keybind.startsWith("NUMPAD"))
				text = "NP" + StringUtils.abbreviateString(keybind.replace("NUMPAD", ""), 1, false);
			else
				text = StringUtils.abbreviateString(keybind, 3, false);

			graphics2D.setFont(new Font(null, Font.PLAIN, 9));

			graphics2D.drawLine(0, 1, 0, 26);
			graphics2D.drawLine(27, 1, 27, 26);
			graphics2D.drawLine(1, 0, 26, 0);
			graphics2D.drawLine(1, 27, 26, 27);

			FontMetrics fontMetrics = graphics2D.getFontMetrics();
			graphics2D
					.drawString(text, (28 - fontMetrics.stringWidth(text)) / 2, (28 - fontMetrics.getHeight()) / 2 + 9);

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates creative tab images.</p>
		 *
		 * @param item <p>The item used as tab's icon.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateCreativeTabPreviewPicture(Workspace workspace, MItemBlock item) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setColor(new Color(255, 255, 255, 180));
			graphics2D.setFont(new Font(null, Font.PLAIN, 9));

			graphics2D.drawLine(0, 1, 0, 27);
			graphics2D.drawLine(27, 1, 27, 27);
			graphics2D.drawLine(1, 0, 26, 0);

			int s = 16;

			graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils
							.toBufferedImage(MCItem.getBlockIconBasedOnName(workspace, item.getUnmappedValue()).getImage())),
					(28 - s) / 2, (28 - s) / 2 + 1, s, s, null);

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates dimension images.</p>
		 *
		 * @param portalTexture      <p>This texture is used for portal's inner filler.</p>
		 * @param triggerTexture     <p>This texture is used for portal's igniter texture above portal.</p>
		 * @param portalFrameTexture <p>The item provided is used to calculate it's average color for portal's frame.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateDimensionPreviewPicture(Workspace workspace, File portalTexture,
				File triggerTexture, MItemBlock portalFrameTexture) {
			if (!portalTexture.isFile())
				return null;

			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			graphics2D.setColor(new Color(255, 255, 255, 180));

			//get avg color of the portal frame
			graphics2D.setColor(ImageUtils.getAverageColor(ImageUtils.toBufferedImage(ImageUtils
					.changeSaturation(MCItem.getBlockIconBasedOnName(workspace, portalFrameTexture.getUnmappedValue()),
							0.3f).getImage())));

			graphics2D.fillRect(3, 0, 5, 28);
			graphics2D.fillRect(20, 0, 5, 28);
			graphics2D.fillRect(8, 0, 12, 5);
			graphics2D.fillRect(8, 23, 12, 5);

			try {
				Image tex = ImageUtils
						.changeSaturation(new ImageIcon(ImageUtils.autoCropTile(ImageIO.read(portalTexture))), 0.1f)
						.getImage();
				graphics2D.drawImage(tex, 8, 5, 6, 6, null);
				graphics2D.drawImage(tex, 14, 5, 6, 6, null);
				graphics2D.drawImage(tex, 8, 11, 6, 6, null);
				graphics2D.drawImage(tex, 14, 11, 6, 6, null);
				graphics2D.drawImage(tex, 8, 17, 6, 6, null);
				graphics2D.drawImage(tex, 14, 17, 6, 6, null);

				BufferedImage igniter = ImageUtils.autoCropTile(ImageIO.read(triggerTexture));
				graphics2D.drawImage(igniter, 2, 2, 24, 24, null);
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates fuel images.</p>
		 *
		 * @param fuel <p>This texture is used as icon of the fuel.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateFuelPreviewPicture(Workspace workspace, MItemBlock fuel) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();
			int zamik = -7;

			graphics2D.setPaint(new GradientPaint(15, 17 + zamik, new Color(255, 179, 31, 180), 14, 10 + zamik,
					new Color(255, 52, 25, 180)));

			graphics2D.drawLine(11, 10 + zamik, 11, 13 + zamik);
			graphics2D.drawLine(10, 14 + zamik, 10, 15 + zamik);
			graphics2D.drawLine(11, 16 + zamik, 14, 13 + zamik);
			graphics2D.drawLine(12, 17 + zamik, 12, 17 + zamik);
			graphics2D.drawLine(13, 16 + zamik, 14, 16 + zamik);
			graphics2D.drawLine(14, 17 + zamik, 15, 17 + zamik);
			graphics2D.drawLine(14, 12 + zamik, 14, 11 + zamik);
			graphics2D.drawLine(13, 10 + zamik, 13, 10 + zamik);
			graphics2D.drawLine(16, 16 + zamik, 16, 16 + zamik);
			graphics2D.drawLine(16, 15 + zamik, 15, 14 + zamik);
			graphics2D.drawLine(17, 14 + zamik, 17, 13 + zamik);
			graphics2D.drawLine(16, 12 + zamik, 16, 11 + zamik);
			graphics2D.drawLine(17, 10 + zamik, 17, 10 + zamik);

			graphics2D.drawImage(ImageUtils.autoCropTile(ImageUtils
							.toBufferedImage(MCItem.getBlockIconBasedOnName(workspace, fuel.getUnmappedValue()).getImage())), 9,
					15, 10, 10, null);

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates armor images.</p>
		 *
		 * @param armorPieces <p>These textures are used to assemble the armor image.</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static BufferedImage generateArmorPreviewPicture(ArrayList<File> armorPieces) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();

			try {
				switch (armorPieces.size()) {
				case 1:
					graphics2D.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(0))), 0, 0, 28, 28, null);
					break;
				case 2:
					graphics2D.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(0))), 0, 7, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(1))), 14, 7, 14, 14, null);
					break;
				case 3:
					graphics2D.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(0))), 7, 0, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(1))), 0, 14, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(2))), 14, 14, 14, 14, null);
					break;
				case 4:
					graphics2D.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(0))), 0, 0, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(1))), 14, 0, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(2))), 0, 14, 14, 14, null);
					graphics2D
							.drawImage(ImageUtils.autoCropTile(ImageIO.read(armorPieces.get(3))), 14, 14, 14, 14, null);
					break;
				}
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}

			graphics2D.dispose();
			return icon;
		}

		/**
		 * <p>This method generates the block icon.</p>
		 *
		 * @param top   <p>Top side texture</p>
		 * @param left  <p>Front side texture</p>
		 * @param front <p>Right side texture</p>
		 * @return <p>Returns generated image.</p>
		 */
		public static Image generateBlockIcon(Image top, Image left, Image front) {
			BufferedImage out = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) out.getGraphics();

			Point2D t1 = new Point2D.Double(15.5, 0.2), f2 = new Point2D.Double(2, 24.5), r3 = new Point2D.Double(29.5,
					24), t2f1 = new Point2D.Double(1.5, 7.6), t4r4 = new Point2D.Double(29.9,
					7.5), f3r2 = new Point2D.Double(15.5, 31), t3f4r1 = new Point2D.Double(15.5, 14.7);

			g2d.drawImage(ImageTransformUtil.computeImage(ImageUtils.brighten(ImageUtils.resizeAndCrop(top, 32)),
					new Point2D.Double(t4r4.getX() - 0.7, t4r4.getY() - 0.3), t1,
					new Point2D.Double(t2f1.getX() + 0.2, t2f1.getY() - 0.4),
					new Point2D.Double(t3f4r1.getX(), t3f4r1.getY() - 0.6)), null, null);
			g2d.drawImage(ImageTransformUtil.computeImage(ImageUtils.resizeAndCrop(left, 32), t2f1, f2, f3r2, t3f4r1),
					null, null);
			g2d.drawImage(ImageTransformUtil
							.computeImage(ImageUtils.darken(ImageUtils.resizeAndCrop(front, 32)), t3f4r1, f3r2, r3, t4r4), null,
					null);
			return out;
		}

		public static BufferedImage generatePotionIcon(Color color) {
			BufferedImage out = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = (Graphics2D) out.getGraphics();

			g2d.drawImage(ImageUtils.resize(ImageUtils.colorize(ImageMakerTexturesCache.CACHE
					.get(new ResourcePointer("templates/textures/texturemaker/potion_fluid_bright.png")), color, true)
					.getImage(), 32), null, 0, 0);
			g2d.drawImage(ImageUtils.resize(ImageMakerTexturesCache.CACHE
							.get(new ResourcePointer("templates/textures/texturemaker/potion_bottle_overlay.png")).getImage(),
					32), null, 0, 0);
			g2d.dispose();

			return out;
		}

		public static BufferedImage generateBiomePreviewPicture(Workspace workspace, Color airColor, Color grassColor,
				Color waterColor, MItemBlock groundBlock, MItemBlock undergroundBlock, boolean lakes, int treesPerChunk,
				int treeType, MItemBlock treeStem, MItemBlock treeBranch) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();

			Color undergroundColor = ImageUtils.getAverageColor(ImageUtils.toBufferedImage(
					MCItem.getBlockIconBasedOnName(workspace, undergroundBlock.getUnmappedValue()).getImage()));
			Color groundColor = ImageUtils.getAverageColor(ImageUtils.toBufferedImage(
					MCItem.getBlockIconBasedOnName(workspace, groundBlock.getUnmappedValue()).getImage()));
			Color topColor;
			if (groundBlock.getUnmappedValue().equals("Blocks.GRASS"))
				if (grassColor != null)
					topColor = grassColor;
				else
					topColor = new Color(0x35A351);
			else
				topColor = groundColor;

			Color airTransparent;
			if (airColor != null)
				airTransparent = airColor;
			else
				airTransparent = new Color(0xA5B9FF);
			airTransparent = new Color((airTransparent.getRGB() & 0x00ffffff) | 0x55000000, true);

			//fill air
			graphics2D.setColor(airTransparent);
			graphics2D.fillRect(0, 0, 28, 28);

			//fill ground
			graphics2D.setColor(undergroundColor);
			graphics2D.fillRect(0, 21, 28, 7);

			//draw ground blocks
			graphics2D.setColor(groundColor);
			graphics2D.fillRect(0, 19, 28, 2);

			//draw grass
			graphics2D.setColor(topColor);
			if (lakes) {
				graphics2D.fillRect(0, 18, 5, 1);
				graphics2D.fillRect(13, 18, 15, 1);
				if (waterColor != null)
					graphics2D.setColor(waterColor);
				else
					graphics2D.setColor(new Color(0x2559CB));
				graphics2D.fillRect(3, 18, 10, 2);
				graphics2D.fillRect(5, 20, 6, 1);
			} else {
				graphics2D.fillRect(0, 18, 28, 1);
			}

			//draw trees: 0 = vanilla, 1 = modded
			if (treesPerChunk > 0) {
				//select colors for the tree parts
				Color stem, leaves;
				if (treeType == 1) {
					stem = (ImageUtils.getAverageColor(ImageUtils.toBufferedImage(
							MCItem.getBlockIconBasedOnName(workspace, groundBlock.getUnmappedValue()).getImage())));
					leaves = new Color((ImageUtils.getAverageColor(ImageUtils.toBufferedImage(
							MCItem.getBlockIconBasedOnName(workspace, groundBlock.getUnmappedValue()).getImage()))
							.getRGB() & 0x00ffffff) | 0xEE000000, false);
				} else {
					stem = new Color(95, 69, 32);
					leaves = new Color(38, 108, 30, 238);
				}
				graphics2D.setColor(stem);
				graphics2D.fillRect(20, 8, 2, 10);

				graphics2D.setColor(leaves);
				graphics2D.fillRect(16, 8, 10, 6);
				graphics2D.fillRect(18, 6, 6, 2);
			}

			graphics2D.dispose();
			return icon;
		}

		public static BufferedImage generateMobPreviewPicture(Workspace workspace, String mobModelTexture,
				Color spawnEggBaseColor, Color spawnEggDotColor, boolean hasSpawnEgg) {
			BufferedImage icon = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics2D = icon.createGraphics();

			Color textureColor = ImageUtils.getAverageColor(ImageUtils.toBufferedImage(new ImageIcon(
					workspace.getFolderManager().getOtherTextureFile(FilenameUtils.removeExtension(mobModelTexture))
							.getAbsolutePath()).getImage()));

			graphics2D.drawImage(ImageUtils.colorize(UIRES.get("entity_base"), textureColor, false).getImage(), 0, 0,
					null);

			if (hasSpawnEgg) {
				graphics2D.setColor(spawnEggBaseColor);
				graphics2D.fillRect(20, 22, 8, 4);
				graphics2D.fillRect(21, 20, 6, 7);
				graphics2D.fillRect(22, 19, 4, 9);
				graphics2D.fillRect(23, 18, 2, 10);
				graphics2D.setColor(spawnEggDotColor);
				graphics2D.fillRect(23, 19, 1, 1);
				graphics2D.fillRect(24, 22, 1, 1);
				graphics2D.fillRect(26, 23, 1, 1);
				graphics2D.fillRect(21, 24, 1, 1);
				graphics2D.fillRect(23, 26, 1, 1);
				graphics2D.fillRect(25, 25, 1, 1);
			}

			graphics2D.dispose();
			return icon;
		}
	}
}
