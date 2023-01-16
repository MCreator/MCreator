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

package net.mcreator.io.net.analytics;

import java.util.UUID;
import java.util.function.Supplier;

// TODO: do we need this class?
public class Analytics {

	private final GoogleAnalytics ga;

	public Analytics(DeviceInfo deviceInfo) {
		ga = new GoogleAnalytics(deviceInfo, UUID.randomUUID().toString());
	}

	public Analytics trackMCreatorLaunch() {
		trackPageViewImpl(AnalyticsConstants.PAGE_LAUNCH);
		return this;
	}

	public Analytics trackMCreatorClose() {
		trackPageViewImpl(AnalyticsConstants.PAGE_CLOSE);
		return this;
	}

	public Analytics trackEvent(String category, String action, String label, String value) {
		ga.trackEvent(category, action, label, value);
		return this;
	}

	// TODO: check where this is used
	public void async(Supplier<Analytics> run) {
		new Thread(run::get).start();
	}

	private void trackPageViewImpl(String page) {
		ga.trackPage("/" + page);
	}

}