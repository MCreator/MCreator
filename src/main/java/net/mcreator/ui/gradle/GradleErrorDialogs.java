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

package net.mcreator.ui.gradle;

import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.impl.gradle.ClearAllGradleCachesAction;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class GradleErrorDialogs {

	private static final Logger LOG = LogManager.getLogger("Gradle Error Dialogs");

	private static final String MESSAGE_TITLE = L10N.t("gradle.error.failed_task");

	public static int showErrorDialog(int errorCode, MCreator whereToShow) {
		if (errorCode == GradleErrorCodes.JAVA_JVM_CRASH_ERROR)
			showJVMCrashErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_XMX_INVALID_VALUE)
			showXMXInvalidErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_XMS_INVALID_VALUE)
			showXMSInvalidErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_JVM_HEAP_SPACE)
			showJVMHeapSpaceErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_NO_INTERNET)
			showNoInternetErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_INTERNET_INTERRUPTED)
			showInternetInterruptedErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_BUILD_FAILED)
			showGradleBuildFailedErrorDialog(whereToShow);
		else if (errorCode == GradleErrorCodes.GRADLE_REOBF_FAILED)
			showGradleReobfFailedErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_CACHEDATA_ERROR)
			showGradleCacheDataErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_CACHEDATA_OUTDATED)
			showGradleCacheOutdatedDialogOfflineMode(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_INVALID_VERSION)
			showGradleInvalidJavaVersionDialog(whereToShow, errorCode);

		else
			LOG.warn("Error with code " + errorCode + " was reported, but no action is registered.");

		return errorCode;
	}

	private static void showGradleCacheDataErrorDialog(MCreator whereToShow, int errorCode) {
		if (PreferencesManager.PREFERENCES.gradle.offline) {
			showGradleCacheOutdatedDialogOfflineMode(whereToShow, errorCode);
		} else {
			String appendx = L10N.t("gradle.error.error_code", GradleErrorCodes.toString(errorCode), errorCode);
			String msg = L10N.t("gradle.error.corrupted_caches");

			String[] options = { L10N.t("gradle.clear_caches"), L10N.t("gradle.do_nothing") };
			int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (option == 0) {
				ClearAllGradleCachesAction.clearAllGradleCaches(whereToShow, false);
			}
		}
	}

	private static void showGradleCacheOutdatedDialogOfflineMode(Window whereToShow, int errorCode) {
		String appendx = L10N.t("gradle.error.error_code", GradleErrorCodes.toString(errorCode), errorCode);
		String msg = L10N.t("gradle.error.outdated_caches");

		String[] options = { L10N.t("gradle.open_options"), L10N.t("gradle.do_nothing")};
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showGradleInvalidJavaVersionDialog(Window whereToShow, int errorCode) {
		String appendx = L10N.t("gradle.error.error_code", GradleErrorCodes.toString(errorCode), errorCode);
		String msg = L10N.t("gradle.error.invalid_java_version");

		String[] options = { L10N.t("gradle.open_options"), L10N.t("gradle.do_nothing")};
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showGradleReobfFailedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.reobfJar_failed"),
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showInternetInterruptedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.internet_connection.interrupted"),
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showNoInternetErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.no_internet"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMHeapSpaceErrorDialog(Window whereToShow, int errorCode) {
		String appendx = L10N.t("gradle.error.error_code", GradleErrorCodes.toString(errorCode), errorCode);
		String msg = L10N.t("gradle.error.jvm_space");

		String[] options = { L10N.t("gradle.open_options"), L10N.t("gradle.do_nothing")};
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showXMSInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.xms_invalid"),
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showXMXInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.xmx_invalid"),
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMCrashErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.error.jvm_crash"), JOptionPane.ERROR_MESSAGE,
				null, errorCode);
	}

	private static void showGradleBuildFailedErrorDialog(Window whereToShow) {
		String[] options = { L10N.t("gradle.open_help_page"), L10N.t("gradle.do_nothing")};
		int reply = JOptionPane.showOptionDialog(whereToShow,
				L10N.t("gradle.error.build_failed"), L10N.t("gradle.error.build_failed.title"),
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
				options[0]);
		if (reply == 0) {
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/gradle-setup-errors");
		}
	}

	private static void showErrorDialog(Window window, String msg, int type, Icon icon, int errorCode) {
		String appendx = L10N.t("gradle.error.error_code", GradleErrorCodes.toString(errorCode), errorCode);
		JOptionPane.showMessageDialog(window, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE, type, icon);
	}

}
