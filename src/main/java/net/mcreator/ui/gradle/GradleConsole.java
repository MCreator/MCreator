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

import net.mcreator.Launcher;
import net.mcreator.gradle.*;
import net.mcreator.io.OutputStreamEventHandler;
import net.mcreator.java.ClassFinder;
import net.mcreator.java.DeclarationFinder;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.gradle.ClearAllGradleCachesAction;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.KeyStrokes;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.dialogs.CodeErrorDialog;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.HtmlUtils;
import net.mcreator.util.math.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.internal.impldep.org.apache.commons.lang.exception.ExceptionUtils;
import org.gradle.tooling.*;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GradleConsole extends JPanel {

	private static final Logger LOG = LogManager.getLogger("Gradle Console");

	private static final Color COLOR_TASK_START = new Color(0xBBD9D0);
	private static final Color COLOR_TASK_COMPLETE = new Color(0xbbe86c);
	private static final Color COLOR_UNIMPORTANT = new Color(0x7B7B7B);
	private static final Color COLOR_BRACKET = new Color(0xB0B0B0);
	private static final Color COLOR_LOGLEVEL_TRACE = new Color(0x8abeb7);
	private static final Color COLOR_LOGLEVEL_DEBUG = new Color(0xAABE92);
	private static final Color COLOR_LOGLEVEL_INFO = new Color(0x94BD68);
	private static final Color COLOR_LOGLEVEL_WARN = new Color(0xf0c674);
	private static final Color COLOR_LOGLEVEL_ERROR = new Color(0xF98771);
	private static final Color COLOR_LOGLEVEL_FATAL = new Color(0xcc6666);
	private static final Color COLOR_MARKER_CLIENTSIDE = new Color(0x81BE8D);
	private static final Color COLOR_MARKER_SERVERSIDE = new Color(0x8489A8);
	private static final Color COLOR_MARKER_MAIN = new Color(0x9BB2C7);
	private static final Color COLOR_STDERR = new Color(0x61D0AE);

	private final ConsolePane pan = new ConsolePane();

	private final List<GradleStateListener> stateListeners = new ArrayList<>();

	private final MCreator ref;

	private final JToggleButton slock = new JToggleButton(UIRES.get("16px.lock"));
	private final JToggleButton searchen = new JToggleButton(UIRES.get("16px.search"));

	public static final int READY = 0;
	public static final int RUNNING = 1;
	public static final int ERROR = -1;

	private int status = READY;
	private boolean gradleSetupTaskRunning = false;

	private final ConsoleSearchBar searchBar = new ConsoleSearchBar();

	private CancellationTokenSource cancellationSource = GradleConnector.newCancellationTokenSource();

	// a flag to prevent infinite re-runs in case when re-run does not solve the build problem
	public boolean rerunFlag = false;

	// Gradle console may be associated with a debug client
	@Nullable private JVMDebugClient debugClient = null;

	public GradleConsole(MCreator ref) {
		this.ref = ref;

		pan.addHyperlinkListener(e -> {
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				String url = e.getURL().toString().replace("file:", "");
				String fileurl = url.replaceAll(":[0-9]+", "");
				String[] split = url.split(":");
				int linenum = Integer.parseInt(split[split.length - 1].trim()) - 1;
				File file = new File(fileurl.replaceAll("build[/|\\\\]sources", "src"));
				if (file.exists()) { // we got the data in file format
					ProjectFileOpener.openFileAtLine(ref, file, linenum);
				} else { // we got FQDN instead
					try {
						ProjectJarManager jarManager = ref.getGenerator().getProjectJarManager();
						if (jarManager != null) {
							if (fileurl.contains(
									"/")) { // we don't have just FQDN but also module definition which we need to remove
								fileurl = fileurl.substring(fileurl.lastIndexOf("/") + 1);
							}

							DeclarationFinder.InClassPosition position = ClassFinder.fqdnToInClassPosition(
									ref.getWorkspace(), fileurl, "mod.mcreator", jarManager);

							if (position != null) {
								CodeEditorView codeEditorView = ProjectFileOpener.openFileSpecific(ref,
										position.classFileNode, position.openInReadOnly, position.caret,
										position.virtualFile);
								if (codeEditorView != null)
									codeEditorView.jumpToLine(linenum);
							}
						}
					} catch (SecurityException | IllegalArgumentException ex) {
						LOG.info("Loading JARs for code editor failed. Error: " + ex.getMessage());
					}
				}
			}
		});

		searchBar.reinstall(pan);

		pan.setBorder(BorderFactory.createEmptyBorder(9, 0, 0, 0));

		JScrollPane aae = new JScrollPane(pan, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		aae.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, Theme.current().getSecondAltBackgroundColor()));
		aae.setBackground(Theme.current().getSecondAltBackgroundColor());

		setLayout(new BorderLayout());

		setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, Theme.current().getSecondAltBackgroundColor()));

		searchBar.setVisible(false);

		JPanel outerholder = new JPanel(new BorderLayout());
		outerholder.add("North", searchBar);
		outerholder.add("Center", aae);
		outerholder.setOpaque(false);

		searchBar.setBorder(BorderFactory.createEmptyBorder(6, 10, 5, 0));

		add("Center", outerholder);

		JToolBar options = new JToolBar(null, SwingConstants.VERTICAL);
		options.setFloatable(false);
		options.setBackground(Theme.current().getSecondAltBackgroundColor());

		searchen.setToolTipText(L10N.t("dialog.gradle_console.search"));
		searchen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		options.add(searchen);

		KeyStrokes.registerKeyStroke(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), pan,
				new AbstractAction() {
					@Override public void actionPerformed(ActionEvent actionEvent) {
						searchen.setSelected(true);
					}
				});

		JButton buildbt = new JButton(UIRES.get("16px.build"));
		buildbt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		buildbt.setToolTipText(L10N.t("dialog.gradle_console.start_build"));
		buildbt.setOpaque(false);
		buildbt.addActionListener(e -> ref.actionRegistry.buildWorkspace.doAction());
		options.add(buildbt);

		JButton rungradletask = new JButton(UIRES.get("16px.runtask"));
		rungradletask.setCursor(new Cursor(Cursor.HAND_CURSOR));
		rungradletask.setToolTipText(L10N.t("dialog.gradle_console.run_specific_task"));
		rungradletask.setOpaque(false);
		rungradletask.addActionListener(e -> ref.actionRegistry.runGradleTask.doAction());
		options.add(rungradletask);

		options.add(ComponentUtils.deriveFont(new JLabel(" "), 2));

		JButton cpc = new JButton(UIRES.get("16px.copyclipboard"));
		cpc.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cpc.setToolTipText(L10N.t("dialog.gradle_console.copy_contents_clipboard"));
		cpc.setOpaque(false);
		cpc.addActionListener(e -> {
			StringSelection stringSelection = new StringSelection(getConsoleText());
			Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
			clpbrd.setContents(stringSelection, null);
		});
		options.add(cpc);

		JButton clr = new JButton(UIRES.get("16px.clear"));
		clr.setCursor(new Cursor(Cursor.HAND_CURSOR));
		clr.setToolTipText(L10N.t("dialog.gradle_console.clear"));
		clr.addActionListener(e -> {
			pan.clearConsole();
			searchBar.reinstall(pan);
		});
		clr.setOpaque(false);
		options.add(clr);
		options.add(clr);

		options.add(ComponentUtils.deriveFont(new JLabel(" "), 2));

		slock.setToolTipText(L10N.t("dialog.gradle_console.lock_scroll"));
		slock.setCursor(new Cursor(Cursor.HAND_CURSOR));
		options.add(slock);

		options.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		setBackground(Theme.current().getSecondAltBackgroundColor());
		add("West", options);

		searchen.addChangeListener(e -> searchBar.setVisible(searchen.isSelected()));
		searchBar.addComponentListener(new ComponentAdapter() {
			@Override public void componentHidden(ComponentEvent e) {
				searchen.setSelected(false);
			}
		});
	}

	public String getConsoleText() {
		return HtmlUtils.html2text(pan.getText());
	}

	private void scrollToBottom() {
		if (!slock.isSelected() && pan.isDisplayable()) // check if pan is displayable,
			// so we don't get IllegalComponentStateException: see http://www.oreilly.com/openbook/javawt/book/ch13.pdf, page 467
			pan.setCaretPosition(pan.getDocument().getLength());
	}

	public void addGradleStateListener(GradleStateListener gradleStateListener) {
		stateListeners.add(gradleStateListener);
	}

	public void exec(String command) {
		exec(command, null);
	}

	public void exec(String command, @Nullable GradleTaskFinishedListener taskSpecificListener) {
		exec(command, null, taskSpecificListener);
	}

	public void exec(String command, @Nullable ProgressListener progressListener,
			@Nullable GradleTaskFinishedListener taskSpecificListener) {
		exec(command, taskSpecificListener, progressListener, null);
	}

	public void exec(String command, @Nullable ProgressListener progressListener,
			@Nullable JVMDebugClient jvmDebugClient) {
		exec(command, null, progressListener, jvmDebugClient);
	}

	public void exec(String command, @Nullable GradleTaskFinishedListener taskSpecificListener,
			@Nullable ProgressListener progressListener, @Nullable JVMDebugClient optionalDebugClient) {
		status = RUNNING;

		ref.consoleTab.repaint();
		ref.statusBar.reloadGradleIndicator();
		ref.statusBar.setGradleMessage("Gradle: " + command);
		stateListeners.forEach(listener -> listener.taskStarted(command));

		StringBuffer taskOut = new StringBuffer();
		StringBuffer taskErr = new StringBuffer();

		pan.clearConsole();
		searchBar.reinstall(pan);

		append("Executing Gradle task: " + command, COLOR_TASK_START);

		String java_home = GradleUtils.getJavaHome();

		if (ref.getApplication() != null) {
			String deviceInfo = "Build info: MCreator " + Launcher.version.getFullString() + ", " + ref.getWorkspace()
					.getGenerator().getGeneratorName() + ", " + ref.getApplication().getDeviceInfo().getSystemBits()
					+ "-bit, " + ref.getApplication().getDeviceInfo().getRamAmountMB() + " MB, " + ref.getApplication()
					.getDeviceInfo().getOsName() + ", JVM " + ref.getApplication().getDeviceInfo().getJvmVersion()
					+ ", JAVA_HOME: " + (java_home != null ? java_home : "Default (not set)") + ", started on: "
					+ new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Calendar.getInstance().getTime());
			append(deviceInfo, COLOR_UNIMPORTANT);
			taskOut.append(deviceInfo);

			if (!ref.getWorkspaceSettings().getMCreatorDependencies().isEmpty()) {
				String apiInfo = "Loaded APIs: " + ref.getWorkspaceSettings().getMCreatorDependencies().stream()
						.map(e -> e.split(":")[0]).collect(Collectors.joining(", "));
				append(apiInfo, COLOR_UNIMPORTANT);
				taskOut.append(apiInfo);
			}

			if (PreferencesManager.PREFERENCES.gradle.offline.get()) {
				append("Gradle is running in offline mode. Some features may not work properly!", COLOR_LOGLEVEL_WARN);
			}

			append(" ");
		}

		// reset mod problems
		ref.getWorkspace().resetModElementCompilesStatus();

		long millis = System.currentTimeMillis();

		if (PreferencesManager.PREFERENCES.gradle.offline.get() && gradleSetupTaskRunning) {
			JOptionPane.showMessageDialog(ref, L10N.t("dialog.gradle_console.offline_mode_message"),
					L10N.t("dialog.gradle_console.offline_mode_title"), JOptionPane.WARNING_MESSAGE);
			PreferencesManager.PREFERENCES.gradle.offline.set(false);
		}

		String[] commandTokens = command.split(" ");
		String[] commands = Arrays.stream(commandTokens).filter(e -> !e.contains("--")).toArray(String[]::new);
		List<String> arguments = Arrays.stream(commandTokens).filter(e -> e.contains("--"))
				.collect(Collectors.toList());

		BuildLauncher task = GradleUtils.getGradleTaskLauncher(ref.getWorkspace(), commands);

		if (optionalDebugClient != null) {
			this.debugClient = optionalDebugClient;
			this.debugClient.init(task, cancellationSource.token());
			ref.getDebugPanel().startDebug(this.debugClient);
		}

		if (PreferencesManager.PREFERENCES.gradle.offline.get())
			arguments.add("--offline");

		task.addArguments(arguments);

		task.withCancellationToken(cancellationSource.token());

		task.setStandardOutput(new OutputStreamEventHandler(line -> SwingUtilities.invokeLater(() -> {
			taskOut.append(line).append("\n");

			if (line.startsWith("Note: Some input files use or ov"))
				return;
			if (line.startsWith("Note: Recompile with -Xlint"))
				return;
			if (line.startsWith("Note: Some input files use unch"))
				return;
			if (line.contains("Advanced terminal features are not available in this environment"))
				return;
			if (line.contains("Disabling terminal, you're running in an unsupported environment"))
				return;
			if (line.contains("uses or overrides a deprecated API"))
				return;
			if (line.contains("unchecked or unsafe operations"))
				return;
			if (line.startsWith("Deprecated Gradle features were used"))
				return;
			if (line.startsWith("WARNING: (c) 2020 Microsoft Corporation."))
				return;
			if (line.contains("to show the individual deprecation warnings and determine"))
				return;
			if (line.contains("#sec:command_line_warnings"))
				return;

			if (line.startsWith("WARNING: This project is configured to use the official obfuscation")) {
				append("The code of this workspace uses official obfuscation mappings provided by Mojang. These mappings fall under their associated license you should be fully aware of.",
						COLOR_LOGLEVEL_WARN);
				append("(c) 2020 Microsoft Corporation. These mappings are provided \"as-is\" and you bear the risk of using them. You may copy and use the mappings for development purposes,",
						COLOR_BRACKET);
				append("but you may not redistribute the mappings complete and unmodified. Microsoft makes no warranties, express or implied, with respect to the mappings provided here.",
						COLOR_BRACKET);
				append("Use and modification of this document or the source code (in any form) of Minecraft: Java Edition is governed by the Minecraft End User License Agreement available",
						COLOR_BRACKET);
				append("at https://account.mojang.com/documents/minecraft_eula.", COLOR_BRACKET);
				append(" ");
				return;
			}

			if (line.startsWith(":") || line.startsWith(">")) {
				if (line.contains(" UP-TO-DATE") || line.contains(" NO-SOURCE") || line.contains(" SKIPPED")
						|| line.contains(" FROM-CACHE"))
					appendPlainText(line, COLOR_UNIMPORTANT);
				else
					appendPlainText(line, Theme.current().getForegroundColor());
			} else if (line.startsWith("BUILD SUCCESSFUL")) {
				append(" ");
				appendPlainText(line, COLOR_TASK_COMPLETE);
			} else {
				appendAutoColor(line);
			}
		})));

		task.setStandardError(new OutputStreamEventHandler(line -> SwingUtilities.invokeLater(() -> {
			taskErr.append(line).append("\n");
			if (line.startsWith("[")) {
				appendAutoColor(line);
			} else {
				if (line.startsWith("Note: Some input files use or ov"))
					return;
				if (line.startsWith("Note: Recompile with -Xlint"))
					return;
				if (line.startsWith("Note: Some input files use unch"))
					return;
				if (line.contains("uses or overrides a deprecated API"))
					return;
				if (line.contains("unchecked or unsafe operations"))
					return;
				if (line.startsWith("WARNING: An illegal reflective access"))
					return;
				if (line.startsWith("WARNING: Illegal reflective access"))
					return;
				if (line.startsWith("WARNING: Please consider reporting this"))
					return;
				if (line.startsWith("WARNING: Use --illegal-access=warn to enable"))
					return;
				if (line.startsWith("WARNING: All illegal access operations will"))
					return;
				if (line.startsWith("SLF4J: "))
					return;

				append(line, COLOR_STDERR);
			}
		})));

		task.addProgressListener((ProgressListener) event -> ref.statusBar.setGradleMessage(event.getDescription()));

		if (progressListener != null) {
			task.addProgressListener(progressListener);
		}

		task.run(new ResultHandler<>() {
			@Override public void onComplete(Void result) {
				SwingUtilities.invokeLater(() -> {
					ref.getWorkspace().checkFailingGradleDependenciesAndClear(); // clear flag without checking

					succeed();
					taskComplete(GradleErrorCodes.STATUS_OK);
				});
			}

			@Override public void onFailure(GradleConnectionException failure) {
				SwingUtilities.invokeLater(() -> {
					AtomicBoolean errorhandled = new AtomicBoolean(false);

					boolean workspaceReportedFailingGradleDependencies = ref.getWorkspace()
							.checkFailingGradleDependenciesAndClear();

					if (failure instanceof BuildException) {
						if (GradleErrorDecoder.doesErrorSuggestRerun(taskErr.toString() + taskOut)) {
							if (!rerunFlag) {
								rerunFlag = true;

								LOG.warn("Gradle task suggested re-run. Attempting re-running task: " + command);

								// Re-run the same command with the same listener
								GradleConsole.this.exec(command, taskSpecificListener, progressListener, debugClient);

								return;
							}
						} else if (workspaceReportedFailingGradleDependencies
								|| GradleErrorDecoder.isErrorCausedByCorruptedCaches(taskErr.toString() + taskOut)) {
							AtomicBoolean shouldReturn = new AtomicBoolean(false);
							ThreadUtil.runOnSwingThreadAndWait(() -> {
								Object[] options = { "Clear Gradle caches", "Clear entire Gradle folder",
										"<html><font color=gray>Do nothing" };
								int reply = JOptionPane.showOptionDialog(ref,
										L10N.t("dialog.gradle_console.gradle_caches_corrupted_message"),
										L10N.t("dialog.gradle_console.gradle_caches_corrupted_title"),
										JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
										options[0]);
								if (reply == 0 || reply == 1) {
									taskComplete(GradleErrorCodes.GRADLE_CACHEDATA_ERROR);

									ClearAllGradleCachesAction.clearAllGradleCaches(ref, reply == 1,
											workspaceReportedFailingGradleDependencies);

									shouldReturn.set(true);
								}
								errorhandled.set(true);
							});

							if (shouldReturn.get())
								return;
						} else if (taskErr.toString().contains("compileJava FAILED") || taskOut.toString()
								.contains("compileJava FAILED")) {
							ThreadUtil.runOnSwingThreadAndWait(() -> errorhandled.set(
									CodeErrorDialog.showCodeErrorDialog(ref, taskErr.toString() + taskOut)));
						}
						append(" ");
						append("BUILD FAILED", COLOR_LOGLEVEL_ERROR);
					} else if (failure instanceof BuildCancelledException) {
						append(" ");
						append("TASK CANCELED", COLOR_LOGLEVEL_WARN);
						succeed();
						taskComplete(GradleErrorCodes.STATUS_OK);
						return;
					} else if (failure.getCause().getClass().getSimpleName().equals("DaemonDisappearedException")
							// workaround for MDK bug with gradle daemon
							&& command.startsWith("run")) {
						append(" ");
						append("RUN COMPLETE", COLOR_TASK_COMPLETE);
						succeed();
						taskComplete(GradleErrorCodes.STATUS_OK);
						return;
					} else {
						String exception = ExceptionUtils.getFullStackTrace(failure);
						taskErr.append(exception);

						Arrays.stream(exception.split("\n")).forEach(line -> {
							if (!line.trim().isEmpty())
								append(line);
						});

						append(" ");
						append("TASK EXECUTION FAILED", COLOR_LOGLEVEL_ERROR);
					}

					fail();

					int resultcode = 0;

					if (!errorhandled.get())
						resultcode = GradleErrorDecoder.processErrorAndShowMessage(taskOut.toString(),
								taskErr.toString(), ref);

					if (resultcode == GradleErrorCodes.STATUS_OK)
						resultcode = GradleErrorCodes.GRADLE_BUILD_FAILED;

					taskComplete(resultcode);
				});
			}

			private void fail() {
				status = ERROR;
				ref.consoleTab.repaint();
				ref.statusBar.reloadGradleIndicator();
				ref.statusBar.setGradleMessage(L10N.t("gradle.idle"));
			}

			private void succeed() {
				status = READY;
				ref.consoleTab.repaint();
				ref.statusBar.reloadGradleIndicator();
				ref.statusBar.setGradleMessage(L10N.t("gradle.idle"));

				// on success, we clear the re-run flag
				if (rerunFlag) {
					rerunFlag = false;
					LOG.info("Clearing the re-run flag after a successful re-run");
				}
			}

			private void taskComplete(int mcreatorGradleStatus) {
				appendPlainText("Task completed in " + TimeUtils.millisToLongDHMS(System.currentTimeMillis() - millis),
						Color.gray);
				append(" ");

				if (debugClient != null) {
					ref.getDebugPanel().stopDebug();
					debugClient.stop();
					debugClient = null;
				}

				if (taskSpecificListener != null)
					taskSpecificListener.onTaskFinished(new GradleTaskResult("", mcreatorGradleStatus));

				stateListeners.forEach(
						listener -> listener.taskFinished(new GradleTaskResult("", mcreatorGradleStatus)));

				// reload mods view to display errors
				ref.mv.reloadElementsInCurrentTab();
			}
		});
	}

	public int getStatus() {
		return status;
	}

	public void markRunning() {
		status = RUNNING;
	}

	public void markReady() {
		status = READY;
	}

	public void setGradleSetupTaskRunningFlag(boolean b) {
		gradleSetupTaskRunning = b;
	}

	public boolean isGradleSetupTaskRunning() {
		return gradleSetupTaskRunning;
	}

	public void cancelTask() {
		cancellationSource.cancel();
		cancellationSource = GradleConnector.newCancellationTokenSource();
	}

	public void append(String text) {
		append(text, Theme.current().getForegroundColor());
	}

	private void appendAutoColor(String text) {
		pan.beginTransaction();

		if (!text.isEmpty()) {
			if (!text.endsWith("\n"))
				text = text + "\n";

			if (text.trim().startsWith("[")) {
				String[] bracketsAndText = text.split("]: ", 2);
				String[] data = (bracketsAndText[0] + "]: ").split("] \\[");

				Color threadColorMarker = null;

				for (int i = 0; i < data.length; i++) {
					String bracketText = data[i];
					if (i == 0)
						bracketText = bracketText + "] ";
					else if (i == data.length - 1)
						bracketText = "[" + bracketText;
					else
						bracketText = "[" + bracketText + "] ";

					Color bracketColor = Theme.current().getForegroundColor();

					// default bracket color
					if (bracketText.contains("]") && bracketText.contains("["))
						bracketColor = COLOR_BRACKET;

					// timestamp color
					if (bracketText.contains(":") && !bracketText.contains("]: ")) {
						bracketColor = COLOR_UNIMPORTANT;
					} else if (threadColorMarker == null) { // handle log levels
						if (bracketText.contains("/TRACE]"))
							bracketColor = COLOR_LOGLEVEL_TRACE;
						else if (bracketText.contains("/DEBUG]"))
							bracketColor = COLOR_LOGLEVEL_DEBUG;
						else if (bracketText.contains("/INFO]"))
							bracketColor = COLOR_LOGLEVEL_INFO;
						else if (bracketText.contains("/WARN]"))
							bracketColor = COLOR_LOGLEVEL_WARN;
						else if (bracketText.contains("/ERROR]") || bracketText.contains("STDERR]"))
							bracketColor = COLOR_LOGLEVEL_ERROR;
						else if (bracketText.contains("/FATAL]"))
							bracketColor = COLOR_LOGLEVEL_FATAL;
					} else {
						bracketColor = threadColorMarker;
						threadColorMarker = null;
					}

					// special bracket colors
					if (bracketText.contains("Client") || bracketText.contains("Render"))
						threadColorMarker = COLOR_MARKER_CLIENTSIDE;
					else if (bracketText.contains("Server"))
						threadColorMarker = COLOR_MARKER_SERVERSIDE;
					else if (bracketText.contains("main/"))
						threadColorMarker = COLOR_MARKER_MAIN;

					SimpleAttributeSet keyWord = new SimpleAttributeSet();
					StyleConstants.setForeground(keyWord, bracketColor);
					pan.insertString(bracketText, keyWord);
				}

				if (bracketsAndText.length > 1)
					append(bracketsAndText[1], Theme.current().getForegroundColor());
			} else {
				append(text, Theme.current().getForegroundColor());
			}
		}

		pan.endTransaction();

		scrollToBottom();
	}

	private final Pattern cepattern = Pattern.compile("\\.java:\\d+: error:");
	private final Pattern cwpattern = Pattern.compile("\\.java:\\d+: warning:");
	private final Pattern repattern = Pattern.compile("\\(.*\\.java:\\d+\\)");

	public void append(String text, Color c) {
		if (cepattern.matcher(text).find()) {
			appendErrorWithCodeLine(text);
		} else if (repattern.matcher(text).find()) {
			appendErrorWithCodeLine2(text);
		} else if (cwpattern.matcher(text).find()) {
			appendPlainText(text, COLOR_LOGLEVEL_WARN);
		} else {
			appendPlainText(text, c);
		}
	}

	private void appendErrorWithCodeLine(String text) {
		if (!text.isEmpty()) {
			String err = text.replaceAll(": error:.*", "");
			String othr = text.replaceAll(".+\\.java:\\d+", "") + "\n";
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, COLOR_LOGLEVEL_ERROR);
			pan.insertLink(err.trim(), err.trim(), othr, keyWord);
		}
		scrollToBottom();
	}

	private final Pattern jpattern = Pattern.compile("\\((.+?)\\.java:\\d+\\)");

	private void appendErrorWithCodeLine2(String text) {
		if (!text.isEmpty()) {
			try {
				Matcher matcher = jpattern.matcher(text);
				matcher.find();
				String crashClassName = matcher.group(1);
				String packageName = text.split("at ")[1].split("\\." + crashClassName)[0];
				String classLine = text.split("\\.java:")[1].split("\\)")[0];

				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setForeground(keyWord, Theme.current().getForegroundColor());
				pan.insertString(text.split("\\(")[0] + "(", keyWord);
				StyleConstants.setForeground(keyWord, Theme.current().getInterfaceAccentColor());
				pan.insertLink(packageName + "." + crashClassName + ":" + classLine,
						text.split("\\(")[1].split("\\)")[0], "", keyWord);
				StyleConstants.setForeground(keyWord, Theme.current().getForegroundColor());
				pan.insertString(")" + text.split("\\(")[1].split("\\)")[1], keyWord);
			} catch (Exception ignored) {  // workspace can be null or we can fail to parse error link
				// if we fail to print styled, fallback to plaintext
				appendPlainText(text, Theme.current().getForegroundColor());
			}
			scrollToBottom();
		}
	}

	public void appendPlainText(String text, Color c) {
		if (!text.isEmpty()) {
			if (!text.endsWith("\n"))
				text = text + "\n";
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setForeground(keyWord, c);
			pan.insertString(text, keyWord);
		}
		scrollToBottom();
	}

	@Nullable public JVMDebugClient getDebugClient() {
		return debugClient;
	}

}