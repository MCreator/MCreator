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

import net.mcreator.io.net.api.update.IUpdateAPI;

import java.util.concurrent.CompletableFuture;

public interface IWebAPI extends IUpdateAPI {

	/**
	 * Initializes API, called on MCreator launch, while loading data
	 *
	 * @return true if the connection was successful - can be used for internet connectivity testing
	 */
	boolean initAPI();

	/**
	 * API request
	 */
	void getWebsiteNews(CompletableFuture<String[]> data);

	/**
	 * API request
	 * <p>
	 * Returns mod of the week data in String array format, where index
	 * <ul>
	 * <li>[0] - mod of the week name</li>
	 * <li>[1] - mod of the week page URL</li>
	 * <li>[2] - mod of the week Minecraft version</li>
	 * <li>[3] - mod of the week author URL</li>
	 * <li>[4] - mod of the week picture URL</li>
	 * </ul>
	 */
	void getModOfTheWeekData(CompletableFuture<String[]> data);

	/**
	 * API URL generator
	 *
	 * @param searchTerm Terms that should be searched
	 * @return Search URL for the website for the given term
	 */
	String getSearchURL(String searchTerm);

}
