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

package net.mcreator.io.net;

import net.mcreator.ui.MCreatorApplication;
import net.mcreator.util.image.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WebIO {

	private static final Logger LOG = LogManager.getLogger("Web IO");

	public static String readURLToString(String s) {
		StringBuilder sb = new StringBuilder();
		if (MCreatorApplication.isInternet) {
			try {
				HttpURLConnection urlConn = (HttpURLConnection) new URL(s).openConnection();
				urlConn.setConnectTimeout(4000);
				urlConn.setInstanceFollowRedirects(true);
				urlConn.connect();
				if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						sb.append(inputLine);
					}
					in.close();
				}
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			}
		}
		return sb.toString();
	}

	public static ImageIcon getIconFromURL(String url, int x, int y, ImageIcon defaultIcon) {
		return getIconFromURL(url, x, y, defaultIcon, false);
	}

	public static ImageIcon getIconFromURL(String url, int x, int y, ImageIcon defaultIcon, boolean noStretch) {
		if (url == null)
			return defaultIcon;

		if (!MCreatorApplication.isInternet || url.equals("err"))
			return defaultIcon;

		Image colorImage = null;
		try {
			Image original = new ImageIcon(ImageIO.read(new URL(url))).getImage();
			if (noStretch)
				colorImage = ImageUtils.cover(original, new Dimension(x, y));
			else
				colorImage = ImageUtils.resize(original, x, y);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
		if (colorImage != null) {
			return new ImageIcon(colorImage);
		}

		return defaultIcon;
	}
}
