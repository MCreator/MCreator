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
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class GradleErrorDialogs {

	private static final Logger LOG = LogManager.getLogger("Gradle Error Dialogs");

	private static final String MESSAGE_TITLE = "Gradle task failed with error";

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
			String appendx =
					"<small><br><br><font color=gray>ERROR CODE: " + GradleErrorCodes.toString(errorCode) + " ["
							+ errorCode + "]";
			String msg = "<html><big>Gradle caches are corrupted!</big><br>"
					+ "<br>It seems that the cached dependencies of Gradle got corrupted or are outdated.<br>"
					+ "Cache needs to be cleared and re-downloaded. You can do this by pressing the button below.<br>"
					+ "<br>After you clear cache, the first Gradle build could take a but longer than usual.";

			String[] options = { "Clear Gradle caches", "Do nothing" };
			int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
			if (option == 0) {
				ClearAllGradleCachesAction.clearAllGradleCaches(whereToShow, false, false);
			}
		}
	}

	private static void showGradleCacheOutdatedDialogOfflineMode(Window whereToShow, int errorCode) {
		String appendx =
				"<small><br><br><font color=gray>ERROR CODE: " + GradleErrorCodes.toString(errorCode) + " [" + errorCode
						+ "]";
		String msg = "<html><big>Gradle caches are outdated!</big><br>"
				+ "<br>You are using MCreator in offline mode. Caches got outdated and need to be updated.<br>"
				+ "You need to disable offline mode in MCreator's preferences under Gradle options section<br>"
				+ "so the Gradle can download new version of Gradle dependencies and cached files.<br>"
				+ "<br>Afterwards, you can turn the offline mode back on.";

		String[] options = { "Open Gradle options", "Do nothing" };
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showGradleInvalidJavaVersionDialog(Window whereToShow, int errorCode) {
		String appendx =
				"<small><br><br><font color=gray>ERROR CODE: " + GradleErrorCodes.toString(errorCode) + " [" + errorCode
						+ "]";
		String msg = "<html><big>Invalid Java version is in use!</big><br>"
				+ "<br>Gradle only works with Java JDK 8. If you use other versions of Java, MCreator won't be able to compile<br>"
				+ "your mod. MCreator tries to find Java JDK 8 automatically. Install Java JDK 8 and try again.<br>"
				+ "<br>If you still see this message or already have the latest Java JDK 8,"
				+ "<br>go to Preferences and manually enter the <i>Java 8 java executable path</i>.";

		String[] options = { "Open Gradle options", "Do nothing" };
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showGradleReobfFailedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>Gradle reobfJar task failed!</big>"
						+ "<br>This happens if you use non ASCII characters in your file names (ex. german or chinese letters)."
						+ "<br>This error is currently not recoverable. MCreator does its best to convert your names into ASCII"
						+ "<br>format but it still failed somehow. You will have to factory reset MCreator (button under Tools)."
						+ "<br>For the further use, please avoid the use of non-english characters in mod and file names.",
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showInternetInterruptedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>Internet connection was interrupted!</big>"
						+ "<br>While Gradle was running, your internet connection was interrupted and it was not"
						+ "<br>able to complete the download of required files. Please make sure that your connection"
						+ "<br>is stable and redo your last action again with the internet connection.",
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showNoInternetErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>Gradle was not able to access the internet connection!</big>"
				+ "<br>Gradle failed to download required files because the internet connection is either not available or blocked by"
				+ "<br>a firewall or antivirus software. If other apps have internet, try to disable firewall or turn off antivirus software."
				+ "<br>If you don't have internet at all, try running MCreator when you have a connection as Gradle needs internet "
				+ "<br>connection to complete some tasks.<br><br>"
				+ "This problem could also be caused by dependency servers being overloaded. If this is the case, try<br>"
				+ "to run MCreator again later when they won't have so much load. It might take several attempts to make<br>"
				+ "it work, as server load can not be predicted.", JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMHeapSpaceErrorDialog(Window whereToShow, int errorCode) {
		String appendx =
				"<small><br><br><font color=gray>ERROR CODE: " + GradleErrorCodes.toString(errorCode) + " [" + errorCode
						+ "]";
		String msg = "<html><big>Java JVM used by Gradle ran out of available RAM!</big><br>"
				+ "The amount of RAM allocated to Java JVM used by Gradle is too low.<br>"
				+ "You can change this setting in MCreator's preferences under Gradle options section.";

		String[] options = { "Open Gradle options", "Do nothing" };
		int option = JOptionPane.showOptionDialog(whereToShow, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE,
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showXMSInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>Invalid RAM allocation detected!</big><br>"
						+ "You have allocated invalid amount for <i>Initial value of RAM dedicated to gradle</i>.<br>"
						+ "You can change this setting in MCreator's preferences under Gradle options section.",
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showXMXInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>Invalid RAM allocation detected!</big><br>"
						+ "You have allocated invalid amount for <i>Maximal value of RAM dedicated to gradle</i>.<br>"
						+ "You can change this setting in MCreator's preferences under Gradle options section.",
				JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMCrashErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, "<html><big>A crash in JVM native code was detected. Don't worry!</big><br>"
						+ "This sometimes happens with Java. This error was not caused by your mod or your code."
						+ "<br>You can fix this by running the task once again."
						+ "<br><hr><small>Error details (produced by Java):<br>"
						+ "A fatal error has been detected by the Java Runtime Environment.<br>"
						+ "If you would like to submit a bug report, please visit: http://bugreport.java.com/bugreport/crash.jsp<br>"
						+ "The crash happened outside the Java Virtual Machine in native code.", JOptionPane.ERROR_MESSAGE,
				null, errorCode);
	}

	private static void showGradleBuildFailedErrorDialog(Window whereToShow) {
		Object[] options = { "Open help page", "Do nothing" };
		int reply = JOptionPane.showOptionDialog(whereToShow,
				"<html><b>MCreator detected Gradle failed to properly complete the build.</b><br><br>"
						+ "There are multiple reasons why this can happen. We have compiled a help page<br>"
						+ "that contains list of common causes and solutions for this problem.<br><br>"
						+ "Check the console log for error warnings and compare them with typical examples<br>"
						+ "listed on the help page and do as instructed to resolve this problem.",
				"Gradle build failed", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
				options[0]);
		if (reply == 0) {
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/gradle-setup-errors");
		}
	}

	private static void showErrorDialog(Window window, String msg, int type, Icon icon, int errorCode) {
		String appendx =
				"<small><br><br><font color=gray>ERROR CODE: " + GradleErrorCodes.toString(errorCode) + " [" + errorCode
						+ "]";
		JOptionPane.showMessageDialog(window, msg + appendx, GradleErrorDialogs.MESSAGE_TITLE, type, icon);
	}

}
