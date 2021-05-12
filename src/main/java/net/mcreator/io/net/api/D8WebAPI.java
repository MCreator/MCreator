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

package net.mcreator.io.net.api;

import com.google.gson.Gson;
import net.mcreator.io.net.WebIO;
import net.mcreator.io.net.api.update.UpdateInfo;
import net.mcreator.ui.MCreatorApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.CookieHandler;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class D8WebAPI implements IWebAPI {

	private static final Logger LOG = LogManager.getLogger("Website API");

	private UpdateInfo updateInfo;

	private String[] news;
	private String[] motw;

	private final Set<CompletableFuture<String[]>> newsFutures = new HashSet<>();
	private final Set<CompletableFuture<String[]>> motwFutures = new HashSet<>();

	@Override public boolean initAPI() {
		CookieHandler.setDefault(null);

		String appData = WebIO.readURLToString(MCreatorApplication.SERVER_DOMAIN + "/repository");
		if (appData.equals(""))
			return false;

		updateInfo = new Gson().fromJson(appData, UpdateInfo.class);

		new Thread(() -> {
			initAPIPrivte();
			newsFutures.forEach(future -> future.complete(news));
			motwFutures.forEach(future -> future.complete(motw));
		}).start();

		return true;
	}

	private void initAPIPrivte() {
		String motwXML = WebIO.readURLToString(MCreatorApplication.SERVER_DOMAIN + "/app/motw");
		String newsXML = WebIO.readURLToString(MCreatorApplication.SERVER_DOMAIN + "/app/news");

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			if (!newsXML.equals("")) {
				news = new String[2];
				InputSource is = new InputSource(new StringReader(newsXML));
				Document doc = dBuilder.parse(is);
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("channel");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() != Node.ELEMENT_NODE)
						continue;
					news[0] = ((Element) node).getElementsByTagName("title").item(1).getChildNodes().item(0)
							.getNodeValue();
					news[1] = ((Element) node).getElementsByTagName("link").item(1).getChildNodes().item(0)
							.getNodeValue();
				}
			}

			dbFactory = DocumentBuilderFactory.newInstance();
			dBuilder = dbFactory.newDocumentBuilder();
			if (!motwXML.equals("")) {
				motw = new String[5];
				InputSource is = new InputSource(new StringReader(motwXML));
				Document doc = dBuilder.parse(is);
				doc.getDocumentElement().normalize();
				NodeList nodes = doc.getElementsByTagName("channel");
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					if (node.getNodeType() != Node.ELEMENT_NODE)
						continue;
					motw[0] = ((Element) node).getElementsByTagName("title").item(1).getChildNodes().item(0)
							.getNodeValue();
					motw[1] =
							MCreatorApplication.SERVER_DOMAIN + "/node/" + ((Element) node).getElementsByTagName("guid")
									.item(0).getChildNodes().item(0).getNodeValue().split("mcreator\\.net/")[1];
					motw[2] = ((Element) node).getElementsByTagName("pubDate").item(0).getChildNodes().item(0)
							.getNodeValue();
					motw[3] = MCreatorApplication.SERVER_DOMAIN + "/user/" + ((Element) node)
							.getElementsByTagName("dc:creator").item(0).getChildNodes().item(0).getNodeValue();
					motw[4] = ((Element) node).getElementsByTagName("description").item(1).getChildNodes().item(0)
							.getNodeValue().split("src=\"")[1].split("\" width")[0];
				}
			}
		} catch (Exception ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	@Override @Nullable public UpdateInfo getUpdateInfo() {
		return updateInfo;
	}

	@Override public String getSearchURL(String searchTerm) {
		return MCreatorApplication.SERVER_DOMAIN + "/search/content?keys=" + searchTerm.replaceAll(" ", "+");
	}

	@Override public void getWebsiteNews(CompletableFuture<String[]> data) {
		if (news != null)
			data.complete(news);
		else
			newsFutures.add(data);
	}

	/**
	 * API request
	 * <p>
	 * Returns as promise mod of the week data in String array format, where index
	 * <ul>
	 * <li>[0] - mod of the week name</li>
	 * <li>[1] - mod of the week page URL</li>
	 * <li>[2] - mod of the week Minecraft version</li>
	 * <li>[3] - mod of the week author URL</li>
	 * <li>[4] - mod of the week picture URL</li>
	 * </ul>
	 */
	@Override public void getModOfTheWeekData(CompletableFuture<String[]> data) {
		if (motw != null)
			data.complete(motw);
		else
			motwFutures.add(data);
	}

}
