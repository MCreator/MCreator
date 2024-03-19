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

package net.mcreator.ui.ide;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.mcreator.io.FileIO;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.java.CodeCleanup;
import net.mcreator.java.DeclarationFinder;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JFileBreadCrumb;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.KeyStrokes;
import net.mcreator.ui.component.util.ThreadUtil;
import net.mcreator.ui.ide.autocomplete.CustomJSCCache;
import net.mcreator.ui.ide.autocomplete.StringCompletitionProvider;
import net.mcreator.ui.ide.debug.BreakpointHandler;
import net.mcreator.ui.ide.json.JsonTree;
import net.mcreator.ui.ide.mcfunction.MinecraftCommandsTokenMaker;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.FileIcons;
import net.mcreator.ui.laf.renderer.AstTreeCellRendererCustom;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.rsta.ac.LanguageSupportFactory;
import org.fife.rsta.ac.java.JavaCompletionProvider;
import org.fife.rsta.ac.java.JavaLanguageSupport;
import org.fife.rsta.ac.java.JavaParser;
import org.fife.rsta.ac.java.tree.JavaOutlineTree;
import org.fife.rsta.ac.js.JavaScriptLanguageSupport;
import org.fife.rsta.ac.js.tree.JavaScriptOutlineTree;
import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rsyntaxtextarea.focusabletip.FocusableTip;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class CodeEditorView extends ViewBase {

	private static final Logger LOG = LogManager.getLogger("Code Editor");

	public SearchBar sed;
	public ReplaceBar rep;

	private final JSplitPane spne = new JSplitPane();

	private final JScrollPane treeSP = new JScrollPane();
	private AbstractSourceTree tree;
	public ChangeListener cl;

	private final RTextScrollPane sp;

	public final RSyntaxTextArea te = new RSyntaxTextArea() {
		@Override public void setCursor(Cursor c) {
			if (jumpToMode)
				return;
			if (c != null && !c.equals(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)))
				super.setCursor(c);
		}
	};

	public File fileWorkingOn;

	public boolean changed = false;

	private AutoCompletion ac = null;

	private boolean jumpToMode = false;
	public MouseEvent mouseEvent;

	private final JLabel ro = new JLabel();

	public final boolean readOnly;

	private final CodeCleanup codeCleanup;

	private final JFileBreadCrumb fileBreadCrumb;

	@Nullable private ModElement fileOwner = null;

	@Nullable private JavaParser parser = null;

	@Nullable private BreakpointHandler breakpointHandler = null;

	public CodeEditorView(MCreator fa, File fs) {
		this(fa, FileIO.readFileToString(fs), fs.getName(), fs, false);
	}

	public CodeEditorView(MCreator fa, String code, String fileName, File fileWorkingOn, boolean readOnly) {
		super(fa);

		this.fileWorkingOn = fileWorkingOn;
		if (this.fileWorkingOn == null)
			this.fileWorkingOn = new File(fileName);

		this.readOnly = readOnly;

		this.codeCleanup = new CodeCleanup();

		setBackground(Theme.current().getBackgroundColor());

		this.fileBreadCrumb = new JFileBreadCrumb(mcreator, fileWorkingOn, fa.getWorkspaceFolder());

		te.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent focusEvent) {
				super.focusGained(focusEvent);
				fileBreadCrumb.reloadPath(fileWorkingOn);
				te.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			}
		});

		LanguageSupportFactory.get().register(te);

		sed = new SearchBar(te);
		rep = new ReplaceBar(te);
		te.setText(code);

		if (readOnly) {
			te.setEditable(false);
		}

		te.requestFocusInWindow();
		te.setMarkOccurrences(true);
		te.setCodeFoldingEnabled(true);
		te.setClearWhitespaceLinesEnabled(true);
		te.setAutoIndentEnabled(true);

		te.setTabSize(4);

		ToolTipManager.sharedInstance().registerComponent(te);

		sp = new RTextScrollPane(te, PreferencesManager.PREFERENCES.ide.lineNumbers.get());

		RSyntaxTextAreaStyler.style(te, sp, PreferencesManager.PREFERENCES.ide.fontSize.get());

		sp.setFoldIndicatorEnabled(true);

		sp.getGutter().setFoldBackground(getBackground());
		sp.getGutter().setBorderColor(getBackground());

		sp.setIconRowHeaderEnabled(true);

		sp.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		sp.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
		sp.setBorder(null);

		treeSP.setCorner(JScrollPane.LOWER_RIGHT_CORNER, new JPanel());
		treeSP.setCorner(JScrollPane.LOWER_LEFT_CORNER, new JPanel());
		treeSP.setBorder(null);

		te.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent documentEvent) {
				if (!changed && !readOnly) {
					changed = true;
					if (cl != null)
						cl.stateChanged(new ChangeEvent(this));
				}
			}

			@Override public void removeUpdate(DocumentEvent documentEvent) {
				if (!changed && !readOnly) {
					changed = true;
					if (cl != null)
						cl.stateChanged(new ChangeEvent(this));
				}

			}

			@Override public void changedUpdate(DocumentEvent documentEvent) {
			}
		});

		sp.setOpaque(false);

		spne.setRightComponent(new JPanel());

		JPanel cp = new JPanel(new BorderLayout());
		cp.setBackground(Theme.current().getBackgroundColor());
		cp.add(sp);

		if (PreferencesManager.PREFERENCES.ide.errorInfoEnable.get()) {
			ErrorStrip errorStrip = new ErrorStrip(te);
			errorStrip.setFollowCaret(false);
			errorStrip.setBackground(Theme.current().getBackgroundColor());
			cp.add(errorStrip, BorderLayout.LINE_END);
		}

		spne.setLeftComponent(cp);
		spne.setContinuousLayout(true);
		spne.setBorder(null);

		JPanel bars = new JPanel(new BorderLayout(2, 2));
		ComponentUtils.deriveFont(ro, 12);
		ro.setOpaque(true);
		Border margin = new EmptyBorder(3, 5, 3, 3);
		ro.setBorder(new CompoundBorder(ro.getBorder(), margin));
		ro.setVisible(false);

		ro.setCursor(new Cursor(Cursor.HAND_CURSOR));
		ro.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				ro.setVisible(false);
			}
		});

		if (!readOnly) {
			bars.add("North", ro);
			bars.add("Center", sed);
			bars.add("South", rep);
		} else {
			ro.setText(L10N.t("ide.warnings.read_only"));
			bars.add("North", ro);
			bars.add("South", sed);
			ro.setVisible(true);
		}

		sed.setVisible(false);
		rep.setVisible(false);

		JPanel topPan = new JPanel(new BorderLayout());
		topPan.setOpaque(false);
		topPan.add("Center", bars);
		topPan.add("North", fileBreadCrumb);

		add("North", topPan);
		add("Center", spne);
		setBorder(null);

		if (!readOnly) {
			KeyStrokes.registerKeyStroke(
					KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							saveCode();
							fa.actionRegistry.buildWorkspace.doAction();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										L10N.t("ide.tips.save_and_build"));
						}
					});

			KeyStrokes.registerKeyStroke(
					KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							reformatTheCodeOrganiseAndFixImports();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										L10N.t("ide.tips.reformat_and_organize_imports"));
						}
					});

			KeyStrokes.registerKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_M,
							Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK, false), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							saveCode();
							fa.actionRegistry.runClient.doAction();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										L10N.t("ide.tips.save_and_launch"));
						}
					});

			KeyStrokes.registerKeyStroke(KeyStroke.getKeyStroke(KeyEvent.VK_D,
							Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx() | InputEvent.SHIFT_DOWN_MASK, false), te,
					new AbstractAction() {
						@Override public void actionPerformed(ActionEvent actionEvent) {
							disableJumpToMode();
							saveCode();
							fa.actionRegistry.debugClient.doAction();
							if (CodeEditorView.this.mouseEvent != null)
								new FocusableTip(te, null).toolTipRequested(CodeEditorView.this.mouseEvent,
										L10N.t("ide.tips.save_and_debug"));
						}
					});
		}

		spne.setResizeWeight(1);

		int posit = te.getText().indexOf("public class");
		if (posit < 0)
			posit = 0;

		te.setHyperlinksEnabled(false);
		te.setCaretPosition(posit);

		te.discardAllEdits();

		new Thread(() -> setupCodeSupport(fileName), "CodeSupport-Loader").start();
	}

	private void setupCodeSupport(String fileName) {
		if (fileName.endsWith(".java")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA));

			JavaLanguageSupport jls = new JavaLanguageSupport();
			jls.setAutoCompleteEnabled(PreferencesManager.PREFERENCES.ide.autocomplete.get());
			jls.setAutoActivationEnabled(!PreferencesManager.PREFERENCES.ide.autocompleteMode.get().equals("Manual"));
			jls.setParameterAssistanceEnabled(true);
			jls.setShowDescWindow(PreferencesManager.PREFERENCES.ide.autocompleteDocWindow.get());

			try {
				Field field = jls.getClass().getDeclaredField("jarManager");
				field.setAccessible(true);
				field.set(jls, mcreator.getGenerator().getProjectJarManager());
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			jls.install(te);

			try {
				Class<?> treeNodeClass = Class.forName("org.fife.rsta.ac.AbstractLanguageSupport");
				Method method = treeNodeClass.getDeclaredMethod("getAutoCompletionFor", RSyntaxTextArea.class);
				method.setAccessible(true);
				ac = (AutoCompletion) method.invoke(jls, te);
			} catch (ClassNotFoundException | SecurityException | InvocationTargetException | IllegalArgumentException |
					 NoSuchMethodException | IllegalAccessException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			JavaCompletionProvider jcp = jls.getCompletionProvider(te);

			try {
				Field field = jcp.getClass().getDeclaredField("sourceProvider");
				field.setAccessible(true);
				DefaultCompletionProvider sourceCompletionProvider = (DefaultCompletionProvider) field.get(jcp);
				jcp.setShorthandCompletionCache(
						new CustomJSCCache(sourceCompletionProvider, new DefaultCompletionProvider()));
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException e1) {
				LOG.error(e1.getMessage(), e1);
			}

			jcp.setStringCompletionProvider(new StringCompletitionProvider(mcreator.getWorkspace()));

			if (ac != null)
				AutocompleteStyle.installStyle(ac, te);

			this.parser = jls.getParser(te);

			this.breakpointHandler = new BreakpointHandler(this, sp, parser);

			te.addKeyListener(new KeyAdapter() {

				private boolean completionInAction = false;

				@Override public void keyPressed(KeyEvent keyEvent) {
					super.keyPressed(keyEvent);
					if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
						te.setCursor(new Cursor(Cursor.HAND_CURSOR));
						jumpToMode = true;
					} else if (PreferencesManager.PREFERENCES.ide.autocompleteMode.get().equals("Smart")
							&& !completionInAction && jls.isAutoActivationEnabled() && Character.isLetterOrDigit(
							keyEvent.getKeyChar()) && jcp.getAlreadyEnteredText(te).length() > 1) {
						if (!completionInAction) {
							new Thread(() -> {
								if (ac != null) {
									completionInAction = true;
									ThreadUtil.runOnSwingThreadAndWait(() -> ac.doCompletion());
									completionInAction = false;
								}
							}, "AutoComplete").start();
						}
					}
				}

				@Override public void keyReleased(KeyEvent keyEvent) {
					super.keyReleased(keyEvent);
					if (keyEvent.getKeyCode() == KeyEvent.VK_CONTROL) {
						disableJumpToMode();
					}
				}
			});

			te.addMouseListener(new MouseAdapter() {
				@Override public void mouseExited(MouseEvent e) {
					super.mouseExited(e);
					disableJumpToMode();
				}

				@Override public void mouseEntered(MouseEvent e) {
					super.mouseEntered(e);
					if ((e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK) {
						te.setCursor(new Cursor(Cursor.HAND_CURSOR));
						jumpToMode = true;
					}
				}
			});

			te.addMouseListener(new MouseAdapter() {

				@Override public void mouseClicked(MouseEvent mouseEvent) {
					CodeEditorView.this.mouseEvent = mouseEvent;
					if (jumpToMode && ac != null) {
						DeclarationFinder.InClassPosition position = DeclarationFinder.getDeclarationOnPos(
								mcreator.getWorkspace(), parser, te, jls.getJarManager());
						if (position != null) {
							if (position.classFileNode == null) {
								te.setCaretPosition(position.carret);
								SwingUtilities.invokeLater(() -> centerLineInScrollPane());
							} else {
								ProjectFileOpener.openFileSpecific(mcreator, position.classFileNode,
										position.openInReadOnly, position.carret, position.virtualFile);
							}
							disableJumpToMode();
						} else {
							new FocusableTip(te, null).toolTipRequested(mouseEvent,
									L10N.t("ide.errors.failed_find_declaration"));
						}
					}
					jumpToMode = false;
				}
			});
		} else if (fileName.endsWith(".mcfunction")) {
			SwingUtilities.invokeLater(() -> {
				AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory) TokenMakerFactory.getDefaultInstance();
				atmf.putMapping("text/mcfunction", MinecraftCommandsTokenMaker.class.getName());
				te.setSyntaxEditingStyle("text/mcfunction");
			});
		} else if (fileName.endsWith(".info") || fileName.endsWith(".json") || fileName.endsWith(".mcmeta")) {
			SwingUtilities.invokeLater(() -> {
				try {
					te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JSON);
				} catch (Exception ignored) {
				}
			});
		} else if (fileName.endsWith(".xml")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_XML));
		} else if (fileName.endsWith(".lang")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_PROPERTIES_FILE));
		} else if (fileName.endsWith(".gradle")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_GROOVY));
		} else if (fileName.endsWith(".md")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_MARKDOWN));
		} else if (fileName.endsWith(".js")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT));

			JavaScriptLanguageSupport javaScriptLanguageSupport = new JavaScriptLanguageSupport();

			javaScriptLanguageSupport.setAutoCompleteEnabled(PreferencesManager.PREFERENCES.ide.autocomplete.get());
			javaScriptLanguageSupport.setAutoActivationEnabled(
					!PreferencesManager.PREFERENCES.ide.autocompleteMode.get().equals("Manual"));
			javaScriptLanguageSupport.setParameterAssistanceEnabled(true);
			javaScriptLanguageSupport.setShowDescWindow(PreferencesManager.PREFERENCES.ide.autocompleteDocWindow.get());

			javaScriptLanguageSupport.install(te);

			if (ac != null)
				AutocompleteStyle.installStyle(ac, te);
		} else if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
			SwingUtilities.invokeLater(() -> te.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_YAML));
		}

		SwingUtilities.invokeLater(this::loadSourceTree);
	}

	private void setCustomNotice(String notice) {
		ro.setText(notice);
		ro.setVisible(true);
	}

	public void hideNotice() {
		ro.setVisible(false);
	}

	public void disableJumpToMode() {
		jumpToMode = false;
		te.setCursor(new Cursor(Cursor.TEXT_CURSOR));
	}

	private void loadSourceTree() {
		String language = te.getSyntaxEditingStyle();

		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			tree = new JavaOutlineTree();
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			tree = new JsonTree();
		} else if (SyntaxConstants.SYNTAX_STYLE_JAVASCRIPT.equals(language)) {
			tree = new JavaScriptOutlineTree();
		}

		if (tree != null) {
			tree.setCellRenderer(new AstTreeCellRendererCustom());
			tree.setOpaque(false);
			tree.listenTo(te);
			tree.setRowHeight(18);
			treeSP.setViewportView(tree);
			treeSP.revalidate();
			spne.setRightComponent(treeSP);
			spne.setDividerLocation(0.8);
		} else {
			spne.setRightComponent(new JPanel());
		}
	}

	public void setChangeListener(ChangeListener changeListener) {
		this.cl = changeListener;
	}

	public void reformatTheCodeOnly() {
		String language = te.getSyntaxEditingStyle();
		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			int pos = te.getCaretPosition();
			String ncode = codeCleanup.reformatTheCodeOnly(te.getText());
			te.setText(ncode);
			te.setCaretPosition(pos);
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			int pos = te.getCaretPosition();
			JsonElement json = JsonParser.parseString(te.getText());
			te.setText(JSONWriter.gson.toJson(json));
			te.setCaretPosition(pos);
		}
	}

	public void reformatTheCodeOrganiseAndFixImports() {
		String language = te.getSyntaxEditingStyle();
		if (SyntaxConstants.SYNTAX_STYLE_JAVA.equals(language)) {
			int pos = te.getCaretPosition();
			String ncode = codeCleanup.reformatTheCodeAndOrganiseImports(mcreator.getWorkspace(), te.getText());
			te.setText(ncode);
			te.setCaretPosition(pos);
		} else if (SyntaxConstants.SYNTAX_STYLE_JSON.equals(language)) {
			int pos = te.getCaretPosition();
			JsonElement json = JsonParser.parseString(te.getText());
			te.setText(JSONWriter.gson.toJson(json));
			te.setCaretPosition(pos);
		}
	}

	public void saveCode() {
		savingMCreatorModElementWarning();
		FileIO.writeStringToFile(te.getText(), fileWorkingOn);
		changed = false;
		if (cl != null)
			cl.stateChanged(new ChangeEvent(this));
	}

	public void centerLineInScrollPane() {
		Container container = SwingUtilities.getAncestorOfClass(JViewport.class, te);

		if (container == null)
			return;

		try {
			Rectangle2D r = te.modelToView2D(te.getCaretPosition());
			if (r == null)
				return;
			JViewport viewport = (JViewport) container;
			int extentHeight = viewport.getExtentSize().height;
			int viewHeight = viewport.getViewSize().height;

			int y = (int) Math.max(0, r.getY() - ((extentHeight - r.getHeight()) / 2));
			y = Math.min(y, viewHeight - extentHeight);

			viewport.setViewPosition(new Point(0, y));
		} catch (BadLocationException ignored) {
		}
	}

	void setFileOwnerModElement(ModElement fileOwner) {
		this.fileOwner = fileOwner;
		boolean codeLocked = this.fileOwner.isCodeLocked();
		if (!codeLocked) {
			setCustomNotice(L10N.t("ide.warnings.created_from_ui", this.fileOwner.getName()));
		}
	}

	private void savingMCreatorModElementWarning() {
		if (this.fileOwner != null) {
			boolean codeLocked = this.fileOwner.isCodeLocked();
			if (!codeLocked) {
				Object[] options = { L10N.t("ide.actions.lock_and_save"), L10N.t("ide.actions.save_without_locking") };
				int n = JOptionPane.showOptionDialog(mcreator, L10N.t("ide.warnings.save_unlocked_element"),
						L10N.t("ide.warnings.save_unlocked_element.title"), JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE, null, options, options[1]);

				if (n == 0) {
					this.fileOwner.setCodeLock(true);
					mcreator.getWorkspace().markDirty();
					ro.setVisible(false);
				} else {
					setCustomNotice(L10N.t("ide.warnings.created_from_ui", this.fileOwner.getName()));
				}
			}
		}
	}

	@Override public ViewBase showView() {
		MCreatorTabs.Tab fileTab = new MCreatorTabs.Tab(this, fileWorkingOn, false);
		fileTab.setTabClosingListener(tab -> {
			if (((CodeEditorView) tab.getContent()).changed) {
				Object[] options = { L10N.t("ide.action.close_and_save"), L10N.t("common.close"),
						UIManager.getString("OptionPane.cancelButtonText") };
				int res = JOptionPane.showOptionDialog(mcreator, L10N.t("ide.warnings.file_not_saved",
								((CodeEditorView) tab.getContent()).fileWorkingOn.getName()), L10N.t("common.warning"),
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
				if (res == 0) {
					((CodeEditorView) tab.getContent()).saveCode();
					return true;
				} else
					return res == 1;
			}
			return true;
		});
		if (readOnly)
			fileTab.setActiveColor(Theme.current().getAltForegroundColor());

		setChangeListener(changeEvent -> {
			if (!readOnly) {
				if (changed) {
					fileTab.setActiveColor(Theme.current().getForegroundColor());
					fileTab.setInactiveColor(Theme.current().getAltForegroundColor());
				} else {
					fileTab.setActiveColor(Theme.current().getInterfaceAccentColor());
					fileTab.setInactiveColor(Theme.current().getAltBackgroundColor());
				}
			}
		});

		MCreatorTabs.Tab existing = mcreator.mcreatorTabs.showTabOrGetExisting(fileWorkingOn);
		if (existing == null) {
			mcreator.mcreatorTabs.addTab(fileTab);
			return this;
		}
		return (ViewBase) existing.getContent();
	}

	public CodeCleanup getCodeCleanup() {
		return codeCleanup;
	}

	@Override public String getViewName() {
		return fileWorkingOn.getName();
	}

	@Override public ImageIcon getViewIcon() {
		return FileIcons.getIconForFile(fileWorkingOn);
	}

	public static boolean isFileSupported(String fileName) {
		return Arrays.asList("java", "info", "txt", "json", "mcmeta", "lang", "gradle", "ini", "conf", "xml",
						"properties", "mcfunction", "toml", "js", "yaml", "yml", "md", "cfg")
				.contains(FilenameUtilsPatched.getExtension(fileName));
	}

	public void jumpToLine(int linenum) {
		new Thread(() -> {
			SwingUtilities.invokeLater(te::requestFocus);
			try {
				Thread.sleep(250);
			} catch (InterruptedException ignored) {
			}
			SwingUtilities.invokeLater(() -> {
				try {
					te.setCaretPosition(te.getLineStartOffset(linenum));
					centerLineInScrollPane();
				} catch (BadLocationException ignored) {
				}
			});
		}, "JumpToLine").start();
	}

	@Nullable public JavaParser getParser() {
		return parser;
	}

	@Nullable public BreakpointHandler getBreakpointHandler() {
		return breakpointHandler;
	}

}
