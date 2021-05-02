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
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.action.impl.gradle.ClearAllGradleCachesAction;
import net.mcreator.ui.component.ConsolePane;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.KeyStrokes;
import net.mcreator.ui.dialogs.CodeErrorDialog;
import net.mcreator.ui.ide.CodeEditorView;
import net.mcreator.ui.ide.ProjectFileOpener;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.util.HtmlUtils;
import net.mcreator.util.math.TimeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.internal.impldep.org.apache.commons.lang.exception.ExceptionUtils;
import org.gradle.tooling.*;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GradleConsole extends JPanel {

	private static final Logger LOG = LogManager.getLogger("Gradle Console");

	ConsolePane pan = new ConsolePane();

	private final List<GradleStateListener> stateListeners = new ArrayList<>();

	private final MCreator ref;

	private final JToggleButton sinfo = new JToggleButton(UIRES.get("16px.sinfo"));
	private final JToggleButton serr = new JToggleButton(UIRES.get("16px.serr"));
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

	public GradleConsole(MCreator ref) {
		this.ref = ref;

		JPanel holder = new JPanel(new BorderLayout());
		setLayout(new BorderLayout());
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
							DeclarationFinder.InClassPosition position = ClassFinder
									.fqdnToInClassPosition(ref.getWorkspace(), fileurl, "mod.mcreator", jarManager);

							if (position != null) {
								CodeEditorView codeEditorView = ProjectFileOpener
										.openFileSpecific(ref, position.classFileNode, position.openInReadOnly,
												position.carret, position.virtualFile);
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

		JScrollPane aae = new JScrollPane(pan, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		aae.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"), aae.getVerticalScrollBar()));
		aae.getVerticalScrollBar().setPreferredSize(new Dimension(7, 0));
		aae.getHorizontalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.DARK_ACCENT"), aae.getHorizontalScrollBar()));
		aae.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 7));
		aae.setBorder(BorderFactory.createMatteBorder(0, 10, 0, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));
		aae.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		holder.setBorder(
				BorderFactory.createMatteBorder(0, 5, 0, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));

		searchBar.setVisible(false);

		JPanel outerholder = new JPanel(new BorderLayout());
		outerholder.add("North", searchBar);
		outerholder.add("Center", aae);
		outerholder.setOpaque(false);

		searchBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));

		holder.add("Center", outerholder);

		JPanel bar = new JPanel();
		bar.setLayout(new BoxLayout(bar, BoxLayout.LINE_AXIS));
		bar.setBackground(Color.gray);

		JButton x = L10N.button("dialog.gradle_console.clear_log");
		x.setMargin(new Insets(1, 1, 1, 1));

		JToolBar options = new JToolBar(null, SwingConstants.VERTICAL);
		options.setFloatable(false);
		options.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		searchen.setToolTipText(L10N.t("dialog.gradle_console.search"));
		searchen.setCursor(new Cursor(Cursor.HAND_CURSOR));
		options.add(searchen);

		KeyStrokes.registerKeyStroke(
				KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()), pan,
				new AbstractAction() {
					@Override public void actionPerformed(ActionEvent actionEvent) {
						searchen.setSelected(true);
					}
				});

		JButton buildbt = new JButton(UIRES.get("16px.build"));
		buildbt.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.normalizeButton2(buildbt);
		buildbt.setToolTipText(L10N.t("dialog.gradle_console.start_build"));
		buildbt.setOpaque(false);
		buildbt.addActionListener(e -> ref.actionRegistry.buildWorkspace.doAction());
		options.add(buildbt);

		JButton rungradletask = new JButton(UIRES.get("16px.runtask"));
		rungradletask.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.normalizeButton2(rungradletask);
		rungradletask.setToolTipText(L10N.t("dialog.gradle_console.run_specific_task"));
		rungradletask.setOpaque(false);
		rungradletask.addActionListener(e -> ref.actionRegistry.runGradleTask.doAction());
		options.add(rungradletask);

		options.add(ComponentUtils.deriveFont(new JLabel(" "), 2));

		JButton cpc = new JButton(UIRES.get("16px.copyclipboard"));
		cpc.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ComponentUtils.normalizeButton2(cpc);
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
		ComponentUtils.normalizeButton2(clr);
		clr.setToolTipText(L10N.t("dialog.gradle_console.clear"));
		clr.addActionListener(e -> {
			pan.clearConsole();
			searchBar.reinstall(pan);
		});
		clr.setOpaque(false);
		options.add(clr);
		options.add(clr);

		options.add(ComponentUtils.deriveFont(new JLabel(" "), 2));

		sinfo.setToolTipText(L10N.t("dialog.gradle_console.show_info_log"));
		sinfo.setCursor(new Cursor(Cursor.HAND_CURSOR));
		sinfo.setSelected(true);
		options.add(sinfo);

		serr.setToolTipText(L10N.t("dialog.gradle_console.show_errors"));
		serr.setCursor(new Cursor(Cursor.HAND_CURSOR));
		serr.setSelected(true);
		options.add(serr);

		slock.setToolTipText(L10N.t("dialog.gradle_console.lock_scroll"));
		slock.setCursor(new Cursor(Cursor.HAND_CURSOR));
		options.add(slock);

		options.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

		holder.add("West", options);

		holder.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		add("Center", holder);

		searchen.addChangeListener(e -> searchBar.setVisible(searchen.isSelected()));

		ComponentUtils.normalizeButton2(sinfo);
		ComponentUtils.normalizeButton2(serr);
		ComponentUtils.normalizeButton2(slock);
		ComponentUtils.normalizeButton2(searchen);
	}

	public String getConsoleText() {
		String retval = pan.getText().replaceAll("(?i)<br[^>]* */?>", System.lineSeparator()).replaceAll("<.*?>", "")
				.trim();
		return HtmlUtils.unescapeHtml(retval).trim();
	}

	private void scrollToBottom() {
		if (!slock.isSelected() && pan.isDisplayable()) // check if pan is displayable
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
		status = RUNNING;

		ref.consoleTab.repaint();
		ref.statusBar.reloadGradleIndicator();
		ref.statusBar.setGradleMessage("Gradle: " + command);
		stateListeners.forEach(listener -> listener.taskStarted(command));

		StringBuffer taskOut = new StringBuffer();
		StringBuffer taskErr = new StringBuffer();

		pan.clearConsole();
		searchBar.reinstall(pan);

		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		StyleConstants.setFontSize(keyWord, 4);
		pan.insertString("\n", keyWord);

		append("Executing Gradle task: " + command, new Color(0xBBD9D0));

		String java_home = GradleUtils.getJavaHome();

		if (ref.getApplication() != null) {
			String deviceInfo = "Build info: MCreator " + Launcher.version.getFullString() + ", " + ref.getWorkspace()
					.getGenerator().getGeneratorName() + ", " + ref.getApplication().getDeviceInfo().getSystemBits()
					+ "-bit, " + ref.getApplication().getDeviceInfo().getRamAmountMB() + " MB, " + ref.getApplication()
					.getDeviceInfo().getOsName() + ", JVM " + ref.getApplication().getDeviceInfo().getJvmVersion()
					+ ", JAVA_HOME: " + (java_home != null ? java_home : "Default (not set)");
			append(deviceInfo, new Color(127, 120, 120));
			taskOut.append(deviceInfo);
		}

		// reset mod problems
		ref.getWorkspace().resetModElementCompilesStatus();

		long millis = System.currentTimeMillis();

		if (PreferencesManager.PREFERENCES.gradle.offline && gradleSetupTaskRunning) {
			JOptionPane.showMessageDialog(ref, L10N.t("dialog.gradle_console.offline_mode_message"),
					L10N.t("dialog.gradle_console.offline_mode_title"), JOptionPane.WARNING_MESSAGE);
			PreferencesManager.PREFERENCES.gradle.offline = false;
		}

		String[] commandTokens = command.split(" ");
		String[] commands = Arrays.stream(commandTokens).filter(e -> !e.contains("--")).toArray(String[]::new);
		List<String> arguments = Arrays.stream(commandTokens).filter(e -> e.contains("--"))
				.collect(Collectors.toList());

		BuildLauncher task = GradleUtils.getGradleTaskLauncher(ref.getWorkspace(), commands);

		if (PreferencesManager.PREFERENCES.gradle.offline)
			arguments.add("--offline");

		task.withArguments(arguments);

		task.withCancellationToken(cancellationSource.token());

		task.setStandardOutput(new OutputStreamEventHandler(line -> SwingUtilities.invokeLater(() -> {
			taskOut.append(line).append("\n");
			if (sinfo.isSelected()) {
				if (!line.startsWith("Note: Some input files use or ov"))
					if (!line.startsWith("Note: Recompile with -Xlint"))
						if (!line.startsWith("Note: Some input files use unch"))
							if (!line.contains("Advanced terminal features are not available in this environment"))
								if (!line.contains("Disabling terminal, you're running in an unsupported environment"))
									if (!line.contains("uses or overrides a deprecated API"))
										if (!line.contains("unchecked or unsafe operations")) {
											if (line.startsWith(":") || line.startsWith(">")) {
												if (line.contains(" UP-TO-DATE") || line.contains(" NO-SOURCE") || line
														.contains(" SKIPPED"))
													append(line, new Color(0x7B7B7B), true);
												else
													append(line, new Color(0xDADADA), true);
											} else if (line.startsWith("BUILD SUCCESSFUL")) {
												append(line, new Color(187, 232, 108), false);
											} else {
												appendAutoColor(line);
											}
										}
			}
		})));

		task.setStandardError(new OutputStreamEventHandler(line -> SwingUtilities.invokeLater(() -> {
			taskErr.append(line).append("\n");
			if (serr.isSelected()) {
				if (line.startsWith("[")) {
					appendAutoColor(line);
				} else {
					if (!line.startsWith("Note: Some input files use or ov"))
						if (!line.startsWith("Note: Recompile with -Xlint"))
							if (!line.startsWith("Note: Some input files use unch"))
								if (!line.contains("uses or overrides a deprecated API"))
									if (!line.contains("unchecked or unsafe operations"))
										append(line, new Color(0, 255, 182));
				}
			}
		})));

		task.addProgressListener((ProgressListener) event -> ref.statusBar.setGradleMessage(event.getDescription()));

		task.run(new ResultHandler<Void>() {
			@Override public void onComplete(Void result) {
				SwingUtilities.invokeLater(() -> {
					ref.getWorkspace().checkFailingGradleDependenciesAndClear(); // clear flag without checking

					succeed();
					taskComplete(GradleErrorCodes.STATUS_OK);
				});
			}

			@Override public void onFailure(GradleConnectionException failure) {
				SwingUtilities.invokeLater(() -> {
					boolean errorhandled = false;

					boolean workspaceReportedFailingGradleDependencies = ref.getWorkspace()
							.checkFailingGradleDependenciesAndClear();

					if (failure instanceof BuildException) {
						if (GradleErrorDecoder.doesErrorSuggestRerun(taskErr.toString() + taskOut)) {
							if (!rerunFlag) {
								rerunFlag = true;

								LOG.warn("Gradle task suggested re-run. Attempting re-running task: " + command);

								// Re-run the same command with the same listener
								GradleConsole.this.exec(command, taskSpecificListener);

								return;
							}
						} else if (workspaceReportedFailingGradleDependencies || GradleErrorDecoder
								.isErrorCausedByCorruptedCaches(taskErr.toString() + taskOut)) {
							Object[] options = { "Clear Gradle caches", "Clear entire Gradle folder",
									"<html><font color=gray>Do nothing" };
							int reply = JOptionPane.showOptionDialog(ref,
									L10N.t("dialog.gradle_console.gradle_caches_corrupted_message"),
									L10N.t("dialog.gradle_console.gradle_caches_corrupted_title"),
									JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
							if (reply == 0 || reply == 1) {
								taskComplete(GradleErrorCodes.GRADLE_CACHEDATA_ERROR);

								ClearAllGradleCachesAction.clearAllGradleCaches(ref, reply == 1,
										workspaceReportedFailingGradleDependencies);

								return;
							}
							errorhandled = true;
						} else if (taskErr.toString().contains("compileJava FAILED") || taskOut.toString()
								.contains("compileJava FAILED")) {
							errorhandled = CodeErrorDialog.showCodeErrorDialog(ref, taskErr.toString() + taskOut);
						}
						append("BUILD FAILED", new Color(0xF98771));
					} else if (failure instanceof BuildCancelledException) {
						append("TASK CANCELED", new Color(0xF5F984));
						succeed();
						taskComplete(GradleErrorCodes.STATUS_OK);
						return;
					} else if (failure.getCause().getClass().getSimpleName().equals("DaemonDisappearedException")
							// workaround for MDK bug with gradle daemon
							&& command.startsWith("run")) {
						append("RUN COMPLETE", new Color(0, 255, 182));
						succeed();
						taskComplete(GradleErrorCodes.STATUS_OK);
						return;
					} else {
						String exception = ExceptionUtils.getFullStackTrace(failure);
						taskErr.append(exception);

						if (serr.isSelected()) {
							Arrays.stream(exception.split("\n")).forEach(line -> {
								if (!line.trim().isEmpty())
									append(line);
							});
						}
						append("TASK EXECUTION FAILED", new Color(0xF98771));
					}

					fail();

					int resultcode = 0;

					if (!errorhandled)
						resultcode = GradleErrorDecoder
								.processErrorAndShowMessage(taskOut.toString(), taskErr.toString(), ref);

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
				append("Task completed in " + TimeUtils.millisToLongDHMS(System.currentTimeMillis() - millis),
						Color.gray, true);

				if (taskSpecificListener != null)
					taskSpecificListener.onTaskFinished(new GradleTaskResult("", mcreatorGradleStatus));

				stateListeners
						.forEach(listener -> listener.taskFinished(new GradleTaskResult("", mcreatorGradleStatus)));

				// reload mods view to display errors
				ref.mv.updateMods();
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
		append(text, (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
	}

	private Color textAccent = null;

	private void appendAutoColor(String text) {
		pan.beginTransaction();

		if (!text.equals("")) {
			Color c = (Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR");

			if (!text.endsWith("\n"))
				text = text + "\n";

			if (text.trim().startsWith("[")) {
				textAccent = null;

				String[] data = text.split("] \\[");
				String logText = "";

				for (int i = 0; i < data.length; i++) {
					String bracketText = data[i];
					if (i == 0)
						bracketText = bracketText + "] ";
					else if (i == data.length - 1)
						bracketText = "[" + bracketText;
					else
						bracketText = "[" + bracketText + "] ";

					Color c2 = c;

					// default bracket color
					if (bracketText.contains("]") && bracketText.contains("["))
						c2 = new Color(239, 239, 239);

					// format timestamps
					if (bracketText.contains(":") && !bracketText.contains("]:")) {
						c2 = new Color(0x95A0A7);
						bracketText = bracketText.replace("[", "").replace("]", "");
						String[] tstmp = bracketText.split(":");
						if (tstmp.length == 3) {
							if (!tstmp[0].replaceAll("\\p{C}", "").equals(tstmp[0]) && tstmp[0].contains("m"))
								tstmp[0] = tstmp[0].split("m")[1].replaceAll("\\p{C}", "");
							bracketText = (tstmp[0] + ":" + tstmp[1] + "." + tstmp[2]);
						}
					}

					// special bracket colors
					if (bracketText.contains("Client") || bracketText.contains("Render"))
						c2 = new Color(0xB3A7D0);
					else if (bracketText.contains("Server"))
						c2 = new Color(0x7CD48B);
					else if (bracketText.contains("main/"))
						c2 = new Color(0xAAB490);
					else if (bracketText.contains("LaunchWrapper]") || bracketText.contains("FML]") || bracketText
							.contains("modloading-worker"))
						c2 = new Color(0xB5D7C3);

					// handle log levels
					if (textAccent == null && bracketText.contains("/TRACE]"))
						textAccent = new Color(0x666666);
					else if (textAccent == null && bracketText.contains("/DEBUG]"))
						textAccent = new Color(0xA3A3A3);
					else if (textAccent == null && bracketText.contains("/WARN]"))
						textAccent = new Color(0xDED6C5);
					else if (textAccent == null && bracketText.contains("/ERROR]") || bracketText.contains("STDERR]"))
						textAccent = new Color(0xFF9696);
					else if (textAccent == null && bracketText.contains("/FATAL]"))
						textAccent = new Color(0xFF5F5F);

					String[] spl = bracketText.split("]:");

					if (spl.length > 1) {
						logText = Arrays.stream(spl).skip(1).collect(Collectors.joining(""));
						bracketText = spl[0] + "]:";
					} else
						bracketText = spl[0];

					SimpleAttributeSet keyWord = new SimpleAttributeSet();
					StyleConstants.setFontSize(keyWord, 9);

					if (bracketText.contains(":") && !bracketText.contains("]:"))
						StyleConstants.setFontSize(keyWord, 6);

					StyleConstants.setForeground(keyWord, c2);
					StyleConstants.setBackground(keyWord, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

					if (bracketText.matches("\\[(\\w{2}\\.)+\\w+/\\w+]:")) {
						pan.insertString(bracketText.replaceAll("(\\w{2}\\.)", ""), keyWord);
					} else {
						pan.insertString(bracketText, keyWord);
					}

				}

				append(logText, textAccent != null ? textAccent : c);
			} else {
				append(text, textAccent != null ? textAccent : c);
			}
		}

		pan.endTransaction();

		scrollToBottom();
	}

	private final Pattern cepattern = Pattern.compile("\\.java:\\d+: error:");
	private final Pattern cwpattern = Pattern.compile("\\.java:\\d+: warning:");
	private final Pattern repattern = Pattern.compile("\\(.*\\.java:\\d+\\)");

	public void append(String text, Color c) {
		Matcher compileError = cepattern.matcher(text);
		Matcher compileWarning = cwpattern.matcher(text);
		Matcher runtimeError = repattern.matcher(text);

		if (compileError.find()) {
			appendErrorWithCodeLine(text);
		} else if (runtimeError.find()) {
			appendErrorWithCodeLine2(text);
		} else if (compileWarning.find()) {
			append(text, new Color(0xF9CD85), false);
		} else {
			append(text, c, false);
		}
	}

	private void appendErrorWithCodeLine(String text) {
		if (!text.equals("")) {
			String err = text.replaceAll(": error:.*", "");
			String othr = text.replaceAll(".+\\.java:\\d+", "") + "\n";
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setFontSize(keyWord, 9);
			StyleConstants.setForeground(keyWord, new Color(0xF98771));
			StyleConstants.setBackground(keyWord, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			pan.insertLink(err.trim(), err.trim(), othr, keyWord);
		}
		scrollToBottom();
	}

	private final Pattern jpattern = Pattern.compile("\\((.+?)\\.java:\\d+\\)");

	private void appendErrorWithCodeLine2(String text) {
		if (!text.equals("")) {
			try {
				Matcher matcher = jpattern.matcher(text);
				matcher.find();
				String crashClassName = matcher.group(1);
				String packageName = text.split("at ")[1].split("\\." + crashClassName)[0];
				String classLine = text.split("\\.java:")[1].split("\\)")[0];

				SimpleAttributeSet keyWord = new SimpleAttributeSet();
				StyleConstants.setFontSize(keyWord, 9);
				StyleConstants.setForeground(keyWord, Color.white);
				StyleConstants.setBackground(keyWord, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
				pan.insertString(text.split("\\(")[0] + "(", keyWord);
				StyleConstants.setForeground(keyWord, new Color(0xE0F3A9));
				pan.insertLink(packageName + "." + crashClassName + ":" + classLine,
						text.split("\\(")[1].split("\\)")[0], "", keyWord);
				StyleConstants.setForeground(keyWord, Color.white);
				pan.insertString(")" + text.split("\\(")[1].split("\\)")[1], keyWord);
			} catch (Exception ignored) {  // workspace can be null or we can fail to parse error link
				// if we fail to print styled, fallback to plaintext
				append(text, Color.white, false);
			}
			scrollToBottom();
		}
	}

	public void append(String text, Color c, boolean a) {
		if (!text.equals("")) {
			if (!text.endsWith("\n"))
				text = text + "\n";
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			StyleConstants.setFontSize(keyWord, 9);
			StyleConstants.setItalic(keyWord, a);
			StyleConstants.setForeground(keyWord, c);
			StyleConstants.setBackground(keyWord, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			pan.insertString("" + text, keyWord);
		}
		scrollToBottom();
	}

}